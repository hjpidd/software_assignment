package automail;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;
import automail.Item;
import automail.MailItem;
import automail.FoodItem;

import exceptions.ItemTooHeavyException;

/**
 * addToPool is called when there are mail items newly arrived at the building to add to the MailPool or
 * if a robot returns with some undelivered items - these are added back to the MailPool.
 * The data structure and algorithms used in the MailPool is your choice.
 * 
 */
public class MailPool {

	private class Delivery {
		MailItem mailItem;
		FoodItem foodItem;
		// Use stable sort to keep arrival time relative positions

		
		public Delivery(MailItem mailItem) {
			this.mailItem = mailItem;
		}
		
		public Delivery(FoodItem foodItem) {
			this.foodItem = foodItem;
		}
	}
	
	public class ItemComparator implements Comparator<Delivery> {
		@Override
		public int compare(Delivery i1, Delivery i2) {
			int order = 0;
			int p1 = i1.mailItem.priority;
			int p2 = i2.mailItem.priority;
			int d1 = i1.mailItem.destination_floor;
			int d2 = i2.mailItem.destination_floor;
			if (p1 < p2) {
				order = 1;
			} else if (p1 > p2) {
				order = -1;
			} else if (d1 < d2) {
				order = 1;
			} else if (d1 > d2) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Delivery> pool;
	private LinkedList<Robot> robots;

	public MailPool(int nrobots){
		// Start empty
		pool = new LinkedList<Delivery>();
		robots = new LinkedList<Robot>();
	}

	/**
     * Adds an item to the mail pool
     * @param mailItem the mail item being added.
     */
	public void addToPool(MailItem mailItem) {
		Delivery delivery = new Delivery(mailItem);
		pool.add(delivery);
		// pool.sort(new ItemComparator());
	}
	
	public void addToPool(FoodItem foodItem) {
		Delivery delivery = new Delivery(foodItem);
		pool.add(delivery);
		// food items are not currently sorted; how do we sort them?
	}
	
	/**
     * load up any waiting robots with mailItems, if any.
     */
	public void loadItemsToRobot() throws ItemTooHeavyException {
		//List available robots
		ListIterator<Robot> robotIterator = robots.listIterator();
		while (robotIterator.hasNext()) {
			try{
				Robot robot = robotIterator.next();
				assert(robot.isEmpty());
				loadItem(robot);
				robotIterator.remove(); //Remove Robot from the queue
				robot.dispatch(); // send the robot off if it has any items to deliver
			} catch (Exception e) { 
	            throw e; 
	        } 
		}
	}
	
	//load items to the robot
	private void loadItem(Robot robot) throws ItemTooHeavyException {
		// System.out.printf("P: %3d%n", pool.size());
		ListIterator<Delivery> j = pool.listIterator();
		if (pool.size() > 0) {
			robot.addToHand(j.next().mailItem); // hand first as we want higher priority delivered first
			j.remove();
			if (pool.size() > 0) {
				robot.addToTube(j.next().mailItem);
				j.remove();
			}
		}
	}

	/**
     * @param robot refers to a robot which has arrived back ready for more mailItems to deliver
     */	
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
