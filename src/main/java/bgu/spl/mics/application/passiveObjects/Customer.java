package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
    private int creditCard ,
            id ,
            distance;
    private AtomicInteger availableAmountInCreditCard;

    private String name , adress;
    private LinkedList<OrderReceipt> receipts ;

    /**
     * Retrieves the name of the customer.
     */

    public Customer(int id, String name, String address, int distance, int creditCard, int availableAmountInCreditCard) {
        this.adress = address;
        this.availableAmountInCreditCard = new AtomicInteger(availableAmountInCreditCard);
        this.id = id;
        this.receipts = new LinkedList<>();
        this.name = name;
        this.creditCard = creditCard;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    /**
     * Retrieves the ID of the customer  .
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the address of the customer.
     */
    public String getAddress() {
        return adress;
    }

    /**
     * Retrieves the distance of the customer from the store.
     */
    public int getDistance() {
        return distance;
    }


    /**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     *
     * @return A list of receipts.
     */
    public List<OrderReceipt> getCustomerReceiptList() {
        return receipts;
    }

    /**
     * Retrieves the amount of money left on this customers creditCard card.
     * <p>
     *
     * @return Amount of money left.
     */
    public int getAvailableCreditAmount() {
        return availableAmountInCreditCard.get();
    }

    /**
     * Retrieves this customers creditCard card serial number.
     */
    public int getCreditNumber() {
        return creditCard;
    }

    public  void setCreditCard(int price) {
        this.availableAmountInCreditCard.getAndAdd(-price);
    }

    //we synchronize the receipt to make the linked list currcurent
    public void addRecipt(OrderReceipt orderReceipt) {
        synchronized (receipts) {
            receipts.add(orderReceipt);
        }
    }
}
