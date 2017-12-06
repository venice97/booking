package com.tv.booking.exception;

@SuppressWarnings("serial")
public class InvalidSeatHoldException extends RuntimeException {
	
	public InvalidSeatHoldException(String message) {
		 super(message);
	}
	
}
