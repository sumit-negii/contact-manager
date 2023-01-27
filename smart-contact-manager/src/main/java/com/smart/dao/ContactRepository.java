package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> 
{
	// Find contact by User ID
	
	@Query("FROM Contact c WHERE c.user.id = :userId ")
	// In Pageable object we have to pass two parameter Current Page = page , Contact Per Page = 5
	public Page<Contact> findContactByUserId(@Param("userId") int userId, Pageable pageable); 
	
		
	// Delete contact by contact ID
	@Query("DELETE Contact c where c.cid = :cid")
	public Contact deleteContactById(@Param("cid") int cid);
	
	
}
