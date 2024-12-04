package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.ContactService;
import com.smart.services.UserService;

import javassist.expr.NewArray;

@Controller
@RequestMapping("/user")
public class UserController 
{
	@Autowired
	private UserService userService;
	@Autowired
	private ContactService contactService;
	
	
	@ModelAttribute   // This annotation is used to add common data on every page because it run implicitely
	public void addCommonData(Model model, Principal principal)
	{
		String email = principal.getName();		
		User user = userService.getUserByEmail(email);
		model.addAttribute("user", user);
		System.out.println("Hello user : " + user);
		
	}
	
	
	// Index Handler
	@GetMapping("/index")
	public String userDashboard(Model model, Principal principal)
	{
		model.addAttribute("title", "Home");
		
		return "user/user_dashboard";
	}
	
	
	
	//  add-contact-form Handler
	@GetMapping("/add-contact")
	public String openAddContact(Model model)
	{
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		model.addAttribute("heading", "Add Contact");
		
		return "user/add-contact-form";
	}
	
	
	
	// Processing add Contact form   PENDING : SERVER SIDE VALIDATION
	@PostMapping("/process-contact")
	public String processAddContact(@Valid @ModelAttribute("contact") Contact contact, 
			BindingResult result, @RequestParam("profileImage") MultipartFile file, 
			Principal principal, HttpSession session, Model model ) throws Exception
	{
		
		
		try
		{
			
			if(result.hasErrors())
			{
				model.addAttribute("contact", contact);
				
				return "redirect:/user/add-contact";
			}
			
			
			String email = principal.getName();
			User user = userService.getUserByEmail(email);
			
			
			// Processing and Uploading file
			
			if(file.isEmpty())
			{
				System.out.println("Image not selected");
				contact.setImageUrl("contact-logo.png");
			}
			else
			{				
				// Sav imgae url name to database
				contact.setImageUrl(file.getOriginalFilename());
				
				// Getting file object where image will be saved
				File saveFile = new ClassPathResource("static/img").getFile();
				
				// Getting the absolute path of the file with name
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				// Copy image from file object to path
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
			}
			
			contact.setUser(user);
	
			contactService.saveContact(contact);
			
			System.out.println("User save to database : "+contact);
					
			session.setAttribute("message", new Message("Your contact is added !! Add more ..", "alert-success"));
			
		}
		catch(Exception e)
		{
			System.out.println("Error : "+e.getMessage());
			
			session.setAttribute("message", new Message("Something went wrong !! Try again ..", "alert-danger"));

		}
		
		
		
		return "redirect:/user/add-contact";
	}
	
	
	
	// show-contact Handler
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") int page, Model model, Principal principal)
	{
		String email = principal.getName();
		User user = userService.getUserByEmail(email);
		
		Pageable pageable = PageRequest.of(page, 10);
		Page<Contact> contactList = contactService.findContactByUserId(user.getId(), pageable);
		
		model.addAttribute("contactList", contactList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", contactList.getTotalPages());
		model.addAttribute("title","Show User Contacts");
		
		return "/user/show-contacts";
	}
	
	
	
	// contact-detail Handler
	@GetMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") int cid, Model model, Principal principal)
	{
		Contact contact = contactService.findById(cid);
		String email = principal.getName();
		User user = userService.getUserByEmail(email);
		
		if(user.getId() == contact.getUser().getId())
		{
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		
		
		return "/user/contact-details";
	}

	

	// Delete Contact Handler  
	@GetMapping("/{cid}/delete")
	public String deleteContact(@PathVariable("cid") int cid, Model model, HttpSession session)
	{
		contactService.deleteContact(cid);
		
		session.setAttribute("message", new Message("One contact deleted ..", "alert-success"));
		System.out.println("User delete successfully "+cid);
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	
	// Update Contact Handler
	@GetMapping("/{cid}/update")
	public String updateContact(@PathVariable("cid") int cid, Model model, HttpSession session, Principal principal)
	{
		
		Contact contact = contactService.findById(cid);
		String email = principal.getName();
		User user = userService.getUserByEmail(email);
		
		if(user.getId() == contact.getUser().getId())
		{
			model.addAttribute("contact", contact);
		}
		else
		{
			session.setAttribute("message", new Message("You don't have permission to update this contact !!", "alert-danger"));
			return "redirect:/user/show-contacts/0";
		}		
		
		return "user/update-contact-form";
	}
	
	
	
	
	// Processing add Contact form   PENDING : VALIDATION TO NOT CHANGE THE OTHER USER CONTACT
		@PostMapping("/process-update")
		public String processUpdateContact(@ModelAttribute("contact") Contact contact,
				@RequestParam("profileImage") MultipartFile file, 
				Principal principal, HttpSession session ) throws Exception
		{
			
			
			try
			{
					String email = principal.getName();
					User user = userService.getUserByEmail(email);

				
					// Old contact detail
					System.out.println("contact = "+contact);
					Contact oldContact = contactService.findById(contact.getCid());
					contact.setCid(oldContact.getCid());
					
					if(!file.isEmpty())
					{
						// Delete old image
						
					
						if(!oldContact.getImageUrl().equals("contact-logo.png"))
						{
							File deleteFile = new ClassPathResource("static/img").getFile();
							File file1 = new File(deleteFile, oldContact.getImageUrl());
							file1.delete();
							
						}
						
						
						
						// Update new image
						

						// Getting File object where image will be saved
						File saveFile = new ClassPathResource("static/img").getFile();
						
						// Getting the absolute path of the File with name
						Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
						
						// Copy new image from File object to path
						Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
						
						// Save new imgae url name to database
						contact.setImageUrl(file.getOriginalFilename());
						
						
					}
					else
					{				
						contact.setImageUrl(oldContact.getImageUrl());			
					}
					
					contact.setUser(user);
			
					contactService.saveContact(contact);
					
					System.out.println("User updated to database : "+contact);
							
					session.setAttribute("message", new Message("Your contact is updated !!", "alert-success"));
				
			}
			catch(Exception e)
			{
				System.out.println("Error : "+e.getMessage());
				e.printStackTrace();
				
				session.setAttribute("message", new Message("Something went wrong !! Try again ..", "alert-danger"));

			}
			
			
			
			return "redirect:/user/"+contact.getCid()+"/contact";
		}

	
	
	// Profile Handler
		
	@GetMapping("/profile")
	public String viewProfile(Model model, Principal principal)
	{
		String email = principal.getName();
		User user = userService.getUserByEmail(email);
		
		model.addAttribute("user", user);
		model.addAttribute("title", "User Profile");
		
		return "user/user-profile";
	}

	
	
}
