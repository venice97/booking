package com.tv.booking.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tv.booking.domain.SeatHold;
import com.tv.booking.exception.AlreadyReservedException;
import com.tv.booking.exception.ExceedSeatAvailabilityException;
import com.tv.booking.exception.InvalidSeatHoldException;

public class TicketServiceImplTest {
	
	private TicketServiceImpl ticketService;
	
	@Before
	public void setUp() {
		ticketService = new TicketServiceImpl();
		ticketService.setArenaMaxSeats(50);
		ticketService.setArenaMaxRows(10);
		ticketService.setTicketExpirationTime(600);
	}
	
	@Test
	public void whenFindAndHoldSeatsAndAvailableExpectReturnSuccessful() {
		SeatHold seatHold = ticketService.findAndHoldSeats(5, "foo.bar@gmail.com");
		Assert.assertTrue(seatHold.getSeatHoldId().contains("HD"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void whenFindAndHoldZeroSeatExpectReturnsException() {
		ticketService.findAndHoldSeats(0, "foo.bar@gmail.com");
	}
	
	@Test
	public void whenReserveSeatsAndAvailableExpectReturnSuccessful() {
		SeatHold seatHold = ticketService.findAndHoldSeats(5, "foo.bar@gmail.com");
		Assert.assertTrue(seatHold.getSeatHoldId().contains("HD"));
		String reservedId = ticketService.reserveSeats(seatHold.getSeatHoldId(), "foo.bar@gmail.com");
		Assert.assertTrue(reservedId.contains("RD"));
	}
	
	@Test(expected = InvalidSeatHoldException.class)
	public void whenReserveSeatsForNonExistedSeatHoldIdExpectReturnException() {
		ticketService.reserveSeats("HD-bogus-id", "foo.bar@gmail.com");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void whenReserveSeatsForNonExistedSeatHoldIdWithNoPrefixExpectReturnException() {
		ticketService.reserveSeats("bogus-id", "foo.bar@gmail.com");
	}
	
	@Test(expected = AlreadyReservedException.class)
	public void whenReserveSeatsMoreThanOnceExpectReturnException() {
		SeatHold seatHold = ticketService.findAndHoldSeats(5, "foo.bar@gmail.com");
		Assert.assertTrue(seatHold.getSeatHoldId().contains("HD"));
		String reservedId = ticketService.reserveSeats(seatHold.getSeatHoldId(), "foo.bar@gmail.com");
		Assert.assertTrue(reservedId.contains("RD"));
		ticketService.reserveSeats(seatHold.getSeatHoldId(), "foo.bar@gmail.com");
	}
	
	@Test
	public void whenReserveSeatsExpectNumberAvailableIsTheSame() {
		SeatHold seatHold = ticketService.findAndHoldSeats(5, "foo.bar@gmail.com");
		Assert.assertTrue(seatHold.getSeatHoldId().contains("HD"));
		int numSeatsAvailableBefore = ticketService.numSeatsAvailable();
		
		String reservedId = ticketService.reserveSeats(seatHold.getSeatHoldId(), "foo.bar@gmail.com");
		int numSeatsAvailableAfter = ticketService.numSeatsAvailable();
		Assert.assertTrue(reservedId.contains("RD"));
		Assert.assertEquals(numSeatsAvailableBefore, numSeatsAvailableAfter);
	}
	
	@Test
	public void whenFindAndHoldSeatsExpectReturnLesserNumberSeatAvailable() {
		int numSeatsAvailableBefore = ticketService.numSeatsAvailable();
		SeatHold seatHold = ticketService.findAndHoldSeats(10, "foo.bar@gmail.com");
		Assert.assertTrue(seatHold.getSeatHoldId().contains("HD"));
		int numSeatsAvailableAfter = ticketService.numSeatsAvailable();
		Assert.assertEquals((numSeatsAvailableBefore - numSeatsAvailableAfter), 10);
	}
	
	@Test(expected = ExceedSeatAvailabilityException.class)
	public void whenFindAndHoldSeatsAndExceedsCapacityExpectReturnException() {
		int numSeatsAvailable = ticketService.numSeatsAvailable();
		ticketService.findAndHoldSeats(numSeatsAvailable + 5, "foo.bar@gmail.com");
	}
}
