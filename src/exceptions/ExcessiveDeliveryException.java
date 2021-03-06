package exceptions;

/**
 * An exception thrown when the robot tries to deliver more items than its tube capacity without refilling.
 */
public class ExcessiveDeliveryException extends Exception {
	/**
   *
   */
  private static final long serialVersionUID = 1L;

  public ExcessiveDeliveryException() {
		super("Attempting to deliver more than 4 items in a single trip!!");
	}
}
