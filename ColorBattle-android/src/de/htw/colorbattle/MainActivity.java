package de.htw.colorbattle;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.htw.colorbattle.bluetooth.BluetoothActionResolverAndroid;
import de.htw.colorbattle.bluetooth.BluetoothMultiplayer;
import de.htw.colorbattle.config.GameMode;
import de.htw.colorbattle.config.RuntimeConfig;
import de.htw.colorbattle.network.MainActivityInterface;


public class MainActivity extends AndroidApplication implements MainActivityInterface{
	
	private  WifiManager wifiManager;
	private MulticastLock multicastLock; 
	private ColorBattleGame colorBattleGame;
	BluetoothMultiplayer bluetoothMultiplayer;
	BluetoothActionResolverAndroid bluetoothActionResolverAndroid;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	private static final int REQUEST_ENABLE_BT = 3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.println(LOG_DEBUG, "INIT", "YEAH!");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useAccelerometer = true;
        cfg.useCompass = false;
        
        RuntimeConfig bcConfig = new RuntimeConfig();
        bcConfig.isWifiConnected = isWifiConnected();
        bcConfig.multicastAddress = "230.0.0.1";
        bcConfig.multicastPort = 1334; //TODO read multicast port from settings view
        bcConfig.playSound = false;
        bcConfig.networkPxlUpdateIntervall = 0.1f;
        bcConfig.gameMode = GameMode.OFF; //default is OFF
        
       	this.bluetoothMultiplayer = new BluetoothMultiplayer();
        bluetoothActionResolverAndroid = new BluetoothActionResolverAndroid(bluetoothMultiplayer);
        this.colorBattleGame = new ColorBattleGame(bcConfig, bluetoothActionResolverAndroid, this);
        initialize(colorBattleGame, cfg);
        this.bluetoothMultiplayer.setColorBattleGame(colorBattleGame);
    }
    
    private boolean isWifiConnected(){
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	return mWifi.isConnected();
    }
    
    private boolean isBluetoothEnabled(){
    	if(mBluetoothAdapter == null) //device doesn't support bluetooth
    		return false;
    	return mBluetoothAdapter.isEnabled();
    }
    
    public void enableBluetoothQuestion(){
        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	AcquireMulticastLock();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	ReleaseMulticastLock();
    }
    
    /**
     * Needed to receive multicast messages on (some) android devices
     */
    private void AcquireMulticastLock(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifiManager.createMulticastLock("myMulticastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();
    	Log.d("MulticastLock","multicastLock.acquire()");
    }
    
    private void ReleaseMulticastLock(){
    	 multicastLock.release();
    	 Log.d("MulticastLock","multicastLock.release()");
    }
    
}