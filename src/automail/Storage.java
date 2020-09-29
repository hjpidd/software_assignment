package automail;

import java.util.LinkedList;
import java.util.List;

import exceptions.StorageFullException;
import exceptions.IncompatibleItemTypeException;

public class Storage {
  
  private int capacity;
  private int bootTime;
  private Class<? extends Item> accepts;
  private LinkedList<Item> items;

  public Storage(int capacity, int bootTime, Class<? extends Item> accepts) {
    this.capacity = capacity;
    this.bootTime = bootTime;
    this.accepts = accepts;
  }

  public int getCapacity() {
    return this.capacity;
  }

  public int getBootTime() {
    return this.bootTime;
  }

  public Class<? extends Item> acceptsWhichType() {
    return this.accepts;
  }

  public List<Item> getItems() {
    return this.items;
  }

  public void addItem(Item item) throws StorageFullException, IncompatibleItemTypeException {

    if (
      item == null ||
      item.getClass() != this.accepts
    ) {
      throw new IncompatibleItemTypeException();
    }

    
  }
}
