package exceptions;

public class StorageFullException extends Exception {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public StorageFullException() {
    super("This storage object is already at capacity.");
  }
}
