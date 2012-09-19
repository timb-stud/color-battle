package de.htw.colorbattle.bluetooth;

import java.util.Observer;

import de.htw.colorbattle.network.NetworkActionResolver;

public class BluetoothActionResolverAndroid implements NetworkActionResolver{
	
	BluetoothMultiplayer bluetoothMultiplayer;
	
	public BluetoothActionResolverAndroid(BluetoothMultiplayer bluetoothMultiplayer) {
		this.bluetoothMultiplayer = bluetoothMultiplayer;
	}

	public void send(Object obj) {
		bluetoothMultiplayer.send(obj);
	}
	
	public void startServer(){
		bluetoothMultiplayer.startServer();
	}
	
	public void connect(){
		bluetoothMultiplayer.connect();
	}
	
	public boolean isServer() {
		return bluetoothMultiplayer.isServer();
		
	}

	public void addObserver(Observer observer) {
		// TODO Auto-generated method stub
		
	}
	
}
