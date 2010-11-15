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

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Details extends Activity{
	
	DataBaseHelper dbHelper = new DataBaseHelper(this);
	String nameOfMeal;
	private String TABLE_NAME = "food";
	Cursor cursor;

	
	
	private Cursor getDetails(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE name = \"" + nameOfMeal + "\";" , null);
		startManagingCursor(cursor);
		cursor.moveToFirst();
		db.close();
		return cursor;
	}

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
            
        
        Bundle bundle = this.getIntent().getExtras();
        nameOfMeal = bundle.getString("name");
        
        cursor = getDetails();
        int uric_acid = cursor.getInt(3);
        int average_portion = cursor.getInt(4);
        
        int uric_acid_per_portion = (int)( ( (double)(average_portion)*(double)uric_acid )/100 );
        
        int signal = cursor.getInt(5);
        cursor.close();
        
        int bgColor = 0;
        switch (signal){
        case 1:
        	//green
        	bgColor = Color.parseColor("#66FF00");
        	break;
        case 2:
        	//yellow
        	bgColor = Color.parseColor("#FFFF66");
        	break;
        case 3:
        	//red
        	bgColor = Color.parseColor("#FF3300");
        	break;
        }
        
        
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl);
        rl.setBackgroundColor(bgColor);

        TextView tv1 = (TextView) findViewById(R.id.uric_acid);
        tv1.setText("Harnsäure in 100g:    " + uric_acid + "mg");
        
        TextView tv2 = (TextView) findViewById(R.id.uric_acid_per_portion);
        tv2.setText("In einer " + average_portion + "g Portion befinden sich also ca. " + uric_acid_per_portion + "mg Harnsäure.");

        this.setTitle(nameOfMeal);
        dbHelper.close();
    }
    
    @Override
    public void onPause(){
    	super.onDestroy();
    	dbHelper.close();
    }

    
    public void onResume(){
    	super.onDestroy();
    	dbHelper.openDataBase();
    }

    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	dbHelper.close();
    }
}