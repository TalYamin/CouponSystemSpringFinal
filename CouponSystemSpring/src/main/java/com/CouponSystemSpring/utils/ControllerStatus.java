package com.CouponSystemSpring.utils;


import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerStatus {
	
	private boolean success;
	private String message;

}