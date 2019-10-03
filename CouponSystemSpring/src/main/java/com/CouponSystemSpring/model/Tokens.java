package com.CouponSystemSpring.model;


import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@Data
@RequiredArgsConstructor
public class Tokens{
	
	private @NonNull Map<String, Session> tokensMap;

}