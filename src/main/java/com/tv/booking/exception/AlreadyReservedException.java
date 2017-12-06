package com.tv.booking.exception;

@SuppressWarnings("serial")
public class AlreadyReservedException extends RuntimeException {
	
	public AlreadyReservedException(String message) {
		 super(message);
	}
	
}
