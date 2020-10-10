package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
	private MessageBus messageBus=MessageBusImpl.getInstance();
	private int currentTick;
	private Customer customer;
	private ConcurrentSkipListMap<Integer,LinkedList<String>> schedule;
	private CountDownLatch countDownLatch;

	public APIService(int i, Customer customer, ConcurrentSkipListMap<Integer, LinkedList<String>> schedule, CountDownLatch  countDownLatch) {
		super("customer" + i);
		this.customer = customer;
		this.schedule=schedule;
		this.countDownLatch=countDownLatch;
	}


	@Override
	protected void initialize() {
		messageBus.register(this);
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)-> {
					currentTick = tick.getCurrentTIck();
					if (schedule.containsKey(currentTick)) {
						LinkedList<String> bookToOrder = schedule.get(currentTick);
						for (String book : bookToOrder) {
							Future<OrderReceipt> orderReceiptFuture = sendEvent(new BookOrderEvent(customer, book));
							if (orderReceiptFuture != null) {
									OrderReceipt orderReceipt = orderReceiptFuture.get();
									if (orderReceipt != null)
									customer.addRecipt(orderReceipt);

							}
						}


					}
				}
			);

		subscribeBroadcast(TerminateAllBroadcast.class, (TerminateAllBroadcast terminateAllBroadcast) -> terminate());
		countDownLatch.countDown();
	}


}