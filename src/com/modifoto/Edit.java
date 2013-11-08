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
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.graphics.drawable.BitmapDrawable;

public class Edit extends Activity { 
	Uri imgUri;
	ImageView imv;
	String selectedImagePath;
	public static final int PICTURE = 0;
	public static final int MAX_WIDTH = 240;
	public static final int MAX_HEIGHT = 240;
	Bitmap srcbmp,oribmp;
	Button hdr = null, tone = null, rotate = null, effect = null, crop = null ;
	SeekBar Satura = null;  
	SeekBar Bright = null;  
	SeekBar Contrast = null;
	TextView seekBarValue1 = null, seekBarValue2=null, seekBarValue3=null;
	HdrGamma hdrgamma = null;
	//SharpChange sharp = new SharpChange();
	Effects effects = new Effects();
	private ColorMatrix cMatrix = new ColorMatrix();

	ColorMatrix cms=new ColorMatrix(); 
    ColorMatrix cmb=new ColorMatrix(); 
    ColorMatrix cmc=new ColorMatrix();
	double[] parameter = {100, 100, 100, 1.25, 11 , 5, 10 , 1};
	int rotateNum = 0;
	int[] rotateArray = new int[100];
	Button HDR;
	Button[] effButtons = new Button[11]; // 0-10
	Button[] cropButtons = new Button[10];//0-9
	Button[] hdrButtons = new Button[5];  // 0-4
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
		setContentView(R.layout.editimv);		
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		imv = (ImageView)findViewById(R.id.imageView);  
		Bundle bundle = this.getIntent().getExtras();
    	
		hdrgamma = new HdrGamma();
		cms.reset();
		cmb.reset();
		cmc.reset();
				  
		Satura = (SeekBar) findViewById(R.id.satura);    
	    Bright = (SeekBar) findViewById(R.id.bright);
	    Contrast = (SeekBar) findViewById(R.id.contrast);
	    seekBarValue1 = (TextView)findViewById(R.id.Saturationseekbarvalue);  
	    seekBarValue2 = (TextView)findViewById(R.id.Brightnessseekbarvalue);  
	    seekBarValue3 = (TextView)findViewById(R.id.Contrastseekbarvalue); 
	    
	    
	    
	    
	    SharedPreferences preferencesGet = getApplicationContext().getSharedPreferences("image",android.content.Context.MODE_PRIVATE);  
	    //String selectedImagePath = preferencesGet.getString("selectedImagePath",""); // 圖片檔案位置，預設為空  
	    
	    
		if(bundle !=null){
			selectedImagePath = preferencesGet.getString("selectedImagePath",""); // 圖片檔案位置，預設為空  
			showImg();
		}
		else
			showMessage();
		
		Gamma();
		
        doToneBar();
        
        doEffect();
        
        doCrop();
		
		hdr=(Button)findViewById(R.id.hdr);
        hdr.setOnClickListener(dohdr);
		
		tone=(Button)findViewById(R.id.tone);
        tone.setOnClickListener(dotone);
        
        rotate=(Button)findViewById(R.id.rotate);
        rotate.setOnClickListener(dorotate);
        
        effect=(Button)findViewById(R.id.effect);
        effect.setOnClickListener(doeffect);
        
        crop=(Button)findViewById(R.id.crop);
        crop.setOnClickListener(docrop);
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
		        //rotate
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
		BitmapFactory.decodeFile(selectedImagePath, option);  
		
	    option.inJustDecodeBounds = false;  
	    option.inPurgeable = true;
	    option.inDither=false;                     //Disable Dithering mode
	    option.inInputShareable=true; 
	    option.inSampleSize = computeSampleSize(option, -1, 1400*1400);
	    srcbmp = BitmapFactory.decodeFile(selectedImagePath, option); 
	    File file = new File(selectedImagePath);
	
        int degree = readPictureDegree(file.getAbsolutePath()); 
        Bitmap cbitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),option);
        srcbmp = rotaingImageView(degree, cbitmap); 
	    oribmp=srcbmp;
	    imv.setImageBitmap(srcbmp);
	}
		

    public void onPick(View v){
    	Intent intent = new Intent();
		intent.setClass(Edit.this, Edit_Select.class);
		  
		startActivity(intent);
		Edit.this.finish();
    }    
    
    private Button.OnClickListener dohdr=new Button.OnClickListener(){
    	public void onClick(View v){
    		LinearLayout toneLayout = (LinearLayout) findViewById(R.id.layoutofTone);
    		toneLayout.setVisibility(View.GONE);
    		LinearLayout rotateLayout = (LinearLayout) findViewById(R.id.layoutofRotate);
    		rotateLayout.setVisibility(View.GONE);
    		LinearLayout effectLayout = (LinearLayout) findViewById(R.id.layoutofEffect);
    		effectLayout.setVisibility(View.GONE);
    		LinearLayout cropLayout = (LinearLayout) findViewById(R.id.layoutofCrop);
       		cropLayout.setVisibility(View.GONE);
    		LinearLayout hdrLayout = (LinearLayout) findViewById(R.id.layoutofHDR);
    		hdrLayout.setVisibility(View.VISIBLE);
    		hdr.setBackgroundResource(R.drawable.gammachange);
    		tone.setBackgroundResource(R.drawable.tone);
        	rotate.setBackgroundResource(R.drawable.move);
        	effect.setBackgroundResource(R.drawable.effect);
        	crop.setBackgroundResource(R.drawable.crop);
	 	    reset = 0;
    	}
	};	
	
	void Gamma(){
		hdrButtons[0]= (Button)findViewById(R.id.lowkey);
		hdrButtons[0].setOnClickListener(doLow);
		hdrButtons[1]= (Button)findViewById(R.id.midkey);
		hdrButtons[1].setOnClickListener(doMid);
 	   	hdrButtons[2]= (Button)findViewById(R.id.highkey);
 	   	hdrButtons[2].setOnClickListener(doHigh);
 		hdrButtons[3]= (Button)findViewById(R.id.morekey);
 	   	hdrButtons[3].setOnClickListener(doMore);
 		hdrButtons[4]= (Button)findViewById(R.id.moreskey);
 	   	hdrButtons[4].setOnClickListener(doMores);
	}
	
	private Button.OnClickListener doLow=new Button.OnClickListener(){
	    public void onClick(View v){
	    	if(hdrButtons[0].getCurrentTextColor() == Color.RED){
	    		parameter[3] = 1.25;
	    		parameter[5] = 5;
	    	}
	    	else{
	    		parameter[3] = 0.4;
	    		parameter[5] = 0;
	    	}       
	    	setImg(parameter);
	    }
	};	
		
	private Button.OnClickListener doMid=new Button.OnClickListener(){
	    public void onClick(View v){
	    	if(hdrButtons[1].getCurrentTextColor() == Color.RED){	                	
	    		parameter[3] = 1.25;	                	
	    		parameter[5] = 5;
	    	}	            	
	    	else{	            		
	    		parameter[3] = 0.8;	            		
	    		parameter[5] = 1;	            	
	    	}
	    	setImg(parameter);
	    }
	};	
	
	private Button.OnClickListener doHigh=new Button.OnClickListener(){
	    public void onClick(View v){
	    	if(hdrButtons[2].getCurrentTextColor() == Color.RED){
	    		parameter[3] = 1.25;
                parameter[5] = 5;                    	
	    	}                    	
	    	else{
	    		parameter[3] = 1.2;
	    		parameter[5] = 2;
	    	}
	    	setImg(parameter);
	    }
	};	
	
	private Button.OnClickListener doMore=new Button.OnClickListener(){
	    public void onClick(View v){
	    	if(hdrButtons[3].getCurrentTextColor() == Color.RED){
	    		parameter[3] = 1.25;
                parameter[5] = 5;                    	
	    	}                    	
	    	else{
	    		parameter[3] = 1.6;
	    		parameter[5] = 3;
	    	}
	    	setImg(parameter);
	    }
	};
	
	private Button.OnClickListener doMores=new Button.OnClickListener(){
	    public void onClick(View v){
	    	if(hdrButtons[4].getCurrentTextColor() == Color.RED){
	    		parameter[3] = 1.25;
                parameter[5] = 5;                    	
	    	}                    	
	    	else{
	    		parameter[3] = 2.0;
	    		parameter[5] = 4;
	    	}
	    	setImg(parameter);
	    }
	};
	
	
	private Button.OnClickListener dotone=new Button.OnClickListener(){
    	public void onClick(View v){
    		LinearLayout harLayout = (LinearLayout) findViewById(R.id.layoutofHDR);
    		harLayout.setVisibility(View.GONE);
    		LinearLayout rotateLayout = (LinearLayout) findViewById(R.id.layoutofRotate);
    		rotateLayout.setVisibility(View.GONE);
    		LinearLayout effectLayout = (LinearLayout) findViewById(R.id.layoutofEffect);
    		effectLayout.setVisibility(View.GONE);
    		LinearLayout cropLayout = (LinearLayout) findViewById(R.id.layoutofCrop);
       		cropLayout.setVisibility(View.GONE);
    		LinearLayout toneLayout = (LinearLayout) findViewById(R.id.layoutofTone);
    		toneLayout.setVisibility(View.VISIBLE);
    		tone.setBackgroundResource(R.drawable.tonechange);
    		hdr.setBackgroundResource(R.drawable.gamma);
        	effect.setBackgroundResource(R.drawable.effect);
            rotate.setBackgroundResource(R.drawable.move);
            crop.setBackgroundResource(R.drawable.crop);
    		
    		reset = 1;
    	}
	};	
	
	public void doToneBar(){	    	    	    
        Satura.setOnSeekBarChangeListener(new OnSeekBarChangeListener()  {        	
        	public void onProgressChanged(SeekBar arg0, int progress,boolean fromUser) {
        		       		
			}
			@Override
			public void onStartTrackingTouch(SeekBar bar) {
        	}  
        	public void onStopTrackingTouch(SeekBar bar) {
        		parameter[0] = bar.getProgress();
			   	setImg(parameter);
        		seekBarValue1.setText(String.valueOf(bar.getProgress()-100)); 
        	}  
		});        
        Bright.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar arg0, int progress,boolean fromUser) { 
        		
	        	}  
				@Override
				public void onStartTrackingTouch(SeekBar bar) {
	        	}  				
	        	public void onStopTrackingTouch(SeekBar bar) {
		        	parameter[1] = bar.getProgress();
				   	setImg(parameter);
	        		seekBarValue2.setText(String.valueOf(bar.getProgress()-100));
	        	}
        });  
        Contrast.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {  
	        public void onProgressChanged(SeekBar arg0, int progress,boolean fromUser) { 
	        	
	        }
	        public void onStartTrackingTouch(SeekBar bar) {
	        }  
	        public void onStopTrackingTouch(SeekBar bar) {
	        	parameter[2] = bar.getProgress();
			   	setImg(parameter);
	        	seekBarValue3.setText(String.valueOf(bar.getProgress()-100));
	        }  
        });
	}	
	
	private Button.OnClickListener dorotate=new Button.OnClickListener(){
	    public void onClick(View v){
	    	LinearLayout harLayout = (LinearLayout) findViewById(R.id.layoutofHDR);
	    	harLayout.setVisibility(View.GONE);
	    	LinearLayout toneLayout = (LinearLayout) findViewById(R.id.layoutofTone);
	    	toneLayout.setVisibility(View.GONE);
	    	LinearLayout effectLayout = (LinearLayout) findViewById(R.id.layoutofEffect);
	    	effectLayout.setVisibility(View.GONE);
    		LinearLayout cropLayout = (LinearLayout) findViewById(R.id.layoutofCrop);
       		cropLayout.setVisibility(View.GONE);
	    	LinearLayout rotateLayout = (LinearLayout) findViewById(R.id.layoutofRotate);
	    	rotateLayout.setVisibility(View.VISIBLE);
	    	rotate.setBackgroundResource(R.drawable.movechange);
	    	 hdr.setBackgroundResource(R.drawable.gamma);
             tone.setBackgroundResource(R.drawable.tone);
             effect.setBackgroundResource(R.drawable.effect);
             crop.setBackgroundResource(R.drawable.crop);	    	
        	
	    	Button Left = (Button)findViewById(R.id.left);
	 	    Left.setOnClickListener(doleft);
	 	    Button Right = (Button)findViewById(R.id.right);
	 	    Right.setOnClickListener(doright);
	 	    Button Vertical = (Button)findViewById(R.id.vertical);
	 	    Vertical.setOnClickListener(dovertical);
	 	    Button Horizontal = (Button)findViewById(R.id.horizontal);
	 	    Horizontal.setOnClickListener(dohorizontal);
	 	    
	 	    reset = 2;
	    }
	};	

	private Button.OnClickListener doleft=new Button.OnClickListener(){
	    public void onClick(View v){
	    	rotateArray[rotateNum++] = 1;
		   	setImg(parameter);
	    }
	};	
		
	private Button.OnClickListener doright=new Button.OnClickListener(){
		public void onClick(View v){
			rotateArray[rotateNum++] = 2;
		   	setImg(parameter);
		}
	};
		
	private Button.OnClickListener dovertical=new Button.OnClickListener(){
		public void onClick(View v){
			rotateArray[rotateNum++] = 3;
		   	setImg(parameter);
		}
	};
	
	private Button.OnClickListener dohorizontal=new Button.OnClickListener(){
		public void onClick(View v){
			rotateArray[rotateNum++] = 4;
		   	setImg(parameter);
		}
	};	

    private Button.OnClickListener doeffect=new Button.OnClickListener(){
       	public void onClick(View v){
       		LinearLayout harLayout = (LinearLayout) findViewById(R.id.layoutofHDR);
       		harLayout.setVisibility(View.GONE);
       		LinearLayout toneLayout = (LinearLayout) findViewById(R.id.layoutofTone);
       		toneLayout.setVisibility(View.GONE);
       		LinearLayout rotateLayout = (LinearLayout) findViewById(R.id.layoutofRotate);
	    	rotateLayout.setVisibility(View.GONE);
	    	LinearLayout cropLayout = (LinearLayout) findViewById(R.id.layoutofCrop);
       		cropLayout.setVisibility(View.GONE);
       		LinearLayout effectLayout = (LinearLayout) findViewById(R.id.layoutofEffect);
       		effectLayout.setVisibility(View.VISIBLE);
       		
       		effect.setBackgroundResource(R.drawable.effectchange);
       		hdr.setBackgroundResource(R.drawable.gamma);
       		tone.setBackgroundResource(R.drawable.tone);
       		rotate.setBackgroundResource(R.drawable.move);
       		crop.setBackgroundResource(R.drawable.crop);
       		
       		reset = 3;
       	}
    };    
    
    public void doEffect(){
    	effButtons[0]=(Button)findViewById(R.id.lomo);
    	effButtons[0].setOnClickListener(doLomo);
    	
    	effButtons[1]=(Button)findViewById(R.id.old);
    	effButtons[1].setOnClickListener(doOld);
	    
    	effButtons[2]=(Button)findViewById(R.id.comic);
    	effButtons[2].setOnClickListener(doComic);
	    
    	effButtons[3]=(Button)findViewById(R.id.landscape);
    	effButtons[3].setOnClickListener(doLand);
	    
    	effButtons[4]=(Button)findViewById(R.id.whitening);
    	effButtons[4].setOnClickListener(doWhiten);
	    
    	effButtons[5]=(Button)findViewById(R.id.halo);
    	effButtons[5].setOnClickListener(doHalo);
	    
    	effButtons[6]=(Button)findViewById(R.id.dim);
    	effButtons[6].setOnClickListener(doDim);
	    
    	effButtons[7]=(Button)findViewById(R.id.bw);
    	effButtons[7].setOnClickListener(doBW);
	    
    	effButtons[8]=(Button)findViewById(R.id.fire);
    	effButtons[8].setOnClickListener(doFire);
	    
    	effButtons[9]=(Button)findViewById(R.id.cold);
    	effButtons[9].setOnClickListener(doCold);
    	
    	effButtons[10]=(Button)findViewById(R.id.sharp);
    	effButtons[10].setOnClickListener(doSharp);
	    
	    effects = new Effects();
    }

    private Button.OnClickListener doLomo=new Button.OnClickListener(){
    	public void onClick(View v){
        	if(effButtons[0].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
        	}
        	else{
        		parameter[4] = 0;
        		v.setBackgroundResource(R.drawable.lomochange);
        		effButtons[1].setBackgroundResource(R.drawable.old);
            	effButtons[2].setBackgroundResource(R.drawable.cartoon);
            	effButtons[3].setBackgroundResource(R.drawable.neno);
            	effButtons[4].setBackgroundResource(R.drawable.miro);
            	effButtons[5].setBackgroundResource(R.drawable.anti_color);
            	effButtons[6].setBackgroundResource(R.drawable.sketch);
            	effButtons[7].setBackgroundResource(R.drawable.bw);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[9].setBackgroundResource(R.drawable.soft);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}        	
		   	setImg(parameter);
    	}
	};	
	
	private Button.OnClickListener doOld=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(effButtons[1].getCurrentTextColor() == Color.RED){
        		parameter[4] =11;
        		effButtons[1].setBackgroundResource(R.drawable.old);
        	}
        	else{
        		parameter[4] = 1;
        		v.setBackgroundResource(R.drawable.oldchange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
            	effButtons[2].setBackgroundResource(R.drawable.cartoon);
            	effButtons[3].setBackgroundResource(R.drawable.neno);
            	effButtons[4].setBackgroundResource(R.drawable.miro);
            	effButtons[5].setBackgroundResource(R.drawable.anti_color);
            	effButtons[6].setBackgroundResource(R.drawable.sketch);
            	effButtons[7].setBackgroundResource(R.drawable.bw);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[9].setBackgroundResource(R.drawable.soft);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doComic=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(effButtons[2].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[2].setBackgroundResource(R.drawable.cartoon);
        	}
        	else{
        		parameter[4] = 2;
        		v.setBackgroundResource(R.drawable.cartoonchange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
            	effButtons[1].setBackgroundResource(R.drawable.old);
            	effButtons[3].setBackgroundResource(R.drawable.neno);
            	effButtons[4].setBackgroundResource(R.drawable.miro);
            	effButtons[5].setBackgroundResource(R.drawable.anti_color);
            	effButtons[6].setBackgroundResource(R.drawable.sketch);
            	effButtons[7].setBackgroundResource(R.drawable.bw);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[9].setBackgroundResource(R.drawable.soft);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doLand=new Button.OnClickListener(){
    	public void onClick(View v){
        	if(effButtons[3].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[3].setBackgroundResource(R.drawable.neno);
        	}
        	else{
        		parameter[4] = 3;
        		v.setBackgroundResource(R.drawable.nenochange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
            	effButtons[1].setBackgroundResource(R.drawable.old);
            	effButtons[2].setBackgroundResource(R.drawable.cartoon);
            	effButtons[4].setBackgroundResource(R.drawable.miro);
            	effButtons[5].setBackgroundResource(R.drawable.anti_color);
            	effButtons[6].setBackgroundResource(R.drawable.sketch);
            	effButtons[7].setBackgroundResource(R.drawable.bw);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[9].setBackgroundResource(R.drawable.soft);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doWhiten=new Button.OnClickListener(){
    	public void onClick(View v){
        	if(effButtons[4].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[4].setBackgroundResource(R.drawable.miro);
        	}
        	else{
        		parameter[4] = 4;
        		v.setBackgroundResource(R.drawable.mirochange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
            	effButtons[1].setBackgroundResource(R.drawable.old);
            	effButtons[2].setBackgroundResource(R.drawable.cartoon);
            	effButtons[3].setBackgroundResource(R.drawable.neno);
            	effButtons[5].setBackgroundResource(R.drawable.anti_color);
            	effButtons[6].setBackgroundResource(R.drawable.sketch);
            	effButtons[7].setBackgroundResource(R.drawable.bw);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[9].setBackgroundResource(R.drawable.soft);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doHalo=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(effButtons[5].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[5].setBackgroundResource(R.drawable.anti_color);
        	}
        	else{
        		parameter[4] = 5;
        		v.setBackgroundResource(R.drawable.antichange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
            	effButtons[1].setBackgroundResource(R.drawable.old);
            	effButtons[2].setBackgroundResource(R.drawable.cartoon);
            	effButtons[3].setBackgroundResource(R.drawable.neno);
            	effButtons[4].setBackgroundResource(R.drawable.miro);
            	effButtons[6].setBackgroundResource(R.drawable.sketch);
            	effButtons[7].setBackgroundResource(R.drawable.bw);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[9].setBackgroundResource(R.drawable.soft);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doDim=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(effButtons[6].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[6].setBackgroundResource(R.drawable.sketch);
        	}
        	else{
        		parameter[4] = 6;
        		v.setBackgroundResource(R.drawable.sketchchange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
            	effButtons[1].setBackgroundResource(R.drawable.old);
            	effButtons[2].setBackgroundResource(R.drawable.cartoon);
            	effButtons[3].setBackgroundResource(R.drawable.neno);
            	effButtons[4].setBackgroundResource(R.drawable.miro);
            	effButtons[5].setBackgroundResource(R.drawable.anti_color);
            	effButtons[7].setBackgroundResource(R.drawable.bw);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[9].setBackgroundResource(R.drawable.soft);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doBW=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(effButtons[7].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[7].setBackgroundResource(R.drawable.bw);
        	}
        	else{
        		parameter[4] = 7;
        		v.setBackgroundResource(R.drawable.bwchange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
            	effButtons[1].setBackgroundResource(R.drawable.old);
            	effButtons[2].setBackgroundResource(R.drawable.cartoon);
            	effButtons[3].setBackgroundResource(R.drawable.neno);
            	effButtons[4].setBackgroundResource(R.drawable.miro);
            	effButtons[5].setBackgroundResource(R.drawable.anti_color);
            	effButtons[6].setBackgroundResource(R.drawable.sketch);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[9].setBackgroundResource(R.drawable.soft);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doFire=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(effButtons[8].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[8].setBackgroundResource(R.drawable.oil);
        	}
        	else{
        		parameter[4] = 8;
        		 v.setBackgroundResource(R.drawable.oilchange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
             	effButtons[1].setBackgroundResource(R.drawable.old);
             	effButtons[2].setBackgroundResource(R.drawable.cartoon);
             	effButtons[3].setBackgroundResource(R.drawable.neno);
             	effButtons[4].setBackgroundResource(R.drawable.miro);
             	effButtons[5].setBackgroundResource(R.drawable.anti_color);
             	effButtons[6].setBackgroundResource(R.drawable.sketch);
             	effButtons[7].setBackgroundResource(R.drawable.bw);
             	effButtons[9].setBackgroundResource(R.drawable.soft);
             	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	}; 
	
	private Button.OnClickListener doCold=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(effButtons[9].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[9].setBackgroundResource(R.drawable.soft);
        	}
        	else{
        		parameter[4] = 9;
        		v.setBackgroundResource(R.drawable.softchange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
            	effButtons[1].setBackgroundResource(R.drawable.old);
            	effButtons[2].setBackgroundResource(R.drawable.cartoon);
            	effButtons[3].setBackgroundResource(R.drawable.neno);
            	effButtons[4].setBackgroundResource(R.drawable.miro);
            	effButtons[5].setBackgroundResource(R.drawable.anti_color);
            	effButtons[6].setBackgroundResource(R.drawable.sketch);
            	effButtons[7].setBackgroundResource(R.drawable.bw);
            	effButtons[8].setBackgroundResource(R.drawable.oil);
            	effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
		   	setImg(parameter);
    	}
	}; 
	
	private Button.OnClickListener doSharp=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(effButtons[10].getCurrentTextColor() == Color.RED){
        		parameter[4] = 11;
        		effButtons[10].setBackgroundResource(R.drawable.sharp);
        	}
        	else{
        		parameter[4] = 10;
        		 v.setBackgroundResource(R.drawable.sharpchange);
        		effButtons[0].setBackgroundResource(R.drawable.lomo);
             	effButtons[1].setBackgroundResource(R.drawable.old);
             	effButtons[2].setBackgroundResource(R.drawable.cartoon);
             	effButtons[3].setBackgroundResource(R.drawable.neno);
             	effButtons[4].setBackgroundResource(R.drawable.miro);
             	effButtons[5].setBackgroundResource(R.drawable.anti_color);
             	effButtons[6].setBackgroundResource(R.drawable.sketch);
             	effButtons[7].setBackgroundResource(R.drawable.bw);
             	effButtons[8].setBackgroundResource(R.drawable.oil);
             	effButtons[9].setBackgroundResource(R.drawable.soft);
        	}
		   	setImg(parameter);
    	}
	}; 
	
	
    private Button.OnClickListener docrop=new Button.OnClickListener(){
       	public void onClick(View v){
        	LinearLayout harLayout = (LinearLayout) findViewById(R.id.layoutofHDR);
       		harLayout.setVisibility(View.GONE);
       		LinearLayout toneLayout = (LinearLayout) findViewById(R.id.layoutofTone);
       		toneLayout.setVisibility(View.GONE);
       		LinearLayout rotateLayout = (LinearLayout) findViewById(R.id.layoutofRotate);
       		rotateLayout.setVisibility(View.GONE);
       		LinearLayout effectLayout = (LinearLayout) findViewById(R.id.layoutofEffect);
       		effectLayout.setVisibility(View.GONE);
       		LinearLayout cropLayout = (LinearLayout) findViewById(R.id.layoutofCrop);
       		cropLayout.setVisibility(View.VISIBLE);
       		crop.setBackgroundResource(R.drawable.cropchange);
       	    hdr.setBackgroundResource(R.drawable.gamma);
     	    tone.setBackgroundResource(R.drawable.tone);
     	    rotate.setBackgroundResource(R.drawable.move);
     	    effect.setBackgroundResource(R.drawable.effect);
       		reset = 4;
       	}
   	};
   	
    public void doCrop(){
    	cropButtons[0]=(Button)findViewById(R.id.twoich);
    	cropButtons[0].setOnClickListener(doTwo);
	    
    	cropButtons[1]=(Button)findViewById(R.id.fourich);
    	cropButtons[1].setOnClickListener(doFour);
	    
    	cropButtons[2]=(Button)findViewById(R.id.thrfiv);
    	cropButtons[2].setOnClickListener(doTrefiv);
	    
    	cropButtons[3]=(Button)findViewById(R.id.fousix);
    	cropButtons[3].setOnClickListener(doFousix);
    	
    	cropButtons[4]=(Button)findViewById(R.id.one);
    	cropButtons[4].setOnClickListener(doOneone);
    	
    	cropButtons[5]=(Button)findViewById(R.id.three);
    	cropButtons[5].setOnClickListener(doThree);
    	
    	cropButtons[6]=(Button)findViewById(R.id.four);
    	cropButtons[6].setOnClickListener(doFourthree);
    	
    	cropButtons[7]=(Button)findViewById(R.id.two);
    	cropButtons[7].setOnClickListener(doTwothree);
    	
    	cropButtons[8]=(Button)findViewById(R.id.threef);
    	cropButtons[8].setOnClickListener(doThreef);
    	
    	cropButtons[9]=(Button)findViewById(R.id.fourteen);
    	cropButtons[9].setOnClickListener(doFourteen);    	
	}
    
    private Button.OnClickListener doTwo=new Button.OnClickListener(){
    	public void onClick(View v){
		   	if(cropButtons[0].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 0;
        	}        	
		   	setImg(parameter);
    	}
	};	
	
	private Button.OnClickListener doFour=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[1].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 1;
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doTrefiv=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[2].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 2;
        	}
		   	setImg(parameter);
    	}
	};
	
	private Button.OnClickListener doFousix=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[3].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 3;
        	}
		   	setImg(parameter);
    	}
	};   
	private Button.OnClickListener doOneone=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[4].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 4;
        	}
		   	setImg(parameter);
    	}
	};  
	private Button.OnClickListener doThree=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[5].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 5;
        	}
		   	setImg(parameter);
    	}
	};
	private Button.OnClickListener doFourthree=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[6].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 6;
        	}
		   	setImg(parameter);
    	}
	}; 
	private Button.OnClickListener doTwothree=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[7].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 7;
        	}
		   	setImg(parameter);
    	}
	}; 
	private Button.OnClickListener doThreef=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[8].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 8;
        	}
		   	setImg(parameter);
    	}
	}; 
	private Button.OnClickListener doFourteen=new Button.OnClickListener(){
    	public void onClick(View v){
    		if(cropButtons[9].getCurrentTextColor()==Color.RED){
		   		parameter[6] = 10;
        	}
        	else{
        		parameter[6] = 9;
        	}
		   	setImg(parameter);
    	}
	}; 
	public void setBColor(Button[] effButtons, double ButtonNum){
		for(int i=0; i<11; i++){
			if(ButtonNum == 12)
				effButtons[i].setTextColor(Color.BLACK);
			else{
				if(i != ButtonNum)
					effButtons[i].setTextColor(Color.BLACK);
				else
					effButtons[i].setTextColor(Color.RED);
			}
		}
	}
	
	public void setBColor2(Button[] hdrButtons, double ButtonNum){
		for(int i=0; i<5; i++){
			if(ButtonNum == 6)
				hdrButtons[i].setTextColor(Color.BLACK);
			else{
				if(i != ButtonNum)
					hdrButtons[i].setTextColor(Color.BLACK);
				else
					hdrButtons[i].setTextColor(Color.RED);
			}
		}
	}
	
	public void setBColor3(Button[] cropButtons, double ButtonNum){
		for(int i=0; i<10; i++){
			if(ButtonNum == 11)
				cropButtons[i].setTextColor(Color.BLACK);
			else{
				if(i != ButtonNum)
					cropButtons[i].setTextColor(Color.BLACK);
				else
					cropButtons[i].setTextColor(Color.RED);
			}
		}
	}
	
	public void onOriimg(View v){
		parameter[3] = 1.25;
		Satura.setProgress(100);
		seekBarValue1.setText("0");
		Bright.setProgress(100);
		seekBarValue2.setText("0");
		Contrast.setProgress(100);
		seekBarValue3.setText("0");
		parameter[0] = 100;
		parameter[1] = 100;
		parameter[2] = 100;
		cms.reset();
		cmb.reset();
		cmc.reset();
		cMatrix.reset();
		rotateNum = 0;
		rotateArray = new int[100];
		parameter[4] = 11;
    	setBColor(effButtons, parameter[4]);
    	parameter[5] = 5;
    	setBColor2(hdrButtons, parameter[5]);
    	parameter[6] = 10;
		setBColor3(cropButtons, parameter[6]);
		effButtons[0].setBackgroundResource(R.drawable.lomo);
		effButtons[1].setBackgroundResource(R.drawable.old);
    	effButtons[2].setBackgroundResource(R.drawable.cartoon);
    	effButtons[3].setBackgroundResource(R.drawable.neno);
    	effButtons[4].setBackgroundResource(R.drawable.miro);
    	effButtons[5].setBackgroundResource(R.drawable.anti_color);
    	effButtons[6].setBackgroundResource(R.drawable.sketch);
    	effButtons[7].setBackgroundResource(R.drawable.bw);
    	effButtons[8].setBackgroundResource(R.drawable.oil);
    	effButtons[9].setBackgroundResource(R.drawable.soft);
    	effButtons[10].setBackgroundResource(R.drawable.sharp);
		
		setImg(parameter);
	}	

	public void setImg(double[] parameter){
		setBColor3(cropButtons, parameter[6]);
		setBColor2(hdrButtons, parameter[5]);
		setBColor(effButtons, parameter[4]);
		Bitmap bmp = Bitmap.createBitmap(srcbmp.getWidth(),
				srcbmp.getHeight(), srcbmp.getConfig());
		Matrix m=new Matrix();
		int width=srcbmp.getWidth();
    	int height=srcbmp.getHeight();
    	
		cms.reset();
		cms.setSaturation((float) (parameter[0] / 100.0));

		cmb.reset();
		float bright = (float)((127*parameter[1]/100) - 127); 
        cmb.set(new float[] { 1, 0, 0, 0, bright, 0, 1, 0, 0, bright, 
           		0, 0, 1, 0, bright, 0, 0, 0, 1, 0 });

		cmc.reset();				
		float contrast = (float) (-0.5f*((float) ((parameter[2]/100)-1f ) +1f)+0.5f)*255f;
        cmc.set(new float[] {
        		(float) ((parameter[2]/100)-1f ) +1f, 0, 0, 0, contrast,
                0, (float) ((parameter[2]/100)-1f ) +1f, 0, 0, contrast,
                0, 0, (float) ((parameter[2]/100)-1f ) +1f, 0, contrast,
                0, 0, 0, 1, 0 });

        for(int i=0; i<100; i++){
        	if(rotateArray[i] == 1)
        		m.postRotate(-90);
        	else if(rotateArray[i] == 2)
        		m.postRotate(90);
        	else if(rotateArray[i] == 3)
        		m.postScale(1.0f, -1.0f);
        	else if(rotateArray[i] == 4)
        		m.postScale(-1.0f, 1.0f);
        	else
        		break;
        }

		cMatrix.reset();
    	cMatrix.postConcat(cms); 
    	cMatrix.postConcat(cmb); 
    	cMatrix.postConcat(cmc); 
    	
    	Paint paint = new Paint();
    	
    	paint.setAntiAlias(true); 
    	paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
    	
    	Canvas canvas = new Canvas(bmp);
    	
    	canvas.drawBitmap(srcbmp, 0, 0, paint);
    	
    	if(parameter[4] == 0)
			bmp = effects.Lomo(bmp);
    	else if(parameter[4] == 1)
    		bmp = effects.Old(bmp);
    	else if(parameter[4] == 2)
    		bmp = effects.Comic(bmp);
    	else if(parameter[4] == 3)
    		bmp = effects.Land(bmp);
    	else if(parameter[4] == 4)
    		bmp = effects.Whiten(bmp);
    	else if(parameter[4] == 5)
    		bmp = effects.Halo(bmp);
    	else if(parameter[4] == 6)
    		bmp = effects.Dim(bmp);
    	else if(parameter[4] == 7)
    		bmp = effects.BW(bmp);
    	else if(parameter[4] == 8)
    		bmp = effects.Fire(bmp);
    	else if(parameter[4] == 9)
    		bmp = effects.Cold(bmp);
    	else if(parameter[4] == 10)
    		bmp = effects.Sharp(bmp);
    	
    	if(parameter[6]==0){
    		int iW = 95;
	        int iH = 132;
    		bmp = Bitmap.createScaledBitmap(bmp, iW, iH, true ); 
    		bmp = Bitmap.createBitmap(bmp, 0, 0, iW, iH, m, true);
    	}
    	else if(parameter[6] == 1){
    		int iW = 132;
 	        int iH = 170;
 	        bmp = Bitmap.createScaledBitmap(bmp, iW, iH, true ); 
 	        bmp = Bitmap.createBitmap(bmp, 0, 0, iW, iH, m, true);
    	}
    	else if(parameter[6] == 2){
    		int iW = 192;
 	        int iH = 288;
 	        bmp = Bitmap.createScaledBitmap(bmp, iW, iH, true ); 
 	        bmp = Bitmap.createBitmap(bmp, 0, 0, iW, iH, m, true);
    		}
    	else if(parameter[6] == 3){
    		int iW = 384;
 	        int iH = 575;
 	        bmp = Bitmap.createScaledBitmap(bmp, iW, iH, true ); 
 	        bmp = Bitmap.createBitmap(bmp, 0, 0, iW, iH, m, true);
    	}

    	else if(parameter[6] == 4){
    		bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
    		bmp = ThumbnailUtils.extractThumbnail(bmp, 479, 479); 
    	}
    	else if(parameter[6] == 5){
    		bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
    		bmp = ThumbnailUtils.extractThumbnail(bmp, 640, 425); 
    	}
    	else if(parameter[6] == 6){
    		bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
    		bmp = ThumbnailUtils.extractThumbnail(bmp, 640, 479); 
    	}
    	else if(parameter[6] == 7){
    		bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
    		bmp = ThumbnailUtils.extractThumbnail(bmp, 320, 479); 
    	}
    	else if(parameter[6] == 8){
    		bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
    		bmp = ThumbnailUtils.extractThumbnail(bmp, 358, 479); 
    	}
    	else if(parameter[6] == 9){
    		bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
    		bmp = ThumbnailUtils.extractThumbnail(bmp, 640, 358); 
    	}
    	else
    		bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);			
    	
    	if(parameter[3] != 1.25){        
    		Bitmap newbmp = hdrgamma.doGamma(parameter[3], bmp);
    		imv.setImageBitmap(newbmp);
    	}
    	else
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
    	Toast.makeText(this,"No picture",Toast.LENGTH_LONG).show();
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
		        		"You are connected to Network!",Toast.LENGTH_LONG).show();isConnected = true;
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