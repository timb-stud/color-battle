package de.htw.colorbattle;

import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
	
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
//        startActivityForResult(intent, REQUEST_ENABLE_BT);
        fillDeviceList();
        
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
   
   // Member fields
   private BluetoothAdapter mBtAdapter;
   private ArrayAdapter<String> mPairedDevicesArrayAdapter;
   private ArrayAdapter<String> mNewDevicesArrayAdapter;
   private void fillDeviceList(){

       // Setup the window
       requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
       setContentView(R.layout.device_list);

       // Set result CANCELED incase the user backs out
       setResult(Activity.RESULT_CANCELED);

       // Initialize the button to perform device discovery
       Button scanButton = (Button) findViewById(R.id.button_scan);
       scanButton.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
//               doDiscovery();
               v.setVisibility(View.GONE);
           }
       });

       // Initialize array adapters. One for already paired devices and
       // one for newly discovered devices
       mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
       mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

       // Find and set up the ListView for paired devices
       ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
       pairedListView.setAdapter(mPairedDevicesArrayAdapter);
       pairedListView.setOnItemClickListener(mDeviceClickListener);

       // Find and set up the ListView for newly discovered devices
       ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
       newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
       newDevicesListView.setOnItemClickListener(mDeviceClickListener);

       // Register for broadcasts when a device is discovered
       IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
       this.registerReceiver(mReceiver, filter);

       // Register for broadcasts when discovery has finished
       filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
       this.registerReceiver(mReceiver, filter);

       // Get the local Bluetooth adapter
       mBtAdapter = BluetoothAdapter.getDefaultAdapter();

       // Get a set of currently paired devices
       Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

       // If there are paired devices, add each one to the ArrayAdapter
       if (pairedDevices.size() > 0) {
           findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
           for (BluetoothDevice device : pairedDevices) {
               mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
           }
       } else {
           String noDevices = getResources().getText(R.string.none_paired).toString();
           mPairedDevicesArrayAdapter.add(noDevices);
       }
   }
   
   // The on-click listener for all devices in the ListViews
   private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
       public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
           // Cancel discovery because it's costly and we're about to connect
           mBtAdapter.cancelDiscovery();

           // Get the device MAC address, which is the last 17 chars in the View
           String info = ((TextView) v).getText().toString();
           String address = info.substring(info.length() - 17);

           // Create the result Intent and include the MAC address
           Intent intent = new Intent();
           intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

           // Set result and finish this Activity
           setResult(Activity.RESULT_OK, intent);
           finish();
       }
   };
   
   // The BroadcastReceiver that listens for discovered devices and
   // changes the title when discovery is finished
   private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();

           // When discovery finds a device
           if (BluetoothDevice.ACTION_FOUND.equals(action)) {
               // Get the BluetoothDevice object from the Intent
               BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               // If it's already paired, skip it, because it's been listed already
               if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                   mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
               }
           // When discovery is finished, change the Activity title
           } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
               setProgressBarIndeterminateVisibility(false);
               setTitle(R.string.select_device);
               if (mNewDevicesArrayAdapter.getCount() == 0) {
                   String noDevices = getResources().getText(R.string.none_found).toString();
                   mNewDevicesArrayAdapter.add(noDevices);
               }
           }
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