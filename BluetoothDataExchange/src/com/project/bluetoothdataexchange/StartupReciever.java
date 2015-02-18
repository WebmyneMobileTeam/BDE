package com.project.bluetoothdataexchange;

import com.project.bluetoothdataexchange.pref.SettingsPrefHandler;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class StartupReciever extends BroadcastReceiver {
	IntentFilter bscrFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	SettingsPrefHandler pref;
	public StartupReciever() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		
		pref=new SettingsPrefHandler(context);
		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
		{
			if(!mBluetoothAdapter.isEnabled())
			{
				mBluetoothAdapter.enable();
			}
			else if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
			{
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				context.startActivity(discoverableIntent);	
			}
			
			System.out.println(pref.getBoot());
			if(pref.getBoot())
			{
				Intent startAppIntent = new Intent(context, MainActivity.class);
				startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(startAppIntent);
			}
		
		}
	}
}
