package de.jaide.wire;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * TODO
 * 
 * @author TODO NAME
 */
public class Crawler extends FileOperation implements WebIntf {
  protected WebClient webClient = null;

  ArrayList<String> urlList = new ArrayList<String>();

  ArrayList<String> bannedWordList = new ArrayList<String>();

  ArrayList<String> bannedSectionList = new ArrayList<String>();

  HtmlPage page = null;
  HtmlPage crawlPage = null;

  HtmlAnchor anchor = null;

  String word = "";

  String anc = "";

  String[] lang = { "en", "ar", "fa", "fr", "de", "id", "es", "tr", "ur" };

  public Crawler(String args[]) {
    /*
     * Reading URL List file to crawl.
     */
    if (args.length >= 1)
      urlList = readFile(args[0]);

    /*
     * Reading bannedSections file
     */
    if (args.length >= 2)
      bannedSectionList = readFile(args[1]);

    /*
     * Reading banned words File
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
    // TODO Auto-generated method stub
    webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
    webClient.setJavaScriptEnabled(false);
    webClient.setCssEnabled(false);
  }

  /*
   * Getting the page information
   */
  @SuppressWarnings("unchecked")
  @Override
  public void crawlPage() {
    // TODO Auto-generated method stub

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
            System.out.println(url);
            CrawlInfo(url);
          }

        }
      } catch (Exception e) {

      }
    }

  }

  /*
   * Extracting the content from the webpage
   */
  private void CrawlInfo(URL url) {
    ArrayList<String> pathList = new ArrayList<String>();
    try {

      setOutputFile();
      /*
       * Xpath for content page
       */
      crawlPage = (HtmlPage) webClient.getPage(url);
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
       * Xpath for Footnotes
       */
      if (!bannedSectionList.contains("Footnotes"))
        pathList.add("//ul[@id='footer-places']//li//a");

      /*
       * Requesting the webpage for the given xpath
       */
      for (int i = 0; i < pathList.size(); i++)
        dataIterator(pathList.get(i).toString());

      closeOutputFile();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * Crawling the webpage for given xpath
   */
  private void dataIterator(String path) {

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
        word = anchor.asText().toString().trim();
        anc = anchor.getHrefAttribute();
        if (!anc.contains("http"))
          value = "http:/" + anc;
        else
          value = anc;
        anc = URLDecoder.decode(value, "UTF-8");

        if (!anc.toLowerCase().contains("jpg") && !anc.toLowerCase().contains("png") && !anc.toString().toLowerCase().contains("edit")
            && !anc.toString().toLowerCase().contains("gif") && !anc.toString().toLowerCase().contains("svg")
            && !bannedWordList.contains(word) && !word.equalsIgnoreCase("") && !anc.contains("cite_note") && !anc.contains("cite_ref")
            && !word.contains("http") && word.length() > 1) {
          writeOutput(word, anc);
        }

      }
    } catch (Exception e) {
    }
  }

  @Override
  public void disconnect() {
    // TODO Auto-generated method stub
    webClient.closeAllWindows();
  }

  public static void main(String args[]) {
    new Crawler(args);
    System.out.println("***********END**************");
  }
}
