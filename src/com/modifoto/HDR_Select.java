package com.modifoto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.widget.Toast;

public class HDR_Select extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        	Intent it = new Intent(Intent.ACTION_GET_CONTENT);  
			it.setType("image/*");
			
			startActivityForResult(it,11);	
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {	
		Uri imgUri;			
		if(resultCode == Activity.RESULT_OK) { 
			switch(requestCode){
			case 10:
				imgUri = convertUri(data.getData());
				Log.d("Edit", imgUri.getPath());
				String imgPath = imgUri.getPath();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
	    		bundle.putString("imagePath", imgPath);
	    		
	    		intent.putExtras(bundle);
	    		intent.setType("*/*");
	    		
	    		Log.d("hellow", "go Editimv");
	  		  	intent.setClass(HDR_Select.this,Edit.class);
	  		  	Log.d("fail", "no Editimv");
	  		  	startActivity(intent);
	  		  	finish();
	        	break;
			case 11:
				imgUri = convertUri(data.getData());
				//Log.d("Edit", imgUri.getPath());
				String imgPath2 = imgUri.getPath();
				Intent intent2 = new Intent();
				Bundle bundle2 = new Bundle();
	    		bundle2.putString("imagePath", imgPath2);
	    		
	    		intent2.putExtras(bundle2);
	    		intent2.setType("*/*");
	    		
	    		//Log.d("hellow", "go Editimv");
	  		  	intent2.setClass(HDR_Select.this,HDR_inf.class);
	  		  	//Log.d("fail", "no Editimv");
	  		  	startActivity(intent2);
	  		  	finish();
	        	break;
			}
		}
		else{  		  	
 			Toast.makeText(this,"沒有選取相片",Toast.LENGTH_LONG).show();
  		  	finish();
		}
	}
	
	Uri convertUri(Uri uri) {
		if(uri.toString().substring(0, 7).equals("content")) {  
			String[] colName = { MediaColumns.DATA };    
			Cursor cursor = getContentResolver().query(uri, colName,null, null, null); 
			cursor.moveToFirst();
			uri = Uri.parse("file://" + cursor.getString(0)); 
		}
		return uri;   
	}
	
	public void onPause() {
        super.onPause();
	}
	
	public void onResume() {
       super.onResume();
   }
	
	public void onDestroy() {
       super.onDestroy();
   }	
}
