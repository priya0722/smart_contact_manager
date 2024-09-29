package com.project.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class Cloudiniary_Image {

	@Bean
	public Cloudinary getCloudinary() {
		Map<String , Object> config = new HashMap<>();
		
		String cloud_name= System.getenv("cloud_name");
		String cloud_api_key= System.getenv("cloud_api_key");
		String cloud_api_secret= System.getenv("cloud_api_secret");
		 
		 
		config.put("cloud_name" , cloud_name);
		config.put("api_key",  cloud_api_key);
		config.put("api_secret", cloud_api_secret);
		config.put("api_secure", true);
		
		return new Cloudinary(config);
	}
	
}
