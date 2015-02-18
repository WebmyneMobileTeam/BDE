package com.project.bluetoothdataexchange;

import android.util.Log;

public class Cl_Contact {
	// private variables
	int _id;
	String _rowid;
	String _date;
	String _title;
	String _context;

	public Cl_Contact() {

	}

	// constructor
	public Cl_Contact(int id, String date, String title, String context) {
		this._id = id;
		this._date = date;
		this._title = title;
		this._context = context;
	}

	// constructor
	public Cl_Contact(String rowid, String _date, String _title, String _context) {
		this._rowid = rowid;
		this._date = _date;
		this._title = _title;
		this._context = _context;
	}

	// getting ID
	public int getID() {
		return this._id;
	}

	// setting id
	public void setID(int id) {
		this._id = id;

	}

	// getting date
	public String getdate() {
		return this._date;
	}

	// setting date
	public void setDate(String date) {
		this._date = date;
	}

	// getting name
	public String gettitle() {
		return this._title;
	}

	// setting name
	public void settitle(String name) {
		this._title = name;
	}

	// getting phone number
	public String getcontext() {
		return this._context;
	}

	// setting phone number
	public void setcontext(String txt) {
		this._context = txt;
	}

	// getting row id
	public String getrownumber() {
		return this._rowid;
	}

	// setting phone number
	public void setrownumber(String row) {
		this._rowid = row;
		Log.d("rw is", "" + row);
	}
}
