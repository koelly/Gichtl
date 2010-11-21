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

import java.util.Locale;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListThem extends ListActivity implements OnClickListener{
	DataBaseHelper dbHelper = new DataBaseHelper(this);
	
	String category;
	private static String TABLE_NAME = "food";
	
	//TODO Put it in extra class
	public String getLanguageColumnsName(){
	      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	      String lang = prefs.getString("language", "auto");
	      String result = "name_en";
	      
	      	      
	      if (lang.equalsIgnoreCase("german")){
	    	  result =  "name";
	      } else if(lang.equalsIgnoreCase("english")){
	    	  result =  "name_en";
	      } else if(lang.equalsIgnoreCase("auto")){
	    	  Locale lang_code = Locale.getDefault();
	    	  if(lang_code.getCountry().equalsIgnoreCase("de")){
	    		  result =  "name";
	    	  } 
	      }
	      return result;
		
	}
    
		
	private Cursor getNames(){
	    String NAME_COLUMN = getLanguageColumnsName();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT " + NAME_COLUMN + ", \"uric_acid\" FROM " + TABLE_NAME + " WHERE category = \"" + category + "\" ORDER BY name ASC;", null);
		startManagingCursor(cursor);		
		return cursor;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.listthem);
      
      
      /**
       * Get UI Elements
       */
      ImageView b_search = (ImageView) findViewById(R.id.search);
      b_search.setOnClickListener(this);
      
      TextView title_text = (TextView) findViewById(R.id.title_text);
      title_text.setOnClickListener(this);
      
      
      /**
       * Reads passed through data (actually only the name of category)
       */
      Bundle bundle = this.getIntent().getExtras();
      category = bundle.getString("category");
      
      
      /**
       * Read all entries of <<category>> from db and put in array for ListView
       */
      Cursor cursor = getNames();
      String[] names4list = new String[cursor.getCount()];
      final String[] names = new String[cursor.getCount()];
      
      int i = 0;
      while (cursor.moveToNext()){
    	  String name = cursor.getString(0);
    	  int uric_acid = cursor.getInt(1);
    	  names4list[i] = name + " (" + uric_acid + "mg/100g)";
    	  names[i++] = name;
      }
      
      
      ListView lv1;
      lv1=(ListView)findViewById(android.R.id.list);
      lv1.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, names4list));

      
      dbHelper.close();
      
      /**
       * Handles clicks inside ListView
       */
      lv1.setOnItemClickListener(new OnItemClickListener() {
        @Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        	//Source: http://www.balistupa.com/blog/2009/08/passing-data-or-parameter-to-another-activity-android/
        	Intent i = new Intent(ListThem.this, Details.class);
        	Bundle bundle = new Bundle();
        	bundle.putString("name", names[position]);
        	bundle.putString("table", TABLE_NAME);
        	i.putExtras(bundle);
        	startActivity(i);
        }
      });
    }
    
    
    @Override
    public void onPause(){
    	super.onDestroy();
    	dbHelper.close();
    }

    
    @Override
	public void onResume(){
    	super.onDestroy();
    	dbHelper.openDataBase();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	dbHelper.close();
    }
    
    

    /**
     * Handles Clicks in titlebar
     */
	@Override
	public void onClick(View v) {
		//Intent i = new Intent(this, gichtl.class);
		
		switch (v.getId()){
			case R.id.title_text:
	        	/*
	        	 * Doesn't work, i don't know why, i don't care...
	        	 */
				//startActivity(i);
				break;
		
			case R.id.search:
				onSearchRequested();
				break;
		}
	}

}