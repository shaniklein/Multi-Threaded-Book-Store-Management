package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicle implements Event<Void> {
    DeliveryVehicle car;
    public ReleaseVehicle(DeliveryVehicle car) {
        this.car=car;

    }

    public DeliveryVehicle getCar() {
        return car;
    }
}
