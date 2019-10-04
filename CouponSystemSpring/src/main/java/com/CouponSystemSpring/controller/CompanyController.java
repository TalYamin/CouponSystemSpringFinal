package com.CouponSystemSpring.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.CouponSystemSpring.model.Tokens;
import com.CouponSystemSpring.service.ClientType;
import com.CouponSystemSpring.service.CompanyService;
import com.CouponSystemSpring.utils.ServiceStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
@RequestMapping("/company")
public class CompanyController {

	private ServiceStatus serviceStatus;

	@Autowired
	private Tokens tokens;

	@Autowired
	ApplicationContext ctx;

	public CompanyService getCompanyService(String token) {
		try {

			if (tokens.getTokensMap().containsKey(token)) {
				CompanyService companyService = (CompanyService) tokens.getTokensMap().get(token).getCouponClient();
				return companyService;
			} else {
				throw new InvalidTokenException("Invalid token: ", token);
			}
		} catch (InvalidTokenException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@PostMapping("/addCoupon/{token}")
	public ResponseEntity<ServiceStatus> addCoupon(@RequestBody String jsonString, @PathVariable("token") String token)
			throws Exception {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			JsonObject object = (JsonObject) parser.parse(jsonString);
			Coupon coupon = gson.fromJson(object, Coupon.class);
			serviceStatus = companyService.addCoupon(coupon);
			if (serviceStatus.isSuccess() == true) {
				System.out.println("Coupon was added in success " + coupon.getCouponId());
				ResponseEntity<ServiceStatus> result = new ResponseEntity<>(serviceStatus, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Company failed to add coupon ",
						companyService.getCompany().getCompanyId(), companyService.getCompany().getCompanyName(),
						ClientType.COMPANY.toString());
			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseEntity<ServiceStatus> badResult = new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		return badResult;
	}

	@DeleteMapping("/removeCoupon/{couponId}/{token}")
	public ResponseEntity<ServiceStatus> removeCoupon(@PathVariable("couponId") long couponId,
			@PathVariable("token") String token) {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			serviceStatus = companyService.removeCoupon(couponId);
			if (serviceStatus.isSuccess() == true) {
				System.out.println("Coupon was removed in success " + couponId);
				ResponseEntity<ServiceStatus> result = new ResponseEntity<>(serviceStatus, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Company failed to reomove coupon ",
						companyService.getCompany().getCompanyId(), companyService.getCompany().getCompanyName(),
						ClientType.COMPANY.toString());
			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseEntity<ServiceStatus> badResult = new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		return badResult;
	}

	@PostMapping("/updateCoupon/{token}")
	public ResponseEntity<ServiceStatus> updateCoupon(@RequestBody String jsonString,
			@PathVariable("token") String token) {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(jsonString).getAsJsonObject();
			long couponId = Long.parseLong(obj.get("couponId").getAsString());
			String newEndDate = obj.get("newEndDate").getAsString();
			double newPrice = Double.parseDouble(obj.get("newPrice").getAsString());

			serviceStatus = companyService.updateCoupon(couponId, newEndDate, newPrice);
			if (serviceStatus.isSuccess() == true) {
				System.out.println("Coupon was updated in success " + couponId);
				ResponseEntity<ServiceStatus> result = new ResponseEntity<>(serviceStatus, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Company failed to update coupon ",
						companyService.getCompany().getCompanyId(), companyService.getCompany().getCompanyName(),
						ClientType.COMPANY.toString());
			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseEntity<ServiceStatus> badResult = new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		return badResult;
	}

	@GetMapping("/getCompany/{token}")
	public ResponseEntity<?> getCompany(@PathVariable("token") String token) {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			Company company = companyService.getCompany();
			if (company != null) {
				System.out.println("company was returned in success " + companyService.getCompany().getCompanyId());
				ResponseEntity<Company> result = new ResponseEntity<>(company, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException(
						"Company" + companyService.getCompany().getCompanyName() + "failed to get company ");

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/getCoupon/{couponId}/{token}")
	public ResponseEntity<?> getCoupon(@PathVariable("couponId") long couponId, @PathVariable("token") String token) {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			Coupon coupon = companyService.getCoupon(couponId);
			if (coupon != null) {
				System.out.println("Coupon was returned in success " + couponId);
				ResponseEntity<Coupon> result = new ResponseEntity<>(coupon, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Company failed to get coupon ",
						companyService.getCompany().getCompanyId(), companyService.getCompany().getCompanyName(),
						ClientType.COMPANY.toString());

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/getAllCoupons/{token}")
	public ResponseEntity<?> getAllCoupons(@PathVariable("token") String token) {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Coupon> coupons = companyService.getAllCoupons();
			if (!coupons.isEmpty()) {
				System.out.println("All coupons were returned in success ");
				ResponseEntity<List<Coupon>> result = new ResponseEntity<>(coupons, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Company" + companyService.getCompany().getCompanyName() + "failed to get all coupons ");

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("getAllCouponsByType/{typeName}/{token}")
	public ResponseEntity<?> getAllCouponsByType(@PathVariable ("typeName") String typeName, @PathVariable("token") String token) {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Coupon> coupons = companyService.getAllCouponsByType(typeName);
			if (!coupons.isEmpty()) {
				System.out.println("All coupons by type were returned in success ");
				ResponseEntity<List<Coupon>> result = new ResponseEntity<>(coupons, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Company" + companyService.getCompany().getCompanyName()
						+ "failed to get all coupons by type " + typeName);

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("getAllCouponsByPrice/{priceTop}/{token}")
	public ResponseEntity<?> getAllCouponsByPrice(@PathVariable ("priceTop") double priceTop, @PathVariable("token") String token) {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Coupon> coupons = companyService.getAllCouponsByPrice(priceTop);
			if (!coupons.isEmpty()) {
				System.out.println("All coupons by price were returned in success ");
				ResponseEntity<List<Coupon>> result = new ResponseEntity<>(coupons, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Company" + companyService.getCompany().getCompanyName()
						+ "failed to get all coupons by price " + priceTop);

			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PostMapping("getAllCouponsByDate/{token}")
	public ResponseEntity<?> getAllCouponsByDate(@RequestBody String jsonString, @PathVariable("token") String token) {
		try {

			CompanyService companyService = getCompanyService(token);

			if (companyService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Company: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}
			
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(jsonString).getAsJsonObject();
			String untilDate = obj.get("untilDate").getAsString();

			List<Coupon> coupons = companyService.getAllCouponsByDate(untilDate);
			if (!coupons.isEmpty()) {
				System.out.println("All coupons by date were returned in success ");
				ResponseEntity<List<Coupon>> result = new ResponseEntity<>(coupons, HttpStatus.OK);
				return result;
			} else {
				throw new ControllerException("Company" + companyService.getCompany().getCompanyName()
						+ "failed to get all coupons by date " + untilDate);
			}
		} catch (ControllerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}