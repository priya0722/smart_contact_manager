package com.project.Service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryImageServiceImpl implements CloudinaryImageService{

	@Autowired
	public Cloudinary cloudinary;
	
	@Override
	public Map upload(MultipartFile file) {
		
		try {
		Map data =	this.cloudinary.uploader().upload(file.getBytes() , ObjectUtils.emptyMap());
		return data;
		} catch (IOException e) {
			throw new RuntimeException("Image uploading fail");
		}
		
	}

	
	
}
