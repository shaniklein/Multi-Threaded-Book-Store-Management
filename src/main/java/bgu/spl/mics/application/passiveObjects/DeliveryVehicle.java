package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class DeliveryVehicle implements Serializable {
	int license,speed;
	/**
     * Constructor.   
     */
	 public DeliveryVehicle(int license, int speed) {
		this.license=license;
		this.speed=speed;
	  }
	/**
     * Retrieves the license of this delivery vehicle.   
     */
	public int getLicense() {
	return license;}

	
	/**
     * Retrieves the speed of this vehicle person.   
     * <p>
     * @return Number of ticks needed for 1 Km.
     */
	public int getSpeed() {
	return speed;
	}
	
	/**
     * Simulates a delivery by sleeping for the amount of time that 
     * it takes this vehicle to cover {@code distance} KMs.  
     * <p>
     * @param address	The address of the customer.
     * @param distance	The distance from the store to the customer.
     */
	public void deliver(String address, int distance) {
		//in instruction you wrote that speed is number of milliseconds needed for 1KM so it is product, and not like physics which is time=distance/speed

		int time=distance*speed;
			try {

				Thread.sleep(time);
			}
			catch (InterruptedException e){

			}
		}
}
