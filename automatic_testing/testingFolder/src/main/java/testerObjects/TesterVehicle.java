package testerObjects;

public class TesterVehicle {
	
	private int license;
	private int speed;
	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	private int distanceToCustomer;
	private boolean isBusy;
	private long timeStartedDriving;
	private TesterOrder order;
	
	public TesterVehicle(int license, int speed) {
		this.license = license;
		this.speed = speed;
		this.distanceToCustomer = 0;
		this.isBusy = false;
		this.timeStartedDriving = -1;
	}

	/**
	 * @return the license
	 */
	public int getLicense() {
		return license;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @return the isBusy
	 */
	public boolean isBusy() {
		if (!isBusy) {
			return false;
		} else {
			long currTime = System.currentTimeMillis();
			// check if we already drove the distance and back to the previous customer
			if (distanceToCustomer * 2 <= (int) Math.floor((currTime-timeStartedDriving)/speed)) {
				isBusy = false;
				return false;
			}
			return true;
		}
	}

	/**
	 * @param isBusy the isBusy to set
	 */
	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}
	
	/**
	 * 
	 * @return true if was able to start driving (if wasn't in the middle of a task)
	 */
	public boolean startDriving(int distance) {
		if (isBusy()) 
			return false;
		isBusy = true;
		distanceToCustomer = distance;
		timeStartedDriving = System.currentTimeMillis();
		return true;
	}

	/**
	 * @return the order
	 */
	public TesterOrder getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(TesterOrder order) {
		this.order = order;
	}

}
