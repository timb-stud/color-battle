package de.htw.colorbattle;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

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
        
        initialize(new ColorBattleGame(), cfg);
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