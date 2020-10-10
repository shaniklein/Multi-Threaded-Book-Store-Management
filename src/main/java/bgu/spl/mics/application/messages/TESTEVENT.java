package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TESTEVENT implements Event<Integer> {
int price;

public  TESTEVENT(int price){
    this.price=price;
}

    public int getPrice() {
        return price;
    }
}
