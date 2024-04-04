package com.tms.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tms.entities.ContactUs;
import com.tms.entities.Course;
import com.tms.entities.Notification;
import com.tms.entities.User;
import com.tms.messages.Message;
import com.tms.messages.Myservices;
import com.tms.repo.ContactUsRepo;
import com.tms.repo.CourseRepository;
import com.tms.repo.NotificationRepo;
import com.tms.repo.UserRepo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	UserRepo userRepo;
	@Autowired
	ContactUsRepo contactUsRepo;

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;
	@Autowired
	Myservices myservices;

	@Autowired
	PasswordEncoder encoder;
	@Autowired
	NotificationRepo notificationRepo;
	@Autowired
	private CourseRepository courseRepository;

	// Common data
	@ModelAttribute("user")
	public void comonData(Principal principal, Model model) {
		String userName = principal.getName();

		User user = userRepo.getUserByUserName(userName);

		model.addAttribute("u", user);
	}

	// ADMIN index
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/index")
	public String index(Model model) {
		String tRole = "ROLE_TEACHER";
		String sRole = "ROLE_STUDENT";
		List<User> allTeachers = this.userRepo.getTeachers(tRole);
		long allT = allTeachers.size();
		List<User> allStudents = this.userRepo.getStudents(sRole);
		long allS = allStudents.size();
		List<ContactUs> allContacts = this.contactUsRepo.findAll();
		long allC = allContacts.size();
		List<Notification> allNotifications = this.notificationRepo.findAll();
		long allNoti = allNotifications.size();
		model.addAttribute("allNoti", allNoti);
		model.addAttribute("allC", allC);
		model.addAttribute("allTeachers", allT);
		model.addAttribute("allStudents", allS);

		return "admin/index";
	}

	// My Profile
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/myProfile")
	public String MyProfile() {

		return "/admin/myProfile";
	}

	// ----------------------
	// Edit Profile ByUserId
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/editProfile/{id}")
	public String editMyProfile(@PathVariable("id") Integer id, User user, Principal principal, Model model) {
		// Update particular user when he is login

		String name = principal.getName();

		User u = userRepo.getUserByName(name);

		if (user.getId() == u.getId()) {

			User updateUser = myservices.updateUser(id, u);

			model.addAttribute("u", updateUser);
			return "/admin/editProfile";
		}
		model.addAttribute("message", "Invalid user with ID " + id + "!");
		return "/admin/pageNotFound";
	}

	// -----------------------
	// Edit Profile Process Form
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/editProfileProcess")
	public String editProfileProcess(@ModelAttribute("u") User user, Model model,
			@RequestParam MultipartFile proImg,
			HttpSession session) {
		try {

			String proImgName = proImg.getOriginalFilename();
			User oldPic = this.userRepo.findById(user.getId()).get();
			if (!proImgName.isEmpty()) {
				user.setImage(proImgName);

				File save = new ClassPathResource("static/images").getFile();
				java.nio.file.Path path = Paths.get(save.getAbsolutePath() + File.separator + proImgName);
				Files.copy(proImg.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			} else {
				user.setImage(oldPic.getImage());
				if (oldPic.getImage().isEmpty()) {
					user.setImage("contact.png");
				}
			}
			// after Update And Save User
			myservices.saveUser(user);

			session.setAttribute("TPUMsg", new Message("Profile Updated", "alert-success"));
		} catch (Exception e) {
			System.out.println("ERROR____!!" + e);
		}

		return "redirect:/admin/myProfile";
	}
	// Edit Profile Process Form End

	// View Contact us / Contacts

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/viewContact")
	public String viewContact(Model model) {
		List<ContactUs> allContacts = contactUsRepo.findAll();

		if (allContacts.isEmpty()) {
			model.addAttribute("records", "Not available");
		} else {
			model.addAttribute("allContacts", allContacts);
		}

		return "admin/viewContact";
	}

	// Delete Contact
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/deleteContact/{id}")
	public String deleteContact(@PathVariable("id") Integer id, Model model) {
		Optional<ContactUs> op = contactUsRepo.findById(id);
		if (op.isPresent()) {
			contactUsRepo.deleteById(id);
		}

		return "redirect:/admin/viewContact";
	}

	// View All Teachers on ADMIN-DashBoard
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/viewTeacher")
	public String viewTeachers(Model model) {
		String s = "ROLE_TEACHER";
		List<User> allTeachers = userRepo.getTeachers(s);

		if (allTeachers != null) {
			model.addAttribute("allTeachers", allTeachers);
		}
		if (allTeachers.isEmpty()) {
			String records = "Not available";
			model.addAttribute("records", records);
		}
		return "/admin/viewTeachers";
	}

	// View All STUDENTS on ADMIN-DashBoard
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/viewStudent")
	public String viewStudents(Model model) {
		String s = "ROLE_STUDENT";
		List<User> allStudents = userRepo.getStudents(s);

		if (allStudents != null) {
			model.addAttribute("allStudents", allStudents);
		}
		if (allStudents.isEmpty()) {
			String records = "Not available";
			model.addAttribute("records", records);
		}
		return "/admin/viewStudent";
	}

	// Delete Teacher
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/deleteTeacher/{id}")
	public String deleteTeacher(@PathVariable("id") Integer id, User user, Model model) {
		try {
			Optional<User> op = userRepo.findById(id);
			// Get Old image from data base..
			User oldPic = this.userRepo.findById(user.getId()).get();
			if (op.isPresent()) {
				// Delete Old file from database..
				File deleteImage = new ClassPathResource("static/images").getFile();
				File getOldImage = new File(deleteImage, oldPic.getImage());
				getOldImage.delete();
				// .......................................................
				userRepo.deleteById(id);
			}
		} catch (Exception e) {
			System.out.println("Exception bro--" + e);
		}
		return "redirect:/admin/viewTeacher";
	}

	// Delete Student
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/deleteStudent/{id}")
	public String deleteStudent(@PathVariable("id") Integer id, User user, Model model) {
		try {
			Optional<User> op = userRepo.findById(id);
			// Get Old image from data base..
			User oldPic = this.userRepo.findById(user.getId()).get();
			if (op.isPresent()) {
				// Delete Old file from database..
				File deleteImage = new ClassPathResource("static/images").getFile();
				File getOldImage = new File(deleteImage, oldPic.getImage());
				getOldImage.delete();
				// ........................................................
				userRepo.deleteById(id);

			}

		} catch (Exception e) {
			System.out.println("Exception delete Student :)" + e);
		}
		return "redirect:/admin/viewStudent";
	}

	// ADD Teacher //Has Role ADMIN

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/addTeacher")
	public String addTeacher(@ModelAttribute User user, Model model) {
		model.addAttribute("user", user);

		return "/admin/addTeacher";
	}

	// Teacher SignupProcess
	// ADMIN DashBoard -- Teacher sign Up form process
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/tSignupProcess")
	public String teacherSignUpProccess(@Valid @ModelAttribute User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {

			if (result.hasErrors()) {
				System.out.println(result);
				return "/admin/addTeacher";
			}

			if (!agreement) {
				throw new Exception(" Please accept terms and conditions!");
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

				model.addAttribute("teacher", user);

				session.setAttribute("masge",
						new com.tms.messages.Message("Registration Successfuly", "alert-success"));

				userRepo.save(user);

				model.addAttribute("user", user);

				return "redirect:/admin/addTeacher";
			}
		}

		catch (DataIntegrityViolationException ex) {
			session.setAttribute("ms", new com.tms.messages.Message(
					"Register failed " + user.getEmail() + " this email is already exist!", "alert-danger"));
		}

		catch (Exception e) {
			model.addAttribute("user", user);
			session.setAttribute("ms",
					new com.tms.messages.Message("Something went wrong" + e.getMessage(), "alert-danger"));
		}
		return "/admin/addTeacher";
	}
	// End teacher signUp Process method

	// ADD STUDENT //Has Role ADMIN
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/addStudent")
	public String addStudent(@ModelAttribute User user, Model model) {
		model.addAttribute("student", user);
		return "/admin/addStudent";
	}

	// student SignupProcess
	// ADMIN dashBoard -- Student sign Up form process
	@PostMapping("/sSignupProcess")
	public String studentSignUpProccess(@Valid @ModelAttribute User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) throws Exception {
		try {

			if (result.hasErrors()) {
				System.out.println(result);
				return "admin/addStudent";
			}
			if (agreement == false) {
				throw new Exception(" Please accept terms and conditions..");
			}

			user.setRole("ROLE_STUDENT");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword().trim()));
			user.setActive(true);
			user.setImage("contact.png");
			user.setDegree("No degree");
			user.setExperience("No Exp");
			user.setMarital("No");
			user.setQualification("Student");

			session.setAttribute("message", new com.tms.messages.Message("Registration Successfuly", "alert-success"));

			userRepo.save(user);
			model.addAttribute("user", user);
			return "redirect:/admin/addStudent";
		}

		catch (DataIntegrityViolationException ex) {
			session.setAttribute("mesag", new com.tms.messages.Message(
					"Register failed " + user.getEmail() + " this email is already exist!", "alert-danger"));
		}

		catch (Exception e) {
			System.out.println(e);

			model.addAttribute("user", user);
			session.setAttribute("mesag",
					new com.tms.messages.Message("Something went wrong" + e.getMessage(), "alert-danger"));
		}

		return "admin/addStudent";
	}

	// End student signUp Process method

	// editTeacher
	// Edit Teacher
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/editTeacher/{id}")
	public String editTeacher(@PathVariable("id") Integer id, User user, Principal principal, Model model) {
		String name = principal.getName();
		User loggedInUser = userRepo.getUserByName(name);

		// Check if the logged-in user has the authority to edit the teacher with the
		// given ID
		if (!id.equals(loggedInUser.getId())) {
			// Retrieve the user with the specified ID
			Optional<User> teacherOptional = userRepo.findById(id);

			if (teacherOptional.isPresent()) {
				User teacherToEdit = teacherOptional.get();

				// Check if the user to be edited has the role "ROLE_TEACHER"
				if ("ROLE_TEACHER".equals(teacherToEdit.getRole())) {
					User update = myservices.updateUser(id, loggedInUser);
					model.addAttribute("user", update);
					return "/admin/editTeacher";
				} else {
					// Redirect if the user to be edited has a role other than "ROLE_TEACHER"
					model.addAttribute("message", "Invalid user with ID " + id + ". Only teacher can be edited.");
					return "/admin/pageNotFound";
				}
			} else {
				// Handle case when the specified teacher ID is not found
				model.addAttribute("message", "Invalid user with ID " + id + "!");
				return "/admin/PageNotFound";
			}
		} else {
			// Redirect if the logged-in user is trying to edit their own profile
			model.addAttribute("message", "Invalid user with ID " + id + "!");
			return "/admin/pageNotFound";
		}
	}

	// editTeacherProcess

	// edit Teacher Process
	// ADMIN DashBoard -- Edit Teacher form process
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/editTeacherProcess")
	public String editTeacherProcess(@Valid @ModelAttribute User user, BindingResult result,
			HttpSession session,
			Model model) {
		try {

			if (result.hasErrors()) {
				System.out.println(result);
				System.out.println("_____________________________________");
				return "/admin/editTeacher";
				// return "editTeacher";

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

				// model.addAttribute("u", user);

				session.setAttribute("masg", new com.tms.messages.Message("Updated Success..", "alert-success"));

				userRepo.save(user);

				return "redirect:/admin/viewTeacher";
			}
		}

		catch (DataIntegrityViolationException ex) {
			session.setAttribute("masg", new com.tms.messages.Message(
					"Register failed " + user.getEmail() + " this email is already exist!", "alert-danger"));
		} catch (Exception e) {
			session.setAttribute("msge",
					new com.tms.messages.Message("Something went wrong" + e.getMessage(), "alert-danger"));
		}
		return "redirect:/admin/viewTeacher";
	}
	// End Edit teacher Process method

	// sendNotification
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/sendNotification")
	public String sendNotification(@ModelAttribute Notification notification, Model model) {
		model.addAttribute("notification", notification);
		return "/admin/sendNotification";
	}

	// sendNotification Process
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/sendNotificationProcess")
	public String sendNotificationProcess(@Valid @ModelAttribute Notification notification, BindingResult result,
			HttpSession httpSession, Model model) throws Exception {
		try {

			if (result.hasErrors()) {
				System.out.println("Sorry All fields are required!!");
				return "/admin/sendNotification";
			}

			model.addAttribute("notification", notification);
			LocalDate localDat = LocalDate.now();
			notification.setDate(localDat);

			myservices.sendNotification(notification);

			httpSession.setAttribute("notiMessage", new Message("Notification sent success .", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/admin/sendNotification";
	}

	// show all notifications
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/allNotifications")
	public String viewAllNotifications(Model model) {
		List<Notification> allNotifications = myservices.getNotification();
		model.addAttribute("allNotifications", allNotifications);
		return "/admin/allNotifications";
	}

	// Delete notifications
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/deleteNotification/{id}")
	public String deleteNotification(@PathVariable Long id, HttpSession httpSession) {
		try {
			myservices.deleteNotification(id);
			httpSession.setAttribute("notiMessage", new Message("Notification deleted successfully.", "alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			httpSession.setAttribute("notiMessage", new Message("Error deleting notification.", "alert-danger"));
			return "/admin/allNotifications";
		}

		return "redirect:/admin/allNotifications";
	}

	// edit notifications
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/editNotification/{id}")
	public String showEditNotificationForm(@PathVariable Long id, Notification notification, Model model) {

		Notification editNotification = myservices.editNotification(id, notification);

		model.addAttribute("notification", editNotification);
		return "/admin/editNotification";
	}

	// Edit Notification Process
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/editNotificationProcess")
	public String editNotificationProcess(@Valid @ModelAttribute Notification editedNotification, BindingResult result,
			HttpSession httpSession, Model model) {
		try {
			if (result.hasErrors()) {
				System.out.println("Sorry, all fields are required!!------------------" + result);

				return "/admin/editNotification";
			}
			LocalDate localDate = LocalDate.now();
			editedNotification.setDate(localDate);
			this.notificationRepo.save(editedNotification);

			httpSession.setAttribute("notiMessage", new Message("Notification updated successfully.", "alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			httpSession.setAttribute("notiMessage", new Message("Error updating notification.", "alert-danger"));
			System.out.println("Error here.....");
			return "/admin/allNotifications";
		}

		return "redirect:/admin/allNotifications";
	}

	// Edit Student
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/editStudent/{id}")
	public String editStudent(@PathVariable Integer id, Principal principal, Model model) {
		User loggedInUser = this.userRepo.getUserByName(principal.getName());

		// Check if the logged-in user has the authority to edit the user with the given
		// ID
		if (id.equals(loggedInUser.getId())) {
			// Redirect unauthorized users to an appropriate page
			model.addAttribute("message", "User with ID " + id + " not found.");
			return "/admin/pageNotFound";
		}

		Optional<User> userOptional = this.userRepo.findById(id);

		if (userOptional.isPresent()) {
			User userToEdit = userOptional.get();

			// Check if the user to edit has the role "ROLE_STUDENT"
			if (!userToEdit.getRole().equals("ROLE_STUDENT")) {
				model.addAttribute("message", "Invalid user with ID " + id + ". Only students can be edited.");
				return "/admin/pageNotFound";
			}

			model.addAttribute("editStudent", userToEdit);
			return "/admin/editStudent";
		} else {
			// Handle case when user with the given id is not found
			model.addAttribute("message", "Invalid user with ID " + id);
			return "/admin/pageNotFound";
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/editStudentProcess")
	public String editStudentProcess(@ModelAttribute User user, Principal principal, Model model) {

		user.setActive(true);
		user.setAddress("Badin");

		user.setDegree("null");
		user.setExperience("student");
		user.setFname("Ali");
		user.setGender("Male");
		user.setGoing("School");
		user.setImage("contact.png");
		user.setMarital("Single");
		user.setQualification("null");
		user.setRole("ROLE_STUDENT");

		user.setTutorPreferred("null");
		user.setTuitionType("Physical");
		user.setSurname("ali");

		userRepo.save(user);

		return "redirect:/admin/viewStudent";
	}

	// Search Teachers
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/searchTeachers")
	public String searchTeachers(@RequestParam(name = "query", required = false) String query, Model model) {

		List<User> searchResults = userRepo.searchTeachers(query);

		if (searchResults != null) {
			model.addAttribute("searchResults", searchResults);
		}
		// if (query == null || query.isEmpty() || searchResults.isEmpty()) {
		// return "redirect:/admin/viewTeacher";
		// }

		if (searchResults.isEmpty()) {
			model.addAttribute("records", "Search result not  found!");
		}

		return "/admin/searchTeachers";
	}

	// Search Student
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/search")
	public String searchStudents(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
		System.out.println("Search Students method called with keyword: " + keyword);

		String role = "ROLE_STUDENT";

		List<User> searchedStudents = userRepo.searchStudents(keyword);

		if (keyword != null && !keyword.isEmpty()) {
			if (searchedStudents.isEmpty()) {
				String msg = "User Not Found!";
				model.addAttribute("msg", msg);
				System.out.println("User not found ~!!! " + msg);
				return "admin/search";
			} else {
				model.addAttribute("searchedStudents", searchedStudents);
			}
		} else {
			// If no keyword is provided, show all students
			List<User> allStudents = userRepo.getStudents(role);
			model.addAttribute("allStudents", allStudents);
			return "/admin/viewStudent";
		}

		return "/admin/search";
	}

	// Search Student End
	/// Change Password----------------------
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/change-password")
	public String changePassword() {
		return "admin/change-password";
	}

	// Change Password Process-----------
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/change-password-process")
	public String changePassword(
			@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,
			Principal principal,
			HttpSession httpSession, Model model) {

		System.out.println("oldPassword " + oldPassword);
		System.out.println("newPassword " + newPassword);

		User existingUser = userRepo.getUserByName(principal.getName());
		if (newPassword == " " || newPassword.isEmpty()) {
			model.addAttribute("newRequired", "New password required! ");
		}
		if (oldPassword == " " || oldPassword.isEmpty()) {

			model.addAttribute("oldRequired", "Old password required! ");
		}
		if (this.encoder.matches(oldPassword, existingUser.getPassword())) {
			// validation for newPassword
			if (newPassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
				existingUser.setPassword(this.encoder.encode(newPassword.trim()));
				userRepo.save(existingUser);
				httpSession.setAttribute("passMsg", new Message("Your password is successfully changed.", "success"));

			} else {
				model.addAttribute("oldPass", oldPassword);
				model.addAttribute("newPass", newPassword);
				// httpSession.setAttribute("passMsg", new Message("Invalid new password
				// format.", "danger"));
				model.addAttribute("newPassError",
						"Please use at least one uppercase letter, one lowercase letter, one digit, and one special character.");
				return "admin/change-password";

			}
		} else {
			httpSession.setAttribute("passMsg", new Message("Please enter the correct old password!", "danger"));
			model.addAttribute("oldPass", oldPassword);
			model.addAttribute("newPass", newPassword);
			return "admin/change-password";
		}

		return "redirect:/admin/index";
	}

	// Some new

	// method to view all courses
	// View all courses
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/viewCourses")
	public String viewCourses(Model model) {
		List<Course> allCourses = this.courseRepository.findAll();
		model.addAttribute("allCourses", allCourses);
		return "admin/viewCourses";
	}

	// Search Courses
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/searchCourse")
	public String searchCourses(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
		System.out.println("Search  Courses method called with keyword: " + keyword);

		// Search for courses
		List<Course> searchedCourses = this.courseRepository.searchName(keyword);
		model.addAttribute("searchedCourses", searchedCourses);

		return "admin/searchCourseResult";
	}

}