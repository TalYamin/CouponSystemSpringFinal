package com.CouponSystemSpring.exception;

public class AmountException extends Exception {
	
	private int amount;
	private long couponId;
	
	public AmountException(String message,int amount, long couponId) {
		super(String.format(message + "amount: %d, couponId: %d", amount, couponId));
		this.amount = amount;
		this.couponId = couponId;
	}

	public int getAmount() {
		return amount;
	}

	public long getCouponId() {
		return couponId;
	}
	
	

}