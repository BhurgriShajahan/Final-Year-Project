package com.tms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tms.entities.ContactUs;
import com.tms.entities.User;
import com.tms.repo.ContactUsRepo;
import com.tms.repo.UserRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeConroller {

	// @Autowired
	// BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;

	@Autowired
	ContactUsRepo contactUsRepo;

	@Autowired
	UserRepo repo;

	// Home / Main portions
	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("currentPage", "home");
		return "home";

	}

	// About Us
	@GetMapping("/aboutus")
	public String aboutUs(Model model) {
		model.addAttribute("currentPage", "aboutus");
		return "aboutus";
	}

	// Contact us
	@GetMapping("/contactus")
	public String contactUs(@ModelAttribute ContactUs contactUs, Model model) {
		model.addAttribute("contactUs", contactUs);
		model.addAttribute("currentPage", "contactus");
		return "contactus";
	}

	// Contact Us Process
	@PostMapping("/contactUsProcess")
	public String contactUsProcess(@Valid @ModelAttribute ContactUs contactUs,
			BindingResult result,
			HttpSession session,
			Model model) throws Exception {
		try {

			if (result.hasErrors()) {
				System.out.println(result);
				return "contactus";
			}

			session.setAttribute("msg",
					new com.tms.messages.Message("Form has been submited Successfully", "alert-success"));
			model.addAttribute("contactUs", contactUs);

			this.contactUsRepo.save(contactUs);

		} catch (Exception e) {
			System.out.println("Sorry..Somthing Wrong.." + e);
		}
		return "redirect:/contactus";

	}

	// Login
	@GetMapping("/slogin")
	public String studentLogin() {
		return "slogin";

	}

	// Home Main portion -- Student sign Up form
	@GetMapping("/sSignup")
	public String studentSignup(@ModelAttribute User user, Model model) {
		model.addAttribute("user", user);
		return "student/signup";

	}

	// student SignupProcess
	// Home Main portion -- Student sign Up form process
	@PostMapping("/sSignupProcess")
	public String studentSignUpProccess(@Valid @ModelAttribute User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) throws Exception {
		try {

			if (result.hasErrors()) {
				System.out.println(result);
				return "student/signup";
			}

			if (agreement == false) {
				throw new Exception(" Please accept terms and conditions..");
			}

			user.setRole("ROLE_STUDENT");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword().trim()));
			user.setActive(true);
			user.setImage("contact.png");
			user.setDegree("null");
			user.setExperience("null");
			user.setMarital("No");
			user.setQualification("Student");

			session.setAttribute("message", new com.tms.messages.Message("Registration Successfuly", "alert-success"));

			repo.save(user);
			model.addAttribute("user", user);
			return "redirect:/sSignup";
		}

		catch (DataIntegrityViolationException ex) {
			session.setAttribute("message", new com.tms.messages.Message(
					"Register failed " + user.getEmail() + " this email is already exist!", "alert-danger"));
		}

		catch (Exception e) {
			System.out.println(e);

			model.addAttribute("user", user);
			session.setAttribute("message",
					new com.tms.messages.Message("Something went wrong" + e.getMessage(), "alert-danger"));
		}

		return "student/signup";
	}

	// End student signUp Process method

	// Teacher SignUp
	// Home Main portion -- sign Up form

	@GetMapping("/tSignup")
	public String teacherSignup(@ModelAttribute User user, Model model) {
		model.addAttribute("user", user);

		return "teacher/signup";
	}

	// Teacher SignupProcess
	// Home Main portion -- Teacher sign Up form process
	@PostMapping("/tSignupProcess")
	public String teacherSignUpProccess(@Valid @ModelAttribute User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {

			if (result.hasErrors()) {
				System.out.println(result);
				return "/teacher/signup";
			}

			if (!agreement) {
				throw new Exception(" Please accept terms and conditions..");
			} else {
				user.setRole("ROLE_TEACHER");
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword().trim()));
				user.setActive(true);
				user.setImage("contact.png");
				user.setFname("null");
				user.setGoing("any");
				user.setStanderd("any");
				user.setSubject("any");
				user.setTutorPreferred("any");
				user.setTuitionType("any");

				session.setAttribute("msge", new com.tms.messages.Message("Registration Successfuly", "alert-success"));

				repo.save(user);

				model.addAttribute("user", user);

				return "redirect:/tSignup";
			}
		}

		catch (DataIntegrityViolationException ex) {
			session.setAttribute("msge", new com.tms.messages.Message(
					"Register failed " + user.getEmail() + " this email is already exist!", "alert-danger"));
		}

		catch (Exception e) {
			model.addAttribute("user", user);
			session.setAttribute("msge",
					new com.tms.messages.Message("Something went wrong" + e.getMessage(), "alert-danger"));
		}
		// return "redirect:/teacher/tSignup";
		return "/teacher/signup";
	}
	// End teacher signUp Process method

}