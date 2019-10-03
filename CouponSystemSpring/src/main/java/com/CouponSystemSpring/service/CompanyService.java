package com.CouponSystemSpring.service;


import java.util.List;

import com.CouponSystemSpring.model.Company;
import com.CouponSystemSpring.model.Coupon;
import com.CouponSystemSpring.utils.ServiceStatus;

public interface CompanyService {

	public ServiceStatus addCoupon(Coupon coupon) throws Exception;

	public ServiceStatus removeCoupon(long couponId) throws Exception;

	public ServiceStatus updateCoupon(long couponId, String newEndDate, double newPrice) throws Exception;

	public Company getCompany() throws Exception;
	
	public Coupon getCoupon(long couponId) throws Exception;
	
	public List<Coupon> getAllCoupons() throws Exception;
	
	public List<Coupon> getAllCouponsByType(String typeName) throws Exception;
	
	public List<Coupon> getAllCouponsByPrice(double priceTop) throws Exception;
	
	public List<Coupon> getAllCouponsByDate(String untilDate) throws Exception;
	
	public void setCompany(Company company);
}
