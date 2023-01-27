package com.smart.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Service
public class UserService 
{
	@Autowired
	UserRepository userRepository;

	// Add user
	public void save(User user)
	{
		userRepository.save(user);
	}
	
	// Get user by id
	public void getById(int id)
	{
		userRepository.getById(id);
	}
	
	
	// Get user by email
	public User getUserByEmail(String email)
	{
		User user = userRepository.getUserByEmail(email);
		
		return user;
	}
	

}
