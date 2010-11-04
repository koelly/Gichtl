package de.koelly.gichtl;


/*
 * Gichtl - Die Harnsäure Datenbank
 * URLs: http://www.koelly.de/blog/gichtl-app/
 * Copyright (C) 2010, Christopher Köllmayr
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/de.koelly.gichtl/databases/";
	private static String DB_NAME = "purin.db";
    private SQLiteDatabase myDataBase; 
    private final Context myContext;
    private static final int DATABASE_VERSION = 9;

 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {    	

    	super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }
    
    /**
     * Main App recognizes a newer Version of DB is available and forces a new install
     */
    public void forceNewDB(){
    	File file = new File(DB_PATH + DB_NAME);
    	boolean deleted = file.delete();
    	Log.d("DB Deleted?",""+deleted);
    	
    	this.getReadableDatabase();
    	try {
			copyDataBase();
		} catch (IOException e) {
    		throw new Error("Error copying database");
    	}
    	
    }
 
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException{
    	Log.d("Gichtl: ","DB createDataBase aufgerufen... ");
     	boolean dbExist = checkDataBase();
    	 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    		
    		
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
    	Log.d("Gichtl: ","DB checkDataBase aufgerufen... ");

    	SQLiteDatabase checkDB = null;
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){
    		//database does't exist yet.
    	}
 
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    }

    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
    	Log.d("Gichtl: ","DB copyDatabase aufgerufen... ");

 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
    	Log.d("Gichtl: ","DB openDataBase aufgerufen... ");

    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	
    }
 
    @Override
	public synchronized void close() {
    	Log.d("Gichtl: ","DB close aufgerufen... ");
    	super.close();
    	    if(myDataBase != null){
    		    myDataBase.close();
    		    Log.d("Gichtl: ","... DB close ausgeführt");
    	    }
 
    	    
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("Gichtl: ","DB onCreate aufgerufen... ");

	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("Gichtl: ","DB onUpgrade aufgerufen... ");
		if (oldVersion < newVersion)
			Log.d("Gichtl: ","DB Upgrade würde ausgeführt werden...");
 
	}

}
