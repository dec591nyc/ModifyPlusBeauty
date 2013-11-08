package GamutRGB;

public class LABtoXYZ {
	
	private  static float  XYZ[];
	
	public LABtoXYZ(float whitepoint[],float LAB[])
	{
		float xr,yr,zr;
		float fy,fx,fz;
		float e,k;
		int q,count=LAB.length/3;
		XYZ = new float[LAB.length];
		e = 216f/24389f;
		k = 24389f/27f;
		for(int i=0;i<count;i++)
		{
		q = i * 3;
		if(LAB[q]>e*k)
		{
			yr = (LAB[q]+16)/116f;
			yr = (float) Math.pow(yr,3f);}
		else 
		{yr = LAB[q]/k;}
		
		if(yr > e)
		{fy = (LAB[q]+16)/116f;}
		else
		{fy = (k*yr+16)/116f;}
		
		
		fx = LAB[q+1]/500f+fy;
		fz = fy -(LAB[q+2]/200f);
		
		if(Math.pow(fx,3) > e)
		{xr = (float) Math.pow(fx,3);}		
		else 
		{xr = (116f*fx-16)/k;}
		
		if(Math.pow(fz, 3f)>e)
		{zr = (float) Math.pow(fz, 3f);}
		else
		{zr = (116f*fz-16)/k;}
		XYZ[q]= (float)(xr*whitepoint[0]);
		XYZ[q+1]= (float)(yr*whitepoint[1]);
		XYZ[q+2]= (float)(zr*whitepoint[2]);
		}	
		
	}
	public float[] XYZ()
	{
		return XYZ;
	}
}
