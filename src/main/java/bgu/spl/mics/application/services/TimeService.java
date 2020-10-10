package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
	private static TimeService me = null;
	private MessageBusImpl messageBus = MessageBusImpl.getInstance();
	private int speed, duration;
	private int currentTIck;
	private Timer timer;


	public TimeService(int speed, int duration) {
		super("time");
		this.duration = duration;
		this.speed = speed;
		currentTIck = 1;
		timer = new Timer();

	}

	@Override
	protected void initialize() {
		timer.schedule(new TimerTask() {

			public void run() {
				if (duration >= 0) {
					sendBroadcast(new TickBroadcast(currentTIck));
					currentTIck++;
					duration--;
				}
				else {
					sendBroadcast(new TerminateAllBroadcast());
					timer.cancel();
				}
			}
		}, new Date(), speed);
		subscribeBroadcast(TerminateAllBroadcast.class, (TerminateAllBroadcast terminateAllBroadcast) -> terminate());
	}
}

