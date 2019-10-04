package com.CouponSystemSpring.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.CouponSystemSpring.exception.AmountException;
import com.CouponSystemSpring.exception.CouponExistsException;
import com.CouponSystemSpring.exception.EndDatePassedException;
import com.CouponSystemSpring.exception.IdExsistsException;
import com.CouponSystemSpring.exception.IncomeException;
import com.CouponSystemSpring.exception.NoDetailsFoundException;
import com.CouponSystemSpring.exception.NotBelongsException;
import com.CouponSystemSpring.exception.ObjectNotFoundException;
import com.CouponSystemSpring.exception.RemovingCouponException;
import com.CouponSystemSpring.model.Company;
import com.CouponSystemSpring.model.Coupon;
import com.CouponSystemSpring.model.CouponType;
import com.CouponSystemSpring.model.Customer;
import com.CouponSystemSpring.model.Income;
import com.CouponSystemSpring.model.IncomeType;
import com.CouponSystemSpring.repository.CompanyRepository;
import com.CouponSystemSpring.repository.CouponRepository;
import com.CouponSystemSpring.repository.CustomerRepository;
import com.CouponSystemSpring.utils.CouponTypeConverter;
import com.CouponSystemSpring.utils.DateConverterUtil;
import com.CouponSystemSpring.utils.ServiceStatus;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CompanyServiceImpl implements CompanyService, CouponClient {

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private IncomeService incomeService;

	private ClientType clientType = ClientType.COMPANY;

	@Autowired
	private ServiceStatus serviceStatus;

	private Company company;

	public CompanyServiceImpl() {

	}

	public CompanyServiceImpl(Company company) {
		this.company = company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Transactional
	@Override
	public ServiceStatus addCoupon(Coupon coupon) throws Exception {

		try {

			if (coupon.getAmount() < 1) {
				throw new AmountException("Company failed to add coupon - wrong amount: 0, couponId: ",
						coupon.getAmount(), coupon.getCouponId());
			}

			if (coupon.getEndDate().isBefore(LocalDate.now())) {
				throw new EndDatePassedException("Company failed to add coupon - the end date already passed. ",
						DateConverterUtil.DateStringFormat(coupon.getEndDate()), coupon.getCouponId(),
						this.company.getCompanyId());
			}

			if (couponRepository.existsByTitle(coupon.getTitle())) {
				throw new CouponExistsException("Company failed to add coupon - this coupon already exists. ",
						coupon.getTitle(), this.company.getCompanyId());
			}

			if (couponRepository.existsById(coupon.getCouponId())) {
				throw new IdExsistsException("Company failed to add coupon - this id already exists. ",
						coupon.getCouponId());
			}

			coupon.setCompany(this.company);
			List<Coupon> couponsList = this.company.getCoupons();
			couponsList.add(coupon);
			this.company.setCoupons(couponsList);
			couponRepository.save(coupon);
			companyRepository.save(this.company);
			Income income = new Income(this.company.getCompanyName(), this.company.getCompanyId(), LocalDate.now(), IncomeType.COMPANY_NEW_COUPON, 100);
			ServiceStatus incomeStatus = incomeService.storeIncome(income);
			if (incomeStatus.isSuccess() == false) {
				throw new IncomeException("Company failed to store income",this.company.getCompanyId(), this.company.getCompanyName());
			}
			
			System.err.println(coupon);
			System.err.println(couponRepository.findById(coupon.getCouponId()));
			System.out
					.println("Company " + this.company.getCompanyName() + " added new coupon: " + coupon.getCouponId());
			serviceStatus.setSuccess(true);
			serviceStatus.setMessage(
					"success, Company " + this.company.getCompanyName() + " added new coupon: " + coupon.getCouponId() + ". Your account was charged for 100.0 NIS");
			return serviceStatus;

		} catch (AmountException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (EndDatePassedException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (CouponExistsException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (IdExsistsException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (IncomeException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Company failed to add coupon. couponId: " + coupon.getCouponId());
		}

		return serviceStatus;
	}

	@Transactional
	@Override
	public ServiceStatus removeCoupon(long couponId) throws Exception {

		try {

			if (!couponRepository.existsById(couponId)) {
				throw new ObjectNotFoundException("couponId does not exist in system. ", this.company.getCompanyId(),
						this.clientType, couponId);
			}

			if (!couponRepository.existsByCouponIdAndCompanyCompanyId(couponId, this.company.getCompanyId())) {
				throw new NotBelongsException(
						"Company failed to remove coupon - this coupon not belongs to this company. ",
						this.company.getCompanyId(), this.clientType.toString(), couponId);
			}
			
			if (!couponRepository.findById(couponId).get().getCustomers().isEmpty()) {
				throw new RemovingCouponException("Company failed to remove coupon - this coupon already was pruchased by customers in system. ",
						this.company.getCompanyId(),  couponId);
			}

			couponRepository.deleteById(couponId);
			List<Coupon> couponsList = couponRepository.findAllByCompanyCompanyId(this.company.getCompanyId());
			this.company.setCoupons(couponsList);
			companyRepository.save(this.company);

			System.out.println("Company " + this.company.getCompanyName() + " removed coupon: " + couponId);
			serviceStatus.setSuccess(true);
			serviceStatus
					.setMessage("success, Company " + this.company.getCompanyName() + " removed coupon: " + couponId);
			return serviceStatus;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (NotBelongsException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (RemovingCouponException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Compnay failed to remove coupon. couponId: " + couponId);
		}

		return serviceStatus;
	}

	/**
	 * There is issue with MySQL - lost day of end date any time there is update
	 **/

	@Transactional
	@Override
	public ServiceStatus updateCoupon(long couponId, String newEndDate, double newPrice) throws Exception {

		try {

			if (!couponRepository.existsById(couponId)) {
				throw new ObjectNotFoundException("couponId does not exist in system", this.company.getCompanyId(),
						this.clientType, couponId);
			}

			if (!couponRepository.existsByCouponIdAndCompanyCompanyId(couponId, this.company.getCompanyId())) {
				throw new NotBelongsException(
						"Company failed to update coupon - this coupon not belongs to this company. ",
						this.company.getCompanyId(), this.clientType.toString(), couponId);
			}

			Coupon coupon = couponRepository.findById(couponId).get();
			coupon.setStartDate(coupon.getStartDate());
			coupon.setEndDate(DateConverterUtil.convertStringDate(newEndDate));
			coupon.setPrice(newPrice);

//			System.err.println(coupon.getStartDate());
//			System.err.println(couponRepository.findById(couponId).get().getStartDate());
//			System.err.println(coupon.getEndDate());
//			System.err.println(couponRepository.findById(couponId).get().getEndDate());
//			System.err.println(LocalDate.now());

			if (coupon.getEndDate().isBefore(LocalDate.now())) {
				throw new EndDatePassedException("Company failed to update coupon - the end date already passed. ",
						newEndDate, coupon.getCouponId(), this.company.getCompanyId());
			}

			couponRepository.save(coupon);
			Income income = new Income(this.company.getCompanyName(), this.company.getCompanyId(), LocalDate.now(), IncomeType.COMPANY_UPDATE_COUPON, 10);
			ServiceStatus incomeStatus = incomeService.storeIncome(income);
			if (incomeStatus.isSuccess() == false) {
				throw new IncomeException("Company failed to store income",this.company.getCompanyId(), this.company.getCompanyName());
			}
			
			System.out.println("Company " + this.company.getCompanyName() + " updated coupon: " + coupon.getCouponId());
			serviceStatus.setSuccess(true);
			serviceStatus.setMessage(
					"success, Company " + this.company.getCompanyName() + " updated coupon: " + coupon.getCouponId()+ ". Your account was charged for 10.0 NIS");
			return serviceStatus;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (NotBelongsException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (EndDatePassedException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		}catch (IncomeException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		}  catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Company failed to update coupon. couponId: " + couponId);
		}

		return serviceStatus;
	}

	@Transactional
	@Override
	public Company getCompany() throws Exception {

		try {

			if (!companyRepository.existsById(this.company.getCompanyId())) {
				throw new ObjectNotFoundException("company does not exist in system", this.company.getCompanyId(),
						this.clientType, this.company.getCompanyId());
			}

			Company company = companyRepository.findById(this.company.getCompanyId()).get();
			return company;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Company failed to get company details. companyId: " + this.company.getCompanyId());
		}
		return null;
	}

	@Transactional
	@Override
	public Coupon getCoupon(long couponId) throws Exception {
		try {

			if (!couponRepository.existsById(couponId)) {
				throw new ObjectNotFoundException("couponId does not exist in system", this.company.getCompanyId(),
						this.clientType, couponId);
			}

			if (!couponRepository.existsByCouponIdAndCompanyCompanyId(couponId, this.company.getCompanyId())) {
				throw new NotBelongsException(
						"Company failed to get coupon - this coupon not belongs to this company. ",
						this.company.getCompanyId(), this.clientType.toString(), couponId);
			}

			Coupon coupon = couponRepository.findById(couponId).get();
			coupon.setStartDate(coupon.getStartDate().plusDays(1));
			coupon.setEndDate(coupon.getEndDate().plusDays(1));
			return coupon;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (NotBelongsException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Company failed to get coupon data. companyId: " + this.company.getCompanyId());
		}

		return null;
	}

	@Transactional
	@Override
	public List<Coupon> getAllCoupons() throws Exception {
		try {

			List<Coupon> coupons = couponRepository.findAllByCompanyCompanyId(this.company.getCompanyId());

			if (coupons.isEmpty()) {
				throw new NoDetailsFoundException(
						"Company " + this.company.getCompanyId() + " failed to get all coupons - no details found",
						this.company.getCompanyId(), this.clientType);
			}
			
			for(Coupon coupon : coupons) {
				coupon.setStartDate(coupon.getStartDate().plusDays(1));
				coupon.setEndDate(coupon.getEndDate().plusDays(1));
			}

			return coupons;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Company failed to get coupons data. companyId: " + this.company.getCompanyId());
		}
		return null;
	}

	@Transactional
	@Override
	public List<Coupon> getAllCouponsByType(String typeName) throws Exception {
		try {

			CouponType couponType = CouponTypeConverter.convertStringToType(typeName);

			List<Coupon> coupons = couponRepository.findAllByCompanyCompanyIdAndType(this.company.getCompanyId(),
					couponType);

			if (coupons.isEmpty()) {
				throw new NoDetailsFoundException(
						"Company " + this.company.getCompanyId()
								+ " failed to get all coupons by type - no details found",
						this.company.getCompanyId(), this.clientType);
			}
			
			for(Coupon coupon : coupons) {
				coupon.setStartDate(coupon.getStartDate().plusDays(1));
				coupon.setEndDate(coupon.getEndDate().plusDays(1));
			}

			return coupons;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Company failed to get coupons data by Type. companyId: " + this.company.getCompanyId()
					+ " couponType: " + typeName);
		}

		return null;
	}

	@Transactional
	@Override
	public List<Coupon> getAllCouponsByPrice(double priceTop) throws Exception {

		try {

			List<Coupon> coupons = couponRepository
					.findAllByCompanyCompanyIdAndPriceLessThanEqual(this.company.getCompanyId(), priceTop);

			if (coupons.isEmpty()) {
				throw new NoDetailsFoundException(
						"Company " + this.company.getCompanyId()
								+ " failed to get all coupons by price - no details found",
						this.company.getCompanyId(), this.clientType);
			}
			
			for(Coupon coupon : coupons) {
				coupon.setStartDate(coupon.getStartDate().plusDays(1));
				coupon.setEndDate(coupon.getEndDate().plusDays(1));
			}

			return coupons;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Company failed to get coupons data by Price. companyId: " + this.company.getCompanyId()
					+ " priceTop: " + priceTop);
		}

		return null;
	}

	@Transactional
	@Override
	public List<Coupon> getAllCouponsByDate(String untilDate) throws Exception {
		try {

			List<Coupon> coupons = couponRepository.findAllByCompanyCompanyIdAndEndDateLessThanEqual(
					this.company.getCompanyId(), DateConverterUtil.convertStringDate(untilDate));

			if (coupons.isEmpty()) {
				throw new NoDetailsFoundException(
						"Company " + this.company.getCompanyId()
								+ " failed to get all coupons by date - no details found",
						this.company.getCompanyId(), this.clientType);
			}
			
			for(Coupon coupon : coupons) {
				coupon.setStartDate(coupon.getStartDate().plusDays(1));
				coupon.setEndDate(coupon.getEndDate().plusDays(1));
			}

			return coupons;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Company failed to get coupons data by Date. companyId: " + this.company.getCompanyId()
					+ " untilDate: " + untilDate);
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