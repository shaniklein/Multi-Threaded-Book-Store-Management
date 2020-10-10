package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBusImpl;
import com.google.gson.Gson;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {

	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}
	private LinkedList<BookInventoryInfo> inventoryInfos;


	/**
	 * Retrieves the single instance of this class.

	 */

	public static Inventory getInstance() {
		return SingletonHolder.instance;	}


	private Inventory(){
		inventoryInfos=new LinkedList<>();
	}
	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @pre: inventory.length>0
	 * @post: this.isEmpty() == false
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */

	public void load (BookInventoryInfo[ ] inventory ) {
		Collections.addAll(inventoryInfos, inventory);
		}




	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 *
	 * @pre: none
	 * @post: isInStock(book)==false; this==NOT_IN_STOCK
	 * @post: isInStock(book)==true; this==SUCCESSFULLY_TAKEN
	 * @
	 */

	public OrderResult take (String book) {
		for (BookInventoryInfo _book : inventoryInfos) {
			synchronized (book) {
				if (_book.getBookTitle().equals(book)) {
					if (_book.getAmountInInventory() > 0) {
						_book.setAmount();
						return OrderResult.SUCCESSFULLY_TAKEN;
					}
				}

			}
		}
		return OrderResult.NOT_IN_STOCK;
	}


	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 * @pre :none
	 * @post: isInStock(book)==true; this!=-1
	 * @post: isInStock(book)==false; this==-1
	 */
	public  int  checkAvailabiltyAndGetPrice(String book) {
			for (BookInventoryInfo _book : inventoryInfos) {
				if (_book.getBookTitle().equals(book))
					if (_book.getAmountInInventory() > 0)
						return _book.getPrice();

			}
			return -1;
		}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printInventoryToFile(String filename)  {
		HashMap<String,Integer>  booksToPrint = new HashMap<>();
		for (BookInventoryInfo book :
				this.inventoryInfos)
			booksToPrint.put(book.getBookTitle(), book.getAmountInInventory());


			try {
				FileOutputStream fileOutputStream = new FileOutputStream(filename);
				ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
				oos.writeObject(booksToPrint);
				oos.close();
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}



}


