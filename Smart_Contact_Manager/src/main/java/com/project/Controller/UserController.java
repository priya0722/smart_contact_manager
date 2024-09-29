package com.project.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.Entities.Contact;
import com.project.Entities.User;
import com.project.Helper.Message;
import com.project.Service.CloudinaryImageService;
import com.project.dao.UserRepository;
import com.project.dao.contactRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	public UserRepository userRepository;
	@Autowired
	public contactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private CloudinaryImageService cloudinaryImageService;
	
	
	@ModelAttribute   //it will run always
	public void common(Model model , Principal principal) {
		String UserName = principal.getName();  //usename is actually the email
		 User user =  userRepository.getUserByUserName(UserName);
		 
		 model.addAttribute("user" , user);
	}
	

	@GetMapping("/dashboard")
	public String dashboard(Model model , Principal principal) {
	     model.addAttribute("title" , "dashboard");
		return"normal/user_dashboard";
	}
	
	
	//open add contact form handler
	@GetMapping("/add-contact")
	public String openAddContactForm( Model model) {
		model.addAttribute("title" , "Add-Contact");
		model.addAttribute("contact" , new Contact());
		return "normal/add_contact_form";
	}
	
	//process add conatct using handler
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact , @RequestParam("profile-image") MultipartFile file , Principal principal , HttpSession session) {
		
		try {
			
			//get the user
			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);
			
			String imageurl = "https://www.pngmart.com/files/23/Profile-PNG-Photo.png";
			
			//procesingand uploadinf file
			if(file.isEmpty()) {
				
				//by default image
				contact.setImageUrl(imageurl);
			}
			else {
				
//				String fileName =file.getOriginalFilename();
//	            contact.setImageUrl(fileName);
//
//	            String UPLOAD_DIR = Paths.get("src/main/resources/static/images").toAbsolutePath().toString();	
//				
//			Files.copy(file.getInputStream(), Path.of(UPLOAD_DIR + "\\" + file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
				
				
				
				//save the data cloudinary and save then get link from there and then save it in database
				@SuppressWarnings("unchecked")
				Map<String , Object> data = this.cloudinaryImageService.upload(file);
				String url = (String) data.get("url");
				contact.setImageUrl(url);
			}
			
			
			
			user.getContacts().add(contact); // add the contact to the user
			contact.setUser(user); // set the userid to the contact databse
			userRepository.save(user);
			
	
			System.out.println(contact.imageUrl);
			
			//on succesfully addition of contact
			session.setAttribute("message", new Message("your Contact is Successfully added", "alert-success") );
			
			System.out.println("DATA"+ contact);
			
		}catch(Exception e) {
			
			session.setAttribute("message", new Message("Something went wrong !! Try Again" , "alert-danger"));
			System.out.println(e.getLocalizedMessage());
			
		}
		
		
		return "normal/add_contact_form";
	}
	
	
	
	//hnadler to show contacts
    @GetMapping("/show-contacts")
	public String showContacts(Model model , Principal principal) {
		
		model.addAttribute("title" , "View-contacts");
		
		//contact ko bhej rhe h
		String Username = principal.getName();  //principal ke help se hum jiske id se login h uska name return kr skte h
	    User user =	userRepository.getUserByUserName(Username); //ab us name ke help se us user ko pt kr rhe h
	
	List<Contact> contacts = 	this.contactRepository.findContactsByUser(user.getId()); // ab userid ke help se pta kr rhe h ki cointact table me konse users h jisme userid yha se bheje jane wale userid ke barabar h
	
	model.addAttribute("contacts", contacts);
		
		return "normal/show_contacts";
	}
	
    
    
    //shewing particular contact detail on clicking on email
    
    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId , Model model , Principal principal) {
    	
    Optional<Contact> contactOptional =	this.contactRepository.findById(cId);
    Contact contact = contactOptional.get();
    
    
    String userName = principal.getName();
    User user = this.userRepository.getUserByUserName(userName);
    
    //ye check krna jsurui h kuki link esa h ki agr koi dusra user kisi dusre user ke ontact ko hit and trial se c=detail le slta h link me cid ke jgh jgh value bhej kr  isliyr user ki id or contsct me save userId ko macth krke hi detail dikhayenge
    if(user.getId()==contact.getUser().getId()) {
    	 model.addAttribute("contact",contact);
    	 model.addAttribute("title",contact.getName());
    }
    
    
   
    	return "normal/contact_detail";
    }
    
    
    //delete the contact
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId , Model model , Principal principal ,HttpSession session) {
    	
    	Optional<Contact> contactOptional = this.contactRepository.findById(cId);
    	Contact contact = contactOptional.get();
    	
    	 String Username = principal.getName();
    	 User user  =this.userRepository.getUserByUserName(Username);
    	
    	if(user.getId() == contact.getUser().getId()) {
    		contact.setUser(null); //unlink the contacr fro the user and then delete
    		this.contactRepository.delete(contact);
    		//also deleting the comtact image from folder
    		String imgstr = contact.getImageUrl();
    		String filePath = "src/main/resources/static/images/" + imgstr;
    		File file = new File(filePath);
    		file.delete();
    		
    		
    		
            session.setAttribute("message", new Message("contact deleted successfully" , "alert-success"));     		
    	}
    	
    	return "redirect:/user/show-contacts";
    }
    
    
    //update the contact
    @PostMapping("/update-contact/{cId}")
    public String updateForm( @PathVariable("cId")Integer cId , Model model) {
    	model.addAttribute( "title","update Contact");
    	
    	Optional <Contact> optionalContact = this.contactRepository.findById(cId);
    	Contact contact = optionalContact.get();
    	model.addAttribute("contact" , contact);
    	
    	
    	return "normal/update_form";
    }
    
    //process the update comment
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact  , @RequestParam("profile-image")  MultipartFile file , Model model , HttpSession session , Principal principal) {
    	
    	//old contact detail
    	Optional<Contact> optionalOldContactDetail = this.contactRepository.findById(contact.getcId());
    	Contact oldContactDetail = optionalOldContactDetail.get();
    			
    		try {
    		if(!file.isEmpty()) {
    			//delete old
    			String imgstr = oldContactDetail.getImageUrl();
    			String filepath = "src/main/resources/static/images/" + imgstr;
    			File myfile = new File(filepath);
    			myfile.delete();
    			
    			
    			//update new photo , pehle image ko leke images wale folder me rakhenge uske baad wha se save krenge database me
    			
    			String filename = file.getOriginalFilename();
    		    String UPLOAD_DIR = Paths.get("src/main/resources/static/images").toAbsolutePath().toString();
    				
    			Files.copy(file.getInputStream() , Path.of(UPLOAD_DIR + "\\" + file.getOriginalFilename()) , StandardCopyOption.REPLACE_EXISTING);
    			contact.setImageUrl(file.getOriginalFilename());
    		}
    		else {
    			contact.setImageUrl(oldContactDetail.getImageUrl());
    		}
    		
    		
    		User user = this.userRepository.getUserByUserName(principal.getName());
    		contact.setUser(user);
    		this.contactRepository.save(contact);
    		
    		session.setAttribute("message",new Message("Your Contact is updated" , "alert-success"));
    		
    		
    	}
    	catch(Exception e) {
    		
    	}
    	
    	System.out.println(contact.getName());
    	return "redirect:/user/" +contact.getcId() + "/contact";
    }
    
    //user profile
    @GetMapping("/profile")
    public String yourProfile(Model model) {
    	model.addAttribute("title","My Profile");
    	return "normal/profile";
    }
    
    //open setting handler
    @GetMapping("/settings")
    public String openSetting() {
    	return "normal/settings";
    }
    
    
    //change password handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword")String oldPassword , @RequestParam("newPassword")String newPassword , Principal principal ,HttpSession session) {
    	
    	System.out.println(oldPassword);
    	System.out.println(newPassword);
    	
    	//first determine the password from the database and then match with old password eneterd by user
    	User currentUser = this.userRepository.getUserByUserName(principal.getName());
    	
    	if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
    		//change the password
    		currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
    		this.userRepository.save(currentUser);
    		session.setAttribute("message", new Message("Your Password is successfully changed", "alert-success"));
    	}
    	else{
    		//warn message
    	session.setAttribute("message", new Message("Old password not matched", "alert-danger"));
    	return "redirect:/user/settings";
    	}
    	
    	System.out.println(currentUser.getPassword());
    	
    	return "redirect:/user/dashboard";
    }
    
    
    
    //creating order for payment
    @PostMapping("/create-order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String , Object> data , Model model) throws RazorpayException {
    	System.out.println(data);
    	
    	//using this to get environment variable
    	String razorKeyId = System.getenv("razor_key_id");
    	String razorKeySecret = System.getenv("razor_key_secret");
    	
    	
    	int amt = Integer.parseInt(data.get("amount").toString());
    	var client = new RazorpayClient(razorKeyId , razorKeySecret) ;
    	
    	JSONObject ob = new JSONObject();
    	ob.put("amount", amt * 100); //in paise
    	ob.put("currency", "INR");
    	ob.put("receipt" , "txn_235425");
    	
    	//creating new order
    Order order =	client.orders.create(ob);
    //if we want we can save this it in database
    System.out.println(order);
    
  
    	
    	
    	
    	
    	return order.toString();
    }
    
    
    //handler tp send environment variable in javascript file
    @RequestMapping("/sendenv")
    @ResponseBody
    public Map<String, String> sendEnv() {
        Map<String, String> mymap = new HashMap<>();
        
        String razorKeyId = System.getenv("razor_key_id");
    	String razorKeySecret = System.getenv("razor_key_secret");
    	
    	
    	
        mymap.put("razorKeyId",razorKeyId );
        mymap.put("razorKeySecret",razorKeySecret );
        return mymap;
    }
    
	
}
