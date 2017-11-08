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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//�]�w�ù����H�������
        
        Button take=(Button)findViewById(R.id.shoot);
        take.setOnClickListener(shoot);  //����shoot���s�s��shoot
        
        Button si=(Button)findViewById(R.id.edit);
        si.setOnClickListener(edit);  //����search_image���s�֦�search_image
        
        Button HDR=(Button)findViewById(R.id.hdr);
        HDR.setOnClickListener(hdr);  //����search_image���s�֦�search_image
      
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