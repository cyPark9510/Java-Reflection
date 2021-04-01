package com.pcy.reflect.controller.dto;

public class LoginDto {

	private String username;
	private String password;
	
	@Override
	public String toString() {
		return "JoinDto [username=" + username + ", password=" + password + "]";
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
