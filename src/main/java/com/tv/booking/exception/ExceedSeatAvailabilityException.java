package com.tv.booking.exception;

@SuppressWarnings("serial")
public class ExceedSeatAvailabilityException extends RuntimeException {
	
	public ExceedSeatAvailabilityException(String message) {
		 super(message);
	}
	
}
