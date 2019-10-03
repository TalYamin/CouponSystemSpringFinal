package com.CouponSystemSpring.utils;


import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatus {
	
	private boolean success;
	private String message;
	


	

}