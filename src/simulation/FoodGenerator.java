package simulation;

import java.util.*;

import automail.FoodItem;
import automail.MailPool;

public class FoodGenerator extends Generator {
    private Map<Integer,ArrayList<FoodItem>> allFood;

	
    /**
     * Constructor for mail generation
     * @param foodToCreate roughly how many mail items to create
     * @param foodMaxWeight limits the maximum weight of the mail
     * @param mailPool where mail items go on arrival
     * @param seed random seed for generating mail
     */
    public FoodGenerator(int foodToCreate, int foodMaxWeight, MailPool mailPool, HashMap<Boolean,Integer> seed){
    	super(mailPool, seed);
        // Vary arriving mail by +/-20%
        toCreate = foodToCreate*4/5 + random.nextInt(foodToCreate*2/5);
        maxWeight = foodMaxWeight;
        // System.out.println("Num Mail Items: "+MAIL_TO_CREATE);
        created = 0;
        allFood = new HashMap<Integer,ArrayList<FoodItem>>();
    }

    /**
     * @return a new mail item that needs to be delivered
     */
    private FoodItem generateFood(){
    	FoodItem newFoodItem;
        int destinationFloor = generateDestinationFloor();
        int arrivalTime = generateArrivalTime();
        int weight = generateWeight();
        newFoodItem = new FoodItem(destinationFloor, arrivalTime, weight);
        return newFoodItem;
    }

    /**
     * This class initializes all mails and sets their corresponding values,
     * All generated mails will be saved in allFood
     */
    public void generateAllFood(){
        while(!complete){
            FoodItem newFood = generateFood();
            int timeToDeliver = newFood.getArrivalTime();
            /** Check if key exists for this time **/
            if(allFood.containsKey(timeToDeliver)){
                /** Add to existing array */
            	allFood.get(timeToDeliver).add(newFood);
            }
            else{
                /** If the key doesn't exist then set a new key along with the array of MailItems to add during
                 * that time step.
                 */
                ArrayList<FoodItem> newFoodList = new ArrayList<FoodItem>();
                newFoodList.add(newFood);
                allFood.put(timeToDeliver,newFoodList);
            }
            /** Mark the food as created */
            created++;

            /** Once we have satisfied the amount of food to create, we're done!*/
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
        if(this.allFood.containsKey(Clock.Time())){
            for(FoodItem foodItem : allFood.get(Clock.Time())){
                System.out.printf("T: %3d > new addToPool [%s]%n", Clock.Time(), foodItem.toString());
                mailPool.addToPool(foodItem);
            }
        }
//        return priority;
    }
}
