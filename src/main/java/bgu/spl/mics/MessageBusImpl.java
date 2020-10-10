package bgu.spl.mics;


import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
@SuppressWarnings("unchecked")


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class  MessageBusImpl implements MessageBus {
	private static class MessageBusHolder {
		private static MessageBusImpl messageBus = new MessageBusImpl();
	}

	private ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> eventMicroServiceQsMap;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceMessageQsMap;
	private ConcurrentHashMap<Event, Future> eventFutureMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedList<MicroService>> broadcastMicroServiceListMap;

	private MessageBusImpl() {
		this.eventMicroServiceQsMap = new ConcurrentHashMap<>();
		this.microServiceMessageQsMap = new ConcurrentHashMap<>();
		this.eventFutureMap = new ConcurrentHashMap<>();
		this.broadcastMicroServiceListMap = new ConcurrentHashMap<>();
	}


	public static MessageBusImpl getInstance() {
		return MessageBusHolder.messageBus;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (type) {
			if (!eventMicroServiceQsMap.containsKey(type))
				eventMicroServiceQsMap.put(type, new LinkedBlockingQueue<>());
			eventMicroServiceQsMap.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (type) {
			if (!broadcastMicroServiceListMap.containsKey(type))
				broadcastMicroServiceListMap.put(type, new LinkedList<>());
			broadcastMicroServiceListMap.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized (eventFutureMap) {
			eventFutureMap.get(e).resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (broadcastMicroServiceListMap) {

			LinkedList<MicroService> services = broadcastMicroServiceListMap.get(b.getClass());
			int size = services.size();
			for (int i = 0; i < size; i++)
				microServiceMessageQsMap.get(services.get(i)).add(b);
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (broadcastMicroServiceListMap) {
			try {
				Future<T> future = new Future<>();
				if (eventMicroServiceQsMap.get(e.getClass()).isEmpty()) {
					future.resolve(null);
					return future;
				}
				eventFutureMap.put(e, future);
				MicroService service = eventMicroServiceQsMap.get(e.getClass()).take();
				microServiceMessageQsMap.get(service).add(e);
				eventMicroServiceQsMap.get(e.getClass()).add(service);
				return future;
			} catch (InterruptedException e1) {
			}

			return null;
		}
	}

	@Override
	public void register(MicroService m) {
//		synchronized(microServiceMessageQsMap) {
		if (!microServiceMessageQsMap.containsKey(m))
			microServiceMessageQsMap.put(m, new LinkedBlockingQueue<>());
//		}
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (broadcastMicroServiceListMap) {
			microServiceMessageQsMap.get(m).forEach((message -> {
				if (!eventFutureMap.get(message).isDone())
					eventFutureMap.get(message).resolve(null);
			}));
			microServiceMessageQsMap.remove(m);
			broadcastMicroServiceListMap.forEach((key, list) -> list.remove(m));
			eventMicroServiceQsMap.forEach((key, queue) -> queue.remove(m));
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message message = microServiceMessageQsMap.get(m).take();
		return message;
	}
}
