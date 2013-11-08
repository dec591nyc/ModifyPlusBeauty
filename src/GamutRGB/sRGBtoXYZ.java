package GamutRGB;

public class sRGBtoXYZ 
{	
	
	private  float  XYZ[];
	private  static float whitepoint[]= new float[]{0.95047f , 1 , 1.08883f};
	public sRGBtoXYZ (float[] rGB)
	{
		float var_R;
		float var_G;
		float var_B;
		float R,G,B;
		
		XYZ = new float[rGB.length];
		int q,count =rGB.length/3;
		for(int i=0;i<count;i++)
		{
		  q = i*3;
		  R = rGB[q]/255f;
		  G = rGB[q+1]/255f;
		  B = rGB[q+2]/255f;
		  if(R > 0.04045)
		  {var_R = (float) Math.pow ((R+ 0.055f)/1.055f,2.4f);}
		  else                        
		  {var_R = R / 12.92f;}
		  if(G > 0.04045)   
		  {var_G = (float) Math.pow((G + 0.055f)/1.055f,2.4f);}
		  else         
		  {var_G = G/12.92f;}		 
		  if(B > 0.04045)   
	      {var_B = (float) Math.pow((B + 0.055f)/1.055f,2.4f);}
		  else                                   
		  {var_B = B / 12.92f;} 		    
		  //Observer.   =   2�X,   Illuminant   =   D65   
		  XYZ[q] = (float)(var_R*0.4124f+var_G*0.3576f+var_B*0.1805f); 
		  XYZ[q+1] = (float)(var_R*0.2126f+var_G*0.7152f+var_B*0.0722f);
		  XYZ[q+2] = (float)(var_R*0.0193f+var_G*0.1192f+var_B*0.9505f);		 
		  
		}
	}
	
	public float[] XYZ() {
	    return XYZ;
	  }
	public float[] whitepoint() {
	    return whitepoint;
	  }
}
