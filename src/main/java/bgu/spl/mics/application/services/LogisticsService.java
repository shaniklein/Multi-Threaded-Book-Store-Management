package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.Future;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private MessageBus messageBus = MessageBusImpl.getInstance();
	private CountDownLatch countDownLatch;

	public LogisticsService(int i, CountDownLatch countDownLatch) {
		super("logistic" + i);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateAllBroadcast.class, (TerminateAllBroadcast terminateAllBroadcast) -> terminate());
		countDownLatch.countDown();
		subscribeEvent(DeliveryEvent.class, (DeliveryEvent deliveryEvent) -> {

					Future<Future<DeliveryVehicle>> vehicleFuture = sendEvent(new TryToAquireCar());
					if (vehicleFuture != null) {
						Future<DeliveryVehicle> vehicle = vehicleFuture.get();

						//asking for vehicle from resourceService
						if (vehicle != null) {
							DeliveryVehicle car = vehicle.get();

							if (car != null) {
								car.deliver(deliveryEvent.getAddress(), deliveryEvent.getDistance());
								sendEvent(new ReleaseVehicle(car));

							}

						}
					}
				});


//			else terminate();

	}
}












