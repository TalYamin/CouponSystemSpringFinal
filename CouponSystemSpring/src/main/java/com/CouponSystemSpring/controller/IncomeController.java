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

import com.CouponSystemSpring.exception.IncomeException;
import com.CouponSystemSpring.exception.InvalidTokenException;
import com.CouponSystemSpring.model.Income;
import com.CouponSystemSpring.model.Tokens;
import com.CouponSystemSpring.service.AdminService;
import com.CouponSystemSpring.service.CompanyService;
import com.CouponSystemSpring.service.CustomerService;
import com.CouponSystemSpring.service.IncomeService;
import com.CouponSystemSpring.utils.ServiceStatus;

@RestController
@RequestMapping("/income")
public class IncomeController {

	private ServiceStatus serviceStatus;
	@Autowired
	private IncomeService incomeService;

	@Autowired
	private Tokens tokens;

	@Autowired
	ApplicationContext ctx;

	public AdminService getAdminService(String token) {
		try {

			if (tokens.getTokensMap().containsKey(token)) {
				AdminService adminService = (AdminService) tokens.getTokensMap().get(token).getCouponClient();
				return adminService;
			} else {
				throw new InvalidTokenException("Invalid token: ", token);
			}
		} catch (InvalidTokenException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

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

	@PostMapping("/storeIncome/{client}/{token}")
	public ResponseEntity<ServiceStatus> storeIncome(@RequestBody Income income, @PathVariable("client") String client,
			@PathVariable("token") String token) throws Exception {
		try {

			switch (client) {
			case "company":
				CompanyService companyService = getCompanyService(token);
				if (companyService == null || companyService.getCompany().getCompanyId() != income.getClientId()) {
					ServiceStatus serviceStatus = new ServiceStatus();
					serviceStatus.setSuccess(false);
					serviceStatus.setMessage("Invalid token to Company: " + token);
					ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
							HttpStatus.UNAUTHORIZED);
					return InvalidToken;
				}
				break;
			case "customer":
				CustomerService customerService = getCustomerService(token);
				if (customerService == null || customerService.getCustomer().getCustomerId() != income.getClientId()) {
					ServiceStatus serviceStatus = new ServiceStatus();
					serviceStatus.setSuccess(false);
					serviceStatus.setMessage("Invalid token to Customer: " + token);
					ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
							HttpStatus.UNAUTHORIZED);
					return InvalidToken;
				}
				break;
			default:
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}
			

			serviceStatus = incomeService.storeIncome(income);
			if (serviceStatus.isSuccess() == true) {
				System.out.println("Income was stored in success: " + income);
				ResponseEntity<ServiceStatus> result = new ResponseEntity<>(serviceStatus, HttpStatus.OK);
				return result;
			} else {
				throw new IncomeException("Failed to store income", income.getClientId(), income.getClientName());
			}
		} catch (IncomeException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseEntity<ServiceStatus> badResult = new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		return badResult;
	}

	@GetMapping("/viewAllIncome/{token}")
	public ResponseEntity<?> viewAllIncome(@PathVariable("token") String token) throws Exception {
		try {

			AdminService adminService = getAdminService(token);

			if (adminService == null) {
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token to Admin: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Income> incomes = incomeService.viewAllIncome();
			if (!incomes.isEmpty()) {
				System.out.println("All Incomes was returned in success: ");
				ResponseEntity<List<Income>> result = new ResponseEntity<>(incomes, HttpStatus.OK);
				return result;
			} else {
				throw new IncomeException("Failed to view all incomes");
			}
		} catch (IncomeException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/viewIncomeByCustomer/{customerId}/{client}/{token}")
	public ResponseEntity<?> viewIncomeByCustomer(@PathVariable("client") String client,
			@PathVariable("customerId") long customerId, @PathVariable("token") String token) throws Exception {
		try {

			switch (client) {
			case "admin":
				AdminService adminService = getAdminService(token);
				if (adminService == null) {
					ServiceStatus serviceStatus = new ServiceStatus();
					serviceStatus.setSuccess(false);
					serviceStatus.setMessage("Invalid token to Admin: " + token);
					ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
							HttpStatus.UNAUTHORIZED);
					return InvalidToken;
				}
				break;
			case "customer":
				CustomerService customerService = getCustomerService(token);
				if (customerService == null || customerService.getCustomer().getCustomerId() != customerId) {
					ServiceStatus serviceStatus = new ServiceStatus();
					serviceStatus.setSuccess(false);
					serviceStatus.setMessage("Invalid token to Customer: " + token);
					ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
							HttpStatus.UNAUTHORIZED);
					return InvalidToken;
				}
				break;
			default:
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Income> incomesByCustomer = incomeService.viewIncomeByCustomer(customerId);
			if (!incomesByCustomer.isEmpty()) {
				System.out.println("All incomes By Customer was returned in success");
				ResponseEntity<List<Income>> result = new ResponseEntity<>(incomesByCustomer, HttpStatus.OK);
				return result;
			} else {
				throw new IncomeException("Failed to view all incomes by customer");
			}
		} catch (IncomeException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/viewIncomeByCompany/{companyId}/{client}/{token}")
	public ResponseEntity<?> viewIncomeByCompany(@PathVariable("companyId") long companyId,
			@PathVariable("client") String client, @PathVariable("token") String token) throws Exception {
		try {

			switch (client) {
			case "admin":
				AdminService adminService = getAdminService(token);
				if (adminService == null) {
					ServiceStatus serviceStatus = new ServiceStatus();
					serviceStatus.setSuccess(false);
					serviceStatus.setMessage("Invalid token to Admin: " + token);
					ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
							HttpStatus.UNAUTHORIZED);
					return InvalidToken;
				}
				break;
			case "company":
				CompanyService companyService = getCompanyService(token);
				if (companyService == null || companyService.getCompany().getCompanyId() != companyId) {
					ServiceStatus serviceStatus = new ServiceStatus();
					serviceStatus.setSuccess(false);
					serviceStatus.setMessage("Invalid token to Company: " + token);
					ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
							HttpStatus.UNAUTHORIZED);
					return InvalidToken;
				}
				break;
			default:
				ServiceStatus serviceStatus = new ServiceStatus();
				serviceStatus.setSuccess(false);
				serviceStatus.setMessage("Invalid token: " + token);
				ResponseEntity<ServiceStatus> InvalidToken = new ResponseEntity<>(serviceStatus,
						HttpStatus.UNAUTHORIZED);
				return InvalidToken;
			}

			List<Income> incomesByCompany = incomeService.viewIncomeByCompany(companyId);
			if (!incomesByCompany.isEmpty()) {
				System.out.println("All incomes By Company was returned in success");
				ResponseEntity<List<Income>> result = new ResponseEntity<>(incomesByCompany, HttpStatus.OK);
				return result;
			} else {
				throw new IncomeException("Failed to view all incomes by company");
			}
		} catch (IncomeException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}