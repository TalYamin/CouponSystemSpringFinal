package com.CouponSystemSpring.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CouponSystemSpring.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	public Customer findByCustomerName (String customerName);
	
	public boolean existsByCustomerName (String CustomerName );
	
	public Customer findByCustomerNameAndCustomerPassword (String customerName, String customerPassword);
}