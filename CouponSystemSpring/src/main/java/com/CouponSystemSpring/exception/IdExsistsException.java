package com.CouponSystemSpring.exception;

public class IdExsistsException extends Exception {

	private long id;

	public IdExsistsException(String message, long id) {
		super(String.format(message + "%d", id));
		this.id = id;
	}

	public long getId() {
		return id;
	}


	

}