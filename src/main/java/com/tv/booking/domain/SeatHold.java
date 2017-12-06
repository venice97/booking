package com.tv.booking.domain;

import java.util.ArrayList;
import java.util.List;

public class SeatHold {
	
	private List<ArenaSeat> arenaSeats = new ArrayList<ArenaSeat>();
	String seatHoldId;
	String customerEmail;
	
	public List<ArenaSeat> getArenaSeats() {
		return arenaSeats;
	}
	
	public void setArenaSeats(List<ArenaSeat> arenaSeats) {
		this.arenaSeats = arenaSeats;
	}
	
	public void setSeatHoldId(String seatHoldId) {
		this.seatHoldId = seatHoldId;
	}
	
	public void addSeat(ArenaSeat seat) {
		arenaSeats.add(seat);
	}
	
	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getSeatHoldId() {
		return seatHoldId;
	}
}
