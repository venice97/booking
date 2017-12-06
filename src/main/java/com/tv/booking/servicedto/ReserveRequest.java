package com.tv.booking.servicedto;

public class ReserveRequest {
	
	String customerEmail;
	String seatHoldId;
	
	public String getCustomerEmail() {
		return customerEmail;
	}
	
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	
	public String getSeatHoldId() {
		return seatHoldId;
	}
	
	public void setSeatHoldId(String seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

}
