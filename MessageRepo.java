package com.tms.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tms.entities.Message;
import com.tms.entities.User;

public interface MessageRepo  extends JpaRepository<Message, Long>{
//	  List<Message> findByRecipient(User recipient);
	  
//	    List<Message> findByRecipientAndUserRole(User recipient, String senderRole);
	    @Query("SELECT m FROM Message m WHERE m.recipient = :recipient AND m.recipient.role = :senderRole")
	    List<Message> findByRecipientAndUserRole(
	        @Param("recipient") User recipient,
	        @Param("senderRole") String senderRole
	    );

}
