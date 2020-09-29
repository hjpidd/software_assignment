package simulation;

import java.util.*;

import javax.sound.sampled.SourceDataLine;

import automail.MailItem;
import automail.MailPool;

/**
 * This class generates the mail
 */
public class MailGenerator extends Generator {

    private Map<Integer,ArrayList<MailItem>> allMail;

    /**
     * Constructor for mail generation
     * @param mailToCreate roughly how many mail items to create
     * @param mailMaxWeight limits the maximum weight of the mail
     * @param mailPool where mail items go on arrival
     * @param seed random seed for generating mail
     */
    public MailGenerator(int mailToCreate, int mailMaxWeight, MailPool mailPool, HashMap<Boolean,Integer> seed){
    	super(mailPool, seed);
        // Vary arriving mail by +/-20%
        toCreate = mailToCreate*4/5 + random.nextInt(mailToCreate*2/5);
        maxWeight = mailMaxWeight;
        // System.out.println("Num Mail Items: "+MAIL_TO_CREATE);
        created = 0;
        allMail = new HashMap<Integer,ArrayList<MailItem>>();
    }

    /**
     * @return a new mail item that needs to be delivered
     */
    private MailItem generateMail(){
    	MailItem newMailItem;
        int destinationFloor = generateDestinationFloor();
        int priorityLevel = generatePriorityLevel();
        int arrivalTime = generateArrivalTime();
        int weight = generateWeight();
        // Check if arrival time has a priority mail
        if(	(random.nextInt(6) > 0) ||  // Skew towards non priority mail
        	(allMail.containsKey(arrivalTime) &&
        	allMail.get(arrivalTime).stream().anyMatch(e -> PriorityMailItem.class.isInstance(e))))
        {
        	newMailItem = new MailItem(destinationFloor,arrivalTime,weight);      	
        } else {
        	newMailItem = new PriorityMailItem(destinationFloor,arrivalTime,weight,priorityLevel);
        }
        newMailItem.priority = (newMailItem instanceof PriorityMailItem) ? ((PriorityMailItem) newMailItem).getPriorityLevel() : 1;
        return newMailItem;
    }

    /**
     * @return a random priority level selected from 1 - 100
     */
    private int generatePriorityLevel(){
        return 10*(1 + random.nextInt(10));
    }

    /**
     * This class initializes all mails and sets their corresponding values,
     * All generated mails will be saved in allMail
     */
    public void generateAllMail(){
        while(!complete){
            MailItem newMail = generateMail();
            int timeToDeliver = newMail.getArrivalTime();
            /** Check if key exists for this time **/
            if(allMail.containsKey(timeToDeliver)){
                /** Add to existing array */
                allMail.get(timeToDeliver).add(newMail);
            }
            else{
                /** If the key doesn't exist then set a new key along with the array of MailItems to add during
                 * that time step.
                 */
                ArrayList<MailItem> newMailList = new ArrayList<MailItem>();
                newMailList.add(newMail);
                allMail.put(timeToDeliver,newMailList);
            }
            /** Mark the mail as created */
            created++;

            /** Once we have satisfied the amount of mail to create, we're done!*/
            if(created == toCreate){
                complete = true;
            }
        }

    }
    
    /**
     * Given the clock time, put the generated mails into the mailPool.
     * So that the robot will can pick up the mails from the pool.
     * @return Priority
     */
    public void addToMailPool(){
    	//Priority is not used
//    	PriorityMailItem priority = null;
    	// Check if there are any mail to create
        if(this.allMail.containsKey(Clock.Time())){
            for(MailItem mailItem : allMail.get(Clock.Time())){
//            	if (mailItem instanceof PriorityMailItem) priority = ((PriorityMailItem) mailItem);
                System.out.printf("T: %3d > new addToPool [%s]%n", Clock.Time(), mailItem.toString());
                mailPool.addToPool(mailItem);
            }
        }
//        return priority;
    }
    
}
