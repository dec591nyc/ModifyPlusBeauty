package GamutRGB;

public class LABtoLCH {
	private float  LCH[];

	public LABtoLCH(float[] lAB)
	{
		float A,B;
		int q,
		count=lAB.length/3;
		LCH = new float[lAB.length];
		for(int i=0;i<count;i++)
		{
			q = i*3;
			
	        LCH[q] = lAB[q];
	        A = lAB[q+1];
			B = lAB[q+2];
			
			float d=  (B / A);
	
	        Math.abs(d);
	        LCH[q+1] =  (float) Math.pow( Math.pow(A,2) + Math.pow(B,2), 1.0/2.0 );
	        float h = (float) Math.atan( d);
	        
	        if(h>0){
	        	LCH[q+2] = (float) (h * 180.0 / 3.141592653589793);
	        }
	        else{
	        	LCH[q+2] = (float) (360.0 - (Math.abs(h) * 180.0 / 3.141592653589793));
	        }        
	        if (A<0 && B >=0){
	        	LCH[q+2] = LCH[q+2]-180;
	        }
	        else if (A<0 && B<0){
	            LCH[q+2] = LCH[q+2]+180;
	        }
		}
	}
	public float[] LCH()
	{
		return LCH;
	}
}
