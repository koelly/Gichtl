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

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MySearchableActivity extends ListActivity{
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.listthem);

	    Intent intent = getIntent();

	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	     
	      DataBaseHelper dbHelper = new DataBaseHelper(this);
	      SQLiteDatabase db = dbHelper.getReadableDatabase();
	      Cursor cursor = db.rawQuery("SELECT * FROM food WHERE name LIKE \"%" + query + "%\"" , null);
	      
	      
	      cursor.moveToFirst();
	      String[] results = new String[cursor.getCount()];
	      final String[] tmp = new String[cursor.getCount()];
	      
	      int i = 0;

	      if (cursor.getCount() > 0){
		      do{
		    	  String name = cursor.getString(2);
		    	  String category = cursor.getString(1);
		    	  Log.d("Gichtl", "Eintrag aus DB: " + name);
		    	  tmp[i] = category;
		    	  results[i++] = name; 
		      } while (cursor.moveToNext());

		      ListView lv1;
		      lv1=(ListView)findViewById(android.R.id.list);
		      lv1.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, results));

	      } else {
	    	  Toast toast = Toast.makeText(this, "Nichts gefunden!", Toast.LENGTH_LONG);
	    	  toast.show();
	      }
	      ListView lv = getListView();
	      lv.setTextFilterEnabled(true);
	      cursor.close();	      
	      
	      lv.setOnItemClickListener(new OnItemClickListener() {
	          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	  String name = (String) ((TextView) view).getText();
      	  
	        	  	
		          Intent i = new Intent(MySearchableActivity.this, details.class);
		          Bundle bundle = new Bundle();
		          bundle.putString("name", name);
		          i.putExtras(bundle);
		          startActivity(i);
	          }
	      });

	      
	    }
	}

}
// Source -> http://developer.android.com/guide/topics/search/search-dialog.html