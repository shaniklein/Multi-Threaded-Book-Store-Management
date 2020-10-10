package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import javafx.util.Pair;
//The Integer represent the price of the book
public class TryToBuyBookEvent implements Event<Pair<OrderResult,Integer>> {
    private String bookId;
    private Customer customer;

    public TryToBuyBookEvent(String bookId, Customer customer ){
        this.bookId = bookId;
        this.customer=customer;
    }

    public String getBookId() {
        return bookId;
    }

    public Customer getCustmerMoney(){
        return customer;
    }

}
