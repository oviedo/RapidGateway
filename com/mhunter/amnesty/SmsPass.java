package com.mhunter.amnesty;

import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;

public class SmsPass extends Activity {
	
	private EditText txtuser;
	private EditText txtpass;
	private Button btnchange;
	private Button btncancel;
	String rms = "sub";

/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passnew);
		initControls2();
	}

	private void initControls2()
	{
		txtuser = (EditText)findViewById(R.id.txtuser);
		txtpass = (EditText)findViewById(R.id.txtpass);
		btnchange = (Button)findViewById(R.id.btnchange);
		btncancel = (Button)findViewById(R.id.btncancel);
		btnchange.setOnClickListener(new Button.OnClickListener() { 
		public void onClick (View v){ calculate(); }
		});
		btncancel.setOnClickListener(new Button.OnClickListener() { 
		public void onClick (View v){ terminate(); }
		});
	}

	private void calculate()
	{
		try {
			write_file(txtuser.getText().toString() + "?" + txtpass.getText().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		terminate();
	}

	private void terminate()
	{
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();					
	}

	public void write_file(String TESTSTRING) throws Exception{
		String url = this.getFilesDir().getAbsolutePath() + "/" + rms + "pass_add.txt";
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
	
}