package com.modifoto;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Home extends Activity {	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//設定螢幕不隨手機旋轉
        
        Button take=(Button)findViewById(R.id.shoot);
        take.setOnClickListener(shoot);  //指使shoot按鈕連接shoot
        
        Button si=(Button)findViewById(R.id.edit);
        si.setOnClickListener(edit);  //指使search_image按鈕擁有search_image
        
        Button HDR=(Button)findViewById(R.id.hdr);
        HDR.setOnClickListener(hdr);  //指使search_image按鈕擁有search_image
      
        Button pen=(Button)findViewById(R.id.about);
        pen.setOnClickListener(about);
        
        Button over=(Button)findViewById(R.id.finish);
        over.setOnClickListener(finish);
    }
    
    private Button.OnClickListener shoot=new Button.OnClickListener(){
    	public void onClick(View v){    		
    		//Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
    		Intent intent = new Intent();
    		intent.setClass(Home.this,TakenCam.class);
    		
    	    startActivity(intent); 
    	}
    };
    
    private Button.OnClickListener edit=new Button.OnClickListener(){
    	public void onClick(View v){    		
    		Intent intent = new Intent();
  		  	intent.setClass(Home.this, Edit_Select.class);
  		  
  		  	startActivity(intent);
    	}
    };    
    
    private Button.OnClickListener hdr=new Button.OnClickListener(){
    	public void onClick(View v){    		
    		Intent intent = new Intent();
  		  	intent.setClass(Home.this, HDR_Select.class);
  		  
  		  	startActivity(intent);
    	}
    };  

    private Button.OnClickListener about=new Button.OnClickListener() {
    	public void onClick(View v){
    		Intent intent=new Intent();
    		intent.setClass(Home.this,Team.class);
    		
    		startActivity(intent);
    	}
    };
    
    private Button.OnClickListener finish=new Button.OnClickListener() {
    	public void onClick(View v){
    		
    		android.os.Process.killProcess(android.os.Process.myPid());
    		//System.exit(0);
    		//Home.finishProgram();  
    	}
    };
        
    public void onPause() {
        super.onPause();
        //finish();
        System.gc();
	}
	
	public void onResume() {
       super.onResume();
   }
	
	public void onDestroy() {
       super.onDestroy();
       System.gc();
   }	
}