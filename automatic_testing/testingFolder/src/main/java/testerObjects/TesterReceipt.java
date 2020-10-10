package testerObjects;

public class TesterReceipt {

	private int buyerId;
	private String bookName;
	private int pricePayed;
	private int orderTick;
	
	public TesterReceipt(int id, String book, int price, int sendingTime) {
		buyerId = id;
		bookName = book;
		pricePayed = price;
		orderTick = sendingTime;
	}

	/**
	 * @return the buyerId
	 */
	public int getBuyerId() {
		return buyerId;
	}

	/**
	 * @return the bookName
	 */
	public String getBookName() {
		return bookName;
	}

	/**
	 * @param buyerId the buyerId to set
	 */
	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
	}

	/**
	 * @param bookName the bookName to set
	 */
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	/**
	 * @return the pricePayed
	 */
	public int getPricePayed() {
		return pricePayed;
	}

	/**
	 * @param pricePayed the pricePayed to set
	 */
	public void setPricePayed(int pricePayed) {
		this.pricePayed = pricePayed;
	}

	/**
	 * @return the timeBookWasSent
	 */
	public int getTimeBookWasSent() {
		return orderTick;
	}

	/**
	 * @param timeBookWasSent the timeBookWasSent to set
	 */
	public void setTimeBookWasSent(int orderTick) {
		this.orderTick = orderTick;
	}

}
