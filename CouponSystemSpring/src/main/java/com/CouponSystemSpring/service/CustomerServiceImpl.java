package com.CouponSystemSpring.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.CouponSystemSpring.exception.CouponExpiredException;
import com.CouponSystemSpring.exception.IncomeException;
import com.CouponSystemSpring.exception.NoDetailsFoundException;
import com.CouponSystemSpring.exception.ObjectNotFoundException;
import com.CouponSystemSpring.exception.OutOfStockException;
import com.CouponSystemSpring.exception.SamePurchaseException;
import com.CouponSystemSpring.model.Coupon;
import com.CouponSystemSpring.model.CouponType;
import com.CouponSystemSpring.model.Customer;
import com.CouponSystemSpring.model.Income;
import com.CouponSystemSpring.model.IncomeType;
import com.CouponSystemSpring.repository.CouponRepository;
import com.CouponSystemSpring.repository.CustomerRepository;
import com.CouponSystemSpring.utils.CouponTypeConverter;
import com.CouponSystemSpring.utils.ServiceStatus;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomerServiceImpl implements CustomerService, CouponClient {

	@Resource
	private CustomerRepository customerRepository;

	@Resource
	private CouponRepository couponRepository;

	private ClientType clientType = ClientType.CUSTOMER;

	@Resource
	private ServiceStatus serviceStatus;
	
	@Resource
	private IncomeService incomeService;

	private Customer customer;

	public CustomerServiceImpl() {

	}

	public CustomerServiceImpl(Customer customer) {
		this.customer = customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Transactional
	@Override
	public Customer getCustomer() throws Exception {
		try {

			if (!customerRepository.existsById(this.customer.getCustomerId())) {
				throw new ObjectNotFoundException("customer does not exist in system", this.customer.getCustomerId(),
						this.clientType, this.customer.getCustomerId());
			}

			Customer customer = customerRepository.findById(this.customer.getCustomerId()).get();
			return customer;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception(
					"Cusstomer failed to get customer details. customerId: " + this.customer.getCustomerId());
		}
		return null;
	}

	@Transactional
	@Override
	public ServiceStatus purchaseCoupon(long couponId) throws Exception {
		try {

			if (!couponRepository.existsById(couponId)) {
				throw new ObjectNotFoundException("couponId does not exist in system. ", this.customer.getCustomerId(),
						this.clientType, couponId);
			}

			if (couponRepository.findById((long) couponId).get().getCustomers()
					.containsKey(this.customer.getCustomerId())) {
				throw new SamePurchaseException("Customer unable to purchase - already purchased same coupon. ",
						couponId, this.customer.getCustomerId());
			}

			Coupon c = couponRepository.findById((long) couponId).get();

			if (c.getAmount() <= 0) {
				throw new OutOfStockException("Customer unable to purchase - this coupon is out of stock. ",
						c.getAmount(), couponId, this.customer.getCustomerId());

			}

			if (c.getEndDate().isBefore(LocalDate.now())) {
				throw new CouponExpiredException("Customer unable to purchase - this coupon has expired. ",
						c.getEndDate().toString(), couponId, this.customer.getCustomerId());
			}

			Coupon newCoupon = couponRepository.findById(couponId).get();

			Map<Long, Customer> customers = couponRepository.findById(couponId).get().getCustomers();
			customers.put(this.customer.getCustomerId(), this.customer);
			newCoupon.setCustomers(customers);
			couponRepository.save(newCoupon);

			Map<Long, Coupon> coupons = customerRepository.findById(this.customer.getCustomerId()).get().getCoupons();
			coupons.put(couponId, newCoupon);
			this.customer.setCoupons(coupons);
			customerRepository.save(this.customer);
			couponRepository.save(newCoupon);

			newCoupon.setAmount(newCoupon.getAmount() - 1);
			couponRepository.save(newCoupon);
			
			Income income = new Income(this.customer.getCustomerName(), this.customer.getCustomerId(), LocalDate.now(), IncomeType.CUSTOMER_PURCHASE, newCoupon.getPrice());
			ServiceStatus incomeStatus = incomeService.storeIncome(income);
			if (incomeStatus.isSuccess() == false) {
				throw new IncomeException("Customer failed to store income",this.customer.getCustomerId(), this.customer.getCustomerName());
			}
			
			System.out.println("Customer " + customer.getCustomerName() + " purchased successfully Coupon " + couponId);
			serviceStatus.setSuccess(true);
			serviceStatus.setMessage(
					"success, Customer " + customer.getCustomerName() + " purchased successfully Coupon " + couponId + ". Your account was charged for " + newCoupon.getPrice()+ " NIS");
			return serviceStatus;
		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (SamePurchaseException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (OutOfStockException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (CouponExpiredException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		}catch (IncomeException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Customer failed to purchase coupon. couponId: " + couponId + " customerId: "
					+ this.customer.getCustomerId());
		}
		return serviceStatus;
	}

	@Transactional
	@Override
	public List<Coupon> getAllPurchases() throws Exception {
		try {

			Map<Long, Coupon> coupons = customerRepository.findById(this.customer.getCustomerId()).get().getCoupons();
			List<Coupon> couponsList = coupons.values().stream().collect(Collectors.toList());

			if (coupons.isEmpty()) {
				throw new NoDetailsFoundException(
						"Customer " + this.customer.getCustomerId()
								+ " failed to get all purchase history - no details found",
						this.customer.getCustomerId(), this.clientType);
			}
			return couponsList;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(
					"Custoemr failed to get all purchase history. customerId: " + this.customer.getCustomerId());
		}
		return null;
	}

	@Transactional
	@Override
	public List<Coupon> getAllCouponsByType(String typeName) throws Exception {
		try {
			Map<Long, Coupon> coupons = customerRepository.findById(this.customer.getCustomerId()).get().getCoupons();
			List<Coupon> couponsList = coupons.values().stream().collect(Collectors.toList());

			List<Coupon> couponsToView = new ArrayList<>();
			for (Coupon c : couponsList) {
				if (c.getType() == CouponTypeConverter.convertStringToType(typeName)) {
					couponsToView.add(c);
				}
			}

			if (couponsToView.isEmpty()) {
				throw new NoDetailsFoundException(
						"Customer " + this.customer.getCustomerId()
								+ " failed to get all coupons by type - no details found",
						this.customer.getCustomerId(), this.clientType);
			}
			return couponsToView;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Customer failed to get coupons data by Type. customerId: "
					+ this.customer.getCustomerId() + " couponType: " + typeName);
		}
		return null;
	}

	@Transactional
	@Override
	public List<Coupon> getAllCouponsByPrice(double priceTop) throws Exception {
		try {
			Map<Long, Coupon> coupons = customerRepository.findById(this.customer.getCustomerId()).get().getCoupons();
			List<Coupon> couponsList = coupons.values().stream().collect(Collectors.toList());

			List<Coupon> couponsToView = new ArrayList<>();
			for (Coupon c : couponsList) {
				if (c.getPrice() <= priceTop) {
					couponsToView.add(c);
				}
			}

			if (couponsToView.isEmpty()) {
				throw new NoDetailsFoundException(
						"Customer " + this.customer.getCustomerId()
								+ " failed to get all coupons by price - no details found",
						this.customer.getCustomerId(), this.clientType);
			}
			return couponsToView;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Customer failed to get coupons data by Price. customerId: "
					+ this.customer.getCustomerId() + " priceTop: " + priceTop);
		}
		return null;
	}

	@Transactional
	@Override
	public List<Coupon> getAllCouponsList() throws Exception {
		try {
			List<Coupon>coupons = couponRepository.findAll();

			if (coupons.isEmpty()) {
				throw new NoDetailsFoundException(
						"Customer " + this.customer.getCustomerId()
								+ " failed to get all coupons list - no details found",
						this.customer.getCustomerId(), this.clientType);
			}
			return coupons;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Customer failed to get coupons list. customerId: "
					+ this.customer.getCustomerId());
		}
		return null;
	}

	@Transactional
	@Override
	public CouponClient login(String name, String password, ClientType clientType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}