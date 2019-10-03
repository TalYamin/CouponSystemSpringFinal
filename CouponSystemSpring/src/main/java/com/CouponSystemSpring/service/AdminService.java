package com.CouponSystemSpring.service;


import java.util.List;

import com.CouponSystemSpring.model.Company;
import com.CouponSystemSpring.model.Customer;
import com.CouponSystemSpring.utils.ServiceStatus;

public interface AdminService {

	public ServiceStatus addCompany(Company company) throws Exception;

	public ServiceStatus removeCompany(long companyId)throws Exception ;

	public ServiceStatus updateCompany(long companyId, String newCompanyPassword, String newCompanyEmail) throws Exception;

	public List<Company> getAllCompanies() throws Exception;
	
	public Company getCompany(long companyId) throws Exception;
	
	public ServiceStatus addCustomer(Customer customer) throws Exception;
	
	public ServiceStatus removeCustomer(long customerId) throws Exception;
	
	public ServiceStatus updateCustomer(long customerId, String newCustomerPassword) throws Exception;
	
	public List<Customer> getAllCustomers() throws Exception;
	
	public Customer getCustomer(long customerId) throws Exception;
	
	
	
}