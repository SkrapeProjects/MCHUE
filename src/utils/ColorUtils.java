package utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ColorUtils 
{	
	public enum ColorName 
	{
		RED, GREEN, BLUE, MAGENTA, CYAN, YELLOW, BLACK, WHITE, GRAY, DARK_GRAY, LIGHT_GRAY, ORANGE, PINK
	}
	
	private static final int CPT_RED = 0,
							 CPT_GREEN = 1,
							 CPT_BLUE = 2;
	
	public static final Map<ColorName, Color> KNOW_COLORS = new HashMap<ColorName, Color>(9);
	public static final List<Point2f> COLOR_POINTS = new ArrayList<Point2f>(3);
	
	static
	{
		// ADD ALL KNOWN COLORS
		KNOW_COLORS.put(ColorName.RED, Color.red);
		KNOW_COLORS.put(ColorName.GREEN, Color.green);
		KNOW_COLORS.put(ColorName.BLUE, Color.blue);
		KNOW_COLORS.put(ColorName.MAGENTA, Color.magenta);
		KNOW_COLORS.put(ColorName.CYAN, Color.cyan);
		KNOW_COLORS.put(ColorName.YELLOW, Color.yellow);
		KNOW_COLORS.put(ColorName.WHITE, Color.white);
		KNOW_COLORS.put(ColorName.ORANGE, Color.orange);
		KNOW_COLORS.put(ColorName.PINK, Color.pink);
		
		// ADD ALL COLOR POINTS
		COLOR_POINTS.add(new Point2f(0.674F, 0.322F)); // Red
		COLOR_POINTS.add(new Point2f(0.408F, 0.517F)); // Green
		COLOR_POINTS.add(new Point2f(0.168F, 0.041F)); // Blue
	}
	

	public static ColorName getColorName(Color color)
	{
		float prevDistance = 999999999.9f;
		ColorName closest = null;
		for (ColorName colorName : KNOW_COLORS.keySet())
		{
			float rgbDistance = Math.abs(color.getRed() - KNOW_COLORS.get(colorName).getRed()) + 
						  Math.abs(color.getGreen() - KNOW_COLORS.get(colorName).getGreen()) +
						  Math.abs(color.getBlue() - KNOW_COLORS.get(colorName).getBlue());
			if (rgbDistance < prevDistance) {
				prevDistance = rgbDistance;
				closest = colorName;
			}
		}
		
		return closest;
	}

	// THANKS TO SteveyO (https://github.com/SteveyO) FOR THE COLOR CONVERSION FUNCTIONS!
	
	public static float[] calculateXYFromRGB(Color color) 
	{
		int rgb = (0xFF << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
		return calculateXY(rgb);
	}

	private static float[] calculateXY(int color) 
	{
		// Default to white
		float red = 1.0f;
		float green = 1.0f;
		float blue = 1.0f;
		
		// Get no. of components
		red = ((color >> 16) & 0xFF) / 255.0f;
		green = ((color >> 8) & 0xFF) / 255.0f;
		blue = (color & 0xFF) / 255.0f;

		// Wide gamut conversion D65
		float r = ((red > 0.04045f) ? (float) Math.pow((red + 0.055f) / (1.0f + 0.055f), 2.4f) : (red / 12.92f));
		float g = (green > 0.04045f) ? (float) Math.pow((green + 0.055f) / (1.0f + 0.055f), 2.4f) : (green / 12.92f);
		float b = (blue > 0.04045f) ? (float) Math.pow((blue + 0.055f) / (1.0f + 0.055f), 2.4f) : (blue / 12.92f);
		
		float x = r * 0.649926f + g * 0.103455f + b * 0.197109f;
		float y = r * 0.234327f + g * 0.743075f + b * 0.022598f;
		float z = r * 0.0000000f + g * 0.053077f + b * 1.035763f;

		float xy[] = new float[2];

		xy[0] = (x / (x + y + z));
		xy[1] = (y / (x + y + z));
		
		if (Float.isNaN(xy[0]))
			xy[0] = 0.0f;
		if (Float.isNaN(xy[1]))
			xy[1] = 0.0f;
		
		// Check if the given XY value is within the colour reach of our lamps.
		Point2f xyPoint = new Point2f(xy[0], xy[1]);
		xyPoint = fixIfOutOfRange(xyPoint);
		
		xy[0] = precision(4, xy[0]);
		xy[1] = precision(4, xy[1]);
		
		return xy;
	}

	public static Color colorFromXY(float[] points) 
	{
		Point2f xy = new Point2f(points[0], points[1]);
		xy = fixIfOutOfRange(xy);

		float x = xy.x;
		float y = xy.y;
		float z = 1.0f - x - y;
		float y2 = 1.0f;
		float x2 = (y2 / y) * x;
		float z2 = (y2 / y) * z;

		// sRGB D65 conversion
		float r = x2 * 3.2406f - y2 * 1.5372f - z2 * 0.4986f;
		float g = -x2 * 0.9689f + y2 * 1.8758f + z2 * 0.0415f;
		float b = x2 * 0.0557f - y2 * 0.2040f + z2 * 1.0570f;

		if (r > b && r > g && r > 1.0f) {

			// red is too big
			g = g / r;
			b = b / r;
			r = 1.0f;

		} else if (g > b && g > r && g > 1.0f) {

			// green is too big
			r = r / g;
			b = b / g;
			g = 1.0f;

		} else if (b > r && b > g && b > 1.0f) {

			// blue is too big
			r = r / b;
			g = g / b;
			b = 1.0f;

		}

		// Apply gamma correction
		r = r <= 0.0031308f ? 12.92f * r : (1.0f + 0.055f) * (float) Math.pow(r, (1.0f / 2.4f)) - 0.055f;
		g = g <= 0.0031308f ? 12.92f * g : (1.0f + 0.055f) * (float) Math.pow(g, (1.0f / 2.4f)) - 0.055f;
		b = b <= 0.0031308f ? 12.92f * b : (1.0f + 0.055f) * (float) Math.pow(b, (1.0f / 2.4f)) - 0.055f;

		if (r > b && r > g) {

			// red is biggest
			if (r > 1.0f) {
				g = g / r;
				b = b / r;
				r = 1.0f;
			}

		} else if (g > b && g > r) {

			// green is biggest
			if (g > 1.0f) {
				r = r / g;
				b = b / g;
				g = 1.0f;
			}

		} else if (b > r && b > g && b > 1.0f) {

			r = r / b;
			g = g / b;
			b = 1.0f;

		}

		// neglecting if the value is negative.
		if (r < 0.0f)
			r = 0.0f;
		if (g < 0.0f)
			g = 0.0f;
		if (b < 0.0f)
			b = 0.0f;

		// Converting float components to int components.
		int sR = (int) (r * 255.0f);
		int sG = (int) (g * 255.0f);
		int sB = (int) (b * 255.0f);

		return new Color(sR, sG, sB);
	}
	
	private static Point2f fixIfOutOfRange(Point2f xy) 
	{
		boolean inReachOfLamps = checkPointInLampsReach(xy, COLOR_POINTS);
		
		if (!inReachOfLamps) 
		{
			// It seems the colour is out of reach
			// let's find the closest colour we can produce with our lamp and
			// send this XY value out.
			// Find the closest point on each line in the triangle.
			Point2f pAB = getClosestPointToPoints(COLOR_POINTS.get(CPT_RED), COLOR_POINTS.get(CPT_GREEN), xy);
	        Point2f pAC = getClosestPointToPoints(COLOR_POINTS.get(CPT_BLUE), COLOR_POINTS.get(CPT_RED), xy);
	        Point2f pBC = getClosestPointToPoints(COLOR_POINTS.get(CPT_GREEN), COLOR_POINTS.get(CPT_BLUE), xy);
	        
	        // Get the distances per point and see which point is closer to our point.
	        float dAB = getDistanceBetweenTwoPoints(xy, pAB);
	        float dAC = getDistanceBetweenTwoPoints(xy, pAC);
	        float dBC = getDistanceBetweenTwoPoints(xy, pBC);
	        
	        float lowest = dAB;
	        Point2f closestPoint = pAB;
	        
	        if (dAC < lowest) 
	        {
	        	lowest = dAC;
	        	closestPoint = pAC;
	        }
	        
	        if (dBC < lowest) 
	        {
	        	lowest = dBC;
	        	closestPoint = pBC;
	        }
	        
	        // Change the xy value to a value which is within the reach of the lamp.
	        xy.x = closestPoint.x;
	        xy.y = closestPoint.y;
		}
		
		return new Point2f(xy.x, xy.y);
	}
	
	private static boolean checkPointInLampsReach(Point2f point, List<Point2f> COLOR_POINTS) 
	{
		if (point == null || COLOR_POINTS == null)
			return false;
		
		Point2f red = COLOR_POINTS.get(CPT_RED);
		Point2f green = COLOR_POINTS.get(CPT_GREEN);
		Point2f blue = COLOR_POINTS.get(CPT_BLUE);
		Point2f v1 = new Point2f(green.x - red.x, green.y - red.y);
		Point2f v2 = new Point2f(blue.x - red.x, blue.y - red.y);
		Point2f q = new Point2f(point.x - red.x, point.y - red.y);
		
		float s = crossProduct(q, v2) / crossProduct(v1, v2);
		float t = crossProduct(v1, q) / crossProduct(v1, v2);
		
		if ((s >= 0.0f) && (t >= 0.0f) && (s + t <= 1.0f))
			return true;
		
		return false;
    }
	
	private static float getDistanceBetweenTwoPoints(Point2f one, Point2f two) 
	{
		float dx = one.x - two.x; // horizontal difference
		float dy = one.y - two.y; // vertical difference
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		return dist;
    }
	
	private static float crossProduct(Point2f point1, Point2f point2) 
	{
		return (point1.x * point2.y - point1.y * point2.x);
    }
	
	private static Point2f getClosestPointToPoints(Point2f pointA, Point2f pointB, Point2f pointP) 
	{
		if (pointA == null || pointB == null || pointP == null)
			return null;
		
		Point2f pointAP = new Point2f(pointP.x - pointA.x, pointP.y - pointA.y);
		Point2f pointAB = new Point2f(pointB.x - pointA.x, pointB.y - pointA.y);
		
		float ab2 = pointAB.x * pointAB.x + pointAB.y * pointAB.y;
		float apAb = pointAP.x * pointAB.x + pointAP.y * pointAB.y;
		float t = apAb / ab2;
		
		if (t < 0.0f)
			t = 0.0f;
		else if (t > 1.0f) 
			t = 1.0f;
		
		Point2f newPoint = new Point2f(pointA.x + pointAB.x * t, pointA.y + pointAB.y * t);
		
		return newPoint;
    }
	
	private static float precision(int decimalPlace, float val) 
	{
		if (Float.isNaN(val))
			return 0.0f;

		String str = String.format(Locale.ENGLISH, "%." + decimalPlace + 'f', val);
		return Float.valueOf(str);
	}
}

/* package */ class Point2f
{
	public float x;
    public float y;
    
    public Point2f() {}

    public Point2f(float x, float y) 
    {
        this.x = x;
        this.y = y; 
    }
    
    
    /**
     * Set the point's x and y coordinates
     */
    public final void set(float x, float y) 
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Set the point's x and y coordinates to the coordinates of p
     */
    public final void set(Point2f p) 
    { 
        this.x = p.x;
        this.y = p.y;
    }
    
    public final void negate() 
    { 
        x = -x;
        y = -y; 
    }
    
    public final void offset(float dx, float dy) 
    {
        x += dx;
        y += dy;
    }
    
    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(float x, float y) 
    { 
        return this.x == x && this.y == y; 
    }
}
