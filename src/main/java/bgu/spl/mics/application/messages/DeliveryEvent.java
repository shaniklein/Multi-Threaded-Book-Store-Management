package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import org.omg.CORBA.INTERNAL;

public class DeliveryEvent implements Event<Integer> {
    private  int distance;
    private String address;

    public DeliveryEvent(int distance,String address){
        this.distance=distance;
        this.address=address;
    }

    public int getDistance() {
        return distance;

    }
    public String getAddress() {
        return address;

    }
}

