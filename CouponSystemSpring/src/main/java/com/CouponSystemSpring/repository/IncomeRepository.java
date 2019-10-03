package com.CouponSystemSpring.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CouponSystemSpring.model.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
	
	public List<Income> findAllByClientId (long clientId);

}