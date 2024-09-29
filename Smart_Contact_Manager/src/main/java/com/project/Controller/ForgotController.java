package com.project.Controller;

import java.security.Principal;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.Entities.User;
import com.project.Helper.Message;
import com.project.Service.EmailService;
import com.project.dao.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	SecureRandom random = new SecureRandom();
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	public EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	//email id
	@RequestMapping("forgot")
	public String openEmailform(HttpSession session) {
		session.removeAttribute("message");
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email")String email , HttpSession session  ) {
		session.removeAttribute("message");
		System.out.println(email);
		
		//generating otp of 4 digit
		
		int otp1 = random.nextInt(999999);
		String otp = String.valueOf(otp1);
		
		System.out.println(otp);
		
		//write code for send otp to mail
		
		
		String receiver = email;
		String subject = "Smart Contact Manager";

				    
		String content = 
				"Hello,\n\n" +
			    "Thank you for using SmartContactManager. Here is the code you requested to confirm your account: "+ otp + 
			    "\n\nThis code will remain active for the next 10 minutes. Please use it on our site or app to proceed." + 
			    "\n\nIf you did not request this code, you can ignore this message." + 
			    "\n\nFor any questions or assistance, feel free to contact us here at this gmail" + 
			    "\n\nBest regards,\nAnkit Raj\nSmartContactManager Support Team";

		User user = this.userRepository.getUserByUserName(email);
		
		if(user!=null) {
			boolean flag =	this.emailService.sendMail(receiver, subject, content);
			
			if(flag) {
				session.removeAttribute("message");
				session.setAttribute("myotp", otp);
				session.setAttribute("email", email);
				session.setAttribute("message", new Message("We have sent OTP to your email..." , "alert-success"));
				return "verify_otp";
			}
			
			
			
		}
		
	
			session.setAttribute("message", new Message("User not exists with this email!!" , "alert-danger"));
			return "forgot_email_form";
		
		
			
	
	}
	
	
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")String otp , HttpSession session , Principal principal) {
		
		String myOtp = (String)session.getAttribute("myotp");
		String email =(String) session.getAttribute("email");
		
		System.out.println(otp);
		System.out.println(myOtp);
		
		if(myOtp != "" && myOtp.equals(otp)) {
			//verirfied and go to password change
			
			session.removeAttribute("message");
			return "password_change_form";
			
		}
		else {
			session.removeAttribute("message");
			session.setAttribute("message", new Message("Incorrect OTP ! Try Again" , "alert-danger"));
			System.out.println("Verify  nhi ho gya !!!!!!!!!!!!!!!");
			return "verify_otp";
		}
		
		
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("newpassword")String newpassword , HttpSession session){	
		
		String email=(String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
		return "redirect:/signin?change=password+changed+sucessfullly..";
		
	}
}
