package com.CouponSystemSpring.model;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.Transient;

import com.CouponSystemSpring.utils.DateConverterUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(value = {"customers", "company", "customStartDate", "customEndDate"})
public class Coupon {
	
	@Id
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull long couponId;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull String title; 
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull LocalDate startDate;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull LocalDate endDate;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull int amount;
	
	@Basic(optional = false)
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private @NonNull CouponType type;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull String couponMessage;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull double price;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull String image;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull boolean active;
	
	@Transient
	private String customStartDate;
	
	@Transient
	private String customEndDate;
	
	@ToString.Exclude
	@ManyToOne
	private Company company;
	
	@ToString.Exclude
	@ManyToMany(mappedBy = "coupons")
	@MapKey(name="customerId")
	private Map<Long,Customer> customers = new HashMap<>();
	
//	@ToString.Exclude
//	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "coupons", cascade=CascadeType.ALL)
//	@MapKey(name="customerId")
//	private Map<Long,Customer> customers = new HashMap<>();;
	
	public Coupon(long couponId, String title, String endDate, int amount, CouponType type, String couponMessage,
			double price, String image) {
		setCouponId(couponId);
		setTitle(title);
		setStartDate(LocalDate.now());
		setEndDate(DateConverterUtil.convertStringDate(endDate));
//		setStartDate(LocalDate.now().plusDays(1));
//		setEndDate(DateConverterUtil.convertStringDate(endDate).plusDays(1));
		setAmount(amount);
		setType(type);
		setCouponMessage(couponMessage);
		setPrice(price);
		setImage(image);
		setActive(true);
	}
	
	@Override
	public String toString() {

		customStartDate = DateConverterUtil.DateStringFormat(this.startDate);
		customEndDate = DateConverterUtil.DateStringFormat(this.endDate);

		return "Coupon [couponId=" + this.getCouponId() + ", title=" + this.getTitle() + ", startDate="
				+ this.customStartDate + ", endDate=" + this.customEndDate + ", amount=" + this.getAmount() + ", type="
				+ this.getType() + ", couponMessage=" + this.getCouponMessage() + ", price=" + this.getPrice()
				+ ", image=" + this.getImage() + ", active=" + this.isActive() + "]";
	}
	

}
