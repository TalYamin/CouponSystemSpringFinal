package com.CouponSystemSpring.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CouponSystemSpring.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
	
	public Company findByCompanyName (String companyName);
	
	public boolean existsByCompanyName (String companyName);
	
	public Company findByCompanyNameAndCompanyPassword (String companyName, String password); 
	
	
}