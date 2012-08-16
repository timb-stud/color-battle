package de.htw.colorbattle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.htw.colorbattle.config.BattleColorConfig;

public class MainActivity extends AndroidApplication {
	
	private  WifiManager wifiManager;
	private MulticastLock multicastLock;
	private WifiLock wifiLock;
	
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
    	acquireWifiLock();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	ReleaseMulticastLock();
    	releaseWifiLock();
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
    
    private void acquireWifiLock(){
    	wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "myWifiLock");
    	wifiLock.acquire();
    	wifiLock.setReferenceCounted(true);
    	Log.d("WIFI Lock", wifiLock.toString());
    }
    
    private void releaseWifiLock(){
    	wifiLock.release();
    }
    
    private void ReleaseMulticastLock(){
    	 multicastLock.release();
    	 Log.d("MulticastLock","multicastLock.release()");
    }
    
}