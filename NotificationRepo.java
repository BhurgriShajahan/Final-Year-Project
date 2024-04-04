package com.tms.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.entities.Notification;

public interface NotificationRepo extends JpaRepository<Notification, Long>{

}
