package com.modifoto;

import java.util.Arrays;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class Hist_enhance {
	public Bitmap hdr(Bitmap bitmap) {

	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();

	    Bitmap processedImage = Bitmap.createBitmap(width, height,Config.ARGB_8888);
	    
	    int A = 0,R,G,B;
	    int pixel;
	    float[][] Y = new float[width][height];
	    float[][] U = new float[width][height];
	    float[][] V = new float [width][height];
	    int [] histogram = new int[256];
	    Arrays.fill(histogram, 0);

	    int [] cdf = new int[256];
	    Arrays.fill(cdf, 0);
	    float min = 257;
	    float max = 0;				

	    for(int x = 0; x < width; ++x) {
	        for(int y = 0; y < height; ++y) {
	            pixel = bitmap.getPixel(x, y);
	            //Log.i("TEST","("+x+","+y+")");
	            A = Color.alpha(pixel);
	            R = Color.red(pixel);
	            G = Color.green(pixel);
	            B = Color.blue(pixel);

	            /*Log.i("TESTEST","R: "+R);
	            Log.i("TESTEST","G: "+G);
	            Log.i("TESTEST","B: "+B);*/

	            // convert to YUV
	            /*Y[x][y] = 0.299f * R + 0.587f * G + 0.114f * B;
	            U[x][y] = 0.492f * (B-Y[x][y]);
	            V[x][y] = 0.877f * (R-Y[x][y]);*/

	            Y[x][y] = 0.299f * R + 0.587f * G + 0.114f * B + 0 ;
	            U[x][y] = -0.169f * R - 0.331f * G+ 0.499f * B + 128;
	            V[x][y] = 0.499f * R -0.418f * G -0.0813f* B + 128;
	            // create a histogram
	            histogram[(int) Y[x][y]]+=1;
	            // get min and max values
	            if (Y[x][y] < min){
	                min = Y[x][y];
	            }
	            if (Y[x][y] > max){
	                max = Y[x][y];
	            }
	        }
	    }

	    cdf[0] = histogram[0];
	    for (int i=1;i<=255;i++){
	        cdf[i] = cdf[i-1] + histogram[i];
	        //Log.i("TESTEST","cdf of: "+i+" = "+cdf[i]);
	    }

	    float minCDF = cdf[(int)min];
	    float denominator = width*height - minCDF;
	    //Log.i("TEST","Histeq Histeq Histeq Histeq Histeq Histeq");
	    for(int x = 0; x < width; ++x) {
	        for(int y = 0; y < height; ++y) {
	            //Log.i("TEST","("+x+","+y+")");
	            pixel = bitmap.getPixel(x, y);
	            A = Color.alpha(pixel);
	            Y[x][y] = ((cdf[ (int) Y[x][y]] - minCDF)/(denominator)) * 255;
	            
	            R = Math.round(Y[x][y] + 1.402f * (V[x][y]-128));
	            G = Math.round(Y[x][y] - 0.344f * (U[x][y]-128) - 0.714f * (V[x][y]-128));
	            B = Math.round(Y[x][y] + 1.772f * (U[x][y]-128));
	            R = R>255? 255 : R<0 ? 0 : R;
	            G = G>255? 255 : G<0 ? 0 : G;
	            B = B>255? 255 : B<0 ? 0 : B;
	            //Log.i("TESTEST","A: "+A);
	            /*Log.i("TESTEST","R: "+R);
	            Log.i("TESTEST","G: "+G);
	            Log.i("TESTEST","B: "+B);*/
	            processedImage.setPixel(x, y, Color.argb(A, R, G, B));
	        }
	    }
		return processedImage;
	}
}
