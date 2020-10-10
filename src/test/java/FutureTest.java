package java;

import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future<String> future;
    @Before


    public void setUp() throws Exception {
    future=new Future<>();
    }

    @After
    public void tearDown() throws Exception {
    future =null;
    }

    @Test
    public void get() {
        future.resolve("Success");
        assertEquals("Success",future.get());
    }


    @Test
    //Same as get- if we result, we will get something in return
    public void resolve() {
        future.resolve("Success");
        assertEquals("Success",future.get());
        assertNotNull(future.get(2, TimeUnit.MILLISECONDS));
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        future.resolve("Success");
        assertTrue(future.isDone());
    }

    @Test
    public void get1() {
        assertNull(future.get(2,TimeUnit.MILLISECONDS));
        future.resolve("Success");
        assertNotNull(future.get(2, TimeUnit.MILLISECONDS));
    }
}