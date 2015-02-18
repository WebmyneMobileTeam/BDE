package com.project.bluetoothdataexchange;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.Fragment;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "bluetooth";

	// Contacts table name
	private static final String TABLE_TEXT_NOTES = "settings";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_SILENCE = "is_silence";
	private static final String KEY_VIBRATE = "is_vibrate";
	private static final String KEY_NOTE = "tone";
	private static final String KEY_ROWID = "id";

	private static final String MODE_WORLD_READABLE = null;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TEXT_NOTES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SILENCE + " TEXT, "
				+ KEY_VIBRATE + " TEXT," + KEY_NOTE + " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXT_NOTES);
		// Create tables again
		onCreate(db);

	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	void addcontact(Cl_Contact contact) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SILENCE, contact.getdate()); // Contact Name
		values.put(KEY_VIBRATE, contact.gettitle()); // Contact Name
		values.put(KEY_NOTE, contact.getcontext()); // Contact Phone

		// Inserting Row
		db.insert(TABLE_TEXT_NOTES, null, values);
		db.close(); // Closing database connection

	}

	// Getting All Contacts
	public List<Cl_Contact> getAllContacts() {
		List<Cl_Contact> contactList = new ArrayList<Cl_Contact>();
		// Select All Query
		String selectQuery = "SELECT  is_silence,is_vibrate FROM "
				+ TABLE_TEXT_NOTES + " WHERE " + KEY_ID + " = " + "1";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Cl_Contact contact = new Cl_Contact();
				// contact.setID(Integer.parseInt(cursor.getString(0)));
				contact.setDate(cursor.getString(0));
				contact.settitle(cursor.getString(1));
				// contact.setcontext(cursor.getString(3));
				// // Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());

		}
		db.close();
		// return contact list
		return contactList;
	}

	// Updating single contact
	public int updateContact(Cl_Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ROWID, contact.getrownumber());
		values.put(KEY_SILENCE, contact.getdate());
		values.put(KEY_VIBRATE, contact.gettitle());
		values.put(KEY_NOTE, contact.getcontext());

		// updating row
		return db.update(TABLE_TEXT_NOTES, values, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.getrownumber()) });

	}

	private SharedPreferences getSharedPreferences(String string,
			String modeWorldReadable) {
		// TODO Auto-generated method stub
		return null;
	}

}
