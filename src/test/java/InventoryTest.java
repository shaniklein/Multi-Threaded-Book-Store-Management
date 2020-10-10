package java;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory inventory;
    private  BookInventoryInfo[] books;
    private BookInventoryInfo fakeBook;
    @Before
    public void setUp() throws Exception {
        inventory = Inventory.getInstance();
        BookInventoryInfo  RobinHood=new BookInventoryInfo("Robin hood",60,2);
        BookInventoryInfo  HarryPotter=new BookInventoryInfo("Harry Potter",50,1);
        books = new BookInventoryInfo[]{HarryPotter,RobinHood};
        fakeBook=new BookInventoryInfo("book that doesn't exist",0,0);
    }

    @After
    public void tearDown() throws Exception {
        inventory=null;
    }

    @Test
    public void getInstance() {
        assertNotNull(inventory);
    }

    @Test
    public void load() {
        inventory.load(books);
        assertEquals(inventory.checkAvailabiltyAndGetPrice(books[0].getBookTitle()),50);
        assertTrue(books[0].getAmountInInventory()>0);
        assertEquals(inventory.take(books[0].getBookTitle()), OrderResult.SUCCESSFULLY_TAKEN);

    }

    @Test
    public void take() {
        inventory.load(books);
        assertEquals(inventory.take(books[0].getBookTitle()),OrderResult.SUCCESSFULLY_TAKEN);
        //in the second time the book does'nt exists
        assertEquals(inventory.take(books[0].getBookTitle()),OrderResult.NOT_IN_STOCK);
        assertEquals(inventory.take(fakeBook.getBookTitle()),OrderResult.NOT_IN_STOCK);

        assertEquals(inventory.take(books[1].getBookTitle()),OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(books[1].getAmountInInventory(),1);
        assertEquals(inventory.take(books[1].getBookTitle()),OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(books[1].getAmountInInventory(),0);
        assertEquals(inventory.take(books[1].getBookTitle()),OrderResult.NOT_IN_STOCK);

    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        inventory.load(books);
        assertEquals(inventory.checkAvailabiltyAndGetPrice(books[0].getBookTitle()),books[0].getPrice());
        assertEquals(inventory.checkAvailabiltyAndGetPrice(fakeBook.getBookTitle()),-1);

    }

    @Test
    public void printInventoryToFile() {


    }
}