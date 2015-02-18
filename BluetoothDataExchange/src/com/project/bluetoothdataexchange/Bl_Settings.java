package com.project.bluetoothdataexchange;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.project.bluetoothdataexchange.pref.SettingsPrefHandler;

@SuppressLint("CutPasteId")
public class Bl_Settings extends Activity implements OnClickListener {

	private CheckBox vsoft, vshort, vhard,vdiff;

	private Dialog dialog;

	private Context context = this;

	NotificationManager NM;
	Notification notification;

	private Button Vibrate, Ring, Silent, Mode;

	// private TextView Status, vsts;
	private AudioManager myAudioManager;

	private NotificationManager notddification;
	// private CheckBox si;//, vi;

	private String silence, vibrate;

	private String tone = "1";

	private Button test1, test2, test3;

	private long[] pattern = { 300 };

	TextView txt_status, this_vistatus, tv_statuslight, tv_statusblthstartup;
	TextView tv_statusstartonboot, tv_statusstartonvisible;
	CheckBox chk, chk_statusStartOnVisible;
	SettingsPrefHandler pref;
	CheckBox chk_sound,chk_light, chk_bluetoothonstart;
	CheckBox chk_vibrate;
	SeekBar sk;
	public int thirty,hund;


	@SuppressLint({ "ServiceCast", "NewApi", "CutPasteId" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		this.setTitle("Settings");


		chk_sound = (CheckBox) findViewById(R.id.chk_sound);
		chk_vibrate = (CheckBox) findViewById(R.id.this_vibrate);
		chk_light = (CheckBox) findViewById(R.id.chk_light);
		chk_bluetoothonstart = (CheckBox) findViewById(R.id.chk_bluetoothonstart);

		txt_status = (TextView) findViewById(R.id.txt_status);
		this_vistatus = (TextView) findViewById(R.id.this_vistatus);
		tv_statuslight = (TextView) findViewById(R.id.tv_statuslight);
		tv_statusblthstartup = (TextView) findViewById(R.id.tv_statusblthstartup);

		pref = new SettingsPrefHandler(this);
		getActionBar().setDisplayShowHomeEnabled(false);
		// create back button
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		tv_statusstartonboot = (TextView) findViewById(R.id.tv_statusstartonboot);
		tv_statusstartonvisible = (TextView) findViewById(R.id.tv_statusstartonvisible);
		// Status = (TextView) findViewById(R.id.txt_status);
		// vsts = (TextView) findViewById(R.id.this_vistatus);

		test1 = (Button) findViewById(R.id.button1);
		test2 = (Button) findViewById(R.id.button2);
		test3 = (Button) findViewById(R.id.btn_tone);

		myAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		read();

		final DatabaseHandler db = new DatabaseHandler(this);

		chk_sound = (CheckBox) findViewById(R.id.chk_sound);
		// vi = (CheckBox) findViewById(R.id.this_vibrate);

		// if (si.isChecked()) {
		//
		// Log.d("silence", "" + "checked");
		// }
		// si.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (si.isChecked()) {
		// db.addcontact(new Cl_Contact("1", "1", "1", "1"));
		//
		// db.updateContact(new Cl_Contact("1", "1", "1", "0"));
		// txt_status.setText("enabled");
		//
		// tone_picker();
		//
		// read();
		// } else if (!si.isChecked()) {
		// db.updateContact(new Cl_Contact("1", "0", "1", "0"));
		// // Log.d("silence", "" + "not checked");
		// txt_status.setText("disabled");
		// read();
		// }
		// }
		// });
		chk_sound.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					db.addcontact(new Cl_Contact("1", "1", "1", "1"));

					db.updateContact(new Cl_Contact("1", "1", "1", "0"));
					txt_status.setText("enabled");
						pref.setSound(true);
					tone_picker();

					read();
				} else {
					db.updateContact(new Cl_Contact("1", "0", "1", "0"));
					// Log.d("silence", "" + "not checked");
					txt_status.setText("disabled");
					pref.setSound(false);
					read();
				}
				//pref.setSound(isChecked);
			}
		});

		 if(pref.getSound())
		 {
		 txt_status.setText("enabled");
		 chk_sound.setChecked(true);
		 }
		 else
		 {
		 txt_status.setText("disabled");
		 chk_sound.setChecked(false);
		 }
		 if(pref.getVibrate())
		 {
		 chk_vibrate.setChecked(true);
		 this_vistatus.setText("enabled");
		 }
		 else
		 {
		 
		 chk_vibrate.setChecked(false);
		 this_vistatus.setText("disabled");
		 }
		if (pref.getLight()) {
			chk_light.setChecked(true);
			tv_statuslight.setText("enabled");
		} else {
			chk_light.setChecked(false);
			tv_statuslight.setText("disabled");
		}
		if (pref.getBTooth()) {
			chk_bluetoothonstart.setChecked(true);
			tv_statusblthstartup.setText("enabled");
		} else {
			chk_bluetoothonstart.setChecked(false);
			tv_statusblthstartup.setText("disabled");
		}

		// chk_sound=(CheckBox)findViewById(R.id.chk_sound);
		// chk_sound.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// // TODO Auto-generated method stub
		// txt_status.setText(""+isChecked);
		// pref.setSound(isChecked);
		// }
		// });
		// this_vibrate=(CheckBox)findViewById(R.id.this_vibrate);
		// this_vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener()
		// {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// // TODO Auto-generated method stub
		// this_vistatus.setText(""+isChecked);
		// pref.setVibrate(isChecked);
		// }
		// });
		chk_light = (CheckBox) findViewById(R.id.chk_light);
		chk_light.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {

					tv_statuslight.setText("enabled");
				} else {
					tv_statuslight.setText("disabled");
				}
				pref.setLight(isChecked);

			}
		});
		chk_bluetoothonstart = (CheckBox) findViewById(R.id.chk_bluetoothonstart);
		chk_bluetoothonstart
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							tv_statusblthstartup.setText("enabled");
							pref.setBTooth(true);

						} else {
							tv_statusblthstartup.setText("disabled");
							pref.setBTooth(false);
							if(pref.getVisibility()){
							tv_statusstartonvisible.setText("disabled");
							pref.setVisibility(false);
							chk_statusStartOnVisible.toggle();}

						}
						//pref.setBTooth(isChecked);
					}
				});
		chk = (CheckBox) findViewById(R.id.chk_startonboot);

		chk_statusStartOnVisible = (CheckBox) findViewById(R.id.chk_startonvisible);

		if (pref.getBoot()) {
			chk.setChecked(true);
			tv_statusstartonboot.setText("enabled");
		} else {
			chk.setChecked(false);
			tv_statusstartonboot.setText("disabled");
		}

		if (pref.getVisibility()) {
			chk_statusStartOnVisible.setChecked(true);
			tv_statusstartonvisible.setText("enabled");
		} else {
			chk_statusStartOnVisible.setChecked(false);
			tv_statusstartonvisible.setText("disabled");
		}

		//if(pref.getBTooth()){

		chk_statusStartOnVisible
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							
							tv_statusstartonvisible.setText("enabled");
							pref.setVisibility(true);
							if(!pref.getBTooth()){
								chk_bluetoothonstart.toggle();
							}
						} else {
							tv_statusstartonvisible.setText("disabled");
							pref.setVisibility(false);
						}
						
					}
				});
		//}else{
			//Toast.makeText(getApplicationContext(), "Enable Blutooth on Statrt !",Toast.LENGTH_LONG).show();
		//}

		chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				System.out.println("Check : " + isChecked);
				if (isChecked) {
					tv_statusstartonboot.setText("enabled");
					pref.setBoot(true);
				} else {
					tv_statusstartonboot.setText("disabled");
					pref.setBoot(false);
				}

			}
		});
		findViewById(R.id.chk_startonboot).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

					}
				});
		// build vibrate
		// chk_vibrate.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (chk_vibrate.isChecked()) {
		// db.updateContact(new Cl_Contact("1", silence, "1", "0"));
		// this_vistatus.setText("enabled");
		//
		// Log.d("vib", "" + " checked");
		// read();
		// dialog();
		// } else if (!chk_vibrate.isChecked()) {
//		db.updateContact(new Cl_Contact("1", silence, "0", "0"));
//		this_vistatus.setText("disabled");
//
//		Log.d("vib", "" + "not checked");
//		read();
		// }
		// }
		// });
		chk_vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					db.updateContact(new Cl_Contact("1", silence, "1", "0"));

					Log.d("vib", "" + " checked");
					read();
					
					this_vistatus.setText("enabled");
					dialog();
					//v.cancel();
					
					
				} else {
					db.updateContact(new Cl_Contact("1", silence, "0", "0"));

					Log.d("vib", "" + "not checked");
					read();
					this_vistatus.setText("disabled");
				}
				pref.setVibrate(isChecked);
				chk_vibrate.setChecked(isChecked);
			}
		});
		Ring = (Button) findViewById(R.id.btn_tone);
		Ring.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
						RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
						"Select Ringtone");

				// for existing ringtone
				Uri urie = RingtoneManager.getActualDefaultRingtoneUri(
						getApplicationContext(),
						RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
						urie);

				startActivityForResult(intent, 5);
			}
		});

		// Mode = (CheckBox) findViewById(R.id.checkBox4);

		myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		test1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (silence.equals("1")) {

					Uri notification = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(
							getApplicationContext(), notification);
					r.play();
					// Vibrator vibe = (Vibrator)
					// getSystemService(Context.VIBRATOR_SERVICE);
					// vibe.vibrate(500);
					notification();
				} else {

					Toast.makeText(getApplicationContext(), "Sound Disabled",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		// test2.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (silence.equals("1")) {
		// notification();
		// } else {
		// notification();
		// Uri notification = RingtoneManager
		// .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		// Ringtone r = RingtoneManager.getRingtone(
		// getApplicationContext(), notification);
		// r.play();
		// Toast.makeText(getApplicationContext(), "Silence Off",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
		// test3.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (tone.equals("1")) {
		// Uri notification = RingtoneManager
		// .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		// Ringtone r = RingtoneManager.getRingtone(
		// getApplicationContext(), notification);
		// r.play();
		//	notification();
		// } else {
		// Toast.makeText(getApplicationContext(), "No tones Set ",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
public boolean key = true;
	public void tone_picker() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);


		// for existing ringtone
		 Uri urie = RingtoneManager.getActualDefaultRingtoneUri(
				getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
		if(key==true){
		 intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urie);
		   // Toast.makeText(getApplicationContext(), "not null", Toast.LENGTH_SHORT).show();

		}else{
		 intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri)null);
		   // Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();

		}
	/*	RingtoneManager.setActualDefaultRingtoneUri(
	    	    getApplicationContext(),
	    	    RingtoneManager.TYPE_NOTIFICATION,
	    	    urie);*/
		
		startActivityForResult(intent,5);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		
		if (resultCode == RESULT_OK) {
		    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		    if (uri != null) {
		    String ringTonePath = uri.toString();
		    RingtoneManager.setActualDefaultRingtoneUri(
		    	    getApplicationContext(),
		    	    RingtoneManager.TYPE_NOTIFICATION,
		    	    uri);
		    //Toast.makeText(getApplicationContext(), ringTonePath, Toast.LENGTH_SHORT).show();
		    key=true;
		}else{
			key = false;
		}
		    
		    
		    
	}
		}

	public void read() {
		// Reading all contacts
		final DatabaseHandler db = new DatabaseHandler(this);
		Log.d("Reading: ", "Reading all contacts..");
		List<Cl_Contact> contacts = db.getAllContacts();

		for (Cl_Contact cn : contacts) {
			silence = "" + cn.getdate();
			vibrate = "" + cn.gettitle();

			if (silence.trim().length() < 0) {
				db.addcontact(new Cl_Contact("1", "1", "1", "1"));
				Log.d("silence", "" + "added");
			}

			Log.e("values are", silence + vibrate + "");
			if (silence.equals("1")) {
				Log.d("yes", "" + silence);
				txt_status.setText("enabled");
				// myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				// si = (CheckBox) findViewById(R.id.this_vibrate);
				chk_vibrate.setChecked(true);

			} else if (silence.equals("0")) {
				// myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				txt_status.setText("disabled");
			}

			if (vibrate.equals("1")) {
				Log.d("yesv", "" + vibrate);
				chk_vibrate = (CheckBox) findViewById(R.id.this_vibrate);
				this_vistatus.setText("enabled");
				chk_vibrate.setChecked(true);
				// myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			} else if (vibrate.equals("0")) {
				this_vistatus.setText("enabled");
			}

		}

	}

	public void vibrate(View view) {
		// myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	}

	public void ring(View view) {
		// myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}

	public void silent(View view) {
		// myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}

	public void mode(View view) {
		int mod = myAudioManager.getRingerMode();
		if (mod == AudioManager.RINGER_MODE_NORMAL) {
			txt_status.setText("Current Status: Ring");
		} else if (mod == AudioManager.RINGER_MODE_SILENT) {
			txt_status.setText("Current Status: Silent");
		} else if (mod == AudioManager.RINGER_MODE_VIBRATE) {
			txt_status.setText("Current Status: Vibrate");
		} else {

		}

	}

	public void notification() {
		String title = "this is test";
		String subject = "this is test";
		String body = "this is test";

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notify = new Notification(
				android.R.drawable.stat_notify_more, title,
				System.currentTimeMillis());
		builder.setLights(Color.BLUE, 500, 500);
		PendingIntent pending = PendingIntent.getActivity(
				getApplicationContext(), 0, new Intent(), 0);
		notify.setLatestEventInfo(getApplicationContext(), subject, body,
				pending);
		NM.notify(0, notify);

	}

	public void dialog() {
		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.vibration);
		final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		// // set the custom dialog components - text, image and button
		// TextView text = (TextView) dialog.findViewById(R.id.text);
		// text.setText("Android custom dialog example!");
		// ImageView image = (ImageView) dialog.findViewById(R.id.image);
		// image.setImageResource(R.drawable.ic_launcher);
		
		vsoft = (CheckBox) dialog.findViewById(R.id.chk_vibration1);
		vshort = (CheckBox) dialog.findViewById(R.id.chk_vibration2);
		vhard = (CheckBox) dialog.findViewById(R.id.chk_vibration3);
		vdiff = (CheckBox) dialog.findViewById(R.id.chk_vibration4);
		
		
		switch(pref.getVibrationItem()){
		
		case 1:
			vsoft.setChecked(true);
			break;
		case 2:
			vshort.setChecked(true);
			break;
		case 3:
			vhard.setChecked(true);
			break;
		case 4:
			vdiff.setChecked(true);
			break;
			}
		// if button is clicked, close the custom dialog
		vsoft.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				// TODO Auto-generated method stub
				//Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

				if (vsoft.isChecked() == true) {
					thirty=500;
					hund=200;
					//long[] pattern = { 0, 200, 500, 500, 200 };
					long[] pattern = {0,thirty,hund};
					vshort.setChecked(false);
					vhard.setChecked(false);
					vdiff.setChecked(false);
					pref.setVibrationItem(1);
					//dialog2();


					
					v.vibrate(pattern, 0);
					Log.d("v works", "" + "0, 200, 500, 500, 200");
				} else {
					//long[] pattern = { 0 };
					//v.vibrate(pattern, 0);
					v.cancel();

				}

			}
		});
		// if button is clicked, close the custom dialog
		vshort.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				// TODO Auto-generated method stub
				//Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

				if (vshort.isChecked() == true) {
					thirty=200;
					hund=100;
					long[] pattern={ 0,thirty,hund};

					//long[] pattern = { 0, 200 };
					vsoft.setChecked(false);
					vhard.setChecked(false);
					vdiff.setChecked(false);
					pref.setVibrationItem(2);
					
					//dialog2();


					v.vibrate(pattern, 0);
					Log.d("v works", "" + "0, 200");

				} else {
					//long[] pattern = { 0 };
					//v.vibrate(pattern, 0);
					v.cancel();
				}

			}
		});

		// if button is clicked, close the custom dialog
		vhard.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				// TODO Auto-generated method stub
			//	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

				if (vhard.isChecked() == true) {
					thirty=500;
					hund=500;
					long[] pattern={ 0,thirty,hund};

					//long[] pattern = { 500, 500, 500, 500 };
					
					vsoft.setChecked(false);
					vshort.setChecked(false);
					vdiff.setChecked(false);
					pref.setVibrationItem(3);



					//dialog2();

					v.vibrate(pattern, 0);
					
					Log.d("v works", "" + "500, 500, 500, 500");
				} else {
					//long[] pattern = { 0 };
					//v.vibrate(pattern, 0);
					v.cancel();
				}

			}
		});
		
	
		vdiff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
			
				if (vdiff.isChecked() == true) {
					thirty=1000;
					hund=100;
					long[] pattern={ 0,thirty,hund};

					//long[] pattern = { 100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100 };
					vsoft.setChecked(false);
					vshort.setChecked(false);
					vhard.setChecked(false);
					pref.setVibrationItem(4);


					
					//dialog2();
					v.vibrate(pattern, 0);
					Log.d("v works", "" + "500, 500, 500, 500");
				} else {
					//long[] pattern = { 0 };
					//v.vibrate(pattern, 0);
					v.cancel();
				}
				
				
			}
		});
		
		// dialogButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// dialog.dismiss();
		// }
		// });
		//
		
		dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
            	v.cancel();
              //  Utils.showShortToast("Back button pressed?");
            
            
            }
        });
	dialog.show();
		//dialog.onActionModeFinished(v.cancel());
		//dialog.on

//	dialog.onAttachedToWindow()
		
	
	}

	public void dialog2() {
		// custom dialog
		final Dialog dialog = new Dialog(context);
		final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.level);
		sk = (SeekBar) dialog.findViewById(R.id.seekBar1); 
        sk.setMax(20000); 
        
	    sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       
	    	int progressChanged = 0;
	    @Override       
	    public void onStopTrackingTouch(SeekBar seekBar) {      
	        // TODO Auto-generated method stub     
			playIntensity(progressChanged);

	    	Toast.makeText(getApplicationContext(),"seek bar progress:"+progressChanged,
					Toast.LENGTH_SHORT).show();
	    }       

	    @Override       
	    public void onStartTrackingTouch(SeekBar seekBar) {     
	        // TODO Auto-generated method stub      
	    //	v.cancel();
			v.cancel();

	    	
	    }       

	    @Override       
	    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {     
	        // TODO Auto-generated method stub      
			progressChanged = progress;

	      //  t1.setTextSize(progress);
	     //   Toast.makeText(getApplicationContext(), progress+"",Toast.LENGTH_LONG).show();

	    }       
	    
	});          
	    
	    
	   // playIntensity(1);
		

		//dialog.show();
	}
	public void playIntensity(int intensity) {
		
		final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        int onTime = (int) (thirty * ((float) intensity / hund));
        if (onTime > hund) {
            onTime = hund;
        }
        int offTime = thirty - onTime;
        if (offTime < 0) {
            offTime = 0;
        }

        long[] pattern = {0, onTime, offTime};
       // if (!rejectNew) {
        Toast.makeText(getApplicationContext(), "sdfdsfsdfsd",Toast.LENGTH_LONG).show();
            v.vibrate(pattern, 0);
     //   }
    }
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
