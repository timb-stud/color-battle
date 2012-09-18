package de.htw.colorbattle;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.htw.colorbattle.bluetooth.BluetoothActionResolverAndroid;
import de.htw.colorbattle.bluetooth.BluetoothMultiplayer;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.network.BtDeviceListInterface;

public class MainActivity extends AndroidApplication implements BtDeviceListInterface{
	
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
        
        initDeviceListDialog();
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useAccelerometer = true;
        cfg.useCompass = false;
        
        BattleColorConfig bcConfig = new BattleColorConfig();
        bcConfig.playSound = false;
        bcConfig.networkPxlUpdateIntervall = 0.1f;
        bcConfig.width = 800;
        bcConfig.height = 480;
        bcConfig.isSinglePlayer = false;
        
//        BtDeviceList btDeviceList = new BtDeviceList();
//        btDeviceList.init();
//        Intent intent = new Intent(this, BtDeviceList.class);
//        startActivity(intent);
        
       	this.bluetoothMultiplayer = new BluetoothMultiplayer();        
        bluetoothActionResolverAndroid = new BluetoothActionResolverAndroid(bluetoothMultiplayer);
        this.colorBattleGame = new ColorBattleGame(bcConfig, bluetoothActionResolverAndroid, this);
        initialize(colorBattleGame, cfg);
        this.bluetoothMultiplayer.setColorBattleGame(colorBattleGame);
    }
    
    
    private boolean isBluetoothEnabled(){
    	if(mBluetoothAdapter == null) //device doesn't support bluetooth
    		return false;
    	return mBluetoothAdapter.isEnabled();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	enableBluetoothQuestion();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    private void enableBluetoothQuestion(){
        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }
    
	Dialog deviceListDialog;
    Handler dialogHandler;
    
    private void initDeviceListDialog(){
        dialogHandler = new Handler();
        deviceListDialog = new Dialog(this);
//        deviceListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deviceListDialog.setContentView(R.layout.device_list);
        deviceListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    } 
	
    public void ShowDialog(){
        dialogHandler.post(showDialogRunnable);
   }

   final Runnable showDialogRunnable = new Runnable(){
          public void run() {
             // TODO Auto-generated method stub
             if(deviceListDialog!=null && !deviceListDialog.isShowing())
                   deviceListDialog.show();
             }

   };
   
   public void HideDialog(){
        dialogHandler.post(hideDialogRunnable);
   }
   
   final Runnable hideDialogRunnable=new Runnable(){
          public void run() {
             // TODO Auto-generated method stub
             if(deviceListDialog!=null && !deviceListDialog.isShowing())
                   deviceListDialog.dismiss();
             }
   };
}