package com.CouponSystemSpring.service;


import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.CouponSystemSpring.exception.CompanyExistsException;
import com.CouponSystemSpring.exception.CustomerExistsException;
import com.CouponSystemSpring.exception.IdExsistsException;
import com.CouponSystemSpring.exception.NoDetailsFoundException;
import com.CouponSystemSpring.exception.ObjectNotFoundException;
import com.CouponSystemSpring.model.Company;
import com.CouponSystemSpring.model.Customer;
import com.CouponSystemSpring.repository.CompanyRepository;
import com.CouponSystemSpring.repository.CouponRepository;
import com.CouponSystemSpring.repository.CustomerRepository;
import com.CouponSystemSpring.utils.ServiceStatus;

@Service
public class AdminServiceImpl implements AdminService, CouponClient {

	@Resource
	private CompanyRepository companyRepository;

	@Resource
	private CustomerRepository customerRepository;

	@Resource
	private CouponRepository couponRepository;

	private ClientType clientType = ClientType.ADMIN;

	@Resource
	private ServiceStatus serviceStatus;

	@Override
	@Transactional
	public ServiceStatus addCompany(Company company) throws Exception {

		// need to check ID too cause it is save so it recognize it as update
		// companyRepository.existsById(id);

		try {

			if (companyRepository.existsById(company.getCompanyId())) {
				throw new IdExsistsException("Admin failed to add company - this id already in use ",
						company.getCompanyId());
			} else {

				if (companyRepository.existsByCompanyName(company.getCompanyName())) {
					throw new CompanyExistsException("Admin failed to add company - this company already exists: ",
							company.getCompanyName());
				} else {
					companyRepository.save(company);
					System.out.println("Admin added new company: " + company.getCompanyId());
					serviceStatus.setSuccess(true);
					serviceStatus.setMessage("success, Admin added new company: " + company.getCompanyId());
					return serviceStatus;
				}
			}

		} catch (CompanyExistsException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (IdExsistsException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Admin failed to add company. companyId: " + company.getCompanyId());
		}
		return serviceStatus;
	}

	@Override
	@Transactional
	public ServiceStatus removeCompany(long companyId) throws Exception {

		try {

			if (!companyRepository.existsById(companyId)) {
				throw new ObjectNotFoundException("companyId does not exist in system", 0, this.clientType, companyId);
			}

			couponRepository.deleteByCompanyCompanyId(companyId);
			companyRepository.deleteById(companyId);
			serviceStatus.setSuccess(true);
			serviceStatus.setMessage("success, Admin removed company successfully. companyId: " + companyId);
			System.out.println("Admin removed company successfully. companyId: " + companyId);
			return serviceStatus;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to remove company. companyId: " + companyId);
		}
		return serviceStatus;
	}

	@Override
	@Transactional
	public ServiceStatus updateCompany(long companyId, String newCompanyPassword, String newCompanyEmail)
			throws Exception {

		try {

			if (!companyRepository.existsById(companyId)) {
				throw new ObjectNotFoundException("companyId does not exist in system. ", 0, this.clientType,
						companyId);
			}

			Company companyToUpdate = companyRepository.findById(companyId).get();
			companyToUpdate.setCompanyPassword(newCompanyPassword);
			companyToUpdate.setCompanyEmail(newCompanyEmail);
			companyRepository.save(companyToUpdate);
			serviceStatus.setSuccess(true);
			serviceStatus.setMessage("success, Admin updated company successfully. companyId: " + companyId);
			System.out.println("Admin updated company successfully. companyId: " + companyId);
			return serviceStatus;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to update company. companyId: " + companyId);
		}
		return serviceStatus;
	}

	@Override
	@Transactional
	public List<Company> getAllCompanies() throws Exception {

		try {

			if (companyRepository.findAll().isEmpty()) {
				throw new NoDetailsFoundException("Admin failed to get all companies - no details found", 0,
						this.clientType);
			}

			List<Company> companies = companyRepository.findAll();
			System.out.println(companies);
			return companies;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to get all companies");
		}

		return null;
	}

	@Override
	@Transactional
	public Company getCompany(long companyId) throws Exception {

		try {

			if (!companyRepository.existsById(companyId)) {
				throw new ObjectNotFoundException("companyId does not exist in system. ", 0, this.clientType,
						companyId);
			}

			Company company = companyRepository.findById(companyId).get();
			System.out.println(company);
			return company;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to get a company. companyId: " + companyId);
		}

		return null;
	}

	@Override
	@Transactional
	public ServiceStatus addCustomer(Customer customer) throws Exception {

		try {
			if (customerRepository.existsById(customer.getCustomerId())) {
				throw new IdExsistsException("Admin failed to add customer - this id already in use ",
						customer.getCustomerId());
			} else {

				if (customerRepository.existsByCustomerName(customer.getCustomerName())) {
					throw new CustomerExistsException("Admin failed to add customer - this customer already exists: ",
							customer.getCustomerName());
				} else {
					customerRepository.save(customer);
					System.out.println("Admin added new custoemr: " + customer.getCustomerId());
					serviceStatus.setSuccess(true);
					serviceStatus.setMessage("success, Admin added new custoemr: " + customer.getCustomerId());
					return serviceStatus;
				}
			}

		} catch (CustomerExistsException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (IdExsistsException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to add customer. customerId: " + customer.getCustomerId());
		}
		return serviceStatus;
	}

	@Override
	@Transactional
	public ServiceStatus removeCustomer(long customerId) throws Exception {

		try {

			if (!customerRepository.existsById(customerId)) {
				throw new ObjectNotFoundException("customerId does not exist in system. ", 0, this.clientType,
						customerId);
			}

			customerRepository.deleteById(customerId);
			serviceStatus.setSuccess(true);
			serviceStatus.setMessage("success, Admin removed customer successfully. customerId: " + customerId);
			System.out.println("Admin removed customer successfully. companyId: " + customerId);
			return serviceStatus;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to remove customer.  customerId: " + customerId);
		}
		return serviceStatus;
	}

	@Override
	@Transactional
	public ServiceStatus updateCustomer(long customerId, String newCustomerPassword) throws Exception {
		try {

			if (!customerRepository.existsById(customerId)) {
				throw new ObjectNotFoundException("customerId does not exist in system. ", 0, this.clientType,
						customerId);
			}

			Customer customerToUpdate = customerRepository.findById(customerId).get();
			customerToUpdate.setCustomerPassword(newCustomerPassword);
			customerRepository.save(customerToUpdate);
			serviceStatus.setSuccess(true);
			serviceStatus.setMessage("success, Admin updated customer successfully. customerId: " + customerId);
			System.out.println("Admin updated customer successfully. companyId: " + customerId);
			return serviceStatus;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
			serviceStatus.setSuccess(false);
			serviceStatus.setMessage(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to update customer. customerId: " + customerId);
		}
		return serviceStatus;
	}

	@Override
	@Transactional
	public List<Customer> getAllCustomers() throws Exception {

		try {

			if (customerRepository.findAll().isEmpty()) {
				throw new NoDetailsFoundException("Admin failed to get all customers - no details found", 0,
						this.clientType);
			}

			List<Customer> customers = customerRepository.findAll();
			System.out.println(customers);
			return customers;

		} catch (NoDetailsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to get all customers");
		}

		return null;
	}

	@Override
	@Transactional
	public Customer getCustomer(long customerId) throws Exception {
		try {

			if (!customerRepository.existsById(customerId)) {
				throw new ObjectNotFoundException("customerId does not exist in system. ", 0, this.clientType,
						customerId);
			}

			Customer customer = customerRepository.findById(customerId).get();
			System.out.println(customer);
			return customer;

		} catch (ObjectNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			throw new Exception("Admin failed to get a customer. customerId: " + customerId);
		}

		return null;
	}

	@Override
	public CouponClient login(String name, String password, ClientType clientType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}