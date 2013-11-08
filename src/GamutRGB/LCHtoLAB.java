package GamutRGB;

public class LCHtoLAB {
	private  static float  lab[];
	
	public LCHtoLAB(float LCH[])
	{
		float angel;
		int q,count=LCH.length/3;
		lab = new float[LCH.length];
		for(int i=0;i<count;i++)
		{
		q = i * 3;
		lab[q] = LCH[q];
		angel= (float) (LCH[q+2] * Math.PI/180f);
		lab[q+1] = (float)(Math.cos(angel));
		lab[q+1] = lab[q+1]*LCH[q+1];
		lab[q+2] = (float)(Math.sin(angel));
		lab[q+2] = lab[q+2]*LCH[q+1];
		}
	}
	public float[] lab()
	{
		return lab;
	}
}
