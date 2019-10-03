package com.CouponSystemSpring.utils;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.CouponSystemSpring.exception.DailyTaskException;
import com.CouponSystemSpring.model.Coupon;
import com.CouponSystemSpring.repository.CouponRepository;

/**
 * @author Tal Yamin
 *
 */

/*
 * DailyCouponExperationTask extends Thread class (implements runnable). The
 * thread is implemented by the CouponSystem. It's purpose is to set the coupon
 * system daily thread operation. The Task is to remove from DB any coupon which
 * is end date passed, coupons which expired.
 */

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DailyCouponExpirationTask implements Runnable {

	/* boolean value for Thread running activation */
	private boolean b = true;

	/* DAO objects in order to proceed the task of removing coupons */

	@Autowired
	private CouponRepository couponRepository;

	/* Thread object for this task */
	private Thread taskThread;

	/* Empty CTOR for DailyCouponExpirationTask */
	public DailyCouponExpirationTask() {

	}

	/*
	 * Start Task method: Receive new Thread object with "this" runnable. start() to
	 * thread is activated.
	 */
	public void startTask() throws Exception {
		try {

			if (Thread.activeCount() < 20) {
				System.err.println(Thread.activeCount());
				taskThread = new Thread(this);
				taskThread.start();
				System.out.println("Daily Coupon Expiration Task starting now");
				System.out.println(Thread.activeCount());
			}

		} catch (Exception e) {
			throw new DailyTaskException("Daily Coupon Expiration Task failed starting");
		}
	}

	/*
	 * Defines the DailyCouponExperationTask activity and puts the Thread to sleep
	 * for 24 hours. Sets the DailyCouponExperationTask thread mechanism. The
	 * DailyCouponExperationTask will check the Coupon table in DB and change to not
	 * active any expired coupon based on its end date. Once it finishes the
	 * activations, it will get list of coupons which not active. any coupon in this
	 * list, will be transfer to Expired_Coupon table and will be removed from
	 * Company_Coupon, Customer_Coupon and Coupon. In addition, it throws an
	 * DailyTaskException in case not all expired coupons were removed successfully
	 * or in case it was unable to complete it's task.
	 */
	@Override
	public void run() {

		try {
			System.out.println("Daily Coupon Expiration Task running now");

			while (b) {
				List<Coupon> coupons = couponRepository.findAllByEndDateLessThan(LocalDate.now());

				/* Change expired coupon to not active */
				for (Coupon c : coupons) {
					c.setActive(false);
					couponRepository.save(c);
				}

				/*
				 * should be 60*60*24*1000 = 24 hrs in milliseconds. But for test only i've used
				 * 10000 milliseconds in order to demonstrate it works.
				 */
				taskThread.sleep(10000);
			}

		} catch (Exception e) {
			System.out.println("Daily Coupon Expiration Task failed running"); // there is no option to throw exception
		}

	}

	/*
	 * Stop Task - used to stop DailyCouponExperationTask. This method used in
	 * Shutdown method in CouponSystem. The boolean changed in order to stop thread
	 * running. The thread is interrupted when he finish his running.
	 */
	public void stopTask() throws Exception {
		try {
			b = false;
			if (!taskThread.isAlive()) {
				taskThread.interrupt();
			}
			System.out.println("Daily Coupon Expiration Task stopped");
		} catch (Exception e) {
			throw new DailyTaskException("Daily Coupon Expiration Task failed stoping");
		}
	}

}
