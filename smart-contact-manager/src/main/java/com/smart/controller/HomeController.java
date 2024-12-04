package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.UserService;

@Controller
public class HomeController 
{
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserService userService;
	
	
	// Home Handler
	@GetMapping("/")
	public String test(Model model)
	{
	
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	
	
	// About Handler
	@GetMapping("/about")
	public String about(Model model)
	{
	
		model.addAttribute("title", "About - Smart Contact Manager");
		System.out.println("Testing println direct on main branch **** ");
		return "about";
	}
	
	
	
	// Sign-Up Handler
	@GetMapping("/signup")
	public String signup(Model model)
	{
	
		model.addAttribute("title", "Sign-Up - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "register";
	}
	
	
	
	// Registration Process Handler
		@PostMapping("/do_register")
	public String registration(@Valid @ModelAttribute("user") User user, BindingResult result,
		  @RequestParam(value="agreement", defaultValue = "false") boolean agreement ,
		  Model model, HttpSession session)
	{
		
		if(result.hasErrors())
		{
			model.addAttribute("user", user);
			return "redirect:/signup";
		}
			
		try
		{
			if(!agreement)
			{
				System.out.println("You have not agreed the term and conditions ..");
				throw new Exception("You have not agreed the term and conditions ..\"");
			}
			
			System.out.println("Agreement : "+agreement);
			System.out.println(user);
			
			user.setRole("ROLE_USER");
			user.setActive(true);
			user.setImageUrl("default-user.png");
			System.out.println("Just before passwordEncoder");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println(user);
				
			userService.save(user);
			System.out.println("1 record added ..");
				
			model.addAttribute("user", new User());
				
			session.setAttribute("message", new Message("Successfully registerd !! ", "alert-success"));
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(), "alert-danger"));
			
			return "redirect:/signup";

		}
			
		return "redirect:/signup";
	}
	
	
	
	// Login Handler
	@GetMapping("/signin")
	public String login(Model model)
	{
		
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
	
	
	
	
	
}
