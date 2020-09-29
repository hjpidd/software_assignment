package automail;

import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;

import simulation.Building;
import simulation.Clock;
import simulation.IMailDelivery;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;

/**
* The robot delivers mail!
*/
public class Robot {
  
  private static final int MAX_ITEM_WEIGHT = 2000;
  
  IMailDelivery deliveryInterface;
  protected final String id;
  private RobotState state;
  private int currentFloor;
  private int destinationFloor;
  private MailPool mailPool;
  private boolean receivedDispatch;
  
  private MailItem deliveryItem = null;
  private MailItem tube = null;
  private int deliveryCounter;
  
  private LinkedList<Storage> storage;
  
  
  /**
  * Initiates the robot's location at the start to be at the mailroom
  * also set it to be waiting for mail.
  * @param behaviour governs selection of mail items for delivery and behaviour on priority arrivals
  * @param delivery governs the final delivery
  * @param mailPool is the source of mail items
  */
  public Robot(IMailDelivery deliveryInterface, MailPool mailPool) {

    id = "R" + hashCode();

    state = RobotState.RETURNING;
    currentFloor = Building.MAILROOM_LOCATION;
    this.deliveryInterface = deliveryInterface;
    this.mailPool = mailPool;
    this.receivedDispatch = false;
    this.deliveryCounter = 0;
  }

  public String getId() {
    return this.id;
  }

  public RobotState getState() {
    return this.state;
  }
  
  /**
  * This is called when a robot is assigned the mail items and ready to dispatch for the delivery 
  */
  public void dispatch() {
    receivedDispatch = true;
  }
  
  /**
  * This is called on every time step
  * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
  */
  public void operate() throws ExcessiveDeliveryException {    	
    switch(state) {

      /** This state is triggered when the robot is returning to the mailroom after a delivery */
      case RETURNING:

        if (currentFloor != Building.MAILROOM_LOCATION) {

          moveTowards(Building.MAILROOM_LOCATION);
          break;
        }

        if (tube != null) {

          mailPool.addToPool(tube);

          System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), tube.toString());

          tube = null;
        }

        /** Tell the sorter the robot is ready */
        mailPool.registerWaiting(this);
        changeState(RobotState.WAITING);

        break;
      
      case WAITING:

        /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
        if (!isEmpty() && receivedDispatch) {

          receivedDispatch = false;
          deliveryCounter = 0; // reset delivery counter
          setDestination();
          changeState(RobotState.DELIVERING);
        }

        break;

      case DELIVERING:

        if (currentFloor != destinationFloor) {

          moveTowards(destinationFloor);
          break;
        }

        deliveryInterface.deliver(deliveryItem);
        deliveryItem = null;
        deliveryCounter++;

        if (deliveryCounter > 2) {  // Implies a simulation bug
          throw new ExcessiveDeliveryException();
        }

        /** Check if want to return, i.e. if there is no item in the tube*/
        if (tube == null) {
          changeState(RobotState.RETURNING);
        } else {

          /** If there is another item, set the robot's route to the location to deliver the item */
          deliveryItem = tube;
          tube = null;
          setDestination();
          changeState(RobotState.DELIVERING);
        }

        break;
    }
  }
  
  /**
  * Sets the route for the robot
  */
  private void setDestination() {
    /** Set the destination floor */
    destinationFloor = deliveryItem.getDestFloor();
  }
  
  /**
  * Generic function that moves the robot towards the destination
  * @param destination the floor towards which the robot is moving
  */
  private void moveTowards(int destination) {
    currentFloor = currentFloor < destination ? currentFloor + 1 : currentFloor - 1;
  }
  
  private String getIdTube() {
    return String.format("%s(%1d)", id, (tube == null ? 0 : 1));
  }
  
  /**
  * Prints out the change in state
  * @param nextState the state to which the robot is transitioning
  */
  private void changeState(RobotState nextState) {

    assert(!(deliveryItem == null && tube != null));

    if (state != nextState) {
      System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), state, nextState);
    }

    state = nextState;
    if (nextState == RobotState.DELIVERING) {
      System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
    }
  }
  
  public MailItem getTube() {
    return tube;
  }
  
  private static int count = 0;
  private static Map<Integer, Integer> hashMap = new TreeMap<>();
  
  @Override
  public int hashCode() {
    Integer hash0 = super.hashCode();
    Integer hash = hashMap.get(hash0);
    if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
    return hash;
  }
  
  public boolean isEmpty() {
    return (deliveryItem == null && tube == null);
  }
  
  public void addToHand(MailItem mailItem) throws ItemTooHeavyException {

    assert(deliveryItem == null);

    if (mailItem.weight > MAX_ITEM_WEIGHT) throw new ItemTooHeavyException();
    deliveryItem = mailItem;
  }
  
  public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
    assert(tube == null);
    tube = mailItem;
    if (tube.weight > MAX_ITEM_WEIGHT) throw new ItemTooHeavyException();
  }
  
  public void addToStorage(Item item) throws ItemTooHeavyException {
    // To do
  }
}
