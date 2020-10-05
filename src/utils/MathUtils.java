package utils;

public class MathUtils 
{
	public static float clamp(float val, float max, float min)
	{
		return (val > max) ? max : ((val < min) ? min : val);
	}
	
	public static float map(float val, float maxIn, float minIn, float maxOut, float minOut)
	{
		// LOSSY CONVERSION BUT WHAT ARE YOU GONNA DO, UH?
		return mapFromPercentage(mapToPercentage(val, maxIn, minIn), maxOut, minOut);
	}
	
	public static float mapToPercentage(float val, float max, float min)
	{
		return ((val - min) / (max - min)) * 100.0f;
	}
	
	public static float mapFromPercentage(float val, float max, float min)
	{
		return ((val / 100.0f) * (max - min)) + min;
	}

//	public static float interpolate3f(float[] p, float[] v1, float[] v2, float[] v3) 
//	{
//		float a = (v3[2] * (v2[1] - v1[1]) + v1[2] * (v3[1] - v2[1]) - v2[2] * (v3[1] - v1[1])) / (v3[0] * v2[1] - v3[0] * v1[1] - v2[0] * v3[1] - v1[0] * v3[1] - v1[0] * v2[1] + v2[0] * v1[1]);
//		float b = (v2[2] - a * v2[0] - v1[2] + a * v1[0]) / (v2[1] - v1[1]);
//		float c = v1[2] - a * v1[0] - b * v1[1];
//		
//		float res = a * p[0] + b * p[1] + c;
//		
//		return res;
//	}
}
