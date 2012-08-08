package de.htw.colorbattle.network;

import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = -5110015554555952175L;

	private String message;

	public Message(String message){
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
