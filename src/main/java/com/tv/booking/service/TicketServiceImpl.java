package com.tv.booking.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.tv.booking.domain.ArenaSeat;
import com.tv.booking.domain.SeatHold;
import com.tv.booking.exception.AlreadyReservedException;
import com.tv.booking.exception.ExceedSeatAvailabilityException;
import com.tv.booking.exception.HoldExpiredException;
import com.tv.booking.exception.InvalidSeatHoldException;

@Service
public class TicketServiceImpl implements TicketService {
	
	public static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
	public static final String HOLD_PREFIX = "HD";
	public static final String RESERVE_PREFIX = "RD";
	
	@Value("${arena.max.rows}")
	private int arenaMaxRows;
	
	@Value("${arena.max.seats}")
	private int arenaMaxSeats;
	
	@Value("${ticket.expiration.time}")
	private int ticketExpirationTime;
	
	private ArenaSeat seats[][] = null;
	private Integer numSeatsAvailable = -1;
	
	public int getArenaMaxRows() {
		return arenaMaxRows;
	}

	public void setArenaMaxRows(int arenaMaxRows) {
		this.arenaMaxRows = arenaMaxRows;
	}

	public int getArenaMaxSeats() {
		return arenaMaxSeats;
	}

	public void setArenaMaxSeats(int arenaMaxSeats) {
		this.arenaMaxSeats = arenaMaxSeats;
	}

	public int getTicketExpirationTime() {
		return ticketExpirationTime;
	}

	public void setTicketExpirationTime(int ticketExpirationTime) {
		this.ticketExpirationTime = ticketExpirationTime;
	}

	public int numSeatsAvailable() {
		init();
		checkExpireSeats();
		return numSeatsAvailable;
	}
	
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
	
		if (numSeats == 0)
			throw new IllegalArgumentException ("numSeats is required");
		
		if (StringUtils.isEmpty(customerEmail)) {
			throw new IllegalArgumentException("customerEmail is required");
		}
		
		init();
		checkExpireSeats();
		
		if (numSeats > numSeatsAvailable) {
			throw new ExceedSeatAvailabilityException("Not enough seats left");
		}
		
		SeatHold seatHold = new SeatHold();
		String seatHoldId =  "HD-" + UUID.randomUUID().toString();
		int numSeatsAssignedPerRow = 0;
		int numSeatsLeft = numSeats;
				
		for (int i=0; i < arenaMaxRows; i++) {
			numSeatsAssignedPerRow = assignGoodSeats(i, numSeats, customerEmail, seatHoldId);
			numSeatsLeft = numSeatsLeft - numSeatsAssignedPerRow;
			
			if (numSeatsLeft == 0) {
				seatHold.setSeatHoldId(seatHoldId);
				seatHold.setCustomerEmail(customerEmail);
				return seatHold;
			}
					
			for (int j=0; j < arenaMaxSeats; j++) { 		
				if (numSeatsLeft > 0) {
					
					if (seats[i][j] == null) {
						seats[i][j] = new ArenaSeat();
					}
					
					ArenaSeat seat = seats[i][j];
					
					if (seat.isAvailable()) {
						seat.setSeatHoldId(seatHoldId);
						seat.setCustomerEmail(customerEmail);
						seat.setBookingTime(LocalDateTime.now());	
						numSeatsLeft--;
						this.numSeatsAvailable--;
						seatHold.addSeat(seat);
					
						if (numSeatsLeft == 0) {
							seatHold.setSeatHoldId(seatHoldId);
							seatHold.setCustomerEmail(customerEmail);
							return seatHold;
						}
					}
				}
			}
		}
			
		return seatHold;
	}
	
	public String reserveSeats(String seatHoldId, String customerEmail) {
		init();
		
		if (StringUtils.isEmpty(seatHoldId)) {
			throw new IllegalArgumentException("seatHoldId is required");
		}
		
		if (!seatHoldId.contains(HOLD_PREFIX)) {
			throw new IllegalArgumentException("invalid seat hold id");
		}
		
		if (StringUtils.isEmpty(customerEmail)) {
			throw new IllegalArgumentException("customerEmail is required");
		}
		
		String reservedId = RESERVE_PREFIX + UUID.randomUUID().toString();
		int numSeatsReserved = 0;
		
		for (int i=0; i < arenaMaxRows; i++) {
			for (int j=0; j < arenaMaxSeats; j++) { 
				ArenaSeat seat = seats[i][j];
				 
				if (seat == null)
					continue;
				
				if (seat.getSeatHoldId() != null && seat.getSeatHoldId().equalsIgnoreCase(seatHoldId) && 
				    seat.getCustomerEmail() != null && seat.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
					
						if ( seat.isExpired(ticketExpirationTime)) {
							this.expireSeats(seatHoldId);
							throw new HoldExpiredException("Hold is expired");
						}
						
						if (StringUtils.isEmpty(seat.getReservedId())) {
							seat.setReservedId(reservedId);
							numSeatsReserved++;
						} else {
							throw new AlreadyReservedException("Seat hold id has already been reserved");
						}
						
				}
			}
		}
			
		if (numSeatsReserved == 0) {
			throw new InvalidSeatHoldException("Hold is invalid, email and hold id combination does not exist");
		}
		
		return reservedId;
	}
	
	private void init() {	
		if (this.seats == null ) {
			this.seats = new ArenaSeat[arenaMaxRows][arenaMaxSeats];
		}
		
		if (this.numSeatsAvailable == -1) {
			this.numSeatsAvailable = arenaMaxRows * arenaMaxSeats;
		}
	}
	
	/*
	 * @return number of seats that has been assigned
	 */
	private int assignGoodSeats(int row, int numSeats, String customerEmail, String seatHoldId) {
		int[] sections = determineSeatBlocks();
		int numSeatsAssigned = 0;
		
		int centerBeginIndex = sections[0] + sections[1] - 1;
		int centerEndIndex = centerBeginIndex + sections[2];
		
		for (int i = centerBeginIndex; i < centerEndIndex; i++) {
			
			if (seats[row][i] == null) {
				seats[row][i] = new ArenaSeat();
			}
			
			ArenaSeat seat = seats[row][i];
			if (seat.isExpired(ticketExpirationTime)) {
				seat.clearBooking();
				increaseSeatsAvailable();
			}
			
			if (seat.isAvailable()) {
				seat.setSeatHoldId(seatHoldId);
				seat.setCustomerEmail(customerEmail);
				numSeatsAssigned++;
				numSeatsAvailable--;
				seat.setBookingTime(LocalDateTime.now());
				
				if (numSeatsAssigned == numSeats) {
					break;
				}
			}
		}
		
		return numSeatsAssigned;
	}
	
	private int[] determineSeatBlocks() {
		/*
		 *  Assume 20% of seats in center of each row are good seats,
		 *  so assign those first
		 *  
		 *  First calculate the quotient then distribute equally the 
		 *  remainder to each section
		 */
		final int numberOfSections = 5;    

		final int quotient = arenaMaxSeats / numberOfSections;
		final int remainder = arenaMaxSeats % numberOfSections;

		int[] sections = new int[numberOfSections];
		for( int i = 0; i < numberOfSections; i++ ) {
		    sections[i] = i < remainder ? quotient + 1 : quotient;
		}
		
		return sections;
	}
	
	private void expireSeats(String seatHoldId) {
		for (int i=0; i < arenaMaxRows; i++) {
			for (int j=0; j < arenaMaxSeats; j++) {
				ArenaSeat seat = seats[i][j];
				if (seat == null)
					continue;
				
				if (seat.getSeatHoldId().equalsIgnoreCase(seatHoldId)) {
					seat.clearBooking();
					increaseSeatsAvailable();
				}
			}
		}
	}
	
	private void checkExpireSeats() {
		for (int i=0; i < arenaMaxRows; i++) {
			for (int j=0; j < arenaMaxSeats; j++) {
				ArenaSeat seat = seats[i][j];
				if (seat == null)
					continue;
				
				if ( StringUtils.isEmpty(seat.getReservedId()) &&  
					 (!StringUtils.isEmpty(seat.getSeatHoldId()) &&
					 seat.isExpired(this.ticketExpirationTime))) {
					
					seat.clearBooking();
					increaseSeatsAvailable();
				}
			}
		}
	}
	
	private void increaseSeatsAvailable() {
		if (numSeatsAvailable < (arenaMaxRows * arenaMaxSeats)) {
			numSeatsAvailable++;
		}
	}
	
}
