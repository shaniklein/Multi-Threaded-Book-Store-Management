package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.Future;
import javafx.util.Pair;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
@SuppressWarnings("unchecked")

public class InventoryService extends MicroService{
//	private MessageBus messageBus=MessageBusImpl.getInstance();
	private Inventory inventory=Inventory.getInstance();


	private CountDownLatch countDownLatch;

	public InventoryService(int i, CountDownLatch countDownLatch ) {
		super(" inventory"+i);
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize() {
		countDownLatch.countDown();
		this.subscribeEvent(TryToBuyBookEvent.class, (TryToBuyBookEvent tryToBuyBookEvent) -> {
					Pair<OrderResult,Integer> orderResult = new Pair(OrderResult.NOT_IN_STOCK,-1);
					synchronized (inventory) {
						int price = inventory.checkAvailabiltyAndGetPrice(tryToBuyBookEvent.getBookId());
						if (price != -1&&(tryToBuyBookEvent.getCustmerMoney().getAvailableCreditAmount())>=price) {
							orderResult=new Pair((inventory.take(tryToBuyBookEvent.getBookId())),price);
								}
							}
			complete(tryToBuyBookEvent, orderResult);
		});

//		this.subscribeEvent(IHaveEnoughMoneyEvent.class,(IHaveEnoughMoneyEvent iHaveEnoughMoney)->{
//			OrderResult orderResult=inventory.take(iHaveEnoughMoney.getBookId());
//			complete(iHaveEnoughMoney, orderResult);
//
//		});
		subscribeBroadcast(TerminateAllBroadcast.class, (TerminateAllBroadcast terminateAllBroadcast) -> terminate());


	}
}

