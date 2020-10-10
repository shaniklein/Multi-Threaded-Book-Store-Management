package testRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.*;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import testerObjects.TesterBookInventoryInfo;
import testerObjects.TesterCustomer;
import testerObjects.TesterMoneyRegister;

public class Tests {

    private HashMap<Integer,Customer> customers;
    private HashMap<String,Integer> inventory;
    private List<OrderReceipt> receipts;
    private MoneyRegister moneyRegister;
    private HashMap<Integer,TesterCustomer> testerCustomers;
    private HashMap<String, TesterBookInventoryInfo> testerInventory;
    private TesterMoneyRegister testerMoneyRegister;
    private StringBuilder testResults;
    private int testInd;

    
	public Tests(int testInd, HashMap<Integer, Customer> customers, HashMap<String, Integer> inventory, List<OrderReceipt> receipts, MoneyRegister moneyRegister, HashMap<Integer, TesterCustomer> testerCustomers, HashMap<String, TesterBookInventoryInfo> testerInventory2, TesterMoneyRegister testerMoneyRegister) {
		this.customers = customers;
		this.inventory = inventory;
		this.receipts = receipts;
		this.moneyRegister = moneyRegister;
		this.testerCustomers = testerCustomers;
		this.testerInventory = testerInventory2;
		this.testerMoneyRegister = testerMoneyRegister;
		this.testResults = new StringBuilder("");
		this.testInd = testInd;
	}

	public StringBuilder runTests() {
		runBasicTests();
		finishUp();
		return testResults;
	}


	private void runBasicTests() {
		checkCustomers();
		checkInventory();
		if (testInd < 5) {
		}
		if (testInd < 9) {
			checkMoneyRegister();
			checkReceipts();
		}
	}

	private void checkCustomers() {
		for (Entry<Integer, TesterCustomer> customerEntry:testerCustomers.entrySet()) {
			int id = customerEntry.getKey();
			TesterCustomer expectedCustomer = customerEntry.getValue();
			if (customers.get(id) == null) {
				testResults.append("####TESTS-ERROR#####\n missing customer: The customer ");
				testResults.append(String.valueOf(id));
				testResults.append(" is missing!\n");
			}else { 
				checkCustomer(id, expectedCustomer);
			}
		}
	}
	
	private void checkCustomer(int id, TesterCustomer expectedCustomer) {
		Customer studentsCustomer = customers.get(id);
		checkAmountInCreditCard(expectedCustomer, studentsCustomer);
	}

	private void checkMoneyRegister() {
		if (testerMoneyRegister.getTotalIncome() != moneyRegister.getTotalEarnings()) {
			testResults.append("####TESTS-ERROR#####\n Total earnings in the money register should be ");
			testResults.append(testerMoneyRegister.getTotalIncome());
			testResults.append("$, but is ");
			testResults.append(moneyRegister.getTotalEarnings());
			testResults.append("$!\n");
		}
		
	}

	private void checkReceipts() {
		checkAmountOfReceipts();
		if (testInd < 5) { // for simple tests we should have the exact receipts 
			findReceiptsAtCustomers();
		}else if (testInd < 8) { // check each customer has at most 5 receipts
			checkReceiptsLimit(5);
		}else if (testInd == 8) { // check each customer has at most 5 receipts
			checkReceiptsLimit(1);
		}
	}

	private void findReceiptsAtCustomers() {
		for (OrderReceipt receipt : receipts) {
			int customer = receipt.getCustomerId();
			checkReceiptSentToCustomer(receipt, customer);
		}
	}

	private void checkReceiptsLimit(int limit) {
		for (Entry<Integer,Customer> customer: customers.entrySet()) {
			int id = customer.getKey();
			int amountOfReceipts = customer.getValue().getCustomerReceiptList().size();
			if (amountOfReceipts > limit) {
				testResults.append("####TESTS-ERROR#####\n Too many receipts for customer: Customer ");
				testResults.append(id);
				testResults.append(" shouldn't have more than ");
				testResults.append(limit);
				testResults.append(" receipts, but has ");
				testResults.append(amountOfReceipts);
				testResults.append(" receipts!\n");
			}
		}
	}
	

	private void checkAmountOfReceipts() {
		int numOftestReceipts = testerMoneyRegister.getReceiptsPerStudent().size();
		if (receipts.size() != numOftestReceipts) {
			testResults.append("####TESTS-ERROR#####\nYour money register should have ");
			testResults.append(String.valueOf(numOftestReceipts));
			testResults.append(" receipts, but has ");
			testResults.append(String.valueOf(receipts.size()));
			testResults.append(" receipts!\n");
		}
	}
	
	private void checkReceiptSentToCustomer(OrderReceipt receipt, int customer) {
		if (!findReceiptInCustomer(receipt, customer)) {
			testResults.append("####TESTS-ERROR#####\nYour money register has a receipt for customer ");
			testResults.append(String.valueOf(customer));
			testResults.append(" for his purchase of ");
			testResults.append(receipt.getBookTitle());
			testResults.append(", but the customer himself is missing a receipt with the same information!\n");
		}
	}

	private boolean findReceiptInCustomer(OrderReceipt receipt, int customer) {
		List<OrderReceipt> customerReceipts = customers.get(customer).getCustomerReceiptList();
		for (OrderReceipt customerReceipt : customerReceipts) { 
			if (foundMatchingReceipts(receipt, customerReceipt)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean foundMatchingReceipts(OrderReceipt receipt, OrderReceipt customerReceipt) {
		return  customerReceipt.getBookTitle().equals(receipt.getBookTitle()) &&
				customerReceipt.getCustomerId() == receipt.getCustomerId() &&
				customerReceipt.getOrderTick() == receipt.getOrderTick() &&
				customerReceipt.getPrice() == receipt.getPrice();
	}
	
	private void checkAmountInCreditCard(TesterCustomer expectedCustomer, Customer studentsCustomer) {
		int expectedAmount = expectedCustomer.getAvailableAmountInCreditCard();
		int actualAmount = studentsCustomer.getAvailableCreditAmount();

		if (testInd < 5) { // for simple tests check for a exact credit card amount
			checkExactCreditCardAmount(studentsCustomer, expectedAmount, actualAmount);
		} 
		checkForNegativeAmount(studentsCustomer, actualAmount);
		
	}

	private void checkForNegativeAmount(Customer studentsCustomer, int actualAmount) {
		if (actualAmount < 0) {
			testResults.append("####TESTS-ERROR#####\n negative amount in credit card: Customer ");
			testResults.append(String.valueOf(studentsCustomer.getId()));
			testResults.append(" has a negative amount in his credit card!\n");
		}
	}

	private void checkExactCreditCardAmount(Customer studentsCustomer, int expectedAmount, int actualAmount) {
		if (expectedAmount != actualAmount) {
			testResults.append("####TESTS-ERROR#####\n wrong amount of credit card: Customer ");
			testResults.append(String.valueOf(studentsCustomer.getId()));
			testResults.append(" should have: ");
			testResults.append(String.valueOf(expectedAmount));
			testResults.append("$ in his credit card, but has: ");
			testResults.append(String.valueOf(actualAmount));
			testResults.append("$.\n");
		}
	}

	private void checkInventory() {
		for (Entry<String, TesterBookInventoryInfo> inventoryEntry: testerInventory.entrySet()) {
			String name = inventoryEntry.getKey();
			int expectedAmount = inventoryEntry.getValue().getAmount();
			if (inventory.get(name) == null) {
				testResults.append("####TESTS-ERROR#####\n missing books in inventory: The book ");
				testResults.append(name);
				testResults.append(" is missing!\n");
			}else {
				checkBookAmount(name, expectedAmount);
			}
		}
	}

	private void checkBookAmount(String name, int expectedAmount) {
		int studentsAmount = inventory.get(name);
		if (testInd < 5) {
			checkExactBookAmount(name, expectedAmount, studentsAmount);
		} else {
			checkForNegativeAmountOfBooks(name, studentsAmount);
		}
	}

	private void checkForNegativeAmountOfBooks(String name, int studentsAmount) {
		if (studentsAmount < 0) {
			testResults.append("####TESTS-ERROR#####\n negative amount of books in inventory: The book ");
			testResults.append(name);
			testResults.append(" has ");
			testResults.append(String.valueOf(studentsAmount));
			testResults.append(" copies!\n");
		}
	}

	private void checkExactBookAmount(String name, int expectedAmount, int studentsAmount) {
		if (expectedAmount != studentsAmount) {
			testResults.append("####TESTS-ERROR#####\n wrong amount of books in inventory: The book ");
			testResults.append(name);
			testResults.append(" should have: ");
			testResults.append(String.valueOf(expectedAmount));
			testResults.append(" copies. But has: ");
			testResults.append(String.valueOf(studentsAmount));
			testResults.append(" copies!\n");
		}
	}
	

	private void finishUp() { 
		if (testResults.toString().isEmpty())
			testResults.append("Nice!! All tests passed! \n");
	}

}
