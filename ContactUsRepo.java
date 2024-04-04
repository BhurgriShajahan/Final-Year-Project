package com.tms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactUsRepo extends JpaRepository<com.tms.entities.ContactUs, Integer>
{
	

}
