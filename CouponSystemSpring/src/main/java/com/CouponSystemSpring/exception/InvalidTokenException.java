package com.CouponSystemSpring.exception;


public class InvalidTokenException extends Exception{
	/*
	 * Data Members which hold user name, password and client type that activated
	 * the exception
	 */
	private String token;


	public InvalidTokenException(String message, String token) {
		super(String.format(message + "token: %s", token));
		this.token = token;
	}


	public String getToken() {
		return token;
	}

	

}