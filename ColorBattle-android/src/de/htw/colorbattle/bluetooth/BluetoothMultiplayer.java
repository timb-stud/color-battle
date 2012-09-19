package de.htw.colorbattle.bluetooth;

import java.util.Iterator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.utils.SerializeUtils;

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
	
	
	public void manageConnectedSocket(BluetoothSocket socket) {
    	connectionThread = new ConnectionThread(socket, mHandler);
    	connectionThread.start();
    	
//    	if(this.isServer){
//    		colorBattleGame.gameScreen.swapPlayers();
//    	}
 
//    	if(!isServer)
    		colorBattleGame.multiGame.joinGame();
	}
	
	public void setColorBattleGame(ColorBattleGame colorBattleGame) {
		this.colorBattleGame = colorBattleGame;
	}
	
	public void connect(){
		Iterator<BluetoothDevice> bluetoothIterator = mBluetoothAdapter.getBondedDevices().iterator();
		if (!bluetoothIterator.hasNext())
			throw new RuntimeException("No bluetooth device was found.");
		BluetoothDevice device = bluetoothIterator.next();
		connectThread = new ConnectThread(device, this);
		connectThread.start();
	}
	
	public void startServer(){
		this.acceptThread = new AcceptThread(this);
		this.acceptThread.start();
		this.isServer = true;
	}
	
	public void send(Object obj){
		if(connectionThread != null){
			Log.d("BT", "send: " + obj.toString());
			connectionThread.write(SerializeUtils.serializeObject(obj));
		}
	}
	
	private void receive(Object obj){
		colorBattleGame.multiGame.update(null, obj);
		Log.d("BT", "receive: " + obj.toString());
	}
	
	public boolean isServer(){
		return isServer;
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
