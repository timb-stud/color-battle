package de.htw.colorbattle;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.htw.colorbattle.bluetooth.BluetoothActionResolverAndroid;
import de.htw.colorbattle.bluetooth.BluetoothMultiplayer;
import de.htw.colorbattle.config.BattleColorConfig;

public class MainActivity extends AndroidApplication {
	
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
        bcConfig.playSound = false;
        bcConfig.networkPxlUpdateIntervall = 0.1f;
        bcConfig.width = 800;
        bcConfig.height = 480;
        bcConfig.isSinglePlayer = false;
        
       	this.bluetoothMultiplayer = new BluetoothMultiplayer();        
        bluetoothActionResolverAndroid = new BluetoothActionResolverAndroid(bluetoothMultiplayer);
        this.colorBattleGame = new ColorBattleGame(bcConfig, bluetoothActionResolverAndroid);
        initialize(colorBattleGame, cfg);
        this.bluetoothMultiplayer.setColorBattleGame(colorBattleGame);
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
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
}