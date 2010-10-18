package com.mhunter.amnesty;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Date;



public class HttpSender extends Activity {
	
	//Local variables	
	static String user = ""; // EMAIL ADDRESS	
	String pass = md5(""); // PASSWORD
	static String rms = "sub";
	static String sms = "";	
	static String url_base = "http://api.eflyer.com.mx"; //URL API, Send SMS
		
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		String address = extras.getString("address");
		String message = extras.getString("message");
		read_line();		
		if(if_exists(this, rms + "pass_add.txt")){
			try {
				read_pass();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(address != null && message != null){
			if(address.length() == 8)
				address = "591" + address; 
			String url;
			try {
				url = url_base + "&u=" + user + "&p=" + pass + "&cel=" + address + "&sms=" + URLEncoder.encode(message, "UTF-8");
				sms = getUrlData(url);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(sms == null)
				sms = " ";
			String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
			currentDateTimeString = currentDateTimeString + " OUT " + address + "  " + sms + "\n";			
			try {
				write_file(this, currentDateTimeString);
				write_sen(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(sms != "OK"){
				try {
					write_file_error(this, address + "/*/" + message + "/**/");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Toast.makeText(getBaseContext(), "SERVER: " + sms,
					Toast.LENGTH_SHORT).show();			
		}
		else{
			Toast.makeText(getBaseContext(), sms,
					Toast.LENGTH_SHORT).show();			
		}
		 //SystemClock.sleep(500);
		 Intent intent = new Intent();
         setResult(RESULT_OK, intent);
         finish();		
	}

	public void write_sen(Context context) throws Exception{
		String url = context.getFilesDir().getAbsolutePath() + "/" + rms + "sender.txt";
		int x = read_sen(context, url) + 1;
		String str = Integer.toString(x);
		byte[] w = str.getBytes();
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(url);
            fos.write(w);
            fos.close();
        } catch (Exception e) {
            if (fos!=null) fos.close();
            throw e;
        }
	}	
	
	public int read_sen(Context context, String url) throws Exception{
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
	
	public void write_file(Context context, String TESTSTRING) throws Exception{
		String url = context.getFilesDir().getAbsolutePath() + "/" + rms + "logosder.txt";
		str2file(TESTSTRING, url);
	}	

	public void write_file_error(Context context, String TESTSTRING) throws Exception{
		String url = context.getFilesDir().getAbsolutePath() + "/" + rms + "errors_flag.txt";
		str2file(TESTSTRING, url);
	}	

	public static void data2file(
        byte[] w,String fileName) throws Exception {
        FileOutputStream fos=null;
        try {
            fos = new FileOutputStream(fileName, true);
            fos.write(w);
            fos.close();
        } catch (Exception e) {
            if (fos != null) fos.close();
            throw e;
        }
    }
	
	public static void str2file(
        String str,String fileName) throws Exception {
        data2file(str.getBytes(),fileName);
    }

	public static boolean if_exists(Context context,String fileName) {
		String url = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        return (new File(url)).exists();
    }    

	public void read_pass() throws Exception{
		String url = this.getFilesDir().getAbsolutePath() + "/" + rms + "pass_add.txt";
		String str = file2str(url);
		String result[] = parseStr(str, '?');
		int x = result.length;
		if(x > 0)
			user = result[0];
		if(x > 1)
			pass = md5(result[1]);
	}

	public static String[] parseStr(String str ,char sep) {
        int i,j,size;
        String[] result;
        if (str.equals("") || str.charAt(str.length()-1) != sep) str += sep;
        size = 0;
        i = str.indexOf(sep);
        while (i >= 0) {
            size++;
            i = str.indexOf(sep,i+1);
        }    
        result = new String[size];
        size = 0;
        j = 0;
        i = str.indexOf(sep);
        while (i >= 0) {
            result[size++] = str.substring(j,i);
            j = i+1;
            i = str.indexOf(sep,j);
        }
        return result;
    }

	public static String file2str(
        String fileName) throws Exception {  
        byte[] w = file2data(fileName);
        return new String(w);
    }

	public static byte[] file2data(String fileName) throws Exception {
        int size;
        byte[] w = new byte[1024];
        FileInputStream fin = null;
        ByteArrayOutputStream out = null;
        try {
        	fin = new FileInputStream(fileName);
            out = new ByteArrayOutputStream();
            while (true) {
            	size = fin.read(w);
                if (size <= 0) break;
                out.write(w,0,size);
            }
            fin.close();
            out.close();
            return out.toByteArray();
        } catch (Exception e) {
        try {
        	if (fin != null) fin.close();
            	if (out != null) out.close();
        } catch (Exception e2) {
        }
        	throw e;
        }
    }
	
	public void read_line(){
		Resources resources = this.getResources();
		InputStream in = null;
		in = resources.openRawResource(R.raw.pass);
		if (in != null) {
		  // prepare the file for reading
		  InputStreamReader input = new InputStreamReader(in);
		  BufferedReader buffreader = new BufferedReader(input); 
		  String line;
		  int count = 0;
		  do {
			  line = null;
			  try {
				line = buffreader.readLine();				
				if(line != null){
					if(count == 0)
						user = line;
					else
						pass = md5(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
			if(count == 2)
				break;
		  }while ( line != null);
 
		}
 		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
		public String getUrlData(String url) {
		   		String websiteData = null;
		   			try {
		   				DefaultHttpClient client = new DefaultHttpClient();
		   				URI uri = new URI(url);
		   				HttpGet method = new HttpGet(uri);
		   				HttpResponse res = client.execute(method);
		   				InputStream data = res.getEntity().getContent();
		   				websiteData = generateString(data);
		   			} catch (ClientProtocolException e) {
		   				// TODO Auto-generated catch block
		   				e.printStackTrace();
		   			} catch (IOException e) {
		   				// TODO Auto-generated catch block
		   				e.printStackTrace();
		   			} catch (URISyntaxException e) {
		   				// TODO Auto-generated catch block
		   					e.printStackTrace();
		   			}
		       return websiteData;
		  	}	
	
		public String generateString(InputStream stream) {
			InputStreamReader reader = new InputStreamReader(stream);
		  	BufferedReader buffer = new BufferedReader(reader);
		  	StringBuilder sb = new StringBuilder();
		  	try {
		  		String cur;
		  	    while ((cur = buffer.readLine()) != null) {
		  	    	sb.append(cur + "\n");
		  	    }
		  	} catch (IOException e) {
		  		// TODO Auto-generated catch block
		  	    e.printStackTrace();
		  	}
		  	try {
		  		stream.close();
		  	} catch (IOException e) {
		  		// TODO Auto-generated catch block
		  	    e.printStackTrace();
		  	}
		  	return sb.toString();
		}
			
        public String md5(String s) {  
        	try {  
        		// Create MD5 Hash  
                MessageDigest digest = java.security.MessageDigest.getInstance("MD5");  
                digest.update(s.getBytes());  
                byte messageDigest[] = digest.digest();  
                // Create Hex String                	 
                StringBuffer hexString = new StringBuffer();  
                for (int i=0; i<messageDigest.length; i++)  
                	hexString.append(Integer.toHexString(0xFF & messageDigest[i]));  
            	return hexString.toString();  
            } catch (NoSuchAlgorithmException e) {  
            	e.printStackTrace();  
            }  
            return "";  
        }
        
}
