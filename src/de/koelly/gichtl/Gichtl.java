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

import java.io.IOException;
import de.koelly.gichtl.DataBaseHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Gichtl extends Activity implements OnClickListener {
	ProgressDialog myProgressDialog = null;
	DataBaseHelper myDbHelper = new DataBaseHelper(this);
	
	//Menu:
	private static final int MENU_ID = 0;
	private static final int ABOUT_ID = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final String PREFERENCES = "gichtl.prf";
        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);


        
        /*
         * If this is first run, or DB changed: delete crudely the old DB
         */
        boolean firstRun = true;        
        firstRun = settings.getBoolean("firstRun2", true);
         if (firstRun){

        	 myDbHelper.forceNewDB();

	      
	        SharedPreferences.Editor settingsEditor = settings.edit();
	        settingsEditor.putBoolean("firstRun2", false);
	        settingsEditor.commit();
         }
         
         
         
         /**
          * Open or create Database
          */
         try {
         	myDbHelper.createDataBase();
         } catch (IOException ioe) {
         	throw new Error("Unable to create database");
         }
  
         try {
         	myDbHelper.openDataBase();	
         }catch(SQLException sqle){
         	throw sqle;
         }

         
         
         /**
          * Get the UI Elements
          */
         ImageView iv_meat = (ImageView)findViewById(R.id.meat);
         iv_meat.setOnClickListener(this);
	        
         ImageView iv_fish = (ImageView)findViewById(R.id.fish);
         iv_fish.setOnClickListener(this);
	        
	     ImageView iv_fruit = (ImageView)findViewById(R.id.fruit);
	     iv_fruit.setOnClickListener(this);
	        
	     ImageView iv_vegetable = (ImageView)findViewById(R.id.vegetable);
	     iv_vegetable.setOnClickListener(this);
	      
	     ImageView iv_beverage = (ImageView)findViewById(R.id.beverage);
	     iv_beverage.setOnClickListener(this);
	        
	     ImageView iv_miscellaneous = (ImageView)findViewById(R.id.miscellaneous);
	     iv_miscellaneous.setOnClickListener(this);
	        
	     ImageView b_search = (ImageView) findViewById(R.id.search);
	     b_search.setOnClickListener(this);
        
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	myDbHelper.close();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
        try {
         	myDbHelper.openDataBase();	
         }catch(SQLException sqle){
         	throw sqle;
         }
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	myDbHelper.close();
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
    	
        menu.add(1, ABOUT_ID, 0, this.getString(R.string.about)).setIcon(android.R.drawable.ic_menu_info_details);
        menu.add(2, MENU_ID, 0, this.getString(R.string.preferences)).setIcon(android.R.drawable.ic_menu_preferences);
        
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		
    		case ABOUT_ID:
    			return(true);

    		case MENU_ID:
    			Intent j = new Intent(this, Preferences.class);
    			startActivity(j);
    			return(true);
    		
    	}
    	return(super.onOptionsItemSelected(item));
    }

    
    /**
     * Handle the clicks...
     */
	@Override
	public void onClick(View v) {
    	Intent i = new Intent(Gichtl.this, ListThem.class);
    	Bundle bundle = new Bundle();
    	bundle.clear();
    	
		switch (v.getId()){
			case R.id.fish:
	        	bundle.putString("category", "fish");
	        	i.putExtras(bundle);
	        	startActivity(i);				
	        	break;
			
			case R.id.meat:
	        	bundle.putString("category", "meat");
	        	i.putExtras(bundle);
	        	startActivity(i);				
	        	break;	
	        	
			case R.id.vegetable:
	        	bundle.putString("category", "vegetables");
	        	i.putExtras(bundle);
	        	startActivity(i);				
	        	break;
			
			case R.id.fruit:
	        	bundle.putString("category", "fruits");
	        	i.putExtras(bundle);
	        	startActivity(i);				
	        	break;
	        	
			case R.id.beverage:
	        	bundle.putString("category", "beverage");
	        	i.putExtras(bundle);
	        	startActivity(i);				
	        	break;
			
			case R.id.miscellaneous:
	        	bundle.putString("category", "miscellanous");
	        	i.putExtras(bundle);
	        	startActivity(i);				
	        	break;
			case R.id.search:
				onSearchRequested();
				break;
		}
	}
}