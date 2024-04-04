package com.tms.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Autowired
	PasswordEncoder encoder;
	@Autowired
	private CourseRepository courseRepo;
	@Autowired
	private MessageRepo messageRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ContactRepo contactRepo;
	@Autowired
	private ContactUsRepo contactUsRepo;
	@Autowired
	MessageRepo messageRepository;
	@Autowired
	private Myservices myservices;

	// Common data
	@ModelAttribute
	public void comonData(Principal principal, Model model) {
		String userName = principal.getName();

		User user = userRepo.getUserByUserName(userName);

		model.addAttribute("user", user);
	}

	@ModelAttribute
	public void showAllNotifications(Model model) {
		List<Notification> getAllNotification = myservices.getNotification();

		if (getAllNotification.size() > 0) {

			model.addAttribute("getAllNotification", getAllNotification);
		}
		if (getAllNotification.size() == 0) {
			model.addAttribute("noNotifications", "No Notifications");
		}
	}

	// Student index
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/index")
	public String home() {
		return "student/index";
	}

	// view Teachers
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/view")
	public String viewTeachers(Model model) {
		String role = "ROLE_TEACHER";
		List<User> allTeachers = userRepo.getTeachers(role);

		if (allTeachers != null) {
			model.addAttribute("allTeachers", allTeachers);
		}
		if (allTeachers.isEmpty()) {
			String records = "Not availble";
			model.addAttribute("records", records);
		}

		return "student/view";
	}

	// Contact with ADMIN
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/adminContact")
	public String adminContact(@ModelAttribute("adminContact") ContactUs contactUs, Model model) {
		model.addAttribute("adminContact", contactUs);

		return "student/adminContact";
	}

	// ADMIN Contact Process
	@PreAuthorize("hasRole('STUDENT')")
	@PostMapping("/adminContactProcess")
	public String contactUsProcess(@Valid @ModelAttribute("adminContact") ContactUs contactUs,
			BindingResult result,
			HttpSession session,
			Model model) throws Exception {
		try {
			// On the way----------
			if (result.hasErrors()) {
				System.out.println(result);
				return "student/adminContact";
			}
			session.setAttribute("adminContactMessage",
					new com.tms.messages.Message("Form has been submited Successfully", "alert-success"));

			model.addAttribute("adminContact", contactUs);

			this.contactUsRepo.save(contactUs);
			return "redirect:/student/adminContact";
		} catch (Exception e) {
			System.out.println("Sorry..Somthing Wrong.." + e);
			model.addAttribute("adminContact", contactUs);
		}
		return "redirect:/student/adminContact";
	}

	// My Profile
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/myProfile")
	public String MyProfile() {
		return "/student/myProfile";
	}

	// ----------------------
	// Edit My Profile ById
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/editProfile/{id}")
	public String editMyProfile(@PathVariable("id") Integer id, User user, Principal principal, Model model) {
		// Update particular user when he is login

		String name = principal.getName();

		User u = userRepo.getUserByName(name);

		if (user.getId() == u.getId()) {

			User updateUser = myservices.updateUser(id, u);

			model.addAttribute("u", updateUser);
			return "/student/editProfile";
		}
		model.addAttribute("message", "Invalid user with ID " + id + "!");
		return "/student/pageNotFound";

	}

	// Edit Profile Process Form
	@PreAuthorize("hasRole('STUDENT')")
	@PostMapping("/editProfileProcess")
	public String editProfileProcess(@ModelAttribute User user, Model model,
			@RequestParam MultipartFile proImg,
			HttpSession session) {
		try {

			String proImgName = proImg.getOriginalFilename();

			User oldPic = this.userRepo.findById(user.getId()).get();

			if (!proImgName.isEmpty()) {

				File save = new ClassPathResource("static/images").getFile();
				java.nio.file.Path path = Paths.get(save.getAbsolutePath() + File.separator + proImgName);
				Files.copy(proImg.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				user.setImage(proImgName);
			} else {
				user.setImage(oldPic.getImage());
				if (oldPic.getImage().isEmpty()) {
					user.setImage("contact.png");
				}
			}

			// userRepo.save(user);
			myservices.saveUser(user);
			// model.addAttribute("editPro", user);

			session.setAttribute("m", new Message("Profile Updated", "alert-success"));
		} catch (Exception e) {
			System.out.println("ERROR____!!" + e);
		}
		return "redirect:/student/myProfile";
	}

	// saveContacts

	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/saveContacts")
	public String saveContacts() {
		return "student/saveContacts";
	}

	// saveContactProcess

	@PreAuthorize("hasRole('STUDENT')")
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

		return "redirect:/student/viewContacts";
	}

	// End Save Contacts process form

	// Search Contacts and view all contacts
	@PreAuthorize("hasRole('STUDENT')")
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

		return "student/viewContacts";
	}

	// Delete Contact
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/deleteContact/{id}")
	public String deleteContact(@PathVariable("id") long id, @ModelAttribute Contacts cImage, Principal principal,
			HttpSession httpSession, Model model) throws IOException {
		String userName = principal.getName();

		User user = userRepo.getUserByUserName(userName);

		Contacts contact = contactRepo.getReferenceById(id);

		if (user.getId() == contact.getUser().getId()) {
			contactRepo.deleteById(id);
		} else {
			System.out.println("Sorry");
			return "student/pageNotFound";
		}
		httpSession.setAttribute("updateContactmsg", new Message("Delete Success..", "alert-success"));

		return "redirect:/student/viewContacts";
	}
	// Delete Contact End

	// editContact

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@PreAuthorize("hasRole('STUDENT')")
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
			return "/student/pageNotFound";
		}

		return "/student/editContact";
	}
	// End Edit Contact Form

	// Edit Contact Process Form
	@PreAuthorize("hasRole('STUDENT')")
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

			return "/student/viewContacts";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception------------------------------------->>");
		}
		return "redirect:/student/viewContacts";
	}

	// Edit Contacts process form End

	// new--
	// Show Specific contact detail
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/showContact/{id}")
	public String showContacts(@PathVariable("id") Long id, Principal principal, Contacts contacts, Model model) {
		String userName = principal.getName();
		User user = userRepo.getUserByName(userName);

		Optional<Contacts> findById = contactRepo.findById(id);

		if (findById.isPresent()) {
			Contacts contact = findById.get();

			// Check if the logged-in user has access to the specified contact
			if (user.getId() == contact.getUser().getId()) {
				model.addAttribute("userDetail", contact);
			} else {
				return "/student/pageNotFound";
			}
		} else {
			// Handle case when the specified contact ID is not found
			model.addAttribute("message", "User with ID " + id + " not found.");
			System.out.println("User Id no present---" + id);
			return "/student/pageNotFound";
		}

		return "/student/showContact";
	}

	// Search Teachers
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/search")
	public String searchTeachers(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
		System.out.println("Search Teachers method called with keyword: " + keyword);

		String role = "ROLE_TEACHER";

		List<User> searchedTeachers = userRepo.searchTeachers(keyword);

		if (keyword != null && !keyword.isEmpty()) {
			if (searchedTeachers.isEmpty()) {
				String msg = "Search result not found!";
				model.addAttribute("msg", msg);
				System.out.println("User not found ~!!! " + msg);
				return "student/searchTeacherResult";
			} else {
				model.addAttribute("searchedTeachers", searchedTeachers);
			}
		} else {
			// If no keyword is provided, show all teachers
			List<User> allTeachers = userRepo.getTeachers(role);
			model.addAttribute("allTeachers", allTeachers);
			return "student/view";
		}

		return "student/searchTeacherResult";
	}
	// Search Teacher End

	// Send Message to teacher
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/sendMessageToTeacher/{id}")
	public String sendMessage(@PathVariable("id") Integer personId, Model model) {
		Optional<User> personOptional = userRepo.findById(personId);

		if (personOptional.isPresent()) {
			User person = personOptional.get();

			model.addAttribute("person", person);
			model.addAttribute("message", new com.tms.entities.Message());
			return "/student/sendMessageToTeacher";
		} else {
			// Handle the case when the teacher with the given ID is not found
			return "/student/pageNotFound";
		}
	}

	// // Process sending message to Teacher
	@Transactional
	@PreAuthorize("hasRole('ROLE_STUDENT')")
	@PostMapping("/sendMessageProcess")
	public String sendMessageToStudentProcess(@Valid @ModelAttribute("message") com.tms.entities.Message message,
			BindingResult bindingResult,
			Principal principal, Model model, HttpSession session) {
		try {
			if (bindingResult.hasErrors()) {
				System.out.println("Message is required!!.........." + bindingResult);
				// model.addAttribute("bindingErrors", "Message is required!!");
				session.setAttribute("m", new Message("Message is required", "alert-danger"));
				return "/student/sendMessageToTeacher";
			}

			String senderName = principal.getName();
			User sender = userRepo.getUserByName(senderName);

			message.setSender(sender.getName());
			LocalDate localDate = LocalDate.now();
			message.setDate(localDate);

			// Get the recipient ID
			User recipient = userRepo.findById(sender.getId()).orElse(null);

			if (recipient != null) {
				// Save the message to the recipient's messages list
				recipient.getReceivedMessages().add(message);

				// Save the updated recipient to update the message list
				userRepo.save(recipient);
				model.addAttribute("messageSent", true);
				session.setAttribute("msge", new Message("Message send success", "alert-success"));
				// Redirect to a URL view Teachers
				return "redirect:/student/view";
			} else {
				// Handle the case when the recipient is not found
				System.out.println("ERROR: Recipient not found.");
				model.addAttribute("messageSent", false);

				// Redirect to a default URL if needed
				return "redirect:/student/view";
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			model.addAttribute("messageSent", false);

			// Redirect to a default URL if needed
			return "redirect:/student/view";
		}
	}

	// Delete Message
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/deleteMessage/{messageId}")
	public String deleteMessage(@PathVariable("messageId") Long messageId, Principal principal, Model model,
			HttpSession session) {
		try {

			// Retrieve the message by ID
			Optional<com.tms.entities.Message> messageOptional = messageRepo.findById(messageId);

			if (messageOptional.isPresent()) {
				this.messageRepo.deleteById(messageId);
				// Delete the message from the database
				this.messageRepo.deleteById(messageId);
				// Redirect to the viewMessages page after deletion
				model.addAttribute("messageDeleted", true);
				session.setAttribute("msge", new Message("Message delete success..", "alert-success"));

				return "redirect:/student/inbox";
			} else {
				// Message not found, redirect to an error page
				return "/student/pageNotFound";
			}
		} catch (Exception e) {
			// Handle other exceptions if needed
			System.out.println("ERROR: " + e.getMessage());
			model.addAttribute("messageDeleted", false);
			return "redirect:/student/inbox"; // Redirect to a default URL if needed
		}
	}

	// Show all messages
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/inbox")
	public String viewMessages(Principal principal, Model model) {
		try {
			// Get the currently logged-in user
			String senderName = principal.getName();
			User sender = userRepo.getUserByName(senderName);

			// Retrieve all messages for the logged-in user (sender)
			List<com.tms.entities.Message> messages = sender.getReceivedMessages();

			// Pass the messages to the view
			model.addAttribute("messages", messages);

			return "/student/inbox";
		} catch (Exception e) {
			// Handle exceptions if any
			System.out.println("ERROR: " + e.getMessage());
			return "/student/pageNotFound"; // Redirect to an error page if needed
		}
	}

	// View all courses
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/viewCourses")
	public String viewCourses(Model model) {
		List<Course> allCourses = courseRepo.findAll();
		model.addAttribute("allCourses", allCourses);
		return "student/viewCourses";
	}

	// @GetMapping("/viewCourses")
	// public String viewCourses(@RequestParam(defaultValue = "0") String page,
	// Model model) {
	// try {
	// int currentPage = Integer.parseInt(page);
	// int pageSize = 2;
	// Page<Course> coursesPage = courseRepo.findAll(PageRequest.of(currentPage,
	// pageSize));
	//
	// model.addAttribute("currentPage", currentPage);
	// model.addAttribute("totalPages", coursesPage.getTotalPages());
	// model.addAttribute("totalItems", coursesPage.getTotalElements());
	// model.addAttribute("allCourses", coursesPage.getContent());
	//
	// return "student/viewCourses";
	// } catch (NumberFormatException e) {
	// // Handle the case where 'page' is not a valid integer
	// // You can redirect to an error page or handle it as appropriate
	// return "student/pageNotFound";
	// }
	// }

	// Search Courses
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/searchCourse")
	public String searchCourses(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
		System.out.println("Search  Courses method called with keyword: " + keyword);

		// Search for courses
		List<Course> searchedCourses = courseRepo.searchName(keyword);
		model.addAttribute("searchedCourses", searchedCourses);

		return "student/searchCourseResult";
	}

	// Change Password----------------------
	@PreAuthorize("hasRole('STUDENT')")
	@GetMapping("/change-password")
	public String changePassword() {
		return "student/change-password";
	}

	// Change Password Process-----------
	@PreAuthorize("hasRole('STUDENT')")
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
				return "student/change-password";

			}
		} else {
			httpSession.setAttribute("passMsg", new Message("Please enter the correct old password!", "danger"));
			model.addAttribute("oldPass", oldPassword);
			model.addAttribute("newPass", newPassword);
			return "student/change-password";
		}

		return "redirect:/student/index";
	}

}
