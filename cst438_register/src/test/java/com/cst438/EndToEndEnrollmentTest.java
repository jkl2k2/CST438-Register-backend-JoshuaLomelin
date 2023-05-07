package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Enrollment;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@SpringBootTest
public class EndToEndEnrollmentTest {

	public static final String FIREFOX_DRIVER_FILE_LOCATION = "C:/Users/joshu/Desktop/CST438/geckodriver.exe";

	public static final String URL = "http://localhost:3000";

	public static final String TEST_USER_NAME = "Johnny Test";
	
	public static final String TEST_USER_EMAIL = "jtest@csumb.edu";

	public static final int SLEEP_DURATION = 1000; // 1 second.

	/*
	 * When running in @SpringBootTest environment, database repositories can be used
	 * with the actual database.
	 */
	
	@Autowired
	StudentRepository studentRepository;

	/*
	 * Student add course TEST_COURSE_ID to schedule for 2021 Fall semester.
	 */
	
	@Test
	public void addEnrollmentTest() throws Exception {
		
		/*
		 * if student is already enrolled, then delete the enrollment.
		 */
		
		Student x = null;
		do {
			x = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (x != null)
				studentRepository.delete(x);
		} while (x != null);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.gecko.driver", FIREFOX_DRIVER_FILE_LOCATION);
		WebDriver driver = new FirefoxDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			
			WebElement we = driver.findElement(By.id("add-student-button"));
			we.click();

			// enter course no and click Add button
			
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.xpath("//button[@id='add']")).click();			
			Thread.sleep(SLEEP_DURATION);
			
			Student student = studentRepository.findByEmail(TEST_USER_EMAIL);
			assertNotNull(student, "Student info not found in database.");

		} catch (Exception ex) {
			throw ex;
		} finally {

			// clean up database.
			
			Student student = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (student != null)
				studentRepository.delete(student);

			driver.quit();
		}

	}
	
}
