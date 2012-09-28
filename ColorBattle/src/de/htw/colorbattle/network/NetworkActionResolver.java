package de.htw.colorbattle.network;

import java.util.Observer;

import de.htw.colorbattle.exception.NetworkException;

/**
 * 
 * This interface is necessary to call android specific methods from the LibGDX code.
 */
public interface NetworkActionResolver {
	public void send(Object obj)  throws NetworkException;;
	public void connect();
	public void startServer();
	public boolean isServer();
	
	public void addObserver(Observer observer);
}
