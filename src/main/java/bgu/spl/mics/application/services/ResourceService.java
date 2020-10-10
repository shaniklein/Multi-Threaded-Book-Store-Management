package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder=ResourcesHolder.getInstance();
	private MessageBus messageBus= MessageBusImpl.getInstance();
	private CountDownLatch countDownLatch;
	public ResourceService(int i, CountDownLatch countDownLatch ) {
		super("resource" + i);
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize() {
		countDownLatch.countDown();
		subscribeEvent(ReleaseVehicle.class,(ReleaseVehicle releaseVehicle)->{
			resourcesHolder.releaseVehicle(releaseVehicle.getCar());

				});

		subscribeEvent(TryToAquireCar.class,(TryToAquireCar tryToAquireCar)->{
			Future<DeliveryVehicle> futureCar=resourcesHolder.acquireVehicle();
			complete(tryToAquireCar,futureCar);

		});
		subscribeBroadcast(TerminateAllBroadcast.class,(TerminateAllBroadcast terminateAllBroadcast)-> {
			resourcesHolder.releaseVehicle(null);
			terminate();
		});




	}

}
