package com.mhunter.amnesty;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.content.Intent;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;

public class SMS extends Activity {
	
	public TextView txtTextView1;
	public TextView txtTextView2;
	Thread myThread;
	public static final int MENU_SETTINGS = 0;
	public static final int MENU_LOGS = 1;
	public static final int MENU_FLUSH = 2;
	public static final int MENU_EXIT = 3;
	public int valor = 1;
	String rms = "sub";
	public boolean received = true;
	public int counter = 0;
	private String lv_arr[][] = new String[50][2];
	private int count = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			write_file("1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request();
		txtTextView1 = (TextView)findViewById(R.id.TextView01);
		txtTextView2 = (TextView)findViewById(R.id.TextView03);
		myThread = new Thread(new UpdateThread());
        myThread.start();		
	}

	public void onResume(){
		super.onResume();
		try {
			write_file("1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request();
	}
	
	public void onRestart(){
		super.onRestart();
		try {
			write_file("1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request();
	}
	
		 
    public void setttext(String str1, String str2) {
    	try{
    	txtTextView1.setText(str1);
    	txtTextView2.setText(str2);
    	txtTextView1.refreshDrawableState();
    	txtTextView2.refreshDrawableState();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
    }
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);;
        menu.add(0, MENU_LOGS, 0, "Logs").setIcon(android.R.drawable.ic_menu_info_details);;
        menu.add(0, MENU_FLUSH, 0, "Flush").setIcon(android.R.drawable.ic_menu_help);;
        menu.add(0, MENU_EXIT, 0, "Exit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);;
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_SETTINGS:
        	pass();
            return true;
        case MENU_LOGS:
            logs();
        	return true;
        case MENU_FLUSH:
        	read_line("");
        	for(int i = 0; i< count; i++){
        		Intent newintent = new Intent(this, HttpSender.class);
        		newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		newintent.putExtra("address", lv_arr[i][0]);
        		newintent.putExtra("message", lv_arr[i][1]);
        		startActivity(newintent);
        	}
            return true;
        case MENU_EXIT:
    		try {
    			write_file("0");
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		finish();
        	return true;
        }
        return false;
    }    

    public void pass() {
    	Intent newintent = new Intent(this, SmsPass.class);
    	newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(newintent);				
    }
    
    public void logs() {
    	Intent newintent = new Intent(this, ListLog.class);
    	newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(newintent);				
    }

    public class UpdateThread implements Runnable {
    	             //@Override
            public void run() {
                    // TODO Auto-generated method stub
                    while(true) {
                    	int x1 = 0;
                    	int x2 = 0;
                    	String url1 = getFilesDir().getAbsolutePath() + "/" + rms + "recder.txt";
                    	String url2 = getFilesDir().getAbsolutePath() + "/" + rms + "sender.txt";
                    	try {
							x1 = read(url1);
							x2 = read(url2);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String str1 = Integer.toString(x1);
						String str2 = Integer.toString(x2);
						setttext(str1, str2);
						if(valor != 0){						
							if(counter == 4){
								request();
								counter = 0;
							}
							counter++;
						}	
						
                        SystemClock.sleep(1000);
                    }
            }
    }
    
	public int read_flag(String url) throws Exception{
		int myNum = 0;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(url);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream in = (InputStream) fin;
		if (in != null) {
		  // prepare the file for reading
		  InputStreamReader input = new InputStreamReader(in);
		  BufferedReader buffreader = new BufferedReader(input); 
		  String line;		  
		  line = null;  
		  try {
				line = buffreader.readLine();				
		  	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  	
			try {
				if(line != null)
					myNum = Integer.parseInt(line);
			} catch(NumberFormatException nfe) {
			   System.out.println("Could not parse " + nfe);
			} 						 
			try {
			in.close();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		return myNum;
	}    
    
	public void request(){
        String url = "http://api.eflyer.com.mx"; //URL API, Activity notification
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httpost = new HttpPost( new URI( url));
            httpclient.execute( httpost);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
            //} 
	public int read(String url) throws Exception{
		int myNum = 0;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(url);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream in = (InputStream) fin;
		if (in != null) {
		  // prepare the file for reading
		  InputStreamReader input = new InputStreamReader(in);
		  BufferedReader buffreader = new BufferedReader(input); 
		  String line;		  
		  line = null;  
		  try {
				line = buffreader.readLine();				
		  	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  	
			try {
				if(line != null)
					myNum = Integer.parseInt(line);
			} catch(NumberFormatException nfe) {
			   System.out.println("Could not parse " + nfe);
			} 						 
			try {
			in.close();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		return myNum;
	}	
	public void write_file(String TESTSTRING) throws Exception{
		valor = Integer.valueOf(TESTSTRING);
		String url = getFilesDir().getAbsolutePath() + "/" + rms + "flagder.txt";
		str2file(TESTSTRING, url);
	}	

	public static void data2file(


        byte[] w,String fileName) throws Exception {
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(fileName);
            fos.write(w);
            fos.close();
        } catch (Exception e) {
            if (fos!=null) fos.close();
            throw e;
        }
    }
	
	public static void str2file(
        String str,String fileName) throws Exception {
        data2file(str.getBytes(),fileName);
    }
	
	public void read_line(String TESTSTRING){
		count = 0;
		String url = this.getFilesDir().getAbsolutePath() + "/" + rms + "errors_flag.txt";
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
				// 	TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(line != null){
					String str = line;
					int aux1 = 0;
					int aux2 = 0;
					int indice = 0;
					for(int i= 0; i < str.length() - 1; i++){
						if(str.charAt(i) == '/' && str.charAt(i+1) == '*' && str.charAt(i+2) == '/'){
							if(aux1 == 0){
								aux1 = i;
								i += 2;
							}
							else{
								aux2 = i;
								i = str.length();							
							}							
						}
						if(str.charAt(i) == '/' && str.charAt(i+1) == '*' && str.charAt(i+2) == '*' && str.charAt(i+3) == '/'){
							if(aux2 == 0){
								aux2 = i;
								i += 3;
							}
						}	
						if(aux1 > 0 && aux2 > 0){
							lv_arr[count][0] = str.substring(indice, aux1);
							lv_arr[count][1] = str.substring(aux1 + 3, aux2);
							count++;
							aux1=0;
							aux2=0;
							indice = i + 1;
						}
					}
				}	
		  	}while ( line != null); 
		}
 		try {
 			if(in != null)
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		try {
 	        FileOutputStream fos = null;
 	        try {
 	            fos = new FileOutputStream(url);
 	            fos.close();
 	        } catch (Exception e) {
 	            if (fos != null) fos.close();
 	            throw e;
 	        } 			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}