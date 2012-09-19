package de.htw.colorbattle;

import com.badlogic.gdx.backends.android.AndroidApplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import de.htw.colorbattle.network.BtDeviceListInterface;

public class BtDeviceList extends Activity implements BtDeviceListInterface {

	Dialog deviceListDialog;
    Handler dialogHandler;
    
    public void init(){
        dialogHandler = new Handler();
        deviceListDialog = new Dialog(this);
        deviceListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

   @Override
   public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   init();
   }
}
