package com.project.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.project.Entities.Contact;
import com.project.Entities.User;

public interface contactRepository extends CrudRepository<Contact, Integer>{

	@Query("from Contact c where c.user.id = :userId")
	public List<Contact> findContactsByUser(@Param("userId") int userId);
	
	
	//search
	public List<Contact> findByNameContainingAndUser(String keywords , User user); //keywords se bna jitna bhi name hoga uska list aaeyga
}
