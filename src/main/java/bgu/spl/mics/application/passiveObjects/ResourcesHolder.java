package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
@SuppressWarnings("unchecked")

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder implements Serializable {

	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	private LinkedList<DeliveryVehicle> vehicles;
	private ConcurrentLinkedQueue<Future> futureVehicles;
//	private LinkedList<Future> futureVehicles;




	/* Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}

	private ResourcesHolder(){
		vehicles=new LinkedList<>();
		futureVehicles=new ConcurrentLinkedQueue<>();
	}



	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> deliveryVehicleFuture = new Future<>();
		synchronized (vehicles) {
			if (!vehicles.isEmpty()) {
				DeliveryVehicle deliveryV = vehicles.poll();
				deliveryVehicleFuture.resolve(deliveryV);
			} else {
//				deliveryVehicleFuture.resolve(null);
				futureVehicles.add(deliveryVehicleFuture);
			}
		}
		return deliveryVehicleFuture;
	}

	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (futureVehicles) {

		if (vehicle == null) {
			for (Future future : futureVehicles
			)
			{
				future.resolve(null);

			}

		}

		else {
//			Future<DeliveryVehicle> futureDelivery = futureVehicles.poll();

			if (futureVehicles.peek()!=null) {
				futureVehicles.poll().resolve(vehicle);
			}
			else {
					vehicles.add(vehicle);
				}
			}

		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		Collections.addAll(this.vehicles,vehicles);

	}

}
