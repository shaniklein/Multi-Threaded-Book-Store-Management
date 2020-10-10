package testerObjects;

public class TestTimer {
	
	private int speed;
	private int duration;
	private int currentTick;
	private long startingTime;

	public TestTimer(int speed, int duration) {
		this.speed = speed;
		this.duration = duration;
		this.currentTick = -1;
		this.startingTime = -1;
	}
	
	public int startTimer() {
		currentTick = 0;
		startingTime = System.currentTimeMillis();
		return currentTick;
	}


	/**
	 * @return the currentTick
	 * updates the time to the current tick given the starting time
	 */
	public int getCurrentTick() { 
		long currTime = System.currentTimeMillis();
		currentTick = (int) Math.floor((currTime-startingTime)/speed);
		return currentTick;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @param currentTick the currentTick to set
	 */
	public void setCurrentTick(int currentTick) {
		this.currentTick = currentTick;
	}
	

}
