package GamutRGB;

public class XYZtoLAB {
	private float LAB[];
	public XYZtoLAB(float whitepoint[],float XYZ[])
	{

		float Xr,Yr,Zr;
		float fx,fy,fz;
		float e,k;
		int q,
		count=XYZ.length/3;
		LAB = new float[XYZ.length];
		e = 216f/24389f;
		k = 24389f/27f;
		for(int i=0;i<count;i++)
		{
		q = i * 3;
		Xr = XYZ[q]/whitepoint[0];
		Yr = XYZ[q+1]/whitepoint[1];
		Zr = XYZ[q+2]/whitepoint[2];

		if (Xr > e)
		{fx = (float) Math.cbrt(Xr);}
		else
		{fx = (k*Xr + 16)/116f;}
		if (Yr > e)
		{fy = (float) Math.cbrt(Yr);}
		else
		{fy = (k*Yr + 16)/116f;}
		if (Zr > e)
		{fz = (float) Math.cbrt(Zr);}		
		else
		{fz = (k*Zr + 16)/116f;}
		LAB[q]=(float)((116f*fy)-16);
		LAB[q+1]=(float)(500f*(fx-fy));
		LAB[q+2]=(float)(200f*(fy-fz));
		}
	}
	public float[] LAB()
	{
		return LAB;
	}
}
