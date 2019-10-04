package com.CouponSystemSpring.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CouponSystemSpring.exception.ControllerException;
import com.CouponSystemSpring.exception.InvalidTokenException;
import com.CouponSystemSpring.model.Company;
import com.CouponSystemSpring.model.Coupon;
import com.CouponSystemSpring.model.Customer;
import com.CouponSystemSpring.model.Tokens;
import com.CouponSystemSpring.service.ClientType;
import com.CouponSystemSpring.service.CompanyService;
import com.CouponSystemSpring.service.CustomerService;
import com.CouponSystemSpring.utils.ServiceStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	private ServiceStatus serviceStatus;

	@Autowired
	private Tokens tokens;

	@Autowired
	ApplicationContext ctx;

	public CustomerService getCustomerService(String token) {
		try {

			if (tokens.getTokensMap().containsKey(token)) {
				CustomerService customerService = (CustomerService) tokens.getTokensMap().get(token).getCouponClient();
				return customerService;
			} else {
				throw new InvalidTokenException("Invalid token: ", token);
			}
		} catch (InvalidTokenException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@GetMapping("/getCustomer/{token}")
	public ResponseEntity<?> getCustomer(@PathVariable("token") String token) {
		try {

			CustomerService customerService = getCustomerService(token);

			if (customerService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Customer: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			Customer customer = customerService.getCustomer();
			if (customer != null) {
				System.out.println("customer was returned in success " + customerService.getCustomer().getCustomerId());
				ResponseEntity<Customer> result = new ResponseEntity<>(customer, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException(
						"Customer" + customerService.getCustomer().getCustomerId() + "failed to get customer ");

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@PostMapping("/purchaseCoupon/{couponId}/{token}")
	public ResponseEntity<ServiceStatus> purchaseCoupon(@PathVariable("couponId") long couponId,
			@PathVariable("token") String token) throws Exception {
		try {

			CustomerService customerService = getCustomerService(token);

			if (customerService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Customer: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			serviceStatus = customerService.purchaseCoupon(couponId);
			if (serviceStatus.isSuccess() == true) {
				System.out.println("coupon was purchased in success " + couponId);
				ResponseEntity<ServiceStatus> result = new ResponseEntity<>(serviceStatus, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Customer failed to purchase coupon ",
						customerService.getCustomer().getCustomerId(), customerService.getCustomer().getCustomerName(),
						ClientType.CUSTOMER.toString());
			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseEntity<ServiceStatus> badResult = new ResponseEntity<>(serviceStatus, HttpStatus.OK);
		return badResult;
	}

	@GetMapping("/getAllPurchases/{token}")
	public ResponseEntity<?> getAllPurchases(@PathVariable("token") String token) {
		try {

			CustomerService customerService = getCustomerService(token);

			if (customerService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Customer: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Coupon>coupons = customerService.getAllPurchases();
			if (!coupons.isEmpty()) {
				System.out.println("All purchases were returned in success ");
				ResponseEntity<List<Coupon>> result = new ResponseEntity<>(coupons, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException(
						("Customer" + customerService.getCustomer().getCustomerName()
								+ "failed to get all purchases "));

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/getAllCouponsByType/{typeName}/{token}")
	public ResponseEntity<?> getAllCouponsByType(@PathVariable ("typeName") String typeName, @PathVariable("token") String token) {
		try {

			CustomerService customerService = getCustomerService(token);

			if (customerService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Customer: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Coupon>coupons = customerService.getAllCouponsByType(typeName);
			if (!coupons.isEmpty()) {
				System.out.println("All coupons by type were returned in success ");
				ResponseEntity<List<Coupon>> result = new ResponseEntity<>(coupons, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException(
						("Customer" + customerService.getCustomer().getCustomerName()
								+ "failed to get all coupons by type " + typeName));

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/getAllCouponsByPrice/{priceTop}/{token}")
	public ResponseEntity<?> getAllCouponsByPrice(@PathVariable ("priceTop") double priceTop, @PathVariable("token") String token) {
		try {

			CustomerService customerService = getCustomerService(token);

			if (customerService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Customer: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Coupon>coupons = customerService.getAllCouponsByPrice(priceTop);
			if (!coupons.isEmpty()) {
				System.out.println("All coupons by price were returned in success ");
				ResponseEntity<List<Coupon>> result = new ResponseEntity<>(coupons, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException(
						("Customer" + customerService.getCustomer().getCustomerName()
								+ "failed to get all coupons by price " + priceTop));

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/getAllCouponsList/{token}")
	public ResponseEntity<?> getAllCouponsList(@PathVariable("token") String token) {
		try {

			CustomerService customerService = getCustomerService(token);

			if (customerService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Customer: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Coupon>coupons = customerService.getAllCouponsList();
			if (!coupons.isEmpty()) {
				System.out.println("All coupons list was returned in success ");
				ResponseEntity<List<Coupon>> result = new ResponseEntity<>(coupons, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException(
						("Customer" + customerService.getCustomer().getCustomerName()
								+ "failed to get all coupons list "));

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}