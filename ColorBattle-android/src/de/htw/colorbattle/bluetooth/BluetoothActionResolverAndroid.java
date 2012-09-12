package de.htw.colorbattle.bluetooth;

import de.htw.colorbattle.network.BluetoothActionResolver;

public class BluetoothActionResolverAndroid implements BluetoothActionResolver{
	
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
}
