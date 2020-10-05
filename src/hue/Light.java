package hue;

import java.awt.Color;

import utils.ColorUtils;

public class Light 
{
	private final int id;
	
	// CHANGED VARIABLE (KIND OF REDUNDANT, CAN PROBABLY BE REMOVED)
	private boolean changed = false;
	
	// OLD LIGHT
	private LightState currentState, oldState;
	
	public Light(int id) 
	{
		this.id = id;
		currentState = new LightState();
		oldState = new LightState();
	}

	/* package */ String getRequestBody()
	{
		float[] xy = ColorUtils.calculateXYFromRGB(currentState.color);
		return "{\"on\": " + currentState.on + 
			   ", \"bri\": " + currentState.brightness + 
			   ", \"xy\": [" + xy[0] + ", " + xy[1] + "]}";
	}
	
	public String toString()
	{
		float[] xy = ColorUtils.calculateXYFromRGB(currentState.color);
		return "\n\n -------------- LIGHT " + id + " -------------- \n" +
			   "   ON: " + currentState.on + "\n" +
			   "   BRIGHTNESS: " + currentState.brightness + "\n" +
			   "   X: " + xy[0] + "  -  Y: " + xy[1] + "\n" +
			   " -------------- LIGHT " + id + " -------------- ";
	}

	public void setProperties(boolean on, Color color, int brightness)
	{
		changed = (currentState.on != on || !color.equals(currentState.color) || currentState.brightness != brightness);
		backup();
		setOn(on); setColor(color); setBrightness(brightness);
	}
	
	private void backup()
	{
		oldState.on = this.currentState.on; oldState.color = currentState.color; oldState.brightness = this.currentState.brightness;
	}
	
	public boolean hasChanged()
	{
		boolean tmp = changed;
		changed = false;
		return tmp;
	}
	
	public int getId() 
	{
		return id;
	}
	
	public boolean isOn() 
	{
		return currentState.on;
	}

	public void setOn(boolean on) 
	{
		currentState.on = on;
	}

	public Color getColor() 
	{
		return currentState.color;
	}

	public void setColor(Color color) 
	{
		currentState.color = color;
	}
	
	public int getBrightness()
	{
		return currentState.brightness;
	}
	
	public void setBrightness(int brightness)
	{
		currentState.brightness = brightness;
	}
	
	public boolean getOldOnStatus()
	{
		return oldState.on;
	}
	
	public Color getOldColor()
	{
		return oldState.color;
	}
	
	public int getOldBrightness()
	{
		return oldState.brightness;
	}
}

/* package */ class LightState
{
	/* package */ boolean on = false;
	/* package */ Color color = Color.WHITE;
	/* package */ int brightness = 254;
}
