/**
 * 
 */
package de.jaide.exception;

/**
 * The exception to throw if file manipulation failed.
 * 
 * @author Rias A. Sherzad (rias.sherzad@jaide.de)
 */
public class FileOperationFailedException extends Exception {
  private static final long serialVersionUID = -132921818779360085L;

  /**
   * Default constructor. Creates a new FileOperationFailedException.
   */
  public FileOperationFailedException() {
    super();
  }

  /**
   * Creates a new FileOperationFailedException.
   * 
   * @param message The message that yielded this Exception.
   * @param throwable The wrapped throwable.
   */
  public FileOperationFailedException(String message, Throwable throwable) {
    super(message, throwable);
  }

  /**
   * Creates a new FileOperationFailedException.
   * 
   * @param message The message that yielded this Exception.
   */
  public FileOperationFailedException(String message) {
    super(message);
  }

  /**
   * Creates a new FileOperationFailedException.
   * 
   * @param throwable The wrapped throwable.
   */
  public FileOperationFailedException(Throwable throwable) {
    super(throwable);
  }
}
