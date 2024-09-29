package com.project.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.project.Entities.User;

public interface UserRepository extends CrudRepository<User , Integer>{

	 @Query("select u from User u where u.email = :email")
	    User getUserByUserName(@Param("email") String email);
}
