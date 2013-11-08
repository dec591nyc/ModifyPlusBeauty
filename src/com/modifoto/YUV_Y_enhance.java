package com.modifoto;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
public class YUV_Y_enhance {

	static private int blockRadius = 63;
	static private int bins = 255;
	static private float slope = 3;
	 	
	public Bitmap hdr(Bitmap bitmap) {
		
		int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    Bitmap processedImage2 = Bitmap.createBitmap(width, height,Config.ARGB_8888);
		int A,R,G,B;
		int pixel;
		float[][] Y = new float[width][height];
	    float[][] U = new float[width][height];
	    float[][] V = new float [width][height];
		 
		for ( int y = 0; y < height; ++y )
		{
			 int yMin = Math.max( 0, y - blockRadius );
			 int yMax = Math.min( bitmap.getHeight(), y + blockRadius + 1 );
			 int h = yMax - yMin;
			
		     int xMin0 = Math.max( 0, 0 - blockRadius );
			 int xMax0 = Math.min( bitmap.getWidth() - 1, 0 + blockRadius );
			
			/* initially fill histogram */
		    int[] hist = new int[ bins + 1 ];
		    int[] clippedHist = new int[ bins + 1 ];
			for ( int yi = yMin; yi < yMax; ++yi )
				for ( int xi = xMin0; xi < xMax0; ++xi )
					++hist[ roundPositive( Y[xi][yi] / 255.0f * bins ) ];
			
			for ( int x = 0; x < width; ++x )
			{
			     	pixel = bitmap.getPixel(x, y);
		            A = Color.alpha(pixel);
		            R = Color.red(pixel);
		            G = Color.green(pixel);
		            B = Color.blue(pixel);
		            Y[x][y] = 0.299f * R + 0.587f * G + 0.114f * B + 0 ;
		            U[x][y] = -0.169f * R - 0.331f * G+ 0.499f * B + 128;
		            V[x][y] = 0.499f * R -0.418f * G -0.0813f* B + 128;
		            
				 int v = roundPositive( Y[x][y] / 255.0f * bins );
				
				 int xMin = Math.max( 0, x - blockRadius );
				 int xMax = x + blockRadius + 1;
				 int w = Math.min( bitmap.getWidth(), xMax ) - xMin;
				 int n = h * w;
				
				 int limit;
				limit = ( int )( slope * n / bins + 0.5f );
			
				/* remove left behind values from histogram */
				if ( xMin > 0 )
				{
					 int xMin1 = xMin - 1;
					for ( int yi = yMin; yi < yMax; ++yi )
					{    
						Y[xMin1][yi] = (Y[xMin1][yi] > 100 ? 100 : (Y[xMin1][yi] < 0 ? 0 :Y[xMin1][yi] ));
						--hist[ (int) (roundPositive( Y[xMin1][yi] / 255.0f * bins) ) ];		
					}
				}
					
				/* add newly included values to histogram */
				if ( xMax <= bitmap.getWidth() )
				{
					 int xMax1 = xMax - 1;
					for ( int yi = yMin; yi < yMax; ++yi )
					{
						Y[xMax1][yi] = (Y[xMax1][yi] > 100 ? 100 : (Y[xMax1][yi] < 0 ? 0 :Y[xMax1][yi] ));
						++hist[ (int) (roundPositive( Y[xMax1][yi] / 255.0f * bins) ) ];	
					}
				}
				
				/* clip histogram and redistribute clipped entries */
				System.arraycopy( hist, 0, clippedHist, 0, hist.length );
				
				int clippedEntries = 0;
					for ( int i = 0; i <= bins; ++i )
					{
						final int d = clippedHist[ i ] - limit;
						if ( d > 0 )
						{
							clippedEntries += d;
							clippedHist[ i ] = limit;
						}
					}
					
					 int d = clippedEntries / ( bins + 1 );
					 int m = clippedEntries % ( bins + 1 );
					for ( int i = 0; i <= bins; ++i)
						clippedHist[ i ] += d;
					
					if ( m != 0 )
					{
						 int s = bins / m;
						for ( int i = 0; i <= bins; i += s )
							++clippedHist[ i ];
					}
				
				
				
				/* build cdf of clipped histogram */
				int hMin = bins;
				for ( int i = 0; i < hMin; ++i )
					if ( clippedHist[ i ] != 0 ) hMin = i;
				
				int cdf = 0;
				for ( int i = hMin; i <= v; ++i )
					cdf += clippedHist[ i ];
				
				int cdfMax = cdf;
				for ( int i = v + 1; i <= bins; ++i )
					cdfMax += clippedHist[ i ];
				
				 int cdfMin = clippedHist[ hMin ];
			    float denominator = cdfMax - cdfMin;
			    
	        	Y[x][y] = (int) (( cdf - cdfMin )/(denominator)* 255.0f);
	            R = Math.round(roundPositive( Y[x][y] + 1.402f * (V[x][y]-128)));
	            G = Math.round(roundPositive( Y[x][y] - 0.344f * (U[x][y]-128) - 0.714f * (V[x][y]-128)));
	            B = Math.round(roundPositive( Y[x][y] + 1.772f * (U[x][y]-128)));
	            R = R>255? 255 : R<0 ? 0 : R;
	            G = G>255? 255 : G<0 ? 0 : G;
	            B = B>255? 255 : B<0 ? 0 : B;
			   processedImage2.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
				return processedImage2;
	}
	
	final static private int roundPositive( float a )
			{
				return ( int )( a + 0.5f );
			}
}
