package com.CouponSystemSpring.controller;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CouponSystemSpring.exception.LoginCouponSystemException;
import com.CouponSystemSpring.model.LoginUser;
import com.CouponSystemSpring.model.Session;
import com.CouponSystemSpring.model.Token;
import com.CouponSystemSpring.model.Tokens;
import com.CouponSystemSpring.service.CouponClient;
import com.CouponSystemSpring.utils.ClientTypeConverter;
import com.CouponSystemSpring.utils.CouponSystem;
import com.CouponSystemSpring.utils.ServiceStatus;

@RestController
@RequestMapping("/logout")
public class LogoutController {

	private ServiceStatus serviceStatus;

	@Autowired
	private Tokens tokens;

	@Autowired
	private CouponSystem couponSystem;

	@PostMapping
	public ResponseEntity<ServiceStatus> login(@RequestBody String token) {

		try {
			
			tokens.getTokensMap().remove(token);
			if (tokens.getTokensMap().containsKey(token)) {
				throw new LoginCouponSystemException("Token failed to be removed: " + token);
			}
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setMessage("Token removed succesfully: " + token);
			serviceStatus.setSuccess(true);
			return new ResponseEntity<>(serviceStatus, HttpStatus.OK);
		} 
		catch (LoginCouponSystemException e) {
			System.out.println(e.getMessage());
			serviceStatus.setMessage(e.getMessage());
			serviceStatus.setSuccess(false);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(serviceStatus, HttpStatus.OK);
		
	}

}
