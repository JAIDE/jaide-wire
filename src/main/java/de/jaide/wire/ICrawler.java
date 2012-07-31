package de.jaide.wire;

import de.jaide.exception.CrawlerFailedException;
import de.jaide.exception.FileOperationFailedException;

/**
 * The interface all crawlers have to implement.
 * 
 * @author Janarthanan Ramar (janarthanan.ramar@jaide.de)
 * @author Rias A. Sherzad (rias.sherzad@jaide.de)
 */
public interface ICrawler {

  /**
   * Connects to the crawlable target resource.
   */
  void connect();

  /**
   * Crawls the target resource.
   * 
   * @throws CrawlerFailedException Thrown if crawling failed.
   * @throws FileOperationFailedException Thrown if saving the crawled data failed.
   */
  void crawl() throws CrawlerFailedException, FileOperationFailedException;

  /**
   * Disconnects from the crawlable target resource.
   */
  void disconnect();
}
