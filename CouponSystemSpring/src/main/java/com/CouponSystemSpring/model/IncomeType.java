package com.CouponSystemSpring.model;


public enum IncomeType {

	CUSTOMER_PURCHASE("customerPurchase"), COMPANY_NEW_COUPON("companyCreateCoupon"), COMPANY_UPDATE_COUPON(
			"companyUpdateCoupon");

	private String description;

	private IncomeType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	
}