package com.CouponSystemSpring.service;


import java.util.List;

import com.CouponSystemSpring.model.Coupon;
import com.CouponSystemSpring.model.Customer;
import com.CouponSystemSpring.utils.ServiceStatus;

public interface CustomerService {

	public Customer getCustomer() throws Exception;
	
	public ServiceStatus purchaseCoupon(long couponId) throws Exception;
	
	public List<Coupon> getAllPurchases() throws Exception;
	
	public List<Coupon> getAllCouponsByType(String typeName) throws Exception;
	
	public List<Coupon> getAllCouponsByPrice(double priceTop) throws Exception;
	
	public List<Coupon> getAllCouponsList() throws Exception;
	
	public void setCustomer(Customer customer);
	
}