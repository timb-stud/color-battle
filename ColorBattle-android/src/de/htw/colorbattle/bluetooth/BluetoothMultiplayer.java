package de.htw.colorbattle.bluetooth;

import java.util.Iterator;
import java.util.Observable;

import com.badlogic.gdx.utils.Json.Serializable;

import de.htw.colorbattle.GameScreen;
import de.htw.colorbattle.utils.SerializeUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothMultiplayer extends Observable {
	public static final int MESSAGE_READ = 1;
	public static AcceptThread acceptThread;
	public static ConnectThread connectThread;
	public ConnectionThread connectionThread;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	public BluetoothMultiplayer() {
		this.acceptThread = new AcceptThread(this);
		this.acceptThread.start();
	}
	
	
	public void manageConnectedSocket(BluetoothSocket socket) {
    	connectionThread = new ConnectionThread(socket, mHandler);
    	connectionThread.start();
	}
	
	public void connect(){
		Iterator<BluetoothDevice> bluetoothIterator = mBluetoothAdapter.getBondedDevices().iterator();
		if (!bluetoothIterator.hasNext())
			throw new RuntimeException("No bluetooth device was found.");
		BluetoothDevice device = bluetoothIterator.next();
		connectThread = new ConnectThread(device, this);
		connectThread.start();
	}
	
	public void send(Object obj){
		connectionThread.write(SerializeUtils.serializeObject(obj));
	}
	
	private void receive(Object obj){
	    setChanged();
	    notifyObservers(obj);
	}
	
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                Object obj = SerializeUtils.deserializeObject(readBuf);
                receive(obj);
                break;
            }
        }
    };
}
