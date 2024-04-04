package com.tms.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	// User name
	@NotBlank(message = "Name is required!")
	@Size(min = 3, max = 30, message = "Minimum 3 and maximum 30 char!!")
	private String name;

	// father name
	@NotBlank(message = "father name is required!")
	@Size(min = 3, max = 30, message = "Minimum 3 and maximum 30 char!!")
	private String fname;

	// User Surname
	@NotBlank(message = "Surname is required!")
	@Size(min = 3, max = 30, message = "Minimum 3 and maximum 30 char!!")
	private String surname;

	// User gender
	@NotBlank(message = "Gender is required!")
	private String gender;

	// User age
	@NotBlank(message = "Age is required!")
	private String age;

	// User country name
	@NotBlank(message = "Country is required!")
	private String country;

	// User City name
	@NotBlank(message = "City is required!")
	private String city;

	// User Address
	@NotBlank(message = "Address is required!")
	@Column(length = 500)
	private String address;

	// Standard -- Class name
	@NotBlank(message = "Class is required!")
	private String standerd;

	// Going to
	@NotBlank(message = "Going to is required!")
	private String going;

	// Subject
	@NotBlank(message = "Subject is required!")
	private String subject;

	// Phone number
	@NotBlank(message = "Phone number is required!")
	private String phone;

	// Tuition type
	@NotBlank(message = "Tuition type is required!")
	private String tuitionType;

	// Tutor preferred
	@NotBlank(message = "Tutor Preferred is required!")
	private String tutorPreferred;

	// Email
	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Must match exampl4@gmail.com")
	@Column(unique = true)
	private String email;

	@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "Min size 8 and use char,number and symbols")
	private String password;

	@NotBlank(message = "Marital status is required!")
	private String marital;

	@NotBlank(message = "Qualification is required!")
	private String qualification;

	@NotBlank(message = "Degree is required!")
	private String degree;

	@NotBlank(message = "Experience is required!")
	private String experience;

	// User role
	private String role;

	// User account
	private boolean active;

	// User image
	private String image;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<Contacts> contact = new ArrayList<>();

	// User.java
	@OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
	private List<Message> receivedMessages = new ArrayList<>();

	// -------------------------Getters Setters

	public List<Message> getReceivedMessages() {
		return receivedMessages;
	}

	public void setReceivedMessages(List<Message> receivedMessages) {
		this.receivedMessages = receivedMessages;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<Contacts> getContact() {
		return contact;
	}

	public void setContact(List<Contacts> contact) {
		this.contact = contact;
	}

	public User(int id,
			@NotBlank(message = "Name is required!") @Size(min = 3, max = 30, message = "Minimum 3 and maximum 30 char!!") String name,
			@NotBlank(message = "father name is required!") @Size(min = 3, max = 30, message = "Minimum 3 and maximum 30 char!!") String fname,
			@NotBlank(message = "Surname is required!") @Size(min = 3, max = 30, message = "Minimum 3 and maximum 30 char!!") String surname,
			@NotBlank(message = "Gender is required!") String gender,
			@NotBlank(message = "Age is required!") String age,
			@NotBlank(message = "Country is required!") String country,
			@NotBlank(message = "City is required!") String city,
			@NotBlank(message = "Address is required!") String address,
			@NotBlank(message = "Class is required!") String standerd,
			@NotBlank(message = "Going to is required!") String going,
			@NotBlank(message = "Subject is required!") String subject,
			@NotBlank(message = "Phone number is required!") String phone,
			@NotBlank(message = "Tuition type is required!") String tuitionType,
			@NotBlank(message = "Tutor Preferred is required!") String tutorPreferred,
			@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Must match exampl4@gmail.com") String email,
			@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "Min size 8 and use char,number and symbols") String password,
			@NotBlank(message = "Marital status is required!") String marital,
			String qualification,
			String degree,
			String experience, String role, boolean active,
			String image) {
		super();
		this.id = id;
		this.name = name;
		this.fname = fname;
		this.surname = surname;
		this.gender = gender;
		this.age = age;
		this.country = country;
		this.city = city;
		this.address = address;
		this.standerd = standerd;
		this.going = going;
		this.subject = subject;
		this.phone = phone;
		this.tuitionType = tuitionType;
		this.tutorPreferred = tutorPreferred;
		this.email = email;
		this.password = password;
		this.marital = marital;
		this.qualification = qualification;
		this.degree = degree;
		this.experience = experience;
		this.role = role;
		this.active = active;
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStanderd() {
		return standerd;
	}

	public void setStanderd(String standerd) {
		this.standerd = standerd;
	}

	public String getGoing() {
		return going;
	}

	public void setGoing(String going) {
		this.going = going;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTuitionType() {
		return tuitionType;
	}

	public void setTuitionType(String tuitionType) {
		this.tuitionType = tuitionType;
	}

	public String getTutorPreferred() {
		return tutorPreferred;
	}

	public void setTutorPreferred(String tutorPreferred) {
		this.tutorPreferred = tutorPreferred;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMarital() {
		return marital;
	}

	public void setMarital(String marital) {
		this.marital = marital;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
