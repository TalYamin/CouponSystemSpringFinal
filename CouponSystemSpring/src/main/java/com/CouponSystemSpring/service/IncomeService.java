package com.CouponSystemSpring.service;


import java.util.List;

import com.CouponSystemSpring.model.Income;
import com.CouponSystemSpring.utils.ServiceStatus;

public interface IncomeService {

	public ServiceStatus storeIncome(Income income) throws Exception;
	
	public List<Income> viewAllIncome() throws Exception;
	
	public List<Income> viewIncomeByCustomer(long customerId) throws Exception;
	
	public List<Income> viewIncomeByCompany(long companyId) throws Exception; 
	
	
	
}