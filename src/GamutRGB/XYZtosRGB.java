package GamutRGB;

public class XYZtosRGB {
	private int RGB[];
	private float rgb[];
	public XYZtosRGB(float XYZ[])
	{

		float r,g,b;	
		int q,count=XYZ.length/3;
		RGB = new int[XYZ.length];
		rgb = new float[XYZ.length];
		for(int i=0;i<count;i++)
		{
			q = i*3;
			r = XYZ[q]*3.24071f - XYZ[q+1]*1.53726f - XYZ[q+2]*0.498571f;
			g = -XYZ[q]*0.969258f + XYZ[q+1]*1.87599f + XYZ[q+2]*0.0415557f;
			b = XYZ[q]*0.0556352f - XYZ[q+1]*0.203996f + XYZ[q+2]*1.05707f;
			
			if(r<=0.0031308f)
			{
				r = (12.92f*r);
			}
			else
			{			
				r = (float) Math.pow(r,1/2.4f);
				r = 1.055f*r - 0.055f;
			}

			if(g<=0.0031308f)
			{
				g = (12.92f*g);
			}
			else
			{
				g = (float) Math.pow(g,1/2.4f);
				g = 1.055f*g - 0.055f;
			}
			
			if(b<=0.0031308f)
			{
				b = (12.92f*b);
				
			}
			else
			{
				b = (float) Math.pow(b,1/2.4f);
				
				b = 1.055f*b - 0.055f;
				
			}
			RGB[q] = (int)(r*255f);
			RGB[q+1] = (int)(g*255f);
			RGB[q+2] = (int)(b*255f);
			rgb[q]=(float)(r*255f);
			rgb[q+1]=(float)(g*255f);
			rgb[q+2]=(float)(b*255f);
	/*		RGB[q] = number(RGB[q]);
			RGB[q+1] = number(RGB[q+1]);
			RGB[q+2] = number(RGB[q+2]);*/
		}
	}
	private static int number(int u) 
	{ 
		if(u<0)
			return 0;
		else if(u>255)
			return 255;
		else
			return u;		
	}
	public int[] RGB()
	{
		return RGB;
	}
	public float[] rgb()
	{
		return rgb;
	}
}
