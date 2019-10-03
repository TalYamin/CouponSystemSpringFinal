package com.CouponSystemSpring.model;


public enum CouponType {
	
	RESTAURANTS
	{
		@Override
	    public String toString() {
	      return "Restaurants";
		}
	},	
	
	HEALTH
	{
		@Override
	    public String toString() {
	      return "Health";
		}
	},
	
	SPORTS
	{
		@Override
	    public String toString() {
	      return "Sports";
		}
	},
	
	TRAVELING
	{
		@Override
	    public String toString() {
	      return "Traveling";
		}
	}

}