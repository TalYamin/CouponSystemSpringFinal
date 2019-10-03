package com.CouponSystemSpring.exception;



public class ControllerException extends Exception {

	private long clientId;
	private String clientName;
	private String clientType;

	public ControllerException(String message, long clientId, String clientName, String clientType) {
		super(String.format(message + "client id: %d, client name: %s, client type: %s ", clientId, clientName,
				clientType));
		this.clientId = clientId;
		this.clientName = clientName;
		this.clientType = clientType;
	}
	
	public ControllerException(String message) {
		super(message);
	}

	public long getClientId() {
		return this.clientId;
	}

	public String getClientName() {
		return this.clientName;
	}

	public String getClientType() {
		return this.clientType;
	}
}