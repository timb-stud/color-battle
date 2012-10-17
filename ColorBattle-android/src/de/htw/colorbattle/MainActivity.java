package de.htw.colorbattle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;

import de.htw.colorbattle.bluetooth.BluetoothActionResolverAndroid;
import de.htw.colorbattle.bluetooth.BluetoothMultiplayer;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.config.GameMode;
import de.htw.colorbattle.config.RuntimeConfig;
import de.htw.colorbattle.network.MainActivityInterface;

/**
 * 
 * Main Activity for the android game
 *
 */
public class MainActivity extends AndroidApplication implements MainActivityInterface{
	
	private  WifiManager wifiManager;
	private MulticastLock multicastLock;
	private ColorBattleGame colorBattleGame;
	BluetoothMultiplayer bluetoothMultiplayer;
	BluetoothActionResolverAndroid bluetoothActionResolverAndroid;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	private static final int REQUEST_ENABLE_BT = 3;
	
	/**
	 * Initializes all android specific and game relevant objects
	 */
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
        bcConfig.useExtConfigFile = true;
        bcConfig.isWifiConnected = isWifiConnected();
        bcConfig.multicastAddress = "230.0.0.1";
        bcConfig.multicastPort = 1334; //TODO read multicast port from settings view
        bcConfig.playSound = true;
        bcConfig.networkPxlUpdateIntervall = 0.1f;
        BattleColorConfig.DEVICE_ID = getDeviceId();
        bcConfig.gameMode = GameMode.OFF; //default is OFF
        bcConfig = configToExternalStorage(bcConfig);
        
       	this.bluetoothMultiplayer = new BluetoothMultiplayer();
        bluetoothActionResolverAndroid = new BluetoothActionResolverAndroid(bluetoothMultiplayer);
        this.colorBattleGame = new ColorBattleGame(bcConfig, bluetoothActionResolverAndroid, this);
        initialize(colorBattleGame, cfg);
        this.bluetoothMultiplayer.setColorBattleGame(colorBattleGame);
    }
    
    /**
     * Saves the config file on the external storage as json file.
     * If a config file exists, it will be load on game start
     * @param config
     * @return
     */
    private RuntimeConfig configToExternalStorage(RuntimeConfig config){
    	if(!canWriteExtStorage())
    		return config;
    	
		String FILENAME = "ColorBattleConfig.json";
		Gson gson = new Gson();
		String json = gson.toJson(config);
    	
		File path = getExternalFilesDir(null);
    	File file = new File(path, FILENAME);
    	Log.d("Json", "Path to config file: " + file.getAbsolutePath());
    	try{
	    	if(!file.exists()){
	            OutputStream os = new FileOutputStream(file);
	            byte[] data =json.getBytes();
	            os.write(data);
	            os.close();
		        Log.d("Json", "Create and save new config file.");
		        return config;
	    	} else {
				InputStream is = new BufferedInputStream(new FileInputStream(file));
				InputStreamReader isr = new InputStreamReader(is);
				RuntimeConfig conf = gson.fromJson( isr, RuntimeConfig.class);
				is.close();
				isr.close();
				if(conf.useExtConfigFile){
					Log.d("Json", "Load config from SD.");
					return conf;
				}
	    	}
		} catch (IOException e) {
	    	Log.d("Json", "Can't read config file.");
			e.printStackTrace();
		}
    	Log.d("Json", "External config load was skipped.");
    	return config;
    }
    
    /**
     * Tests if the external storage is mounted and writeable
     * @return
     */
    private boolean canWriteExtStorage(){
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
    	String state = Environment.getExternalStorageState();

    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // We can read and write the media
    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	    // We can only read the media
    	    mExternalStorageAvailable = true;
    	    mExternalStorageWriteable = false;
    	} else {
    	    // Something else is wrong. It may be one of many other states, but all we need
    	    //  to know is we can neither read nor write
    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
    	}
    	return mExternalStorageWriteable;
    }
    
    /**
     * Checks if wifi is connected
     * @return true if wifi is connected
     * 			false if wifi is not connected
     */
    private boolean isWifiConnected(){
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	return mWifi.isConnected();
    }
    
    /**
     * Checks if bluetooth is enabled
     * @return true if bluetooth is enabled
     * 			false if bleutooth is not enabled
     */
    private boolean isBluetoothEnabled(){
    	if(mBluetoothAdapter == null) //device doesn't support bluetooth
    		return false;
    	return mBluetoothAdapter.isEnabled();
    }
    
    /**
     * Displays a dialog asking to enable bluetooth
     */
    public boolean enableBluetoothQuestion(){
        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        return isBluetoothEnabled();
    }
    
    /**
     * 
     * @return the device id as string
     */
    private String getDeviceId(){
    	  final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

    	    final String tmDevice, tmSerial, androidId;
    	    tmDevice = "" + tm.getDeviceId();
    	    tmSerial = "" + tm.getSimSerialNumber();
    	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

    	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
    	    return deviceUuid.toString();
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
    
    /**
     * Releases the multicast lock
     */
    private void ReleaseMulticastLock(){
    	 multicastLock.release();
    	 Log.d("MulticastLock","multicastLock.release()");
    }
    
}