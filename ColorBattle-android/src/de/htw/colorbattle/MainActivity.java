package de.htw.colorbattle;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.htw.colorbattle.bluetooth.BluetoothActionResolverAndroid;
import de.htw.colorbattle.bluetooth.BluetoothMultiplayer;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.config.GameMode;

public class MainActivity extends AndroidApplication {
	
	private  WifiManager wifiManager;
	private MulticastLock multicastLock; 
	private ColorBattleGame colorBattleGame;
	BluetoothMultiplayer bluetoothMultiplayer;
	BluetoothActionResolverAndroid bluetoothActionResolverAndroid;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.println(LOG_DEBUG, "INIT", "YEAH!");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useAccelerometer = true;
        cfg.useCompass = false;
        
        BattleColorConfig bcConfig = new BattleColorConfig();
        bcConfig.isWifiConnected = isWifiConnected();
        bcConfig.multicastAddress = "230.0.0.1";
        bcConfig.multicastPort = 1334; //TODO read multicast port from settings view
        bcConfig.playSound = false;
        bcConfig.networkPxlUpdateIntervall = 0.1f;
        bcConfig.width = 800;
        bcConfig.height = 480;
        bcConfig.multigamePlayerCount = 2;
        bcConfig.gameMode = isBluetoothEnabled() ? GameMode.BLUETOOTH : GameMode.WIFI;
        
        if(bcConfig.gameMode == GameMode.BLUETOOTH)
        	Gdx.app.log("GameMode", "Bluetooth game is enabled. Hint: Disable your bluetooth to start Wifi game!");
        else
        	Gdx.app.log("GameMode", "Wifi game is enabled. Hint: Enable your bluetooth to start bluetooth game!");
        
        bluetoothActionResolverAndroid = new BluetoothActionResolverAndroid(bluetoothMultiplayer);
        this.colorBattleGame = new ColorBattleGame(bcConfig, bluetoothActionResolverAndroid);
        
        initialize(colorBattleGame, cfg);
        
        if(bcConfig.gameMode == GameMode.BLUETOOTH){
        	this.bluetoothMultiplayer = new BluetoothMultiplayer(colorBattleGame);
        }
    }
    
    private boolean isWifiConnected(){
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	return mWifi.isConnected();
    }
    
    private boolean isBluetoothEnabled(){
    	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(mBluetoothAdapter == null) //device doesn't support bluetooth
    		return false;
    	return mBluetoothAdapter.isEnabled();
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