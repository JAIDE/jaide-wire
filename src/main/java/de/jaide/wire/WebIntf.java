package de.jaide.wire;

import de.jaide.exception.CrawlerFailedException;
import de.jaide.exception.FileOperationFailedException;

/**
 * Web Interface
 * 
 * @author Rias A. Sherzad (rias.sherzad@jaide.de)
 */
public interface WebIntf {

  void connect();

  void crawlPage() throws CrawlerFailedException, FileOperationFailedException;

  void disconnect();

}
