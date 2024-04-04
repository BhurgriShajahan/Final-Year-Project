package com.tms.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tms.entities.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

	 List<Course> findByTeacherId(Long teacherId);

	    // Search for courses by name or description
	    @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	    List<Course> searchName(@Param("keyword") String keyword);
}
