package testerObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class TesterCustomer {
	
	private int id;
	private String address;
	private int distance;
	private int creditCard;
	private int availableAmountInCreditCard;
	private HashMap<Integer, List<TesterOrder>> orders;
	private List<TesterReceipt> receipts;
	
	/**
	 * @param id
	 * @param address
	 * @param distance
	 * @param creditCard
	 * @param availableAmountInCreditCard
	 * @param orders
	 */
	public TesterCustomer(int id, String address, int distance, int creditCard, int availableAmountInCreditCard, HashMap<Integer, List<TesterOrder>> orders) {
		this.id = id;
		this.address = address;
		this.distance = distance;
		this.creditCard = creditCard;
		this.availableAmountInCreditCard = availableAmountInCreditCard;
		this.orders = orders;
		this.receipts = new ArrayList<TesterReceipt>();
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @return the creditCard
	 */
	public int getCreditCard() {
		return creditCard;
	}

	/**
	 * @return the availableAmountInCreditCard
	 */
	public int getAvailableAmountInCreditCard() {
		return availableAmountInCreditCard;
	}

	/**
	 * @param availableAmountInCreditCard the availableAmountInCreditCard to set
	 */
	public void setAvailableAmountInCreditCard(int availableAmountInCreditCard) {
		this.availableAmountInCreditCard = availableAmountInCreditCard;
	}


	/**
	 * @return the orders
	 */
	public HashMap<Integer, List<TesterOrder>> getOrders() {
		return orders;
	}
	
	public void addOrder(Integer tick, TesterOrder order) {
		if (orders.get(tick) == null) {
			orders.put(tick, new ArrayList<TesterOrder>(Arrays.asList(order)));
		}else {
			orders.get(tick).add(order);
		}
	}


	/**
	 * @return the receipts
	 */
	public List<TesterReceipt> getReceipts() {
		return receipts;
	}


	/**
	 * @param receipts the receipts to set
	 */
	public void addReceipt(TesterReceipt receipt) {
		this.receipts.add(receipt);
	}

}
