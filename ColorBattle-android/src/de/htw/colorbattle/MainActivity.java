package de.htw.colorbattle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.htw.colorbattle.config.BattleColorConfig;

public class MainActivity extends AndroidApplication {
	
	private  WifiManager wifiManager;
	private MulticastLock multicastLock; 
	
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
        bcConfig.multicastPort = 1234; //TODO read multicast port from settings view
        bcConfig.playSound = false;
        bcConfig.networkPxlUpdateIntervall = 0.1f;
        bcConfig.multigamePlayerCount = 2;
        
        initialize(new ColorBattleGame(bcConfig), cfg);
    }
    
    private boolean isWifiConnected(){
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	return mWifi.isConnected();
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