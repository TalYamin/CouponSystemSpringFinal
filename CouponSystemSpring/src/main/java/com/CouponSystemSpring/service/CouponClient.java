package com.CouponSystemSpring.service;




//interface which include login method

public interface CouponClient {

	CouponClient login(String name, String password, ClientType clientType) throws Exception;
	
	
}