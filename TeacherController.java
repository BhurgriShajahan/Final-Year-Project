package com.tms.controllers;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.tms.entities.ContactUs;
import com.tms.entities.Contacts;
import com.tms.entities.Course;
import com.tms.entities.Notification;
import com.tms.entities.User;
import com.tms.messages.Message;
import com.tms.messages.Myservices;
import com.tms.repo.ContactRepo;
import com.tms.repo.ContactUsRepo;
import com.tms.repo.CourseRepository;
import com.tms.repo.MessageRepo;
import com.tms.repo.UserRepo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

	@Autowired
	PasswordEncoder encoder;
	@Autowired
	UserRepo userRepo;
	@Autowired
	ContactUsRepo contactUsRepo;
	@Autowired
	MessageRepo messageRepo;
	@Autowired
	Myservices myservices;
	// Common data
	@Autowired
	ContactRepo contactRepo;
	private User user;
	@Autowired
	private CourseRepository courseRepository;
	int uId = 0;

	@ModelAttribute
	public void comonData(Principal principal, Model model) {
		String userName = principal.getName();

		user = userRepo.getUserByUserName(userName);

		model.addAttribute("user", user);

		uId = user.getId();
	}

	// Teacher index / home
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/index")
	public String home(Model model) {

		List<Notification> getAllNotification = myservices.getNotification();
		if (getAllNotification.size() > 0) {
			model.addAttribute("getAllNotification", getAllNotification);
		}
		if (getAllNotification.size() == 0) {
			model.addAttribute("noNotifications", "No Notifications");
		}
		return "teacher/index";
	}

	// View All student On Teacher dash board
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/viewStudents")
	public String viewStudents(Model model) {
		String role = "ROLE_STUDENT";
		List<User> allStudents = userRepo.getStudents(role);
		if (allStudents != null) {
			model.addAttribute("allStudents", allStudents);
		}
		if (allStudents.isEmpty()) {
			String records = "Not availble";
			model.addAttribute("records", records);
		}
		return "teacher/viewStudents";
	}

	// Contact with ADMIN
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/adminContact")
	public String adminContact(@ModelAttribute("adminContact") ContactUs contactUs, Model model) {
		model.addAttribute("adminContact", contactUs);
		return "teacher/adminContact";
	}

	// Contact with ADMIN
	@PreAuthorize("hasRole('TEACHER')")
	@PostMapping("/adminContactProcess")
	public String contactUsProcess(@Valid @ModelAttribute("adminContact") ContactUs contactUs,
			BindingResult result,
			HttpSession session,
			Model model) throws Exception {
		try {
			// On the way----------

			if (result.hasErrors()) {
				System.out.println(result);
				return "teacher/adminContact";
			}

			session.setAttribute("adminContactMessage",
					new com.tms.messages.Message("Form has been submited Successfully", "alert-success"));

			model.addAttribute("adminContact", contactUs);

			this.contactUsRepo.save(contactUs);
			return "redirect:/teacher/adminContact";
		} catch (Exception e) {
			System.out.println("Sorry..Somthing Wrong.." + e);
			model.addAttribute("adminContact", contactUs);
		}
		return "redirect:/teacher/adminContact";
	}
	// Teacher My Profile

	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/myProfile")
	public String myProfile(Model model) {
		return "/teacher/myProfile";
	}

	// Edit MyProfile
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/editProfile/{id}")
	public String editMyProfile(@PathVariable("id") int id, User user, Principal principal, Model model) {
		String name = principal.getName();
		User userByName = userRepo.getUserByName(name);
		if (userByName.getId() != user.getId()) {
			myservices.updateTeacherProfile(id, user);
			model.addAttribute("user", userByName);
		}

		return "/teacher/editMyProfile";
	}

	// Edit myProfile process
	@PreAuthorize("hasRole('TEACHER')")
	@PostMapping("/editProfileProcess")
	public String editProfileProcess(@ModelAttribute User user, Model model,
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
			// Save User
			myservices.saveUser(user);

			session.setAttribute("TPUMsg", new Message("Profile Updated", "alert-success"));
		} catch (Exception e) {
			System.out.println("ERROR____!!" + e);
		}
		return "redirect:/teacher/myProfile";
	}

	// END Edit Profile Form Process

	// view Notification
	@PreAuthorize("hasRole('TEACHER')")
	@ModelAttribute
	public String viewNotification(Model model) {
		List<Notification> getAllNotification = myservices.getNotification();

		model.addAttribute("getAllNotification", getAllNotification);

		return "/teacher/viewNotification";
	}
	// View Notification ENd

	// saveContacts

	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/saveContacts")
	public String saveContacts() {
		return "/teacher/saveContacts";
	}
	// saveContactProcess

	@PreAuthorize("hasRole('TEACHER')")
	@PostMapping("/saveContactProcess")
	public String saveContactProcess(@ModelAttribute Contacts contacts, Principal principal, HttpSession httpSession,
			Model model) {
		String user = principal.getName();

		User getUser = userRepo.getUserByName(user);

		contacts.setUser(getUser);
		contacts.setImage("contactpic.png");
		if (contacts.getAbout().isEmpty()) {
			contacts.setAbout("-");
		}

		contactRepo.save(contacts);

		httpSession.setAttribute("saveContactmsg", new Message("Your Contact saved..", "alert-success"));
		System.out.println("Contact save success....");

		return "redirect:/teacher/viewContacts";
	}

	// End Save Contacts process form

	// Search Contacts and view all contacts
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/viewContacts")
	public String viewContacts(Principal principal, Model model,
			@RequestParam(name = "keyword", required = false) String keyword) {
		String userName = principal.getName();
		User user = userRepo.getUserByName(userName);

		List<Contacts> allContactsByUser;

		if (keyword != null && !keyword.isEmpty()) {
			// Search for contacts based on the keyword
			allContactsByUser = this.contactRepo.searchContactsByKeyword(user.getId(), keyword);
			model.addAttribute("searchKeyword", keyword);
		} else {
			// Retrieve all contacts for the user
			allContactsByUser = contactRepo.getAllContactsByUser(user.getId());
		}

		model.addAttribute("allContacts", allContactsByUser);

		if (allContactsByUser.isEmpty()) {
			String emptyList = "Contact not found!";
			model.addAttribute("emptyContact", emptyList);
		}

		return "/teacher/viewContacts";
	}

	// viewContacts End

	// Delete Contact
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/deleteContact/{id}")
	public String deleteContact(@PathVariable("id") long id, @ModelAttribute Contacts cImage, Principal principal,
			Model model) throws IOException {
		String userName = principal.getName();

		User user = userRepo.getUserByUserName(userName);

		Contacts contact = contactRepo.getReferenceById(id);

		if (user.getId() == contact.getUser().getId()) {
			contactRepo.deleteById(id);
		} else {
			System.out.println("Sorry");
			return "teacher/pageNotFound";
		}
		return "redirect:/teacher/viewContacts";
	}
	// Delete Contact End

	// editContact

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/editContact/{id}")
	public String editContacts(@PathVariable("id") Long id, Principal principal, Contacts contacts, Model model) {
		String userName = principal.getName();
		User user = userRepo.getUserByName(userName);
		Contacts contact = contactRepo.getReferenceById(id);

		if (user.getId() == contact.getUser().getId()) {

			Optional<Contacts> findById = contactRepo.findById(id);
			contact = findById.get();
			model.addAttribute("updateContact", contact);

		} else {
			return "/teacher/pageNotFound";
		}

		return "/teacher/editContact";
	}
	// End Edit Contact Form

	// Edit Contact Process Form
	@PreAuthorize("hasRole('TEACHER')")
	@PostMapping("/editContactProcess")
	public String editContactProcess(@ModelAttribute Contacts contacts, Principal principal, HttpSession httpSession,
			MultipartFile cImage, Model model) throws Exception {
		try {
			String user = principal.getName();

			User getUser = userRepo.getUserByName(user);

			contacts.setUser(getUser);

			if (contacts.getAbout().isEmpty()) {
				contacts.setAbout("-");
			}
			// Update New file image name...
			String fileName = cImage.getOriginalFilename();

			// Get Old image from data base..
			Contacts oldPic = this.contactRepo.findById(contacts.getId()).get();

			if (!cImage.isEmpty()) {

				// Delete Old file from database..
				File deleteImage = new ClassPathResource("static/images").getFile();
				File getOldImage = new File(deleteImage, oldPic.getImage());
				getOldImage.delete();
				// ........................................................
				// Set contact profile image
				File save = new ClassPathResource("static/images").getFile();
				java.nio.file.Path path = Paths.get(save.getAbsolutePath() + File.separator + fileName);
				Files.copy(cImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				// Set image
				contacts.setImage(fileName);
			} else {
				// aghr image update na karni pary to old pic save he rahy:)
				contacts.setImage(oldPic.getImage());
			}

			contactRepo.save(contacts);

			httpSession.setAttribute("updateContactmsg", new Message("Contact updated..", "alert-success"));
			System.out.println("Contact Updated success....");

		} catch (MaxUploadSizeExceededException e) {

			e.printStackTrace();
			System.out.println("Maximum File Size Required----------" + e);

			return "/teacher/viewContacts";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception------------------------------------->>");
		}
		return "redirect:/teacher/viewContacts";
	}

	// Edit Contacts process form End

	// Show Specific contact detail
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/showContact/{id}")
	public String showContacts(@PathVariable("id") Long id, Principal principal, Contacts contacts, Model model) {
		String userName = principal.getName();
		User user = userRepo.getUserByName(userName);

		Optional<Contacts> findById = contactRepo.findById(id);

		if (findById.isPresent()) {
			Contacts contact = findById.get();

			// Check if the logged-in teacher has access to the specified contact
			if (user.getId() == contact.getUser().getId()) {
				model.addAttribute("userDetail", contact);
			} else {
				return "/teacher/pageNotFound";
			}
		} else {
			// Handle case when the specified contact ID is not found
			model.addAttribute("message", "User with ID " + id + " not found.");
			return "/teacher/pageNotFound";
		}

		return "/teacher/showContact";
	}

	// Search Student
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/search")
	public String searchTeachers(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
		System.out.println("Search Students method called with keyword: " + keyword);

		String role = "ROLE_STUDENT";

		List<User> searchedStudents = userRepo.searchStudents(keyword);

		if (keyword != null && !keyword.isEmpty()) {
			if (searchedStudents.isEmpty()) {
				String msg = "User Not Found!";
				model.addAttribute("msg", msg);
				System.out.println("User not found ~!!! " + msg);
				return "teacher/search";
			} else {
				model.addAttribute("searchedStudents", searchedStudents);
			}
		} else {
			// If no keyword is provided, show all students
			List<User> allTeachers = userRepo.getTeachers(role);
			model.addAttribute("allStudents", allTeachers);
			return "/teacher/viewStudents";
		}

		return "/teacher/search";
	}
	// Search Student End

	// Send Message to Student
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/sendMessage/{id}")
	public String sendMessage(@PathVariable("id") Integer personId, Model model) {
		Optional<User> personOptional = userRepo.findById(personId);

		if (personOptional.isPresent()) {
			User person = personOptional.get();
			model.addAttribute("person", person);
			model.addAttribute("message", new com.tms.entities.Message());
			return "/teacher/sendMessage";
		} else {
			// Handle the case when the teacher with the given ID is not found
			return "/teacher/pageNotFound";
		}
	}

	// Process sending message to student
	@PreAuthorize("hasRole('ROLE_TEACHER')")
	@PostMapping("/sendMessageProcess")
	public String sendMessageToStudentProcess(@ModelAttribute("message") com.tms.entities.Message message,
			Principal principal, Model model, HttpSession httpSession) {
		try {
			String senderName = principal.getName();
			User sender = userRepo.getUserByName(senderName);
			message.setSender(sender.getName());
			LocalDate localDate = LocalDate.now();
			message.setDate(localDate);

			// Get the recipient (student) by ID
			// Replace 'studentId' with the actual student's ID
			User recipient = userRepo.findById(uId).orElse(null);
			// message.setRecipient(recipient);

			if (recipient != null) {
				// Save the message to the recipient's messages list
				recipient.getReceivedMessages().add(message);

				// Save the updated recipient to update the message list
				userRepo.save(recipient);
				model.addAttribute("messageSent", true);
				// Redirect to a URL including the sender's ID
				// return "redirect:/teacher/sendMessage/" + sender.getId();
				httpSession.setAttribute("msge", new Message("Message send success", "alert-success"));
				return "redirect:/teacher/viewStudents";
			} else {
				// Handle the case when the recipient is not found
				System.out.println("ERROR: Recipient not found.");
				model.addAttribute("messageSent", false);
				return "redirect:/teacher/viewStudents"; // Redirect to a default URL if needed
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			model.addAttribute("messageSent", false);
			return "redirect:/teacher/viewStudents"; // Redirect to a default URL if needed
		}
	}

	// Show all messages
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/viewMessages")
	public String viewMessages(Principal principal, Model model) {
		try {
			// Get the currently logged-in user
			String senderName = principal.getName();
			User sender = userRepo.getUserByName(senderName);

			// Retrieve all messages for the logged-in user (sender)
			List<com.tms.entities.Message> messages = sender.getReceivedMessages();

			// Pass the messages to the view
			model.addAttribute("messages", messages);

			return "/teacher/viewMessages";
		} catch (Exception e) {
			// Handle exceptions if any
			System.out.println("ERROR: " + e.getMessage());
			return "/teacher/pageNotFound"; // Redirect to an error page if needed
		}
	}

	// Delete Message
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/deleteMessage/{messageId}")
	public String deleteMessage(@PathVariable("messageId") Long messageId, Principal principal, Model model,
			HttpSession httpSession) {
		try {
			// Get the currently logged-in teacher
			String senderName = principal.getName();
			User sender = userRepo.getUserByName(senderName);

			// Retrieve the message by ID
			Optional<com.tms.entities.Message> messageOptional = messageRepo.findById(messageId);

			if (messageOptional.isPresent()) {
				com.tms.entities.Message message = messageOptional.get();
				this.messageRepo.deleteById(messageId);
				// Delete the message from the database
				this.messageRepo.deleteById(messageId);

				// Delete the message from the sender's list
				sender.getReceivedMessages().remove(message);

				// Save the updated sender to remove the message from the list
				userRepo.save(sender);
				httpSession.setAttribute("msge", new Message("Message delete success..", "alert-success"));

				// Redirect to the viewMessages page after deletion
				model.addAttribute("messageDeleted", true);
				return "redirect:/teacher/viewMessages";
			} else {
				// Message not found, redirect to an error page
				return "/teacher/pageNotFound";
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			model.addAttribute("messageDeleted", false);
			return "redirect:/teacher/viewMessages"; // Redirect to a default URL if needed
		}
	}

	// Create Course
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/createCourse")
	public String createCourse(@ModelAttribute("course") Course course) {
		return "/teacher/createCourse";
	}

	// Process form submission for creating a new course
	@PreAuthorize("hasRole('TEACHER')")
	@PostMapping("/createCourseProcess")
	public String createCourseProcess(@Valid @ModelAttribute("course") Course course,
			BindingResult result,
			HttpSession session,
			Principal principal, // Inject Principal
			@RequestParam("startDateString") String startDateString,
			@RequestParam("endDateString") String endDateString,
			Model model) throws ParseException {
		try {
			if (result.hasErrors()) {
				System.out.println("Errors--------" + result);
				return "/teacher/createCourse";
			}

			LocalDate startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			LocalDate endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			// Set the parsed dates to the course
			course.setStartDate(startDate);
			course.setEndDate(endDate);
			;

			// Get the logged-in teacher
			String username = principal.getName();
			User teacher = this.userRepo.getUserByUserName(username);

			// Set the teacher for the course
			course.setTeacher(teacher);

			// Save the course
			courseRepository.save(course);

			session.setAttribute("courseCreatedMsg", new Message("Course Created Successfully", "success"));
			return "redirect:/teacher/viewCourses";
		} catch (Exception e) {
			// Handle other exceptions if needed
			model.addAttribute("dateParseError", "An error occurred while processing the form");
			return "/teacher/createCourse";
		}
	}

	// method to view all courses
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/viewCourses")
	public String viewCourses(Model model, Principal principal) {
		// Get the currently logged-in teacher
		String username = principal.getName();
		User teacher = this.userRepo.getUserByName(username);

		// Check if the teacher is found
		if (teacher != null) {
			// Get the ID of the logged-in teacher
			Long teacherId = (long) teacher.getId();

			// Get the courses belonging to the logged-in teacher
			List<Course> teacherCourses = courseRepository.findByTeacherId(teacherId);
			model.addAttribute("allCourses", teacherCourses);
		}

		// // Get all courses (optional, if you want to display all courses)
		// List<Course> allCourses = courseRepository.findAll();
		// model.addAttribute("allCourses", allCourses);

		return "/teacher/viewCourses";
	}

	// Method to delete a course
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/deleteCourse/{id}")
	public String deleteCourse(@PathVariable("id") Long courseId, Principal principal, HttpSession session) {
		// Get the logged-in teacher
		String username = principal.getName();
		User teacher = this.userRepo.getUserByUserName(username);

		// Check if the course exists
		Optional<Course> courseOptional = courseRepository.findById(courseId);

		if (courseOptional.isPresent()) {
			Course course = courseOptional.get();

			// Check if the logged-in teacher is the owner of the course
			if (course.getTeacher().getId() == teacher.getId()) {
				// Teacher matches, delete the course
				courseRepository.deleteById(courseId);

				// Redirect to the viewCourses page after deletion
				session.setAttribute("courseCreatedMsg", new Message("Course Deleted Successfully", "success"));
				return "redirect:/teacher/viewCourses";
			} else {
				// Teacher is not the owner, show unauthorized page
				return "/teacher/pageNotFound";
			}
		} else {
			// Course not found, redirect to an error page
			return "/teacher/pageNotFound";
		}
	}

	// Edit Course
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/editCourse/{id}")
	public String editCourse(@PathVariable("id") Long courseId, Model model, Principal principal) {
		// Get the logged-in teacher
		String username = principal.getName();
		User teacher = this.userRepo.getUserByUserName(username);

		// Check if the course exists
		Optional<Course> courseOptional = courseRepository.findById(courseId);

		if (courseOptional.isPresent()) {
			Course course = courseOptional.get();

			// Check if the logged-in teacher is the owner of the course
			if (course.getTeacher().getId() == teacher.getId()) {
				// Teacher matches, allow editing
				model.addAttribute("course", course);
				return "/teacher/editCourse";
			} else {
				// Teacher is not the owner, show unauthorized page
				model.addAttribute("message", "User with ID " + teacher.getId() + " not found.");
				return "/teacher/pageNotFound";
			}
		} else {
			// Course not found, redirect to an error page
			model.addAttribute("message", "Course with ID " + courseId + " not found.");
			return "/teacher/pageNotFound";
		}
	}

	// Process form submission for editing an existing course
	@PreAuthorize("hasRole('TEACHER')")
	@PostMapping("/editCourseProcess")
	public String editCourseProcess(@Valid @ModelAttribute("course") Course course,
			BindingResult result,
			HttpSession session,
			Principal principal,
			Model model) {
		try {
			if (result.hasErrors()) {
				return "/teacher/editCourse";
			}

			// Get the logged-in teacher
			String username = principal.getName();
			User teacher = this.userRepo.getUserByUserName(username);

			// Check if the course exists
			Optional<Course> existingCourseOptional = courseRepository.findById(course.getId());

			if (existingCourseOptional.isPresent()) {
				Course existingCourse = existingCourseOptional.get();

				// Check if the logged-in teacher is the owner of the course
				if (existingCourse.getTeacher().getId() == teacher.getId()) {
					// Teacher matches, update and save the course
					existingCourse.setName(course.getName());
					existingCourse.setDescription(course.getDescription());
					existingCourse.setStartDate(course.getStartDate());
					existingCourse.setEndDate(course.getEndDate());
					// Update other properties as needed

					// Save the updated course
					courseRepository.save(existingCourse);

					session.setAttribute("courseCreatedMsg", new Message("Course Updated Successfully", "success"));
					return "redirect:/teacher/viewCourses";
				} else {
					// Teacher is not the owner, show unauthorized page
					return "/teacher/pageNotFound";
				}
			} else {
				// Course not found, redirect to an error page
				return "/teacher/pageNotFound";
			}
		} catch (Exception e) {
			// Handle other exceptions if needed
			model.addAttribute("updateError", "An error occurred while processing the form");
			return "/teacher/editCourse";
		}
	}

	// Some New --
	// Change Password
	// Change Password----------------------
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/change-password")
	public String changePassword() {
		return "teacher/change-password";
	}

	// Change Password Process-----------
	@PreAuthorize("hasRole('TEACHER')")
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
				return "teacher/change-password";

			}
		} else {
			httpSession.setAttribute("passMsg", new Message("Please enter the correct old password!", "danger"));
			model.addAttribute("oldPass", oldPassword);
			model.addAttribute("newPass", newPassword);
			return "teacher/change-password";
		}

		return "redirect:/teacher/index";
	}

	// Some new here

}
