package com.modifoto;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class Team extends Activity {	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teampage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//設定螢幕不隨手機旋轉
        
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