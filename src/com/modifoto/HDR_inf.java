package com.modifoto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;

public class HDR_inf extends Activity { 
	Uri imgUri;
	ImageView imv;
	public static final int PICTURE = 0;
	public static final int MAX_WIDTH = 240;
	public static final int MAX_HEIGHT = 240;
	Bitmap srcbmp,oribmp;
	Button hdr = null, hdrs = null,hdr_channel = null ,hdr_channels =null ;
	YUV_Y_enhance YUV_Y = new YUV_Y_enhance();
	LCH_C_enhance LCH_C = new LCH_C_enhance();
	LCH_L_enhance LCH_L = new LCH_L_enhance();
	Hist_enhance hist = new Hist_enhance();
	float[] parameter = {1,8};
	Button[] hdrsButton = new Button[2];
	Button[] hdrButton = new Button[8];// Y and Histogram LCH_L and LCH_C
	int reset = 0;	
    public ProgressDialog pDialog = null;
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    private WifiManager wifiManager;
	    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.hdrimv);		
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		imv = (ImageView)findViewById(R.id.imageView);  
		Bundle bundle = this.getIntent().getExtras();
		
		if(bundle !=null){
			String imgPath = bundle.getString("imagePath");
			imgUri = Uri.fromFile(new File(imgPath));
			showImg();
		}
		else
			showMessage();
		
		hdr_channel=(Button)findViewById(R.id.ButofLv);
		hdr_channel.setOnClickListener(dochannel);
		
		hdr_channels=(Button)findViewById(R.id.ButofCv);
		hdr_channels.setOnClickListener(dochannels);
		
		HDR();
		HDRs();
	}
	
	private static int computeInitialSampleSize(BitmapFactory.Options option,int minSideLength, int maxNumOfPixels) {
		 
	    double w = option.outWidth;
	    double h = option.outHeight;
	 
	    int lowerBound = (maxNumOfPixels == -1) ? 1 :
	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 :
	            (int) Math.min(Math.floor(w / minSideLength),
	            Math.floor(h / minSideLength));
	 
	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }
	 
	    if ((maxNumOfPixels == -1) &&
	            (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}	
	
	public static int computeSampleSize(BitmapFactory.Options option,int minSideLength, int maxNumOfPixels) {
		 
	    int initialSize = computeInitialSampleSize(option, minSideLength,maxNumOfPixels);
	    int roundedSize;
	 
	    if (initialSize<=8){
	    	roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<=1;
	         }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }	 
	    return roundedSize;
	}	
	
	public static int readPictureDegree(String path) {
		 int degree  = 0;
		 try 
		 {
			 ExifInterface exifInterface = new ExifInterface(path);

			 int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			 switch (orientation) {
			 case ExifInterface.ORIENTATION_ROTATE_90:
				 degree = 90;
				 break;
				 case ExifInterface.ORIENTATION_ROTATE_180:
					 degree = 180;
					 break;
					 case ExifInterface.ORIENTATION_ROTATE_270:
						 degree = 270;
						 break;
						 }

		         } catch (IOException e) {

		        	 e.printStackTrace();
		         }
		         return degree;
		     }
	 public static Bitmap rotaingImageView(int angle , Bitmap bitmap) { 
		        Matrix matrix = new Matrix();; 
		        matrix.postRotate(angle); 
		        System.out.println("angle2=" + angle); 
		        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, 
		        bitmap.getWidth(), bitmap.getHeight(), matrix, true); 
		        return resizedBitmap; 
		    }


	void showImg() {
		BitmapFactory.Options option = new BitmapFactory.Options(); 
		option.inJustDecodeBounds = true;    
		BitmapFactory.decodeFile(imgUri.getPath(), option);  
			
	    option.inJustDecodeBounds = false; 
	    option.inPurgeable = true;        
	    option.inDither=false;                     //Disable Dithering mode
	    option.inInputShareable=true; 
	    option.inSampleSize = computeSampleSize(option, -1, 1400*1400);
	    srcbmp = BitmapFactory.decodeFile(imgUri.getPath(), option);
	    File file = new File(imgUri.getPath());

	    int degree = readPictureDegree(file.getAbsolutePath()); 
	    Bitmap cbitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),option);

	    srcbmp = rotaingImageView(degree, cbitmap); 
	  	oribmp=srcbmp;
	  	imv.setImageBitmap(srcbmp);
	  	
	}
	
	void HDRs(){		
 	   	hdrsButton[0]=(Button)findViewById(R.id.ButofLv);
	   	hdrsButton[0].setOnClickListener(dochannel);
	   	hdrsButton[1]=(Button)findViewById(R.id.ButofCv);
	   	hdrsButton[1].setOnClickListener(dochannels);
	}

	void HDR(){
		hdrButton[0]=(Button)findViewById(R.id.ButofYv);
	   	hdrButton[0].setOnClickListener(enhanceY);
	   	hdrButton[1]=(Button)findViewById(R.id.ButofHist);
	   	hdrButton[1].setOnClickListener(doHistogram);
	   	hdrButton[2]=(Button)findViewById(R.id.Lch_L1);
	   	hdrButton[2].setOnClickListener(enhanceL1);
	   	hdrButton[3]=(Button)findViewById(R.id.Lch_L2);
	   	hdrButton[3].setOnClickListener(enhanceL2);
	   	hdrButton[4]=(Button)findViewById(R.id.Lch_L3);
	   	hdrButton[4].setOnClickListener(enhanceL3);
	   	hdrButton[5]=(Button)findViewById(R.id.Lch_C1);
	   	hdrButton[5].setOnClickListener(enhanceC1);
	   	hdrButton[6]=(Button)findViewById(R.id.Lch_C2);
	   	hdrButton[6].setOnClickListener(enhanceC2);
	   	hdrButton[7]=(Button)findViewById(R.id.Lch_C3);
	   	hdrButton[7].setOnClickListener(enhanceC3);
	}
	
	 private Button.OnClickListener dochannel=new Button.OnClickListener(){
	    	public void onClick(View v){
	    		hdrButton[0].setBackgroundResource(R.drawable.lch);
	    		hdrButton[1].setBackgroundResource(R.drawable.lch);
	    		hdr_channel.setBackgroundResource(R.drawable.lch_change);
	    		hdr_channels.setBackgroundResource(R.drawable.lch);
	    		LinearLayout LCH_LLayout = (LinearLayout) findViewById(R.id.layoutofBlock1);
	    		LCH_LLayout.setVisibility(View.VISIBLE);
	    		LinearLayout LCH_CLayout = (LinearLayout) findViewById(R.id.layoutofBlock2);
		    	LCH_CLayout.setVisibility(View.GONE);
	    	}
		};	
		private Button.OnClickListener dochannels=new Button.OnClickListener(){
			public void onClick(View v){
				hdrButton[0].setBackgroundResource(R.drawable.lch);
	    		hdrButton[1].setBackgroundResource(R.drawable.lch);
				hdr_channels.setBackgroundResource(R.drawable.lch_change);
				hdr_channel.setBackgroundResource(R.drawable.lch);
				LinearLayout LCH_LLayout = (LinearLayout) findViewById(R.id.layoutofBlock1);
	    		LCH_LLayout.setVisibility(View.GONE);
		    	LinearLayout LCH_CLayout = (LinearLayout) findViewById(R.id.layoutofBlock2);
		    	LCH_CLayout.setVisibility(View.VISIBLE);
		    	}
			};
	
	Handler mHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            if(msg.what == 1) {  
            	setImg(parameter);
            }  
            super.handleMessage(msg);  
        }  
    }; 

	private Button.OnClickListener enhanceY=new Button.OnClickListener(){
	    public void onClick(View v){
	    	final CharSequence strTitle = getString(R.string.title);
	        final CharSequence strText = getString(R.string.Ytext);    
	        LinearLayout LCH_LLayout = (LinearLayout) findViewById(R.id.layoutofBlock1);
    		LCH_LLayout.setVisibility(View.GONE);
    		LinearLayout LCH_CLayout = (LinearLayout) findViewById(R.id.layoutofBlock2);
        	LCH_CLayout.setVisibility(View.GONE);
        	hdrButton[0].setBackgroundResource(R.drawable.lch_change);
    		hdrButton[1].setBackgroundResource(R.drawable.lch);
    		hdr_channel.setBackgroundResource(R.drawable.lch);
    		hdr_channels.setBackgroundResource(R.drawable.lch);
        	
	    	pDialog = ProgressDialog.show(HDR_inf.this, strTitle, strText, true);
	    	
            new Thread(){ 
                @Override
                public void run(){ 
                    try{
                    	Thread.sleep(1000);
                    	if(hdrButton[0].getCurrentTextColor() == Color.RED){
                    		parameter[1] = 8;
                    		
                    	}
                    	else{
                    		parameter[1] = 0;
                    	}
                    	Message m = new Message();
    	            	m.what = 1;
    	            	mHandler.sendMessage(m);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        pDialog.dismiss();
                    }
                }
            }.start();
	    }
	};	
	
	private Button.OnClickListener doHistogram=new Button.OnClickListener(){
	    public void onClick(View v){
	    	final CharSequence strTitle = getString(R.string.title);
	        final CharSequence strText = getString(R.string.Htext);
	        LinearLayout LCH_LLayout = (LinearLayout) findViewById(R.id.layoutofBlock1);
    		LCH_LLayout.setVisibility(View.GONE);
    		LinearLayout LCH_CLayout = (LinearLayout) findViewById(R.id.layoutofBlock2);
        	LCH_CLayout.setVisibility(View.GONE);
        	hdrButton[0].setBackgroundResource(R.drawable.lch);
    		hdrButton[1].setBackgroundResource(R.drawable.lch_change);
    		hdr_channel.setBackgroundResource(R.drawable.lch);
    		hdr_channels.setBackgroundResource(R.drawable.lch);
	        
	    	pDialog = ProgressDialog.show(HDR_inf.this, strTitle, strText, true);
            new Thread(){ 
                @Override
                public void run(){ 
                    try{
                    	Thread.sleep(1000);
                    	if(hdrButton[1].getCurrentTextColor() == Color.RED){
                    		parameter[1] = 8;
                    	}
                    	else{
                    		parameter[1] = 1;
                    	}
                    	Message m = new Message();
    	            	m.what = 1;
    	            	mHandler.sendMessage(m);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        pDialog.dismiss();
                    }
                }
            }.start();
	    }
	};	
	
	private Button.OnClickListener enhanceL1=new Button.OnClickListener(){
	    public void onClick(View v){
	    	final CharSequence strTitle = getString(R.string.title);
	        final CharSequence strText = getString(R.string.Ltext);    
	        
	    	pDialog = ProgressDialog.show(HDR_inf.this, strTitle, strText, true);
            new Thread(){ 
                @Override
                public void run(){ 
                    try{
                    	Thread.sleep(1000);
                    	if(hdrButton[2].getCurrentTextColor() == Color.RED){
                    		parameter[0] = 1;
                    		parameter[1] = 8;
                    	}
                    	else{
                    		parameter[0] = 3;
                    		parameter[1] = 2;
                    	}
                    	Message m = new Message();
    	            	m.what = 1;
    	            	mHandler.sendMessage(m);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        pDialog.dismiss();
                    }
                }
            }.start();
	    }
	};	
	
	private Button.OnClickListener enhanceL2=new Button.OnClickListener(){
	    public void onClick(View v){
	    	final CharSequence strTitle = getString(R.string.title);
	        final CharSequence strText = getString(R.string.Ltext);    
	        
	    	pDialog = ProgressDialog.show(HDR_inf.this, strTitle, strText, true);
            new Thread(){ 
                @Override
                public void run(){ 
                    try{
                    	Thread.sleep(1000);
                    	if(hdrButton[3].getCurrentTextColor() == Color.RED){
                    		parameter[0] = 1;
                    		parameter[1] = 8;
                    	}
                    	else{
                    		parameter[0] = (float) 4.5;
                    		parameter[1] = 3;
                    	}
                    	Message m = new Message();
    	            	m.what = 1;
    	            	mHandler.sendMessage(m);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        pDialog.dismiss();
                    }
                }
            }.start();
	    }
	};
	
	private Button.OnClickListener enhanceL3=new Button.OnClickListener(){
	    public void onClick(View v){
	    	final CharSequence strTitle = getString(R.string.title);
	        final CharSequence strText = getString(R.string.Ltext);    
	        
	    	pDialog = ProgressDialog.show(HDR_inf.this, strTitle, strText, true);
            new Thread(){ 
                @Override
                public void run(){ 
                    try{
                    	Thread.sleep(1000);
                    	if(hdrButton[4].getCurrentTextColor() == Color.RED){
                    		parameter[0] = 1;
                    		parameter[1] = 8;
                    	}
                    	else{
                    		parameter[0] = 6;
                    		parameter[1] = 4;
                    	}
                    	Message m = new Message();
    	            	m.what = 1;
    	            	mHandler.sendMessage(m);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        pDialog.dismiss();
                    }
                }
            }.start();
	    }
	};	
	
	private Button.OnClickListener enhanceC1=new Button.OnClickListener(){
	    public void onClick(View v){
	    	final CharSequence strTitle = getString(R.string.title);
	        final CharSequence strText = getString(R.string.Ctext);    
	        
	    	pDialog = ProgressDialog.show(HDR_inf.this, strTitle, strText, true);
            new Thread(){ 
                @Override
                public void run(){ 
                    try{
                    	Thread.sleep(1000);
                    	if(hdrButton[5].getCurrentTextColor() == Color.RED){
                    		parameter[0] = 1;
                    		parameter[1] = 8;
                    	}
                    	else{
                    		parameter[0] = 3;
                    		parameter[1] = 5;
                    	}
                    	Message m = new Message();
    	            	m.what = 1;
    	            	mHandler.sendMessage(m);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        pDialog.dismiss();
                    }
                }
            }.start();
	    }
	};	
	
	private Button.OnClickListener enhanceC2=new Button.OnClickListener(){
	    public void onClick(View v){
	    	final CharSequence strTitle = getString(R.string.title);
	        final CharSequence strText = getString(R.string.Ctext);    
	        
	    	pDialog = ProgressDialog.show(HDR_inf.this, strTitle, strText, true);
            new Thread(){ 
                @Override
                public void run(){ 
                    try{
                    	Thread.sleep(1000);
                    	if(hdrButton[6].getCurrentTextColor() == Color.RED){
                    		parameter[0] = 1;
                    		parameter[1] = 8;
                    	}
                    	else{
                    		parameter[0] = (float) 4.5;
                    		parameter[1] = 6;
                    	}
                    	Message m = new Message();
    	            	m.what = 1;
    	            	mHandler.sendMessage(m);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        pDialog.dismiss();
                    }
                }
            }.start();
	    }
	};	
	
	private Button.OnClickListener enhanceC3=new Button.OnClickListener(){
	    public void onClick(View v){
	    	final CharSequence strTitle = getString(R.string.title);
	        final CharSequence strText = getString(R.string.Ctext);    
	        
	    	pDialog = ProgressDialog.show(HDR_inf.this, strTitle, strText, true);
            new Thread(){ 
                @Override
                public void run(){ 
                    try{
                    	Thread.sleep(1000);
                    	if(hdrButton[7].getCurrentTextColor() == Color.RED){
                    		parameter[0] = 1;
                    		parameter[1] = 8;
                    	}
                    	else{
                    		parameter[0] = 6;
                    		parameter[1] = 7;
                    	}
                    	Message m = new Message();
    	            	m.what = 1;
    	            	mHandler.sendMessage(m);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        pDialog.dismiss();
                    }
                }
            }.start();
	    }
	};	
	
	public void setBColor4(Button[] hdrButton, double ButtonNum){
		for(int i=0; i<8; i++){
			if(ButtonNum == 9){
				hdrButton[i].setTextColor(Color.BLACK);
			}
			else{
				if(i != ButtonNum)
					hdrButton[i].setTextColor(Color.BLACK);
				else
					hdrButton[i].setTextColor(Color.RED);
				
			}
		}
	}
		
	public void onOriimg(View v){	
		parameter[0] = 1;
		parameter[1] = 8;
    	setBColor4(hdrButton, parameter[1]);
		setImg(parameter);
	}	

	public void setImg(float[] parameter){
		setBColor4(hdrButton, parameter[1]);
				Bitmap bmp = Bitmap.createBitmap(srcbmp.getWidth(),
				srcbmp.getHeight(), srcbmp.getConfig());    	
		
    	Paint paint = new Paint();
    	
    	paint.setAntiAlias(true); 
    	
    	Canvas canvas = new Canvas(bmp);
    	
    	canvas.drawBitmap(srcbmp, 0, 0, paint);
    	
    	if(parameter[1] == 0)
			bmp = YUV_Y.hdr(bmp);
    	else if(parameter[1] == 1)
    		bmp = hist.hdr(bmp);
    	
    	if(parameter[0] != 1)
    		bmp = LCH_L.hdr(parameter[0], bmp);
    	else if(parameter[0] != 1)
    		bmp = LCH_C.hdr(parameter[0], bmp);
    	
    	imv.setImageBitmap(bmp);
	}	
	
	public void onShare(View v){
    	IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkChangeReceiver();
		registerReceiver(receiver, filter);	
		
		FileOutputStream outStream;		
		BitmapDrawable drawable = (BitmapDrawable) imv.getDrawable();
	    Bitmap bitmap = drawable.getBitmap();
	    File folder = new File(Environment.getExternalStorageDirectory() + "/MPBShared");
	    folder.mkdir();
	    String currentTime = (String) DateFormat.format("yyyyMMdd-hh:mm:ss", new Date());
	    File image = new File(folder, currentTime+"-"+imgUri.getLastPathSegment());	
	    String savedPath = image.getPath();
	    
	    imgUri = Uri.fromFile(new File(savedPath));
	    
	    try {
	        outStream = new FileOutputStream(image);
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	        outStream.flush();
	        outStream.close();
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    	    		
	    Intent it = new Intent(Intent.ACTION_SEND);
    	it.setType("image/*");
    	it.putExtra(Intent.EXTRA_STREAM, imgUri);
    	startActivity(it);
    	
    	sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
		
	}
	
    public void showMessage(){
    	Toast.makeText(this,"no picture",Toast.LENGTH_LONG).show();
    }
    
    public void onBtn(View v){
	    finish();
	}
	
	public void onSave(View v){
		boolean success = false;
		FileOutputStream outStream;
		
		BitmapDrawable drawable = (BitmapDrawable) imv.getDrawable();
	    Bitmap bitmap = drawable.getBitmap();
	    File folder = new File(Environment.getExternalStorageDirectory() + "/Modifoto");
	    folder.mkdir();
	    String currentTime = (String) DateFormat.format("yyyyMMdd-hh:mm:ss", new Date());
	    File image = new File(folder, currentTime+"-"+imgUri.getLastPathSegment());	
	    //savedPath = image.getPath();
	    try {
	        outStream = new FileOutputStream(image);
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	        outStream.flush();
	        outStream.close();
	        success = true;
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    if (success) {
	        Toast.makeText(getApplicationContext(), 
	        		"saved success",Toast.LENGTH_LONG).show();
	    } else {
	        Toast.makeText(getApplicationContext(),
	                "saved failed", Toast.LENGTH_LONG).show();
	    }
	    
	    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
	}
	
	public class NetworkChangeReceiver extends BroadcastReceiver {		 
		  @Override
		  public void onReceive(final Context context, final Intent intent) {		 
		   isNetworkAvailable(context);		 
		  }
		 		 
		  private boolean isNetworkAvailable(Context context) {
		   ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		   if (connectivity != null) {
		    NetworkInfo[] info = connectivity.getAllNetworkInfo();
		    if (info != null) {
		     for (int i = 0; i < info.length; i++) {
		      if (info[i].getState() == NetworkInfo.State.CONNECTED) {
		       if(!isConnected){
		        Toast.makeText(getApplicationContext(), 
		        		"You are connected to Network!",Toast.LENGTH_LONG).show();
		        isConnected = true;
		        //do your processing here ---
		        //if you need to post any data to the server or get status
		        //update from the server
		       }
		       return true;
		      }
		     }
		    }
		   }
		   Toast.makeText(getApplicationContext(), 
	        		"Not detecte the Network,connecting the WIFI",Toast.LENGTH_LONG).show();
		   		
			   	wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	
		   	   	if(wifiManager.isWifiEnabled()){		   	   		
		   	   		wifiManager.setWifiEnabled(false);
		   	   	}
		   	   	else{
		   	   		wifiManager.setWifiEnabled(true);
		   	   	}
		   		
		   	   	isConnected = false;
		   	   	return false;
		  }
	}
	
    public void onPause() {
        super.onPause();
	}
	
	public void onResume() {
       super.onResume();
   }
	
	public void onDestory()	{
    	if(srcbmp != null){
        	if(!srcbmp.isRecycled()){
        		srcbmp.recycle();
        		srcbmp = null;
            
            	System.gc(); // system garbage recycle            
        	}
    	}
	}
}