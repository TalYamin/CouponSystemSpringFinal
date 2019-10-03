package com.CouponSystemSpring.exception;

public class RemovingCouponException extends Exception{

	private long companyId;
	private long couponId;

	public RemovingCouponException(String message, long companyId, long couponId) {
		super(String.format(message + "company id: %d, coupon id: %d ", companyId, couponId));
		this.companyId = companyId;
		this.couponId = couponId;
	}

	public RemovingCouponException(String message) {
		super(message);
	}

	public long getCompanyId() {
		return companyId;
	}

	public long getCouponId() {
		return couponId;
	}

	
	

}