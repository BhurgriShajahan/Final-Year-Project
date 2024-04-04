package com.tms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class ContactUs {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@NotBlank(message = "Name is required!")
	@Size(min = 3, max = 30, message = "Minimum 3 and maximum 30 char!!")
	private String name;

	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Must match exampl4@gmail.com")
	private String email;

	@NotBlank(message = "This field is required!")
	private String subject;

	@NotBlank(message = "Sorry message is required!")
	@Column(length = 500)
	private String message;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ContactUs(int id,
			@NotBlank(message = "Name is required!") @Size(min = 3, max = 30, message = "Minimum 3 and maximum 30 char!!") String name,
			@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Must match exampl4@gmail.com") String email,
			@NotBlank(message = "This field is required!") String subject,
			@NotBlank(message = "Sorry message is required!") String message) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.subject = subject;
		this.message = message;
	}

	public ContactUs() {
		super();
		// TODO Auto-generated constructor stub
	}

}
