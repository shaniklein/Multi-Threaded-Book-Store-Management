package testRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import testerObjects.TestTimer;
import testerObjects.TesterBookInventoryInfo;
import testerObjects.TesterCustomer;
import testerObjects.TesterMoneyRegister;
import testerObjects.TesterOrder;
import testerObjects.TesterReceipt;
import testerObjects.TesterVehicle;

public class Tester {
	
    private static HashMap<Integer,Customer> customers;
    private static HashMap<String,Integer> inventory;
    private static List<OrderReceipt> receipts;
    private static MoneyRegister moneyRegister;
    private static HashMap<Integer,TesterCustomer> testerCustomers;
    private static HashMap<String,TesterBookInventoryInfo> testerInventory;
    private static TesterMoneyRegister testerMoneyRegister;
    private static List<TesterVehicle> testerVehicles;
    private static TestTimer timer;
    private static int currentTick;
    private static String testResults;
    private static int testInd;


	public static void main(String [] args) throws IOException {
		testResults="";
		testInd = Integer.parseInt(args[5]);
		ExtractProgramOutput(args); //extract the student's serializable program output
		prepareTesterObjects(args[0]);
		runTesterProgram();
		if (testResults.isEmpty())//if we didn't get exceptions regarding the student's serializable output file. 
			testResults = runTests().toString();//send the test number.
		outputTestResults(testResults);
	    System.out.println("done.");
	}


	
	private static void runTesterProgram() {
		System.out.println("Running tester program...");
		currentTick = 0;
		while (currentTick < timer.getDuration()+1) {
			findAndOrder();
			//System.out.println("Tester handling orders in tick "+currentTick+".");
			currentTick++;
		}
		System.out.println("Tester shutting down program.");
		
	}


	private static void findAndOrder() {
		for (TesterCustomer customer : testerCustomers.values()) {
			List<TesterOrder> orders = customer.getOrders().get(currentTick);
			if (orders != null) {
				for (TesterOrder order : orders) {
					if (order!= null) {
						if (checkIfcustomerCanBuy(customer, order)) {
							orderBook(customer, order, currentTick);
						}
					}
				}
			}
		}
	}


	private static boolean checkIfcustomerCanBuy(TesterCustomer customer, TesterOrder order) {
		boolean custHasCash = customer.getAvailableAmountInCreditCard() >= order.getOrderPrice();
		boolean invHasGoods = testerInventory.get(order.getBookName()).getAmount() > 0;
		return custHasCash && invHasGoods;
	}


	private static void orderBook(TesterCustomer customer, TesterOrder order, int tick) {
		updateObjectsWithOrder(customer, order, tick);
	}


	private static void updateObjectsWithOrder(TesterCustomer customer, TesterOrder order, int tick) {
		order.setHandled(true);
		testerMoneyRegister.setTotalIncome(testerMoneyRegister.getTotalIncome()+order.getOrderPrice());
		addReceipt(customer, order, tick);
		String bookName = order.getBookName();
		TesterBookInventoryInfo bookInfo = testerInventory.get(bookName);
		bookInfo.setAmount(bookInfo.getAmount()-1);
		updateCustomersAccount(customer, order);
	}


	private static void addReceipt(TesterCustomer customer, TesterOrder order, int tick) {
		TesterReceipt receipt = new TesterReceipt(customer.getId(), order.getBookName(), order.getOrderPrice(), tick);
		testerMoneyRegister.addReceiptsPerStudent(customer.getId(), receipt);
		customer.addReceipt(receipt);
		
	}


	private static void updateCustomersAccount(TesterCustomer customer, TesterOrder order) {
		int oldAmount = customer.getAvailableAmountInCreditCard();
		int price = order.getOrderPrice();
		customer.setAvailableAmountInCreditCard(oldAmount-price);
		
	}


	private static StringBuilder runTests() {
		Tests generalTests = new Tests(testInd, customers, inventory, receipts, moneyRegister, testerCustomers, testerInventory, testerMoneyRegister);
		StringBuilder testResults = generalTests.runTests();
		return testResults;
	}

	private static void outputTestResults(String testResults) throws IOException {
		Files.write(Paths.get("test_result.txt"), testResults.getBytes());
	}
	
	
	private static void ExtractProgramOutput(String[] srializedObjects) {
		  try {
			 getCustomers(srializedObjects[1]);
			 getInventory(srializedObjects[2]);
			 getReceipts(srializedObjects[3]);
			 getMoneyRegister(srializedObjects[4]);
		  } catch (Exception e) {
			  testResults="################  Could not open the serialized output file for test "+testInd+"!!";
		     e.printStackTrace();
		  }
	}

	private static void getMoneyRegister(String moneyRegisterObj)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fileIn = new FileInputStream(moneyRegisterObj);
		 ObjectInputStream in = new ObjectInputStream(fileIn);
		 moneyRegister = (MoneyRegister) in.readObject();
		 in.close();
		 fileIn.close();
	}

	private static void getReceipts(String receiptsObj)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fileIn = new FileInputStream(receiptsObj);
		 ObjectInputStream in = new ObjectInputStream(fileIn);
		 receipts = (List<OrderReceipt>) in.readObject();
		 in.close();
		 fileIn.close();
	}

	private static void getInventory(String inventoryObj)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fileIn = new FileInputStream(inventoryObj);
		 ObjectInputStream in = new ObjectInputStream(fileIn);
		 inventory = (HashMap<String,Integer>) in.readObject();
		 in.close();
		 fileIn.close();
	}
	

	private static void prepareTesterObjects(String jsonFilePath) {
        Gson gson = new Gson();
        JsonObject json;
        try {
            JsonReader reader = new JsonReader(new FileReader(jsonFilePath));
            json = gson.fromJson(reader, JsonObject.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("json file not found");
            return;
        }
		createTesterObjects(json);
	}


	private static void createTesterObjects(JsonObject json) {
		prepareTimer(json);
		testerMoneyRegister = new TesterMoneyRegister();
		prepareTestInventory(json);
		prepareTestCustomers(json);
	}


	private static void prepareTimer(JsonObject json) {
		json = json.getAsJsonObject().get("services").getAsJsonObject();
        int speed = json.get("time").getAsJsonObject().get("speed").getAsInt();
        int duration = json.get("time").getAsJsonObject().get("duration").getAsInt();
        timer = new TestTimer(speed, duration);
		//in order for the tester to go faster, changing the speed to 10 milliseconds.
        timer.setSpeed(10);
	}



	private static void prepareTestInventory(JsonObject json) {
		testerInventory = new HashMap<String,TesterBookInventoryInfo>();
        JsonArray invArray = json.get("initialInventory").getAsJsonArray();
        for (JsonElement j : invArray) {
        		TesterBookInventoryInfo bookInfo = new TesterBookInventoryInfo(j.getAsJsonObject().get("bookTitle").getAsString(),
                        j.getAsJsonObject().get("price").getAsInt(),
                        j.getAsJsonObject().get("amount").getAsInt());
        		testerInventory.put(bookInfo.getName(), bookInfo);

        }	        
	}

	private static void prepareTestCustomers(JsonObject json) {
		testerCustomers = new HashMap<Integer,TesterCustomer>();
        JsonArray customers = json.getAsJsonObject().get("services").getAsJsonObject().get("customers").getAsJsonArray();
        for (JsonElement j: customers) {
        		TesterCustomer customer = parseCustomer(j.getAsJsonObject());
            testerCustomers.put(customer.getId(),customer);
        }
		
	}

	private static TesterCustomer parseCustomer(JsonObject json) 
    {
		int id = json.get("id").getAsInt();
        //String name = json.get("name").getAsString();
        String address = json.get("address").getAsString();
        int distance = json.get("distance").getAsInt();
        int creditCardNum = json.get("creditCard").getAsJsonObject().get("number").getAsInt();
        int balance = json.get("creditCard").getAsJsonObject().get("amount").getAsInt();
        JsonArray ordersArray = json.get("orderSchedule").getAsJsonArray();
        HashMap<Integer, List<TesterOrder>> orders = addOrdersToCustomer(ordersArray);
        TesterCustomer customer = new TesterCustomer(id, address, distance, creditCardNum, balance, orders);
        return customer;
	}


	private static HashMap<Integer, List<TesterOrder>> addOrdersToCustomer(JsonArray ordersArray) {
		HashMap<Integer,List<TesterOrder>> orders = new HashMap<Integer,List<TesterOrder>>();
        for (JsonElement j: ordersArray) {
            String title = j.getAsJsonObject().get("bookTitle").getAsString();
            int tick = j.getAsJsonObject().get("tick").getAsInt();
            int price = testerInventory.get(title).getPrice();
            TesterOrder order = new TesterOrder(title, tick, price);
            if (!orders.containsKey(tick)) {
                orders.put(tick,new LinkedList<TesterOrder>());
            }
            orders.get(tick).add(order);
        }
		return orders;
	}


	private static void getCustomers(String customersObj)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fileIn = new FileInputStream(customersObj);
		 ObjectInputStream in = new ObjectInputStream(fileIn);
		 customers = (HashMap<Integer,Customer>) in.readObject();
		 in.close();
		 fileIn.close();
	}

}
