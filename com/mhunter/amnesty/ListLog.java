package com.mhunter.amnesty;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListLog extends Activity
{
	public static final int MENU_CLOSE = 0;
	private ListView lv1;
	private String lv_arr[] = new String[30];	
	private int count = 0;
	String rms = "sub";
	
	@Override
	
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.lista);
		read_line("");
		lv1 = (ListView)findViewById(R.id.ListView);
		String array[] = new String[count];
		if(count == 0){
			array = new String[1];
			array[0] = "0 records";
		}	
		else{
			for(int i = 0; i < count; i++){
				if(lv_arr[i] != null)
					array[i] = lv_arr[i];
				else
					array[i] = " ";
			}
		}
		lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , array));
	}

	/* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_CLOSE, 0, "Close").setIcon(android.R.drawable.ic_menu_close_clear_cancel);;
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_CLOSE:
        	terminate();
            return true;
        }
        return false;
    }    

    private void terminate()
	{
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();					
	}

	public void read_line(String TESTSTRING){
		String url = this.getFilesDir().getAbsolutePath() + "/" + rms + "logosder.txt";
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(url);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		InputStream in = null;
		if(fin != null)
				in = (InputStream) fin;
		if (in != null) {
		  // prepare the file for reading
		  InputStreamReader input = new InputStreamReader(in);
		  BufferedReader buffreader = new BufferedReader(input); 
		  String line;		  
		  do {
				line = null;  
			  try {
				line = buffreader.readLine();				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(line != null)
				lv_arr[count] = line;
			count++;
		  }while ( line != null);
 
		}
 		try {
 			if(in != null)
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}		
}