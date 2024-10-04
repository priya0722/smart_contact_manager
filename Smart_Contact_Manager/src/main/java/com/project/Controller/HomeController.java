package com.project.Controller;


import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.Entities.User;
import com.project.Helper.Message;
import com.project.Service.CloudinaryImageService;
import com.project.Service.EmailService;
import com.project.dao.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	SecureRandom random = new SecureRandom();
	
	@Autowired
	private CloudinaryImageService cloudinaryImageService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailservice;
	

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title" , "Home - SmarContactManager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title" , "About - SmarContactManager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model , HttpSession session) {
		session.removeAttribute("message");
		model.addAttribute("title" , "Signup - SmarContactManager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	

	
	//this handler for registering
	@PostMapping("/do_register")
	public String registerUser(@Valid
			@ModelAttribute("user") User user , BindingResult result1 , @RequestParam(value="agreement" , defaultValue="false")boolean agreement , Model model , HttpSession session ) {
		//user will accept the vlaue of email , name , password from form but agreement is not in class user so ww will accept it using @RequestParam
		
		 session.removeAttribute("message");
		
		try {
			if(result1.hasErrors()) {
				model.addAttribute("user" , user);
				return "signup";
			}
			
		if(!agreement) {
		System.out.println("You have not agreed to terms and conditions");
		 throw new Exception("You have nnot agreed to terms and conditions");
				
			}
		
		int otp1 = random.nextInt(999999);
		String otp = String.valueOf(otp1);
		
		System.out.println(otp);
		
		//write code for send otp to mail
		
		
		String receiver = user.getEmail();
		String subject = "Smart Contact Manager";

				    
		String content = 
				"Hello,\n\n" +
			    "Thank you for using SmartContactManager. Here is the code you requested to confirm your account: "+ otp + 
			    "\n\nThis code will remain active for the next 10 minutes. Please use it on our site or app to proceed." + 
			    "\n\nIf you did not request this code, you can ignore this message." + 
			    "\n\nFor any questions or assistance, feel free to contact us here at this gmail" + 
			    "\n\nBest regards,\nPriya sinha\nSmartContactManager Support Team";

	
		this.emailservice.sendMail(receiver, subject, content);
		
		
		
		String imageurl = "https://www.pngmart.com/files/23/Profile-PNG-Photo.png";
		
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl(imageurl);	
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement" + agreement);
			System.out.println("USER" + user);
			
			model.addAttribute("user" , user);
			model.addAttribute("otp" , otp);
			return "code_verify";
			
			//User resultUser =this.userRepository.save(user);
			
			
//		  model.addAttribute("user" , new User());
//		  
//		  session.removeAttribute("message"); //remove message already appearing for past
//		  session.setAttribute("message", new Message("Successfully registerd","alert-success"));
//		  
		  
			

			
			
		} catch (Exception e) {
			
			
			
			e.printStackTrace();
			session.removeAttribute("message"); //remove message already appearing for past
			session.setAttribute("message", new Message("Something Went Wrong!!"+e.toString(),"alert-danger"));
			
			 
			return "signup";
		}
		
		}
	
	@PostMapping("/handle_user")
	public String handleuser(@ModelAttribute("user")User user , @RequestParam("orgcode")String orgcode , @RequestParam("inpcode")String code ,  Model model , HttpSession session) {
		
		System.out.println(orgcode);
		System.out.println(code);
		
		if(orgcode.equals(code)) {
			session.setAttribute("message", new Message("Succesfully registered" , "alert-success"));
			try {
				
				this.userRepository.save(user);
			}catch(Exception e) {
				session.removeAttribute("message"); //remove message already appearing for past
				session.setAttribute("message", new Message("Something Went Wrong!!"+e.toString(),"alert-danger"));
			}
			return "signup";
		}
		else {
			System.out.println("Hello");
			model.addAttribute("user" , user);
			return "code_verify";
		}
		
		
	}
	
	
	
	//creating the login page
	@GetMapping("/signin")
	public String Signin(Model model) {
		model.addAttribute("title","Login-Smart Contact Manager");
		return "login";
	}
	
}
