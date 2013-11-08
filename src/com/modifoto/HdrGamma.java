package com.modifoto;

import android.graphics.Bitmap;
import android.graphics.Color;

public class HdrGamma {
	public Bitmap doGamma(double level, Bitmap srcBitmap) {
		// create output image
		Bitmap bmp = Bitmap.createBitmap(srcBitmap.getWidth(),
				srcBitmap.getHeight(), srcBitmap.getConfig());
		// get image size
		int width = srcBitmap.getWidth();
		int height = srcBitmap.getHeight();
		// color information
		int A, R, G, B;
		int pixel;
		// constant value curve
		int MAX_SIZE = 256;
		float MAX_VALUE_DBL = (float) 255.0;
		int MAX_VALUE_INT = 255;
		float REVERSE = (float) 1.0;

		// gamma arrays
		int[] gammaR = new int[MAX_SIZE];
		int[] gammaG = new int[MAX_SIZE];
		int[] gammaB = new int[MAX_SIZE];

		// setting values for every gamma channels
		for (int i = 0; i < MAX_SIZE; ++i) {
			gammaR[i] = (int) Math.min(
					MAX_VALUE_INT,
					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
							* level)) + 0.5));
			gammaG[i] = (int) Math.min(
					MAX_VALUE_INT,
					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
							* level)) + 0.5));
			gammaB[i] = (int) Math.min(
					MAX_VALUE_INT,
					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
							* level)) + 0.5));
		}

		// apply gamma table
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = srcBitmap.getPixel(x, y);
				A = Color.alpha(pixel);
				// look up gamma
				R = gammaR[Color.red(pixel)];
				G = gammaG[Color.green(pixel)];
				B = gammaB[Color.blue(pixel)];
				// set new color to output bitmap
				bmp.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		return bmp;
	}
}
