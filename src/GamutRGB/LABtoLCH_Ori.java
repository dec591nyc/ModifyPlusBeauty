package GamutRGB;

public class LABtoLCH_Ori {
	private float  LCH[];

	public LABtoLCH_Ori(float[] lAB)
	{
		float angel;
		float A,B;
		int q,count=lAB.length/3;
		LCH = new float[lAB.length];
		for(int i=0;i<count;i++)
		{
		q = i*3;
		/*LCH[q] = lab[q];
		A = Math.pow(lab[q+1], 2f);
		B = Math.pow(lab[q+2], 2f);
		LCH[q+1] = (float)(A + B);
		LCH[q+1] = (float)(Math.sqrt(LCH[q+1]));

		angel= Math.atan2(lab[q+2],lab[q+1]);
		LCH[q+2] = (float)(angel /(Math.PI/180f));
*/
        LCH[q] = lAB[q];
        A = lAB[q+1];
		B = lAB[q+2];/*
		LCH[q+1] = (float)(A + B);
                float d= lab[q+2]/lab[q+1];
                Math.abs(d);

                float h = (float) Math.atan( d);
            if(h>0){
                LCH[q+2] = (float) (h * 180.0 / 3.141592653589793);
            }
            else{
                LCH[q+2] = (float) (360.0 - (Math.abs(h) * 180.0 / 3.141592653589793));
            }
            if (lab[q+1]<0 && lab[q+2] >=0){
                LCH[q+2] = LCH[q+2]-180;
            }else if (lab[q+1]<0 && lab[q+2]<0){
                LCH[q+2] = LCH[q+2]+180;
            }*/
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
            }else if (A<0 && B<0){
                LCH[q+2] = LCH[q+2]+180;
            }


                }
	}
	public float[] LCH()
	{
		return LCH;
	}
}
