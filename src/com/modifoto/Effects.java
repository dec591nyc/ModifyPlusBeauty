package com.modifoto;

import java.util.Arrays;
import java.util.Random;

import android.graphics.Bitmap;  
import android.graphics.Canvas;
import android.graphics.Color;  
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;
  
public class Effects {
	//Lomo
    public Bitmap Lomo(Bitmap bitmap){  
    	int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        int dst[] = new int[width*height];  
        bitmap.getPixels(dst, 0, width, 0, 0, width, height); 
        int ratio = width > height ? height*32768/width : width*32768/height;  
        int cx = width >> 1;  
        int cy = height >> 1;  
        int max = cx * cx + cy * cy;  
        int min = (int) (max * (1 - 0.8f));  
        int diff = max - min;            
        int ri, gi, bi;  
        int dx, dy, distSq, v;            
        int R, G, B;            
        int value;  
        int pos, pixColor;  
        int newR, newG, newB;          
        Log.d("Lomo", "enter lomo");
        
        for(int y=0; y<height; y++){  
            for(int x=0; x<width; x++){  
                pos = y*width + x;  
                pixColor = dst[pos];  
                R = Color.red(pixColor);          
                G = Color.green(pixColor);        
                B = Color.blue(pixColor);  
                  
                value = R<128 ? R : 256-R;  
                newR = (value*value*value)/64/256;  
                newR = (R<128 ? newR : 255-newR);  
                  
                value = G<128 ? G : 256-G;  
                newG = (value*value)/128;  
                newG = (G<128 ? newG : 255-newG);  
                  
                newB = B/2 + 0x25;                      
            
                dx = cx - x;  
                dy = cy - y;  
                if (width > height)   
                    dx = (dx * ratio) >> 15;  
                else   
                    dy = (dy * ratio) >> 15;  
                  
                distSq = dx * dx + dy * dy;  
                if (distSq > min){  
                    v = ((max - distSq) << 8) / diff;  
                    v *= v;  
  
                    ri = (int)(newR * v) >> 16;  
                    gi = (int)(newG * v) >> 16;  
                    bi = (int)(newB * v) >> 16;  
  
                    newR = ri > 255 ? 255 : (ri < 0 ? 0 : ri);  
                    newG = gi > 255 ? 255 : (gi < 0 ? 0 : gi);  
                    newB = bi > 255 ? 255 : (bi < 0 ? 0 : bi);  
                }  
                               
                dst[pos] = Color.rgb(newR, newG, newB);  
            }  
        }    
        Bitmap acrossFlushBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
        acrossFlushBitmap.setPixels(dst, 0, width, 0, 0, width, height);  
        
        return acrossFlushBitmap;  
    }  
    
 
    public Bitmap Old(Bitmap bitmap){ 	    
		float[] colorArray = {(float) 0.393,(float) 0.768,(float) 0.189,0,0,   
	            (float) 0.349,(float) 0.686,(float) 0.168,0,0,   
	            (float) 0.272,(float) 0.534,(float) 0.131,0,0,   
	            0,0,0,1,0};  
		
		Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),  
				Config.ARGB_8888); 
		Canvas canvas = new Canvas(bmp);
		Paint myPaint = new Paint();     
             
  
		ColorMatrix myColorMatrix = new ColorMatrix();  
        myColorMatrix.set(colorArray);             

        myPaint.setColorFilter(new ColorMatrixColorFilter(myColorMatrix));     
   
        canvas.drawBitmap(bitmap,0,0,myPaint);
        
        return bmp;
    }
    

	public Bitmap Comic(Bitmap bitmap){ 
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int R, G, B, pixel;
		int pos, pixColor;  
		int dst[] = new int[width*height];  
        bitmap.getPixels(dst, 0, width, 0, 0, width, height);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y*width + x;  
                pixColor = dst[pos];  
                R = Color.red(pixColor);          
                G = Color.green(pixColor);        
                B = Color.blue(pixColor);  

				pixel = G - B + G + R;
				if (pixel < 0)
					pixel = -pixel;
				pixel = pixel * R / 256;
				if (pixel > 255)
					pixel = 255;
				R = pixel;


				pixel = B - G + B + R;
				if (pixel < 0)
					pixel = -pixel;
				pixel = pixel * R / 256;
				if (pixel > 255)
					pixel = 255;
				G = pixel;

				pixel = B - G + B + R;
				if (pixel < 0)
					pixel = -pixel;
				pixel = pixel * G / 256;
				if (pixel > 255)
					pixel = 255;
				B = pixel;
				dst[pos] = Color.rgb(R, G, B);  
			}
		}
		Bitmap acrossFlushBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
		acrossFlushBitmap.setPixels(dst, 0, width, 0, 0, width, height);  
		
		return acrossFlushBitmap;		
	} 
	
	public Bitmap Halo(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		Bitmap returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		int colorArray[] = new int[width * height];
		int r, g, b;
		bitmap.getPixels(colorArray, 0, width, 0, 0, width, height);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				r = 255 - Color.red(colorArray[y * width + x]);
				g = 255 - Color.green(colorArray[y * width + x]);
				b = 255 - Color.blue(colorArray[y * width + x]);
				
				colorArray[y * width + x] = Color.rgb(r, g, b);
				returnBitmap.setPixel(x, y, colorArray[y * width + x]);
			}
		}
		
		return returnBitmap;
	}	
	

	public Bitmap Dim(Bitmap bitmap ) {
		 int pos, row, col, clr;
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        int[] pixSrc = new int[width * height];
	        int[] pixNvt = new int[width * height];
	       
	        bitmap.getPixels(pixSrc, 0, width, 0, 0, width, height);

	        for (row = 0; row < height; row++) {
	            for (col = 0; col < width; col++) {
	                pos = row * width + col;
	                pixSrc[pos] = (Color.red(pixSrc[pos])
	                        + Color.green(pixSrc[pos]) + Color.blue(pixSrc[pos])) / 3;
	                pixNvt[pos] = 255 - pixSrc[pos];
	            }
	        }

	        
	        gaussGray(pixNvt, 5.0, 5.0, width, height);

	
	        for (row = 0; row < height; row++) {
	            for (col = 0; col < width; col++) {
	                pos = row * width + col;

	                clr = pixSrc[pos] << 8;
	                clr /= 256 - pixNvt[pos];
	                clr = Math.min(clr, 255);

	                pixSrc[pos] = Color.rgb(clr, clr, clr);
	            }
	        }
	        bitmap.setPixels(pixSrc, 0, width, 0, 0, width, height);

	        return bitmap;

	    }

	    private static int gaussGray(int[] psrc, double d, double e,
	            int width, int height) {
	        int[] dst, src;
	        float[] n_p, n_m, d_p, d_m, bd_p, bd_m;
	        float[] val_p, val_m;
	        int i, j, t, k, row, col, terms;
	        int[] initial_p, initial_m;
	        float std_dev;
	        int row_stride = width;
	        int max_len = Math.max(width, height);
	        int sp_p_idx, sp_m_idx, vp_idx, vm_idx;

	        val_p = new float[max_len];
	        val_m = new float[max_len];

	        n_p = new float[5];
	        n_m = new float[5];
	        d_p = new float[5];
	        d_m = new float[5];
	        bd_p = new float[5];
	        bd_m = new float[5];

	        src = new int[max_len];
	        dst = new int[max_len];

	        initial_p = new int[4];
	        initial_m = new int[4];


	        if (e > 0.0) {
	            e = Math.abs(e) + 1.0;
	            std_dev = (float) Math.sqrt(-(e * e) / (2 * Math.log(1.0 / 255.0)));

	          
	            findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);

	            for (col = 0; col < width; col++) {
	                for (k = 0; k < max_len; k++) {
	                    val_m[k] = val_p[k] = 0;
	                }

	                for (t = 0; t < height; t++) {
	                    src[t] = psrc[t * row_stride + col];
	                }

	                sp_p_idx = 0;
	                sp_m_idx = height - 1;
	                vp_idx = 0;
	                vm_idx = height - 1;

	                initial_p[0] = src[0];
	                initial_m[0] = src[height - 1];

	                for (row = 0; row < height; row++) {
	                    terms = (row < 4) ? row : 4;

	                    for (i = 0; i <= terms; i++) {
	                        val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
	                                * val_p[vp_idx - i];
	                        val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
	                                * val_m[vm_idx + i];
	                    }
	                    for (j = i; j <= 4; j++) {
	                        val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
	                        val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
	                    }

	                    sp_p_idx++;
	                    sp_m_idx--;
	                    vp_idx++;
	                    vm_idx--;
	                }

	                transferGaussPixels(val_p, val_m, dst, 1, height);

	                for (t = 0; t < height; t++) {
	                    psrc[t * row_stride + col] = dst[t];
	                }
	            }
	        }

	     
	        if (d > 0.0) {
	            d = Math.abs(d) + 1.0;

	            if (d != e) {
	                std_dev = (float) Math.sqrt(-(d * d)
	                        / (2 * Math.log(1.0 / 255.0)));

	                
	                findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);
	            }

	            for (row = 0; row < height; row++) {
	                for (k = 0; k < max_len; k++) {
	                    val_m[k] = val_p[k] = 0;
	                }

	                for (t = 0; t < width; t++) {
	                    src[t] = psrc[row * row_stride + t];
	                }

	                sp_p_idx = 0;
	                sp_m_idx = width - 1;
	                vp_idx = 0;
	                vm_idx = width - 1;

	                initial_p[0] = src[0];
	                initial_m[0] = src[width - 1];

	                for (col = 0; col < width; col++) {
	                    terms = (col < 4) ? col : 4;

	                    for (i = 0; i <= terms; i++) {
	                        val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
	                                * val_p[vp_idx - i];
	                        val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
	                                * val_m[vm_idx + i];
	                    }
	                    for (j = i; j <= 4; j++) {
	                        val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
	                        val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
	                    }

	                    sp_p_idx++;
	                    sp_m_idx--;
	                    vp_idx++;
	                    vm_idx--;
	                }

	                transferGaussPixels(val_p, val_m, dst, 1, width);

	                for (t = 0; t < width; t++) {
	                    psrc[row * row_stride + t] = dst[t];
	                }
	            }
	        }

	        return 0;
	    }

	    private static void transferGaussPixels(float[] src1, float[] src2,
	            int[] dest, int bytes, int width) {
	        int i, j, k, b;
	        int bend = bytes * width;
	        float sum;

	        i = j = k = 0;
	        for (b = 0; b < bend; b++) {
	            sum = (float) (src1[i++] + src2[j++]);

	            if (sum > 255)
	                sum = 255;
	            else if (sum < 0)
	                sum = 0;

	            dest[k++] = (int) sum;
	        }
	    }

	    private static void findConstants(float[] n_p, float[] n_m, float[] d_p,
	    		float[] d_m, float[] bd_p, float[] bd_m, float std_dev) {
	    	float div = (float) (Math.sqrt(2 * 3.141593) * std_dev);
	    	float x0 = (float) (-1.783 / std_dev);
	    	float x1 = (float) (-1.723 / std_dev);
	    	float x2 = (float) (0.6318 / std_dev);
	    	float x3 = (float) (1.997 / std_dev);
	    	float x4 = (float) (1.6803 / div);
	    	float x5 = (float) (3.735 / div);
	    	float x6 = (float) (-0.6803 / div);
	    	float x7 = (float) (-0.2598 / div);
	        int i;

	        n_p[0] = x4 + x6;
	        n_p[1] = (float) (Math.exp(x1)
	                * (x7 * Math.sin(x3) - (x6 + 2 * x4) * Math.cos(x3)) + Math
	                .exp(x0) * (x5 * Math.sin(x2) - (2 * x6 + x4) * Math.cos(x2)));
	        n_p[2] = (float) (2
	                * Math.exp(x0 + x1)
	                * ((x4 + x6) * Math.cos(x3) * Math.cos(x2) - x5 * Math.cos(x3)
	                        * Math.sin(x2) - x7 * Math.cos(x2) * Math.sin(x3)) + x6
	                * Math.exp(2 * x0) + x4 * Math.exp(2 * x1));
	        n_p[3] = (float) (Math.exp(x1 + 2 * x0)
	                * (x7 * Math.sin(x3) - x6 * Math.cos(x3)) + Math.exp(x0 + 2
	                * x1)
	                * (x5 * Math.sin(x2) - x4 * Math.cos(x2)));
	        n_p[4] = (float) 0.0;

	        d_p[0] = (float) 0.0;
	        d_p[1] = (float) (-2 * Math.exp(x1) * Math.cos(x3) - 2 * Math.exp(x0)
	                * Math.cos(x2));
	        d_p[2] = (float) (4 * Math.cos(x3) * Math.cos(x2) * Math.exp(x0 + x1)
	                + Math.exp(2 * x1) + Math.exp(2 * x0));
	        d_p[3] = (float) (-2 * Math.cos(x2) * Math.exp(x0 + 2 * x1) - 2 * Math.cos(x3)
	                * Math.exp(x1 + 2 * x0));
	        d_p[4] = (float) Math.exp(2 * x0 + 2 * x1);

	        for (i = 0; i <= 4; i++) {
	            d_m[i] = d_p[i];
	        }

	        n_m[0] = (float) 0.0;
	        for (i = 1; i <= 4; i++) {
	            n_m[i] = n_p[i] - d_p[i] * n_p[0];
	        }

	        float sum_n_p, sum_n_m, sum_d;
	        float a, b;

	        sum_n_p = (float) 0.0;
	        sum_n_m = (float) 0.0;
	        sum_d = (float) 0.0;

	        for (i = 0; i <= 4; i++) {
	            sum_n_p += n_p[i];
	            sum_n_m += n_m[i];
	            sum_d += d_p[i];
	        }

	        a = (float) (sum_n_p / (1.0 + sum_d));
	        b = (float) (sum_n_m / (1.0 + sum_d));

	        for (i = 0; i <= 4; i++) {
	            bd_p[i] = d_p[i] * a;
	            bd_m[i] = d_m[i] * b;
	            }
	        }	

	public Bitmap BW(Bitmap bitmap) {		
		int width = bitmap.getWidth(); 
		int height = bitmap.getHeight(); 
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];
				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);
				grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);
		newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
		return newBmp;
	}
	
    public Bitmap Fire(Bitmap bitmap) {
    	int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int dst[] = new int[width * height];
		bitmap.getPixels(dst, 0, width, 0, 0, width, height);

		int color = 0;
		Random random = new Random();
		int iModel = 2;
		int i = width - iModel;
		int pos = 0, iPos;

		while (i > 1) {
			int j = height - iModel;
			while (j > 1) {
				iPos = random.nextInt(10000) % iModel;
				pos = (j + iPos) * width + (i + iPos);
				color = dst[pos];
				pos = j * width + i;
				dst[pos] = color;
				j--;
			}
			i--;
		}

		Bitmap returnBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		returnBitmap.setPixels(dst, 0, width, 0, 0, width, height);
		return returnBitmap;
	}
	
    public Bitmap Cold(Bitmap bitmap){ 
    	long start = System.currentTimeMillis(); 
        // 高斯矩阵 
        int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 }; 
         
        int width = bitmap.getWidth(); 
        int height = bitmap.getHeight(); 
        Bitmap newbmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565); 
         
        int pixR = 0; 
        int pixG = 0; 
        int pixB = 0; 
         
        int pixColor = 0; 
         
        int newR = 0; 
        int newG = 0; 
        int newB = 0; 
         
        int delta = 16; // 值越小图片会越亮，越大则越暗 
         
        int idx = 0; 
        int[] pixels = new int[width * height]; 
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height); 
        for (int i = 1, length = height - 1; i < length; i++) 
        { 
            for (int k = 1, len = width - 1; k < len; k++) 
            { 
                idx = 0; 
                for (int m = -1; m <= 1; m++) 
                { 
                    for (int n = -1; n <= 1; n++) 
                    { 
                        pixColor = pixels[(i + m) * width + k + n]; 
                        pixR = Color.red(pixColor); 
                        pixG = Color.green(pixColor); 
                        pixB = Color.blue(pixColor); 
                         
                        newR = newR + (int) (pixR * gauss[idx]); 
                        newG = newG + (int) (pixG * gauss[idx]); 
                        newB = newB + (int) (pixB * gauss[idx]); 
                        idx++; 
                    } 
                } 
                 
                newR /= delta; 
                newG /= delta; 
                newB /= delta; 
                 
                newR = Math.min(255, Math.max(0, newR)); 
                newG = Math.min(255, Math.max(0, newG)); 
                newB = Math.min(255, Math.max(0, newB)); 
                 
                pixels[i * width + k] = Color.argb(255, newR, newG, newB); 
                 
                newR = 0; 
                newG = 0; 
                newB = 0; 
            } 
        } 
         
        newbmp.setPixels(pixels, 0, width, 0, 0, width, height); 
        long end = System.currentTimeMillis(); 
        Log.d("may", "used time="+(end - start)); 
        return newbmp; 
    }
   
    public Bitmap Land(Bitmap bitmap){ 

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap returnBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int colorArray[] = new int[width * height];
		int r, g, b;
		bitmap.getPixels(colorArray, 0, width, 0, 0, width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int index = y * width + x;
				r = (colorArray[index] >> 16) & 0xff;
				g = (colorArray[index] >> 8) & 0xff;
				b = colorArray[index] & 0xff;
				colorArray[index] = 0xff000000 | (r << 16) | (g << 8) | b;
			}
		}

		Paint grayMatrix[] = new Paint[256];

		// Init gray matrix
		int outlineCase = 1;
		double rand = Math.random();
		if (rand > 0.33 && rand < 0.66) {
			outlineCase = 2;
		} else if (rand > 0.66) {
			outlineCase = 3;
		}
		for (int i = 255; i >= 0; i--) {
			Paint p = new Paint();
			int red = i, green = i, blue = i;
			if (i > 127) {
				switch (outlineCase) {
				case 1:
					red = 255 - i;
					break;

				case 2:
					green = 255 - i;
					break;

				case 3:
					blue = 255 - i;
					break;
				}
			}
			p.setColor(Color.rgb(red, green, blue));
			grayMatrix[255 - i] = p;
		}

		int[][] luminance = new int[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				luminance[x][y] = (int) luminance(
						(colorArray[((y * width + x))] & 0x00FF0000) >> 16,
						(colorArray[((y * width + x))] & 0x0000FF00) >> 8,
						colorArray[((y * width + x))] & 0x000000FF);
			}
		}

		int grayX, grayY;
		int magnitude;
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				// sobel
				grayX = -luminance[x - 1][y - 1] + luminance[x - 1][y - 1 + 2]
						- 2 * luminance[x - 1 + 1][y - 1] + 2
						* luminance[x - 1 + 1][y - 1 + 2]
						- luminance[x - 1 + 2][y - 1]
						+ luminance[x - 1 + 2][y - 1 + 2];

				grayY = luminance[x - 1][y - 1] + 2
						* luminance[x - 1][y - 1 + 1]
						+ luminance[x - 1][y - 1 + 2]
						- luminance[x - 1 + 2][y - 1] - 2
						* luminance[x - 1 + 2][y - 1 + 1]
						- luminance[x - 1 + 2][y - 1 + 2];

				// Magnitudes sum
				magnitude = 255 - truncate(Math.abs(grayX) + Math.abs(grayY));
				Paint grayscaleColor = grayMatrix[magnitude];

				// Apply the color into a new image
				returnBitmap.setPixel(x, y, grayscaleColor.getColor());
			}
		}

		return returnBitmap;
	}
	private static int luminance(int r, int g, int b) {
		return (int) ((0.299 * r) + (0.58 * g) + (0.11 * b));
	}

	private static int truncate(int a) {
		if (a < 0)
			return 0;
		else if (a > 255)
			return 255;
		else
			return a;
	}

	public Bitmap Whiten(Bitmap bitmap){ 
		int centerX = bitmap.getWidth() / 2;
		int centerY = bitmap.getHeight() / 2;
		float radius = Math.min(centerX*2/3, centerY*2/3);
		float mutiple = 2.0f;
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int[] src = new int[width*height];
		int[] dst = new int[width*height];
		bitmap.getPixels(src, 0, width, 0, 0, width, height);
		
		int x, y, pos, color;
		int R, G, B;
		int distance;
		int src_x, src_y, src_color;
		int real_radius = (int)(radius / mutiple);
		
		for(y=0; y<height; y++){
			for(x=0; x<width; x++){
				pos = y*width + x;
				color = src[pos];
				
				R = Color.red(color);
				G = Color.green(color);
				B = Color.blue(color);
				
				distance = (centerX-x)*(centerX-x) + (centerY-y)*(centerY-y);
				if (distance < radius * radius){
					
					src_x = (int)((float)(x-centerX) / mutiple );
					src_y = (int)((float)(y-centerY) / mutiple );
					src_x = (int)(src_x * (Math.sqrt(distance) / real_radius));
					src_y = (int)(src_y * (Math.sqrt(distance) / real_radius));
					src_x += centerX;
					src_y += centerY;
					
					src_color = src[src_y*width+src_x];
					R = Color.red(src_color);
					G = Color.green(src_color);
					B = Color.blue(src_color);
					
					R = Math.min(255, Math.max(0, R));
					G = Math.min(255, Math.max(0, G));
					B = Math.min(255, Math.max(0, B));
					
					dst[pos] = Color.rgb(R, G, B);
				}
				else{
					dst[pos] = src[pos];
				}
				
			}
		}
		
		Bitmap returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		returnBitmap.setPixels(dst, 0, width, 0, 0, width, height);
		return returnBitmap;
	
	}
	
	public Bitmap Sharp(Bitmap bitmap) {
		int laplacian[] = {  0, -1, 0, -1, 4, -1, 0, -1, 0 };  
	    
	    int width = bitmap.getWidth();  
	    int height = bitmap.getHeight();  
	    Bitmap returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
	      
	    int pixR = 0;  
	    int pixG = 0;  
	    int pixB = 0;  
	      
	    int pixColor = 0;  
	      
	    int newR = 0;  
	    int newG = 0;  
	    int newB = 0;
	      
	    int idx = 0; 
	    int[] pixels = new int[width * height];
	    int[] tempPixels = new int[width * height];
	    Arrays.fill(tempPixels, 0);
	    
	    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);  
	    
	    for (int i = 1; i < height - 1; i++)  // y
	    {  
	        for (int k = 1; k < width - 1; k++)  // x
	        {
	            idx = 0;
	            newR = newG = newB = 0;
	            for (int m = -1; m <= 1; m++)  
	            {  
	                for (int n = -1; n <= 1; n++)  
	                {  
	                    pixColor = pixels[(i + n) * width + k + m];  
	                    pixR = Color.red(pixColor);  
	                    pixG = Color.green(pixColor);  
	                    pixB = Color.blue(pixColor);  
	                      
	                    newR += (int) (pixR * laplacian[idx]);  
	                    newG += (int) (pixG * laplacian[idx]);  
	                    newB += (int) (pixB * laplacian[idx]);  
	                    idx++;  
	                }  
	            }  
	              
	            newR = Math.min(255, Math.max(0, newR));  
	            newG = Math.min(255, Math.max(0, newG));  
	            newB = Math.min(255, Math.max(0, newB));  
	              
	            tempPixels[i * width + k] = Color.rgb(newR, newG, newB);
	            newR = newG = newB = 0;
	             
	        }  
	    }
	    
	    for (int i = 0; i < width * height; i++) {
	    	int r = Color.red(tempPixels[i]);
	    	int g = Color.green(tempPixels[i]);
	    	int b = Color.blue(tempPixels[i]);
	    	
	    	int nR = Color.red(pixels[i]);
	    	int nG = Color.green(pixels[i]);
	    	int nB = Color.blue(pixels[i]);
	    	
	    	pixels[i] = Color.rgb(Math.min(255, Math.max(0, nR + r))	, Math.min(255, Math.max(0, nG + g)), 
	    			Math.min(255, Math.max(0, nB + b)));
	    }
	    
	    returnBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	    
	    return returnBitmap;
	}
}  