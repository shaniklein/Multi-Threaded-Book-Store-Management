package testerObjects;

public class TesterOrder {
	
	private String bookName;
	private int orderTime;
	private boolean isHandled;
	private int orderPrice;

	public TesterOrder(String book, int time, int price) {
		bookName = book;
		orderTime = time;
		orderPrice = price;
		isHandled = false;
	}

	/**
	 * @return the bookName
	 */
	public String getBookName() {
		return bookName;
	}

	/**
	 * @return the orderTime
	 */
	public int getOrderTime() {
		return orderTime;
	}

	/**
	 * @return the orderPrice
	 */
	public int getOrderPrice() {
		return orderPrice;
	}

	/**
	 * @return the isHandled
	 */
	public boolean isHandled() {
		return isHandled;
	}

	/**
	 * @param isHandled the isHandled to set
	 */
	public void setHandled(boolean isHandled) {
		this.isHandled = isHandled;
	}

}
