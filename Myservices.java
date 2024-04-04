package com.tms.messages;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tms.entities.ContactUs;
import com.tms.entities.Notification;
import com.tms.entities.User;
import com.tms.repo.ContactUsRepo;
import com.tms.repo.NotificationRepo;
import com.tms.repo.UserRepo;
import jakarta.servlet.http.HttpSession;


@Service
public class Myservices {

    @Autowired
    UserRepo userRepo;
    @Autowired
    ContactUsRepo contactUsRepo;
    @Autowired
    NotificationRepo notificationRepo;

    public void removeMessage() throws Exception {
        try {

            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                    .getSession();

            session.removeAttribute("msg");
            session.removeAttribute("msge");
            session.removeAttribute("message");
            session.removeAttribute("masge");
            session.removeAttribute("mesag");
            session.removeAttribute("adminContactMessage");
            session.removeAttribute("saveContactmsg");
            session.removeAttribute("updateContactmsg");
            session.removeAttribute("notiMessage");
            session.removeAttribute("ms");
            session.removeAttribute("masg");
            session.removeAttribute("m");
            session.removeAttribute("TPUMsg");
            session.removeAttribute("passMsg");
            session.removeAttribute("courseCreatedMsg");
            
           
            System.out.println("Removed..................");

        } catch (Exception e) {
            System.out.println("Sorry Broher " + e);
        }
    }

    // delete Contact
    public ContactUs name(int id) {

        return null;

    }

    // Insert User
    public User saveUser(User user) {
        User save = userRepo.save(user);
        return save;
    }

    // Update ADMIN MyProfile
    public User updateUser(int id, User user) {
        Optional<User> findById = userRepo.findById(id);
        if (findById.isPresent()) {
            user = findById.get();
            return user;
        }
        return null;
    }

    // Update STUDENT MyProfile
    public User updateStudentProfile(int id, User user) {
        Optional<User> findById = userRepo.findById(id);
        if (findById.isPresent()) {
            user = findById.get();
            return user;
        }
        return null;
    }

    // Update TEACHER MyProfile
    public User updateTeacherProfile(int id, User user) {
        Optional<User> findById = userRepo.findById(id);
        if (findById.isPresent()) {
            user = findById.get();
            return user;
        }
        return null;
    }

    // Get Student Profile
    public User getStudentProfile(final int id, User user) {
        final Optional<User> findById = userRepo.findById(id);
        if (findById.isPresent()) {
            user = findById.get();
            return user;
        }
        return null;
    }

    // Send Notification
    public Notification sendNotification(Notification notification) {
        notification = notificationRepo.save(notification);
        return notification;
    }

    // Get Notification
    public List<Notification> getNotification() {

        List<Notification> findAll = notificationRepo.findAll();

        return findAll;
    }
    
    //delete notification
    
    // Get Notification
    public void deleteNotification(Long id) {
        Optional<Notification> findById = this.notificationRepo.findById(id);
        if(findById.isPresent())
        {
        	this.notificationRepo.deleteById(id);
        }
    }
    
    
    // edit Notification
    public Notification editNotification(Long id,Notification notification) 
    {
        this.notificationRepo.findById(id);
    	
    	Optional<Notification> findById = this.notificationRepo.findById(id);
        
        
        if(findById.isPresent())
        {
        	notification = findById.get();
        	return notification;
        }
        return null;
    }
    
 
   
}
