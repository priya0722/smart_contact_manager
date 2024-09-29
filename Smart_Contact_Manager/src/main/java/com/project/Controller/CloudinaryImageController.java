package com.project.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.Service.CloudinaryImageService;

@RestController
@RequestMapping("/cloudinary/upload")
public class CloudinaryImageController {
	
	@Autowired
	private CloudinaryImageService cloudinaryImageService;

	@PostMapping
	public ResponseEntity<Map> uploadImage(@RequestParam("image")MultipartFile file){
		
		 try {
	            Map data = this.cloudinaryImageService.upload(file);
	            String url = (String) data.get("url");
	            System.out.println(url);
	            
	            return new ResponseEntity<>(data, org.springframework.http.HttpStatus.ACCEPTED);
	        } catch (Exception e) {
	            // Handle the exception and return an appropriate response
	            return new ResponseEntity<>(Map.of("error", e.getMessage()), org.springframework.http.HttpStatus.BAD_REQUEST);
	        }
		
	}
	
}
