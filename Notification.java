package com.tms.entities;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Length(max = 50,message = "Message field is must be 50 characters!!")
	@NotBlank(message = "This field is required!")
	private String title;

	@NotBlank(message = "Sorry message is required!")
	@Column(length = 500)
	@Length(max = 500,message = "Message field is must be 500 characters!!")
	private String message;

	private LocalDate date;

	public long getId() {
		return id;
	}

	public void setId(long l) {
		this.id = l;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDate getDate() {
		return date;
	}
	

	public void setDate(LocalDate d) {
		this.date = d;
	}

}
