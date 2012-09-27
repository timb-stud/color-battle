package de.htw.colorbattle.bluetooth;

import java.util.Iterator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.utils.SerializeUtils;

/**
 * Manager Class for all BluetoothMultiplayer
 * 
 */
public class BluetoothMultiplayer {
	public static final int MESSAGE_READ = 1;
	public AcceptThread acceptThread;
	public ConnectThread connectThread;
	public ConnectionThread connectionThread;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private ColorBattleGame colorBattleGame;
	private boolean  isServer = false;
	
	public BluetoothMultiplayer() {
	}
	
	/**
	 * This function is called when a connection between two bluetooth devices is established.
	 * @param socket bluetooth socket
	 */
	public void manageConnectedSocket(BluetoothSocket socket) {
    	connectionThread = new ConnectionThread(socket, mHandler);
    	connectionThread.start();
 
    	if(!isServer)
    		colorBattleGame.multiGame.joinGame();
	}
	
	/**
	 * Set colorBattleGame variable
	 * @param colorBattleGame
	 */
	public void setColorBattleGame(ColorBattleGame colorBattleGame) {
		this.colorBattleGame = colorBattleGame;
	}
	
	/**
	 * Tries to connect to the first bluetooth device in the paired devices list.
	 */
	public void connect(){
		Iterator<BluetoothDevice> bluetoothIterator = mBluetoothAdapter.getBondedDevices().iterator();
		if (!bluetoothIterator.hasNext())
			throw new RuntimeException("No bluetooth device was found.");
		BluetoothDevice device = bluetoothIterator.next();
		connectThread = new ConnectThread(device, this);
		connectThread.start();
	}
	
	/**
	 * Starts a bluetooth server that waits for a connection.
	 */
	public void startServer(){
		this.acceptThread = new AcceptThread(this);
		this.acceptThread.start();
		this.isServer = true;
	}
	
	/**
	 * Sends a serializable object to the bluetooth device.
	 * @param obj
	 */
	public void send(Object obj){
		if(connectionThread != null){
			connectionThread.write(SerializeUtils.serializeObject(obj));
		}
	}
	
	/**
	 * This method gets called if a new object is received.
	 * @param obj
	 */
	private void receive(Object obj){
		colorBattleGame.multiGame.update(null, obj);
	}
	
	/**
	 * 
	 * @return true if this device is the server
	 * 			false if this device is not the server
	 */
	public boolean isServer(){
		return isServer;
	}
	
	/**
	 * Handler for incoming messages.
	 * 
	 * It calls the BluetoothMultiplayer.receive method with the received object.
	 */
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
