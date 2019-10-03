package com.CouponSystemSpring.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

//@CrossOrigin
@RestController
@RequestMapping("/login")
public class LoginController {

	@Resource
	private Tokens tokens;

	@Resource
	private CouponSystem couponSystem;

	@PostMapping
	public ResponseEntity<Token> login(@RequestBody LoginUser loginUser) {

		Session session = new Session();
		CouponClient couponClient = null;
		String token = UUID.randomUUID().toString();
		long lastAccessed = System.currentTimeMillis();

		try {
			couponClient = couponSystem.login(loginUser.getUsername(), loginUser.getPassword(),
					ClientTypeConverter.convertStringToType(loginUser.getClientType()));
			if (couponClient == null) {
				throw new LoginCouponSystemException("Invalid details", loginUser.getUsername(),
						loginUser.getPassword(), ClientTypeConverter.convertStringToType(loginUser.getClientType()));
			}
			session.setCouponClient(couponClient);
			session.setLastAccessed(lastAccessed);
			tokens.getTokensMap().put(token, session);
			System.out.println("token: " + token);
			Token tokenObject = new Token(token, loginUser.getClientType());
			return new ResponseEntity<>(tokenObject, HttpStatus.OK);
		} catch (LoginCouponSystemException e) {
			System.out.println(e.getMessage());
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}