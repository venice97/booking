package com.tv.booking.domain;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

public class ArenaSeat {

	LocalDateTime bookingTime;
	
	String seatHoldId;
	String customerEmail;
	String reservedId;
	
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
	
	public LocalDateTime getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(LocalDateTime bookingTime) {
		this.bookingTime = bookingTime;
	}
	
	public String getReservedId() {
		return reservedId;
	}

	public void setReservedId(String reservedId) {
		this.reservedId = reservedId;
	}

	public boolean isAvailable() {
		if (!StringUtils.isEmpty(reservedId) || !StringUtils.isEmpty(customerEmail) || !StringUtils.isEmpty(seatHoldId))  
			return false;
		else	
			return true;
	}
	
	public void clearBooking() {
		this.customerEmail = null;
		this.seatHoldId = null;
		this.reservedId = null;
	}
	
	public boolean isExpired(int ticketExpirationTime) {
		LocalDateTime now = LocalDateTime.now();
		if (bookingTime == null)
			return false;
		
		Duration between = Duration.between(bookingTime, now);	
		return (between.getSeconds() > ticketExpirationTime) ? true : false;
	}
	
}
