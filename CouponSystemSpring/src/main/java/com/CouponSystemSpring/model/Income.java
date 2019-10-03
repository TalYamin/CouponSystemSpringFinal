package com.CouponSystemSpring.model;


import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Income {
	
	@Id
	@GeneratedValue
	private long  incomeId;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull String  clientName;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull long  clientId;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull LocalDate operationDate;
	
	@Basic(optional = false)
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private @NonNull IncomeType description;
	
	@Basic(optional = false)
	@Column(nullable = false)
	private @NonNull double amount;

}