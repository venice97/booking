package com.tv.booking.exception;

@SuppressWarnings("serial")
public class HoldExpiredException extends RuntimeException {
	
	public HoldExpiredException(String message) {
		 super(message);
	}
	
}
