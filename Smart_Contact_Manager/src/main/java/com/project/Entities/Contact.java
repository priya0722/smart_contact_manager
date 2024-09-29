package com.project.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	public int cId;
	public String name;
	public String email;
	public String secondName;
	public String work;
	public String phone;
	public String imageUrl;
	
	@Column(length=5000)
	public String description;
	
	@ManyToOne
	@JsonIgnore   //we dont want to serialize it when searching contact
    public User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	//@Override
//	public String toString() {
//		return "Contact [cId=" + cId + ", name=" + name + ", email=" + email + ", secondName=" + secondName + ", work="
//				+ work + ", phone=" + phone + ", imageUrl=" + imageUrl + ", description=" + description + ", user="
//				+ user + ", getUser()=" + getUser() + ", getcId()=" + getcId() + ", getName()=" + getName()
//				+ ", getEmail()=" + getEmail() + ", getSecondName()=" + getSecondName() + ", getWork()=" + getWork()
//				+ ", getPhone()=" + getPhone() + ", getImageUrl()=" + getImageUrl() + ", getDescription()="
//				+ getDescription() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
//				+ super.toString() + "]";
//	}
//	
//	
	
	
	
}
