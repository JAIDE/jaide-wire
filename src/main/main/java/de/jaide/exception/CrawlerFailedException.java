/**
 * 
 */
package de.jaide.exception;

/**
 * The exception to throw if the connection to the web page failed.
 * 
 * @author Rias A. Sherzad (rias.sherzad@jaide.de)
 */
public class CrawlerFailedException extends Exception {
  private static final long serialVersionUID = -45867054037124314L;

  /**
   * Default constructor. Creates a new CrawlerFailedException.
   */
  public CrawlerFailedException() {
    super();
  }

  /**
   * Creates a new CrawlerFailedException.
   * 
   * @param message The message that yielded this Exception.
   * @param throwable The wrapped throwable.
   */
  public CrawlerFailedException(String message, Throwable throwable) {
    super(message, throwable);
  }

  /**
   * Creates a new CrawlerFailedException.
   * 
   * @param message The message that yielded this Exception.
   */
  public CrawlerFailedException(String message) {
    super(message);
  }

  /**
   * Creates a new CrawlerFailedException.
   * 
   * @param throwable The wrapped throwable.
   */
  public CrawlerFailedException(Throwable throwable) {
    super(throwable);
  }
}
