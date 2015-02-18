package com.project.bluetoothdataexchange.pref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsPrefHandler {
	SharedPreferences pref;

	Editor editor;

	Context _context;

	int PRIVATE_MODE = 0;

	private static final String PREF_NAME = "settings_pref";

	final String BOOT = "boot";
	final String VISIBLE = "visible";
	final String SOUND = "sound";
	final String VIBRATE = "vibrate";
	final String LIGHT = "light";
	final String BTOOTH = "btooth";
	final String VISIBLESTATUS = "visiblestatus";
	final String BLUESTATUS = "buetoothstatus";
	final String WBUTTON = "wether Button";
	final String VITEM = "selectedVibration";

	@SuppressLint("CommitPrefEdits")
	public SettingsPrefHandler(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public boolean getVibrate() {
		return pref.getBoolean(VIBRATE, true);
	}

	public void setVibrate(boolean flag) {
		editor.putBoolean(VIBRATE, flag);
		editor.commit();
	}

	public boolean getLight() {
		return pref.getBoolean(LIGHT, true);
	}

	public void setLight(boolean flag) {
		editor.putBoolean(LIGHT, flag);
		editor.commit();
	}

	public boolean getBTooth() {
		return pref.getBoolean(BTOOTH, true);
	}

	public void setBTooth(boolean flag) {
		editor.putBoolean(BTOOTH, flag);
		editor.commit();
	}

	public boolean getBoot() {
		return pref.getBoolean(BOOT, true);
	}

	public void setBoot(boolean flag) {
		editor.putBoolean(BOOT, flag);
		editor.commit();
	}

	public boolean getSound() {
		return pref.getBoolean(SOUND, true);
	}

	public void setSound(boolean flag) {
		editor.putBoolean(SOUND, flag);
		editor.commit();
	}

	public boolean getVisibility() {
		return pref.getBoolean(VISIBLE, true);
	}

	public void setVisibility(boolean flag) {
		editor.putBoolean(VISIBLE, flag);
		editor.commit();
	}
	public void setStatusVisibility(boolean flag) {
		editor.putBoolean(VISIBLESTATUS, flag);
		editor.commit();
	}
	public boolean getStatusVisibility() {
		return pref.getBoolean(VISIBLESTATUS, getVisibility());
		//editor.commit();
	}
	public void setBlueStatus(boolean flag){
		editor.putBoolean(BLUESTATUS, flag);
		editor.commit();
	}
	public boolean getBlueStatus(){
		
		return pref.getBoolean(BLUESTATUS, true );
	//return pref.getBoolean(BLUESTATUS, getBTooth());
	}
	public void setWButton(boolean flag){
		editor.putBoolean(WBUTTON, flag);
		editor.commit();
	}
	public boolean getWButton(){
			
	return pref.getBoolean(WBUTTON, false );
		//return pref.getBoolean(BLUESTATUS, getBTooth());
	}
	
	public void setVibrationItem(int flag)
	{
		editor.putInt(VITEM, flag);
		editor.commit();
	}
	
	public int getVibrationItem(){
		
		return pref.getInt(VITEM, 0);
	}
	

	

}
