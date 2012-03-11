package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.Date;

public class User {
	private String username;
	private String email;
	private Date createdAt;

	public User(String username, String email, Date createdAt) {
		this.username = username;
		this.email = email;
		this.createdAt = createdAt;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
}
