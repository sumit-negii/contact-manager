package com.smart.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.smart.dao.ContactRepository;
import com.smart.entities.Contact;

@Service
public class ContactService 
{
	@Autowired
	ContactRepository contactRepository;
	
	
	// Save contact
	public void saveContact(Contact contact)
	{
		contactRepository.save(contact);
		
	}
	
	
	
	// Find contact by user ID
	public Page<Contact> findContactByUserId(int userId, Pageable pageable)
	{
		Page<Contact> contactList = contactRepository.findContactByUserId(userId, pageable);
		
		return contactList;
	}

	
	// Find contact by contact ID
	public Contact findById(int cid)
	{
		Contact contact = contactRepository.findById(cid).get();
		
		return contact;
	}

	// Delete contact by contact ID
	@Transactional
	public void deleteContact(int cid)
	{
		Contact contact = contactRepository.findById(cid).get();
		System.out.println(contact);
		contact.setUser(null);
		System.out.println(contact);
		
		contactRepository.delete(contact);
		
	}
}
