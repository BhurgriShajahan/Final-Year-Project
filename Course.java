package com.tms.entities;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Course {
 @Id
 @GeneratedValue(strategy = GenerationType.AUTO)
 private Long id;
 //Course name
 @NotBlank(message = "Course name is required!")
 @Size(max = 30, message = "Maximum 30 char!!")
 private String name;
 @NotBlank(message = "Description is required!")
 @Size(min = 20, max = 300, message = "Minimum 20 and maximum 200 char!!")
 private String description;


 @ManyToOne
 @JoinColumn(name = "teacher_id")
 private User teacher;
 @DateTimeFormat(pattern = "yyyy-MM-dd")
 private LocalDate startDate;
 @DateTimeFormat(pattern = "yyyy-MM-dd")
 private LocalDate endDate;
 
 
public LocalDate getStartDate() {
	return startDate;
}

public void setStartDate(LocalDate startDate) {
	this.startDate = startDate;
}

public LocalDate getEndDate() {
	return endDate;
}

public void setEndDate(LocalDate endDate) {
	this.endDate = endDate;
}

public User getTeacher() {
	return teacher;
}

public void setTeacher(User teacher) {
	this.teacher = teacher;
}

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}


}
