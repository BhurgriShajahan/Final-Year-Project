package com.tms.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tms.entities.Contacts;

@Repository
public interface ContactRepo extends JpaRepository<Contacts, Long>{
	
	// retrieve all contacts from database with user id 
	   @Query("from Contacts as c where c.user.id =:userId")
	   List<Contacts> getAllContactsByUser(@Param("userId") long id);
	   
	// retrieve all contacts from database with user id 
	   @Query("from Contacts as c where c.user.id =:userId")
	   Contacts getUserById(@Param("userId") long id);

	   @Query("SELECT c FROM Contacts c WHERE c.user.id = :userId "
		        + "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
		        + "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) "  // Added closing parenthesis here
		        + "OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
		List<Contacts> searchContactsByKeyword(@Param("userId") int i, @Param("keyword") String keyword);

}
