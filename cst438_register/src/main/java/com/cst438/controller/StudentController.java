package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentController {
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	GradebookService gradebookService;
	
	@GetMapping("/student")
	public Student getStudent( @RequestParam("email") String email ) {
		System.out.println("/student called.");
		
		Student student = studentRepository.findByEmail(email);
		if (student != null) {
			System.out.println("/student GET result: "+student.getName()+" "+student.getStudent_id());
			return student;
		} else {
			System.out.println("/student GET unsuccessful! Not found: " + email);
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found. " );
		}
	}
	
	@PostMapping("/student")
	@Transactional
	public Student postStudent ( @RequestParam("email") String email, @RequestParam("name") String name ) {
		System.out.println("/student called.");
		
		Student student = studentRepository.findByEmail(email);
		if (student != null) {
			System.out.println("/student POST unsuccessful! Already exists: "+email);
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student already exists. " );
		}else if (email == null || name == null) {
			System.out.println("/student POST unsuccessful! Bad parameters.");
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Bad/missing parameters. " );
		} else {
			Student s = new Student();
			s.setEmail(email);
			s.setName(name);
			
			studentRepository.save(s);
			
			System.out.println("/student POST successful! Registered: " + email + " " + name);
			
			return s;
		}
	}
	
	@PostMapping("/student/holds")
	@Transactional
	public void updateStudentHoldStatus ( @RequestParam ("email") String student_email, @RequestParam("hold") boolean holdStatus ) {
		System.out.println("/student called.");
		
		Student student = studentRepository.findByEmail(student_email);
		
		if (student == null) {
			System.out.println("/student/hold POST unsuccessful! Not found: " + student_email);
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found. " );
		} else {
			if (holdStatus == true) {
				student.setStatus("HOLD");
				student.setStatusCode(1);
			} else {
				student.setStatus("CLEAR");
				student.setStatusCode(0);
			}
			
			System.out.println("/student POSTed student hold status: " + student.getStatus() + " " + student.getStatusCode());
		}
	}
}
