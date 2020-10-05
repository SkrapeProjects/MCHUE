package utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import hue.Light;

public class JsonUtils 
{
	private static char[] notAllowed = { '}', '{', '"', '\'', ':', '[', ']' };
	
	// READS LIGHT INFORMATION
	public static void readLightInfo(Light light, String json)
	{
		boolean on = Boolean.parseBoolean(getValue(json, "\"on\"", ","));
		int brightness = Integer.parseInt(getValue(json, "\"bri\"", ","));
		String[] xy = getValue(json, "\"xy\"", "]").split("\\,");
		Color color = ColorUtils.colorFromXY(new float[] { Float.parseFloat(xy[0]), Float.parseFloat(xy[1]) });
		light.setProperties(on, color, brightness);
	}
	
	private static String getValue(String json, String entry, String end)
	{
		int entryIndex = json.indexOf(entry);
		String data = json.substring(entryIndex, json.indexOf(end, entryIndex));
		String value = data.split("\\:")[1];
		for (char c : notAllowed)
			value = value.replace(String.valueOf(c), "");
		return value;
	}

	// READS EACH LIGHT ID FROM JSON OUTPUT
	public static List<Integer> readLightsIds(String output) 
	{
		int indent = 0;
		boolean readId = false;
		String id = "";
		List<Integer> ids = new ArrayList<Integer>();
		
		for (int i = 1; i < output.length() - 1; i ++)
		{
			char c = output.charAt(i);
			
			switch(c)
			{
			case '{':
				indent ++;
				break;
			case '}':
				indent --;
				break;
			case '"':
				readId = !readId;
				break;
			}
			
			if (!readId && !id.equalsIgnoreCase("")) {
				
				ids.add(Integer.valueOf(id.substring(1)));
				id = "";
				
			} else if (readId && indent == 0)
				id += c;
		}
		
		
		
		return ids;
	}

	public static String parseUsername(String output) 
	{
		return getValue(output, "\"username\"", "}");
	}
}
