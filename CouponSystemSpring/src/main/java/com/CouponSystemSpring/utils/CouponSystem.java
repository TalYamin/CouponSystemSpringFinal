package com.CouponSystemSpring.utils;


import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.CouponSystemSpring.exception.LoginCouponSystemException;
import com.CouponSystemSpring.model.Company;
import com.CouponSystemSpring.model.Customer;
import com.CouponSystemSpring.repository.CompanyRepository;
import com.CouponSystemSpring.repository.CustomerRepository;
import com.CouponSystemSpring.service.AdminService;
import com.CouponSystemSpring.service.AdminServiceImpl;
import com.CouponSystemSpring.service.ClientType;
import com.CouponSystemSpring.service.CompanyService;
import com.CouponSystemSpring.service.CompanyServiceImpl;
import com.CouponSystemSpring.service.CouponClient;
import com.CouponSystemSpring.service.CustomerService;
import com.CouponSystemSpring.service.CustomerServiceImpl;

@Service
public class CouponSystem {

	@Autowired
	private ApplicationContext ctx;

	@Resource
	private AdminService adminService;

	@Resource
	private CustomerRepository customerRepository;

	@Resource
	private CompanyRepository companyRepository;

	// private DailyCouponExpirationDate

	// private SessionTimeoutHandelr

	public CouponClient login(String userName, String password, ClientType clientType) throws Exception {

		try {
			switch (clientType) {

			case ADMIN:

				/* Check the validity of the parameters values */
				if (userName.equals("admin") && password.equals("1234")) {
					AdminService adminF = ctx.getBean(AdminServiceImpl.class);
					System.out.println("Admin logged in to system");
					return (CouponClient) adminF;
				} else {
					throw new LoginCouponSystemException("invalid details for Admin user. ", userName, password,
							clientType);
				}

			case COMPANY:

				Company company = companyRepository.findByCompanyNameAndCompanyPassword(userName, password);
				if (company != null) {
					CompanyService companyF = ctx.getBean(CompanyServiceImpl.class);
					companyF.setCompany(company);
					System.out.println("Company " + company.getCompanyName() + " logged in to system");
					return (CouponClient) companyF;
				} else {
					throw new LoginCouponSystemException("invalid details for Company user. ", userName, password,
							clientType);
				}

			case CUSTOMER:

				Customer customer = customerRepository.findByCustomerNameAndCustomerPassword(userName, password);
				if (customer != null) {
					CustomerService customerF = ctx.getBean(CustomerServiceImpl.class);
					customerF.setCustomer(customer);
					System.out.println("Customer " + customer.getCustomerName() + " logged in to system");
					return (CouponClient) customerF;
				} else {
					throw new LoginCouponSystemException("invalid details for Customer user. ", userName, password,
							clientType);
				}
			}
		} catch (LoginCouponSystemException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Login failed");
		}
		return null;
	}

}