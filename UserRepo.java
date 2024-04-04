package com.tms.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tms.entities.User;

@org.springframework.transaction.annotation.Transactional(readOnly = true)
@Repository
public interface UserRepo extends JpaRepository<User, Integer>
{
	@Query("select u from User u where u.email=:email")
	User getUserByUserName(@Param("email")String email);
	
	@Query("select u from User u where u.email=:email")
	User  getUserByName(@Param("email")String name);
	
	@Query("select u from User u where u.role=:rol")
	List<User> getTeachers(@Param("rol") String role);
	
	@Query("select u from User u where u.role=:rol")
	List<User> getStudents(@Param("rol") String role);
  
	Optional<User> findByName(String name);

//	//Search 
//	@Query("SELECT u FROM User u WHERE u.role = :role AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//	List<User> searchTeachers(@Param("keyword") String keyword, @Param("role") String role);

    // Search Teachers
//    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_TEACHER'"
//    		+ " AND (u.name LIKE %:query% "
//    		+ "OR u.surname LIKE %:query% "
//    		+ "OR u.email LIKE %:query%"
//    		+ " OR u.country LIKE %:query% "
//    		+ "OR u.city LIKE %:query% )")
//    List<User> searchTeachers(@Param("query") String query,String role);
//
//    // Search Students
//    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_STUDENT'"
//    		+ " AND (u.name LIKE %:query% "
//    		+ "OR u.subject LIKE %:query%"
//    		+ " OR u.email LIKE %:query%"
//    		+ " OR u.city LIKE %:query%) "
//    		+ "OR u.country LIKE %:query% "
//    		+ "OR u.subject LIKE %:query%")
//    List<User> searchStudents(@Param("query") String query, String role);
	// Search Teachers
	@Query("SELECT u FROM User u WHERE u.role = 'ROLE_TEACHER' "
	        + "AND (u.name LIKE %:query% "
	        + "OR u.surname LIKE %:query% "
	        + "OR u.email LIKE %:query% "
	        + "OR u.country LIKE %:query% "
	        + "OR u.city LIKE %:query%)")
	List<User> searchTeachers(@Param("query") String query);

	// Search Students
	@Query("SELECT u FROM User u WHERE u.role = 'ROLE_STUDENT' "
	        + "AND (u.name LIKE %:query% "
	        + "OR u.subject LIKE %:query% "
	        + "OR u.email LIKE %:query% "
	        + "OR u.city LIKE %:query% "
	        + "OR u.country LIKE %:query%)")
	List<User> searchStudents(@Param("query") String query);


//	List<User> searchStudents(String keyword, String role);
}
