package com.project.bluetoothdataexchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.project.bluetoothdataexchange.pref.SettingsPrefHandler;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {
	SettingsPrefHandler pref;
	final static int DISCOVERY_ENABLE = 0;
	final static int UPDATE_DEVICE_LISTS_ON_CONNECTED = 1;
	final static int UPDATE_FOUND_LIST_ON_APP_CHECK = 2;
	final static int ESTABLISH_SHARE_ON_REQUEST = 3;
	final static int ESTABLISH_SHARE_ON_ACCEPT = 4;
	final static int UPDATE_SENT_MESSAGE = 5;
	final static int UPDATE_RECEIVED_MESSAGE = 6;

	final static UUID MY_UUID = UUID
			.fromString("1252856b-8ef6-4ada-8bc5-9c97fcdea900");
	final static String SERVICE_NAME = "BluetoothDataExchange";

	boolean bscrRegistered = false;
	boolean isFound = false;

	BluetoothStateChangedReceiver bscReceiver = new BluetoothStateChangedReceiver();
	FoundDeviceReceiver foundDeviceReceiver;
	DiscoveryCompleteReceiver discoveryCompleteReceiver;
	UUIDReceiver UuidReceiver;

	IntentFilter bscrFilter = new IntentFilter(
			BluetoothAdapter.ACTION_STATE_CHANGED);
	IntentFilter foundDevicesFilter = new IntentFilter(
			BluetoothDevice.ACTION_FOUND);
	IntentFilter dicoveryCompleteFilter = new IntentFilter(
			BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	IntentFilter UUIDFilter = new IntentFilter(BluetoothDevice.ACTION_UUID);

	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	BluetoothSocket shareSocket;
	public Menu menuItems;
	Button searchButton;
	Button sendTextButton;
	Button backButton;
	
	//ImageButton visiblityButton;
	ProgressBar seacrhProgressBar;
	ListView foundDevicesListView;
	ListView textsListView;
	ViewFlipper viewFlipper;
	TextView deviceNameTV;
	// TextView foundDevicesTextTV;
	TextView noDeviceFoundTextTV;
	EditText typeText;
	String deviceName;

	List<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();

	ArrayAdapter<String> deviceArrayAdapter;
	ArrayAdapter<String> textsListAdapter;

	AcceptConnection acceptThread = null;
	RequestConnection requestThread = null;
	SendMessage sendThread = null;
	ReceiveMessage receiveThread = null;
	UIHandler uiHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	// @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pref = new SettingsPrefHandler(this);
		uiHandler = new UIHandler();

		foundDeviceReceiver = new FoundDeviceReceiver();
		discoveryCompleteReceiver = new DiscoveryCompleteReceiver();
		UuidReceiver = new UUIDReceiver();

		deviceArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1);
		textsListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1);

		foundDevicesListView = (ListView) findViewById(R.id.foundDevicesListView);
		foundDevicesListView.setAdapter(deviceArrayAdapter);

		textsListView = (ListView) findViewById(R.id.textsList);
		textsListView.setAdapter(textsListAdapter);

		typeText = (EditText) findViewById(R.id.typeText);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		deviceNameTV = (TextView) findViewById(R.id.deviceName);
		// foundDevicesTextTV = (TextView) findViewById(R.id.foundDevicesText);
		noDeviceFoundTextTV = (TextView) findViewById(R.id.noDeviceFoundText);

		seacrhProgressBar = (ProgressBar) findViewById(R.id.seacrhProgressBar);
		//visiblityButton=(ImageButton) findViewById(R.id.visi);
		searchButton = (Button) findViewById(R.id.searchButton);
		sendTextButton = (Button) findViewById(R.id.sendTextButton);
		backButton = (Button) findViewById(R.id.backButton);
		
		
		
		//Toast.makeText(getApplicationContext(), "on Activity ", Toast.LENGTH_LONG).show();

		if(pref.getBTooth()){
			mBluetoothAdapter.enable();
		}
		if (!mBluetoothAdapter.isEnabled()) {
			if (!bscrRegistered) {
				registerReceiver(bscReceiver, bscrFilter);
				bscrRegistered = true;
			}
			
			//mBluetoothAdapter.enable();
			// new Handler().postDelayed(new Runnable(){
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// AcceptConnection anotherAcceptThread = new AcceptConnection();
			// anotherAcceptThread.start();
			// }
			//
			// }, 10000);
		} else if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			if (pref.getVisibility()) {
				

				//startActivityForResult(discoverableIntent,
					//	MainActivity.DISCOVERY_ENABLE);
				 Intent discoverableIntent = new
	                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
	                startActivity(discoverableIntent);
			}
		} 
		else {
			acceptThread = new AcceptConnection();
			acceptThread.start();
		}
	

		/// MUNEEF
		/*
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

			visiblityButton.setImageResource(R.drawable.ic_eyeoff);

        }else{
			visiblityButton.setImageResource(R.drawable.ic_eyeon);

        }*/
		
		 registerReceiver(ScanModeChangedReceiver,
	                new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));

	/*	
		visiblityButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

			           
                    Intent discoverableIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                    startActivity(discoverableIntent);
			        }else{
	                    Intent discoverableIntent = new
	                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
	                    startActivity(discoverableIntent);


			        }

			
			
			
			}
			});
*/
		
		
		
		
		
		
		
		
		
		
		
		
		
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub

				isFound = false;
				if (mBluetoothAdapter.startDiscovery()) {
					deviceArrayAdapter.clear();
					searchButton.setEnabled(false);
					seacrhProgressBar.setVisibility(View.VISIBLE);
					foundDevicesListView.setEnabled(false);
					registerReceiver(foundDeviceReceiver, foundDevicesFilter);
					registerReceiver(discoveryCompleteReceiver,
							dicoveryCompleteFilter);
					registerReceiver(UuidReceiver, UUIDFilter);
				} else {
					Toast toast = Toast.makeText(MainActivity.this,
							"Bluetooth was disabled from outside of app",
							Toast.LENGTH_SHORT);
					toast.show();
					mBluetoothAdapter.enable();
					Toast toast1 = Toast
							.makeText(
									MainActivity.this,
									"Please wait for app to turn on bluetooth before trying again",
									Toast.LENGTH_SHORT);
					toast1.show();
					if (!bscrRegistered) {
						registerReceiver(bscReceiver, bscrFilter);
						bscrRegistered = true;
					}
				}
			}
		});

		sendTextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				String msg = typeText.getText().toString();
				if (msg != "") {
					sendThread = new SendMessage(msg);
					sendThread.start();
				}
			}

		});

		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (sendThread != null) {
					sendThread.cancel();
				}
				if (receiveThread != null) {
					receiveThread.cancel();
				}
				acceptThread = new AcceptConnection();
				acceptThread.start();
				textsListAdapter.clear();
				viewFlipper.showPrevious();
				deviceArrayAdapter.clear();
			}

		});

		foundDevicesListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						requestThread = new RequestConnection(foundDevices
								.get(position));
						requestThread.start();
					}
				});

		if (getIntent().getAction() != Intent.ACTION_MAIN) {
			this.moveTaskToBack(true);
		}
	}
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
	    switch(keycode) {
	        case KeyEvent.KEYCODE_MENU:
	          //  doSomething();
    // Toast.makeText(getApplicationContext(), "Menu Button Pressed", Toast.LENGTH_SHORT).show();
	        	try {
					Intent i = new Intent(MainActivity.this, Bl_Settings.class);
					startActivity(i);
				} catch (Exception dd) {

				}

	            return true;
	    }

	    return super.onKeyDown(keycode, e);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		menuItems = menu;
	//menu.getItem(R.id.visi).setIcon(R.drawable.ic_eyeon);
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			//visiblityButton.setImageResource(R.drawable.ic_eyeoff);
			menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
			pref.setStatusVisibility(false);


        }else{
			//visiblityButton.setImageResource(R.drawable.ic_eyeon);
			menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);
			

        }

		
		
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.menu_settings:

			try {
				Intent i = new Intent(MainActivity.this, Bl_Settings.class);
				startActivity(i);
			} catch (Exception dd) {

			}

			break;
			
		case R.id.visible:
            //mBluetoothAdapter.enable();

			if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			
		           
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                startActivity(discoverableIntent);
                	pref.setWButton(true);
		        }
			
			//}
  else{
                   Intent discoverableIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                    startActivity(discoverableIntent);
                	pref.setWButton(true);
					
		        	
                   // discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                   // startActivity(discoverableIntent);
                    
                	pref.setWButton(true);

                	//Toast.makeText(getApplicationContext(), "Visibility are Disabled", Toast.LENGTH_SHORT).show();

                    //Toast.makeText(getApplicationContext(), "Visibility disabled", Toast.LENGTH_SHORT).show();



		        }
			break;
		
		}
		
		return true;
	}
	
	private void tyry() {
		Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);
        	pref.setWButton(true);
		
		
		// TODO Auto-generated method stub
		
	}

	private final BroadcastReceiver ScanModeChangedReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,
                        BluetoothAdapter.ERROR);
              //  String strMode = "";
               
                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
            			//visiblityButton.setImageResource(R.drawable.ic_eyeon);
                    menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);
                    if(!pref.getStatusVisibility()&&pref.getWButton()){
                   // Toast.makeText(getApplicationContext(), "Visibility Enabled", Toast.LENGTH_SHORT).show();
                    pref.setStatusVisibility(true);
                    pref.setWButton(false);
                    }
                    if(!pref.getBlueStatus()){
                     //   Toast.makeText(getApplicationContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                        pref.setBlueStatus(true);
                    }
                    	break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
            			//visiblityButton.setImageResource(R.drawable.ic_eyeoff);
                        menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
                       if(!pref.getBlueStatus()){
                        //    Toast.makeText(getApplicationContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                            pref.setBlueStatus(true);}
                      //  Toast.makeText(getApplicationContext(), " ", Toast.LENGTH_SHORT).show();
                        if(pref.getStatusVisibility()&&pref.getWButton()){
                           // Toast.makeText(getApplicationContext(), "Visibility Disabled", Toast.LENGTH_SHORT).show();
                            pref.setStatusVisibility(false);
                            pref.setWButton(false);
                        }
                        //Toast.makeText(getApplicationContext(), "Visibility disabled", Toast.LENGTH_SHORT).show();

                        
                    	break;	
                    	
                    case BluetoothAdapter.SCAN_MODE_NONE:
            			//visiblityButton.setImageResource(R.drawable.ic_eyeoff);
                        menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
                        if(pref.getStatusVisibility()){
                         //Toast.makeText(getApplicationContext(), "Visibility disabled", Toast.LENGTH_SHORT).show();
                            pref.setStatusVisibility(false);
                            pref.setWButton(false);
                        }
                        if(pref.getBlueStatus()){
                          //  Toast.makeText(getApplicationContext(), "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
							pref.setBlueStatus(false);
                        	
                        }
                        
                        break;
                }

              
            }
        }};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (bscrRegistered) {
			unregisterReceiver(bscReceiver);
			bscrRegistered = false;
		}
		if (acceptThread != null) {
			acceptThread.cancel();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		//Toast.makeText(getApplicationContext(), "on resume ", Toast.LENGTH_LONG).show();

		
/*
		if (!mBluetoothAdapter.isEnabled()) {
			if (!bscrRegistered) {
				registerReceiver(bscReceiver, bscrFilter);
				bscrRegistered = true;
			}
			
			//mBluetoothAdapter.enable();
			// new Handler().postDelayed(new Runnable(){
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// AcceptConnection anotherAcceptThread = new AcceptConnection();
			// anotherAcceptThread.start();
			// }
			//
			// }, 10000);
		} else if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			if (pref.getVisibility()) {
				Intent discoverableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(
						BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivityForResult(discoverableIntent,
						MainActivity.DISCOVERY_ENABLE);
			}
		} else {
			acceptThread = new AcceptConnection();
			acceptThread.start();
		}*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case MainActivity.DISCOVERY_ENABLE:
			if (resultCode != MainActivity.RESULT_CANCELED) {
				acceptThread = new AcceptConnection();
				acceptThread.start();
			} else {
				//Toast toast = Toast.makeText(this,
					//	"Your device is not visible", Toast.LENGTH_SHORT);
				//toast.show();
				//finish();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);

		}
	}

	class AcceptConnection extends Thread {

		private BluetoothServerSocket tempServerSocket = null;

		public AcceptConnection() {
			try {
				tempServerSocket = mBluetoothAdapter
						.listenUsingInsecureRfcommWithServiceRecord(
								SERVICE_NAME, MY_UUID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run() {
			BluetoothSocket tempSocket = null;
			// Keep listening until exception occurs or a socket is returned
			while (true) {
				try {
					tempSocket = tempServerSocket.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				// If a connection was accepted
				if (tempSocket != null) {
					shareSocket = tempSocket;
					deviceName = shareSocket.getRemoteDevice().getName();
					uiHandler.obtainMessage(
							MainActivity.ESTABLISH_SHARE_ON_ACCEPT)
							.sendToTarget();
					try {
						tempServerSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				tempServerSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class RequestConnection extends Thread {
		private BluetoothSocket tempSocket = null;

		public RequestConnection(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tempSocket = device
						.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();

			try {
				tempSocket.connect();
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					tempSocket.close();
				} catch (IOException closeException) {
					closeException.printStackTrace();
				}
				return;
			}

			shareSocket = tempSocket;
			deviceName = shareSocket.getRemoteDevice().getName();
			uiHandler.obtainMessage(MainActivity.ESTABLISH_SHARE_ON_REQUEST)
					.sendToTarget();
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				tempSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class SendMessage extends Thread {
		private OutputStream outputStream;
		private String message;
		private byte[] messageBytes;

		public SendMessage(String msg) {
			messageBytes = msg.getBytes();
			message = msg;
			try {
				outputStream = shareSocket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				outputStream.write(messageBytes);
				uiHandler.obtainMessage(MainActivity.UPDATE_SENT_MESSAGE,
						message).sendToTarget();
				;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void cancel() {
			try {
				shareSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class ReceiveMessage extends Thread {
		private InputStream inputStream;

		public ReceiveMessage() {

			try {
				inputStream = shareSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int numBytes = 0;
			String message = "";
			try {
				while (true) {
					do {
						numBytes = inputStream.read(buffer, 0, 1024);
						message += new String(buffer, 0, numBytes);
					} while (numBytes == 1024);
					uiHandler.obtainMessage(
							MainActivity.UPDATE_RECEIVED_MESSAGE, message)
							.sendToTarget();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void cancel() {
			try {
				shareSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class UIHandler extends Handler {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MainActivity.UPDATE_DEVICE_LISTS_ON_CONNECTED:
				// pairedArrayAdapter.add(((BluetoothDevice)
				// msg.obj).getName());
				deviceArrayAdapter
						.remove(((BluetoothDevice) msg.obj).getName());
				break;
			case MainActivity.UPDATE_FOUND_LIST_ON_APP_CHECK:
				deviceArrayAdapter.add((String) msg.obj);
				break;
			case MainActivity.ESTABLISH_SHARE_ON_ACCEPT:
				if (requestThread != null) {
					requestThread.cancel();
					requestThread = null;
				}
				Toast toast = Toast.makeText(MainActivity.this,
						"Incoming Connection Accepted", Toast.LENGTH_SHORT);
				toast.show();
				viewFlipper.showNext();
				deviceNameTV.setText(deviceName);
				receiveThread = new ReceiveMessage();
				receiveThread.start();
				break;
			case MainActivity.ESTABLISH_SHARE_ON_REQUEST:
				if (acceptThread != null) {
					acceptThread.cancel();
					acceptThread = null;
				}
				Toast toast1 = Toast.makeText(MainActivity.this,
						"Outgoing Connection Accepted", Toast.LENGTH_SHORT);
				toast1.show();
				viewFlipper.showNext();
				deviceNameTV.setText(deviceName);
				receiveThread = new ReceiveMessage();
				receiveThread.start();
				break;
			case MainActivity.UPDATE_SENT_MESSAGE:
				textsListAdapter.add("Me: " + (String) msg.obj);
				typeText.setText("");
				break;
			case MainActivity.UPDATE_RECEIVED_MESSAGE:
				textsListAdapter.add(deviceName + ": " + (String) msg.obj);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private class FoundDeviceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				isFound = true;
				BluetoothDevice btDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (BluetoothClass.Device.PHONE_SMART == btDevice
						.getBluetoothClass().getDeviceClass()) {
					foundDevices.add(btDevice);
					deviceArrayAdapter.add(btDevice.getName());
					btDevice.fetchUuidsWithSdp();
				}
			}
		}
	}

	private class DiscoveryCompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent
					.getAction())) {
				unregisterReceiver(foundDeviceReceiver);
				searchButton.setEnabled(true);
				seacrhProgressBar.setVisibility(View.INVISIBLE);
				if (isFound) {
					foundDevicesListView.setEnabled(true);
				} else {
					noDeviceFoundTextTV.setVisibility(View.VISIBLE);
				}
				unregisterReceiver(UuidReceiver);
				unregisterReceiver(this);
			}
		}
	};

	private class UUIDReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (BluetoothDevice.ACTION_UUID.equals(intent.getAction())) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String nameDevice = device.getName();
				Parcelable[] uuidExtra = intent
						.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
				// for (int i=0; i<uuidExtra.length; i++)
				// {
				// if(uuidExtra[i].toString()==MY_UUID.toString())
				// {
				// foundDevices.add(device);
				// deviceArrayAdapter.add(nameDevice);
				// }
				// }
			}
		}
	}

	private class BluetoothStateChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO: This method is called when the BroadcastReceiver is
			// receiving
			// an Intent broadcast.
			if (BluetoothAdapter.ACTION_STATE_CHANGED
					.equals(intent.getAction())) {
				int currentState = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				if (currentState == BluetoothAdapter.STATE_ON) {
					//Toast toast = Toast.makeText(MainActivity.this,
						//	" Test ", Toast.LENGTH_SHORT);
			//		toast.show();
				//Toast.makeText(getApplicationContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
					
					
					if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
						if (pref.getVisibility()) {
							Intent discoverableIntent = new Intent(
									BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
							discoverableIntent
									.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							discoverableIntent
									.putExtra(
											BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
											0);
							((Activity) context).startActivityForResult(
									discoverableIntent,
									MainActivity.DISCOVERY_ENABLE);
							pref.setWButton(true);
						}
					}
				}
			}
		}
	}

}
