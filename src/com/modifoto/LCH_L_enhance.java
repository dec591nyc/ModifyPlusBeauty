package com.modifoto;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import GamutRGB.*;

public class LCH_L_enhance {

		static private int blockRadius = 50;
		static private int bins = 255;
		sRGBtoXYZ sRGB2XYZ;
		XYZtoLAB sXYZ2LAB;
		LABtoLCH_Ori sLAB2LCH;
		LCHtoLAB LCH2sLAB;
		LABtoXYZ LAB2sXYZ;
		XYZtosRGB XYZ2sRGB;
		
		public Bitmap hdr(float slope,Bitmap bitmap) {
			
			int width = bitmap.getWidth();
		    int height = bitmap.getHeight();
		    Bitmap bmp = Bitmap.createBitmap(width, height,Config.ARGB_8888);
			int A,R,G,B;
			int pixel;
			float[][] L = new float[width][height];
		    float  RGB[]=new float[3];
		    float  XYZ[]=new float[3];
		    float  LAB[]=new float[3];
		    float  LCH[]=new float[3];
		    float[] whitePoint_sRGB={(float) 0.95047f, (float) 1, (float) 1.08883f};
		    
			 
			for ( int y = 0; y < height; ++y )
			{
				final int yMin = Math.max( 0, y - blockRadius );
				final int yMax = Math.min( bitmap.getHeight(), y + blockRadius + 1 );
				final int h = yMax - yMin;
				
				final int xMin0 = Math.max( 0, 0 - blockRadius );
				final int xMax0 = Math.min( bitmap.getWidth() - 1, 0 + blockRadius );
				
				/* initially fill histogram */
				final int[] hist = new int[ bins + 1 ];
				final int[] clippedHist = new int[ bins + 1 ];
				for ( int yi = yMin; yi < yMax; ++yi )
					for ( int xi = xMin0; xi < xMax0; ++xi )
						++hist[ roundPositive( L[xi][yi] / 100.0f * bins ) ];
				
				for ( int x = 0; x < width; ++x )
				{
				     	pixel = bitmap.getPixel(x, y);
			            A = Color.alpha(pixel);
			            R = Color.red(pixel);
			            G = Color.green(pixel);
			            B = Color.blue(pixel);
			            //System.out.println("R="+ R);
			            //System.out.println("G="+ G);
			            //System.out.println("B="+ B);
			            
			            RGB[0]=R;
			            RGB[1]=G;
			            RGB[2]=B;
			            
			           
			            //RGB 2 LCH
			            //-----------------------------------------------------
			            //1. RGB2XYZ
			            sRGBtoXYZ sRGB2XYZ=new sRGBtoXYZ(RGB);
			            XYZ=sRGB2XYZ.XYZ();
			            //-----------------------------------------------------
			            
			            //2. XYZ2LAB
			            XYZtoLAB sXYZ2LAB=new XYZtoLAB(whitePoint_sRGB, XYZ);			            
			            LAB=sXYZ2LAB.LAB();
			            		 
			            //3. LAB2LCH
			            LABtoLCH_Ori sLAB2LCH=new LABtoLCH_Ori(LAB);
			            LCH=sLAB2LCH.LCH();	

			            L[x][y] = (float) LCH[0] ;
			            
					final int v = (int)roundPositive( L[x][y] / 100.0f * bins );
					
					final int xMin = Math.max( 0, x - blockRadius );
					final int xMax = x + blockRadius + 1;
					final int w = Math.min( bitmap.getWidth(), xMax ) - xMin;
					final int n = h * w;
					
					final int limit;
					limit = ( int )( slope * n / bins + 0.5f );
				
					/* remove left behind values from histogram */
					if ( xMin > 0 )
					{
						final int xMin1 = xMin - 1;
						for ( int yi = yMin; yi < yMax; ++yi )
						{
							L[xMin1][yi] = (L[xMin1][yi] > 100 ? 100 : (L[xMin1][yi] < 0 ? 0 :L[xMin1][yi] ));
							--hist[ (int) (roundPositive( L[xMin1][yi] / 100.0f * bins) ) ];		
						}
					}
						
					/* add newly included values to histogram */
					if ( xMax <= bitmap.getWidth() )
					{
						final int xMax1 = xMax - 1;
						for ( int yi = yMin; yi < yMax; ++yi )
						{  
							//if (L[xMax1][yi]> 100) L[xMax1][yi]=100;
							L[xMax1][yi] = (L[xMax1][yi] > 100 ? 100 : (L[xMax1][yi] < 0 ? 0 :L[xMax1][yi] ));
						    //if (L[xMax1][yi]< 0) L[xMax1][yi]=0;
							++hist[ (int) (roundPositive( L[xMax1][yi] / 100.0f * bins) ) ];		
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
						
						final int d = clippedEntries / ( bins + 1 );
						final int m = clippedEntries % ( bins + 1 );
						for ( int i = 0; i <= bins; ++i)
							clippedHist[ i ] += d;
						
						if ( m != 0 )
						{
							final int s = bins / m;
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
					
					final int cdfMin = clippedHist[ hMin ];
				    float denominator = cdfMax - cdfMin;
				    
		        	L[x][y] = (int) (( cdf - cdfMin )/(denominator)* 100.0f);
		        	
		        	LCH[0]=L[x][y];
		        	
		        	//LCH2LAB
		        	LCHtoLAB LCH2sLAB=new LCHtoLAB(LCH);
		            LAB=LCH2sLAB.lab();
		        	//LAB2XYZ
		    		LABtoXYZ LAB2sXYZ=new LABtoXYZ(whitePoint_sRGB,LAB);
		    		XYZ=LAB2sXYZ.XYZ();
		        	//XYZ2RGB
		    		XYZtosRGB XYZ2sRGB=new XYZtosRGB(XYZ);
		    		RGB=XYZ2sRGB.rgb();
		    		
		    		
		            R=Math.round(roundPositive(RGB[0]));
		            G=Math.round(roundPositive(RGB[1]));
		            B=Math.round(roundPositive(RGB[2]));
		            //System.out.println("R_AFTER="+ R);
		            //System.out.println("G_AFTER="+ G);
		            //System.out.println("B_AFTER="+ B);
		            
		            R = (R>255? 255 : R<0 ? 0 : R);
		            G = (G>255? 255 : G<0 ? 0 : G);
		            B = (B>255? 255 : B<0 ? 0 : B);
		            bmp.setPixel(x, y, Color.argb(A, R, G, B));
				}
			}
					return bmp;
		}
		
		final static private int roundPositive( float a )
				{
					return ( int )( a + 0.5f );
				}
	}
