package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class IHaveEnoughMoneyEvent implements Event<OrderResult> {
    private String bookId;

    public IHaveEnoughMoneyEvent(String bookId) {
        this.bookId=bookId;
    }

    public String getBookId(){
        return bookId;
    }
}
