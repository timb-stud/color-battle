package de.htw.colorbattle.exception;

/**
 * Class to catch exceptions on network service.
 * 
 */

public class NetworkException extends Exception{

	private static final long serialVersionUID = -1636743259242441488L;

	/**
	 * Constructor of the class to set exception message.
	 * @param msg exception message
	 */
	
	public NetworkException(String msg){
		super(msg);
	}
		
}
