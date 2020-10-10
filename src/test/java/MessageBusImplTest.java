package java;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.example.ServiceCreator;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    MessageBusImpl messageBus;
    MicroService mBroad;
    MicroService mEvent;
    Class<? extends Event<String>> event;
    Class<? extends Broadcast> broadcast;


    @Before
    public void setUp() throws Exception {
        messageBus = MessageBusImpl.getInstance();
        messageBus.register(mEvent);
        messageBus.register(mBroad);
        broadcast=ExampleBroadcast.class;
        event=ExampleEvent.class;
    }
    @After
    public void tearDown() throws Exception {
        messageBus.unregister(mEvent);
        messageBus.unregister(mBroad);
        messageBus=null;
    }

    @Test
    public void getInstance() {

        assertNotNull(messageBus);
    }

    @Test
    public void subscribeEvent() {
        try {

            messageBus.subscribeEvent(event ,mEvent);
            assertEquals(event, messageBus.awaitMessage(mBroad));
        }
        catch (InterruptedException e){
            fail();
        }
    }

    @Test
    public void subscribeBroadcast() {
        try {

            messageBus.subscribeBroadcast(broadcast, mBroad);
            assertEquals(broadcast, messageBus.awaitMessage(mBroad));
        }
        catch (InterruptedException e){
            fail();
        }

    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
    }

    @Test
    public void register() {

    }

    @Test
    public void unregister() {
    }

    @Test
    public void awaitMessage() {
    }

}