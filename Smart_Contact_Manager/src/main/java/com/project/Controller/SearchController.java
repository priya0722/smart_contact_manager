package com.project.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.project.Entities.Contact;
import com.project.Entities.User;
import com.project.dao.UserRepository;
import com.project.dao.contactRepository;

@RestController
public class SearchController {

	@Autowired
	public UserRepository userRepository;
	@Autowired
	public contactRepository contactRepository;
	
	//seacrh handler
	@GetMapping("/search/{query}") //query is actually keyword which we are searching in search box
	public ResponseEntity<?> search(@PathVariable("query")String query ,  Principal principal){
		
		System.out.println(query);
		User user = this.userRepository.getUserByUserName(principal.getName());
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
		
}
}
