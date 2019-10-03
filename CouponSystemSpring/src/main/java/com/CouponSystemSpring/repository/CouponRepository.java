package com.CouponSystemSpring.repository;


import java.time.LocalDate;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.CouponSystemSpring.model.Coupon;
import com.CouponSystemSpring.model.CouponType;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
	
	public List<Coupon> findAllByCompanyCompanyId(long companyId); 
	
	public List<Coupon> findAllByCompanyCompanyIdAndType(long companyId, CouponType typeName);
	
	public List<Coupon> findAllByCompanyCompanyIdAndPriceLessThanEqual(long companyId, double priceTop);
	
	public List<Coupon> findAllByCompanyCompanyIdAndEndDateLessThanEqual(long companyId, LocalDate untilDate);
	
	public long deleteByCompanyCompanyId(long companyId);
	
	public boolean existsByTitle (String title);
	
	public boolean existsByCouponIdAndCompanyCompanyId(long couponId, long companyId);
	
//	@Query("SELECT c FROM customer AS cust join customer_coupon AS c WHERE cust.customer_id=:customerId AND c.type =:typeName")
//	public List<Coupon> findAllCustomerCouponByType (long customerId, String typeName);
	

}