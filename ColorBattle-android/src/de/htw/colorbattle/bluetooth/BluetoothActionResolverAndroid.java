package de.htw.colorbattle.bluetooth;

import java.util.Observer;

import de.htw.colorbattle.network.NetworkActionResolver;

/**
 * 
 * Android implementation of the NetworkActionResolver interface.
 * This interface is necessary to call android specific methods from the LibGDX code.
 * 
 */
public class BluetoothActionResolverAndroid implements NetworkActionResolver{
	
	BluetoothMultiplayer bluetoothMultiplayer;
	
	public BluetoothActionResolverAndroid(BluetoothMultiplayer bluetoothMultiplayer) {
		this.bluetoothMultiplayer = bluetoothMultiplayer;
	}

	/**
	 * Send a message per bluetooth
	 */
	public void send(Object obj) {
		bluetoothMultiplayer.send(obj);
	}
	
	/**
	 * Starts a new bluetooth server
	 */
	public void startServer(){
		bluetoothMultiplayer.startServer();
	}
	
	/**
	 * Tries to connect to an other bluetooth device
	 */
	public void connect(){
		bluetoothMultiplayer.connect();
	}
	
	/**
	 * @return true if this device is the server
	 * 			false if this device is not the server
	 */
	public boolean isServer() {
		return bluetoothMultiplayer.isServer();
		
	}
	
	/**
	 * Not implemented
	 */
	public void addObserver(Observer observer) {
		// TODO Auto-generated method stub
		
	}
	
}
