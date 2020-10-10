package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.*;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String bookId;

   public BookOrderEvent(Customer customer, String bookId) {
        this.bookId = bookId;
        this.customer = customer;
   }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookId() {
        return bookId;
    }

 }


