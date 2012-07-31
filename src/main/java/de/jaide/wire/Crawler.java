package de.jaide.wire;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import de.jaide.exception.CrawlerFailedException;
import de.jaide.exception.FileOperationFailedException;

/**
 * Wikipedia Crawler
 * 
 * @author Rias A. Sherzad (rias.sherzad@jaide.de)
 */
public class Crawler extends FileOperation implements WebIntf {
  /**
   * Our logger.
   */
  private static Logger LOG = Logger.getLogger(Crawler.class);
  protected WebClient webClient = null;

  ArrayList<String> urlList = new ArrayList<String>();
  ArrayList<String> bannedWordList = new ArrayList<String>();
  ArrayList<String> bannedSectionList = new ArrayList<String>();
  ArrayList<String> wordList = new ArrayList<String>();

  HtmlPage page = null;
  HtmlPage crawlPage = null;
  HtmlAnchor anchor = null;

  String word = "";
  String anc = "";
  String[] lang = { "en", "ar", "fa", "fr", "de", "id", "es", "tr", "ur" };

  public Crawler(String args[]) throws CrawlerFailedException, FileOperationFailedException {
    /*
     * Reading URL
     */
    if (args.length >= 1)
      urlList = readFile(args[0]);

    /*
     * Reading banned Sections
     */
    if (args.length >= 2)
      bannedSectionList = readFile(args[1]);

    /*
     * Reading banned words
     */
    if (args.length == 3)
      bannedWordList = readFile(args[2]);

    connect();
    crawlPage();
    disconnect();
  }

  /*
   * Connecting the webPage
   */
  @Override
  public void connect() {
    webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
    webClient.setJavaScriptEnabled(false);
    webClient.setCssEnabled(false);
  }

  /*
   * Getting the page information
   */
  @SuppressWarnings("unchecked")
  @Override
  public void crawlPage() throws CrawlerFailedException, FileOperationFailedException {
    String content = "";
    String[] value = null;
    String host = "";
    String[] inputLang = null;
    List<HtmlAnchor> frAnchors = null;

    /*
     * Reading the webpage for all the urls
     */
    for (int i = 0; i < urlList.size(); i++) {
      try {
        content = urlList.get(i);
        value = content.split(",");
        outputFileName = value[0].toString();
        url = new URL(value[1]);
        /*
         * Getting locale of the given url
         */
        host = url.getHost();
        inputLang = host.split("\\.");
        locale = inputLang[0];
        CrawlInfo(url);
        page = (HtmlPage) webClient.getPage(url);

        /*
         * Reading the webpage for other languages
         */
        for (int j = 0; j < lang.length; j++) {
          locale = lang[j].toString().trim();
          frAnchors = (List<HtmlAnchor>) page.getByXPath("//div[@id='p-lang']//a[@lang='" + locale + "']");

          if (frAnchors.size() > 0) {
            url = new URL(URLDecoder.decode("http:" + frAnchors.get(0).getHrefAttribute(), "UTF-8"));
            CrawlInfo(url);
          }

          Thread.sleep(250);
        }
        Thread.sleep(250);
      } catch (MalformedURLException e) {
        throw new CrawlerFailedException(
            "The server tried to connect to or was redirected to a host that could not be resolved. This could be a firewall or a DNS issue.",
            e.fillInStackTrace());
      } catch (UnsupportedEncodingException e) {
        throw new CrawlerFailedException("UTF-8 not supported", e.fillInStackTrace());
      } catch (FailingHttpStatusCodeException e) {
        throw new CrawlerFailedException(
            "The server tried to connect to or was redirected to a host that could not be resolved. This could be a firewall or a DNS issue.",
            e.fillInStackTrace());
      } catch (InterruptedException e) {
        throw new CrawlerFailedException(e.fillInStackTrace());
      } catch (IOException e) {
        throw new CrawlerFailedException("Some general problem occured. Please see the stacktrace for more information.",
            e.fillInStackTrace());

      }
    }

  }

  /*
   * Extracting the content from the webpage
   */
  private void CrawlInfo(URL url) throws CrawlerFailedException, FileOperationFailedException {
    ArrayList<String> pathList = new ArrayList<String>();
    wordList = new ArrayList();
    try {

      setOutputFile();
      crawlPage = (HtmlPage) webClient.getPage(url);

      /*
       * Print the response headers - in trace mode only.
       */
      if (LOG.isTraceEnabled()) {
        List<NameValuePair> responseHeaders = crawlPage.getWebResponse().getResponseHeaders();
        for (NameValuePair nameValuePair : responseHeaders)
          LOG.trace(nameValuePair.getName() + " --> " + nameValuePair.getValue());
      }

      /*
       * Xpath for content page
       */
      pathList.add("//div[@id='mw-content-text']//p//a");

      /*
       * Xpath for references
       */
      if (!bannedSectionList.contains("References"))
        pathList.add("//ol[@class='references']//li//span[@class='reference-text']//span//a[@class='external text']");

      /*
       * Xpath for Bibliography
       */
      if (!bannedSectionList.contains("Bibliography"))
        pathList.add("//div[@class='refbegin']//ul//li//a[@class='external text']");

      /*
       * Xpath for See Also
       */
      if (!bannedSectionList.contains("See also")) {
        pathList.add("//div[@class='column-count column-count-2']//ul//li//a");
        pathList.add("//table[@class='multicol']//tbody//tr//td[@valign='top' and @align='left']//ul//li//a");
        // pathList.add(".//*[@id='mw-content-text']//ul//li//a");
      }
      /*
       * Xpath for Further reading
       */
      else if (!bannedSectionList.contains("Further reading"))
        pathList.add(".//*[@id='mw-content-text']//ul//li//span//a");

      /*
       * Xpath for External links
       */
      if (!bannedSectionList.contains("External links"))
        pathList.add("//ul//li//a[@class='external text']");

      /*
       * Requesting the webpage for the given xpath
       */
      for (int i = 0; i < pathList.size(); i++)
        dataIterator(pathList.get(i).toString());

      closeOutputFile();

    } catch (FailingHttpStatusCodeException e) {
      throw new CrawlerFailedException(
          "The server tried to connect to or was redirected to a host that could not be resolved. This could be a firewall or a DNS issue.",
          e.fillInStackTrace());
    } catch (IOException e) {
      throw new CrawlerFailedException("Some general problem occured. Please see the stacktrace for more information.",
          e.fillInStackTrace());
    }
  }

  /*
   * Check if the given word is string
   */
  public boolean checkIfString(String in) {

    try {
      Integer.parseInt(in);
    } catch (NumberFormatException ex) {
      return true;
    }

    return false;
  }

  /*
   * Crawling the webpage for given xpath
   */
  private void dataIterator(String path) throws CrawlerFailedException, FileOperationFailedException {

    Iterator<HtmlAnchor> iterator = null;
    @SuppressWarnings("unchecked")
    List<HtmlAnchor> anchors = (List<HtmlAnchor>) crawlPage.getByXPath(path);
    iterator = anchors.iterator();
    try {
      /*
       * Extracting the url and word from the webpage
       */
      while (iterator.hasNext()) {
        anchor = (HtmlAnchor) iterator.next();
        word = WordUtils.capitalize(anchor.asText().toString().trim());
        
        boolean isString = checkIfString(word);
        anc = anchor.getHrefAttribute();
        
        if (!anc.contains("http"))
          value = "http://" + locale + ".wikipedia.org" + anc;
        else
          value = anc;
        anc = URLDecoder.decode(value, "UTF-8");

        if (!anc.toLowerCase().contains("jpg") && !anc.toLowerCase().contains("png") && !anc.toString().toLowerCase().contains("edit")
            && !anc.toString().toLowerCase().contains("gif") && !anc.toString().toLowerCase().contains("svg")
            && !bannedWordList.contains(word) && !word.equalsIgnoreCase("") && !anc.contains("cite_note") && !anc.contains("cite_ref")
            && !word.contains("http") && word.length() > 1 && !word.contains("?") && !word.contains(":") && !wordList.contains(word)
            && isString) {
          /*
           * For unique check
           */
          wordList.add(word);
          writeOutput(word, anc);
        }

      }
    } catch (UnsupportedEncodingException e) {
      throw new CrawlerFailedException("UTF-8 not supported", e.fillInStackTrace());
    }
  }

  @Override
  public void disconnect() {
    webClient.closeAllWindows();
  }

  public static void main(String args[]) {
    try {
      new Crawler(args);
    } catch (CrawlerFailedException e) {
      e.printStackTrace();
    } catch (FileOperationFailedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
