package exceptions;

public class IncompatibleItemTypeException extends Exception {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public IncompatibleItemTypeException() {
    super("This storage type does not accept this item type.");
  }
}
