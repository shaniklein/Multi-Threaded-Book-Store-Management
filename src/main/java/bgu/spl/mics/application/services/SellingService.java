package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import javafx.util.Pair;


import java.util.concurrent.CountDownLatch;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
	private MoneyRegister moneyRegister = MoneyRegister.getInstance();
	private MessageBus messageBus = MessageBusImpl.getInstance();
	private static AtomicInteger idRecipt;
	private int issuedTick;
	private int orderTick;
	private int proccessTick;
	private  int currentTick;
	private  CountDownLatch countDownLatch;
	public SellingService(int i, CountDownLatch countDownLatch) {
		super("selling"+i);
		idRecipt = new AtomicInteger(0);
		this.countDownLatch=countDownLatch;

	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)->{
			currentTick=tick.getCurrentTIck();

		});
		countDownLatch.countDown();

		this.subscribeEvent(BookOrderEvent.class, (BookOrderEvent bookOrderEvent) -> {
			proccessTick=currentTick;
			Customer customer = bookOrderEvent.getCustomer();
			String bookId = bookOrderEvent.getBookId();

			//if the customer has enough money and the book is availabe we will get future with the price of the book, otherwise we will get null
			Future<Pair<OrderResult,Integer>> future = sendEvent(new TryToBuyBookEvent(bookId,customer));
			orderTick=currentTick;
			if (future != null) {
				Pair<OrderResult,Integer> orderResultIntegerPair=future.get();
				if(orderResultIntegerPair!=null) {
					OrderResult orderResult = orderResultIntegerPair.getKey();
					int price = future.get().getValue();
					if (orderResult.equals(OrderResult.SUCCESSFULLY_TAKEN) && price != -1 && (customer.getAvailableCreditAmount() >= price)) {
						//if the book is available and there is enough money
						moneyRegister.chargeCreditCard(customer, price);//take the money from the customer
						issuedTick = currentTick;
						OrderReceipt orderReceipt = new OrderReceipt(idRecipt.getAndIncrement(), customer.getName(), customer.getId(), bookOrderEvent.getBookId(), price, issuedTick, orderTick, proccessTick);
						moneyRegister.file(orderReceipt);
						sendEvent(new DeliveryEvent(customer.getDistance(), customer.getAddress()));
						complete(bookOrderEvent, orderReceipt);
						return;
					}
				}
			}
			complete(bookOrderEvent, null);
				}
			);


		subscribeBroadcast(TerminateAllBroadcast.class,(TerminateAllBroadcast terminateAllBroadcast)->terminate());

	}
}



