package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import automail.MailPool;

public class Generator {
	
	protected int toCreate;
    protected int maxWeight;
    
    protected int created;

	protected final Random random;
    /** This seed is used to make the behaviour deterministic */
    
    protected boolean complete;
    protected MailPool mailPool;

    public Generator() {
		this.random = new Random();
		};
    
    public Generator(MailPool mailPool, HashMap<Boolean,Integer> seed) {
    	if(seed.containsKey(true)){
        	this.random = new Random((long) seed.get(true));
        }
        else{
        	this.random = new Random();	
        }
    	complete = false;
    	this.mailPool = mailPool;
    };
    
    /**
     * @return a destination floor between the ranges of GROUND_FLOOR to FLOOR
     */
    protected int generateDestinationFloor(){
        return Building.LOWEST_FLOOR + random.nextInt(Building.FLOORS);
    }
    
    /**
     * @return a random weight
     */
    protected int generateWeight(){
    	final double mean = 200.0; // grams for normal item
    	final double stddev = 1000.0; // grams
    	double base = random.nextGaussian();
    	if (base < 0) base = -base;
    	int weight = (int) (mean + base * stddev);
        return weight > maxWeight ? maxWeight : weight;
    };
    
    /**
     * @return a random arrival time before the last delivery time
     */
    protected int generateArrivalTime(){
        return 1 + random.nextInt(Clock.MAIL_RECEVING_LENGTH);
    }
}
