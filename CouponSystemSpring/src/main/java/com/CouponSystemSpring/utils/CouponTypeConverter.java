package com.CouponSystemSpring.utils;


import com.CouponSystemSpring.model.CouponType;

public class CouponTypeConverter {

	private static CouponType couponType;
	
	public static CouponType convertStringToType(String typeName) {
		
		switch (typeName) {
		case "Restaurants":
			couponType = CouponType.RESTAURANTS;
			break;
		case "Health":
			couponType = CouponType.HEALTH;
			break;
		case "Sports":
			couponType = CouponType.SPORTS;
			break;
		case "Traveling":
			couponType = CouponType.TRAVELING;
			break;
		default:
			break;
		}
		
		return couponType;
		
	}
}