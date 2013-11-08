package com.modifoto;

import java.io.File;
import java.util.Date;
import com.modifoto.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.widget.ImageView;

public class TakenCam extends Activity {	
	Uri imgUri; 
	ImageView imv;
	File folder,image;
	String currentTime;
	boolean success = false;
	public void onCreate(Bundle savedInstanceState) {
	       	super.onCreate(savedInstanceState);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//設定螢幕不隨手機旋轉
	       	imv = (ImageView)findViewById(R.id.imageView);  //參照 Layout 中的 ImageView 元件
	       	
	       	folder = new File(Environment.getExternalStorageDirectory() + "/Modifoto");
	       	//folder.mkdir();
	       	currentTime = (String) DateFormat.format("yyyyMMdd-hh:mm:ss", new Date());
	       	imgUri = Uri.parse("file://" + folder + "/" + currentTime);	    
		    image = new File(folder, currentTime+"-"+imgUri.getLastPathSegment());
	       	Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
	       	it.putExtra(MediaStore.EXTRA_OUTPUT, image);
	       	startActivityForResult(it, 10);
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);		
        Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath());
        if(resultCode == Activity.RESULT_OK && requestCode==10) {
            bmp = BitmapFactory.decodeFile(imgUri.getPath());
    		imv.setImageBitmap(bmp);   
 		}
    }
	
	public void onPause() {
		super.onPause();
	    finish();
	}
		
	public void onResume() {
	    super.onResume();
	}
		
	public void onDestroy() {
	    super.onDestroy();
	    finish();
	}
}