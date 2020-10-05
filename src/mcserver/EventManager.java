package mcserver;

import java.awt.Color;

import hue.Light;
import hue.PhilipsHueController;
import utils.ColorUtils;
import utils.MathUtils;

public class EventManager 
{
	private static final String KEY_WORD = "[HueController]";
	
	// PARSES THE CONSOLE OUTPUT AND CHECKS FOR COMMANDS TO EXECUTE
	public static void parseOutput(String line, PhilipsHueController phc)
	{
		// Prints the console
		System.out.println(line);
		
		// This two lines are kind of redundant
		if (!line.contains(KEY_WORD) || phc == null) return;
		line = (line.substring(line.indexOf(KEY_WORD)).replace(KEY_WORD + " ", "")).toLowerCase();
		
		if (line.contains("set light >")) {
			// Syntax: set light >ID -color(r,g,b) -brightness(value) etc..
			String[] parts = line.split("\\>")[1].replace(" ", "").split("\\-");
			Light light = phc.getLightById(Integer.parseInt(parts[0]));
			
			for (int i = 1; i < parts.length; i ++)
			{
				String[] propertyParts = parts[i].replace(")", "").split("\\(");
				String propertyName = propertyParts[0];
				String[] propertyArgs = propertyParts[1].split("\\,");
				
				try {
					
					switch(propertyName.toLowerCase())
					{
					case "on":
						light.setOn(Boolean.parseBoolean(propertyArgs[0]));
						System.out.println("[MCHUE] Light " + light.getId() + " should be turning " + ((light.isOn()) ? "on" : "off"));
						break;
					case "brightness":
						light.setBrightness(Math.round(MathUtils.mapFromPercentage(MathUtils.clamp(Integer.parseInt(propertyArgs[0]), 100.0f, 0.0f), 254, 0)));
						System.out.println("[MCHUE] Light " + light.getId() + " brightness should be set to " + propertyArgs[0]);
						break;
					case "color":
						light.setColor(new Color(Integer.parseInt(propertyArgs[0]), Integer.parseInt(propertyArgs[1]), Integer.parseInt(propertyArgs[2])));
						System.out.println("[MCHUE] Light " + light.getId() + " color should be set to [ R: " + light.getColor().getRed() + " | G: " + light.getColor().getGreen() + " | B: " + light.getColor().getBlue() + " ]");
						break;
					}
				
				} catch (NumberFormatException e) {
					
					System.err.println("[MCHUE] Error: Parameter is not an integer");
					e.printStackTrace();
					
				} catch (Exception e) {
					
					System.err.println("[MCHUE] Error: Parameter is not a boolean");
					e.printStackTrace();
					
				}
			}
			
			phc.updateLight(light);
		}
	}

	// CHECK IF THERE WAS A CHANGE IN THE LIGHTS STATUS
	public static void checkForEvents(ServerProcess server, PhilipsHueController phc) 
	{
		for (Light light : phc.getLights())
		{
			if (light.hasChanged()) 
			{
				System.out.println(light.isOn() + " - " + light.getOldOnStatus());
				if (!light.isOn() && light.getOldOnStatus())
					server.sendCommand("kill @a");
				else if (!light.getColor().equals(light.getColor())) {
					
					switch (ColorUtils.getColorName(light.getColor()))
					{
					case RED:
						server.sendCommand("effect give @a minecraft:instant_damage 1");
						break;
					case ORANGE:
						server.sendCommand("execute at @a run setblock ~ ~ ~ minecraft:fire replace");
						break;
					case CYAN:
						server.sendCommand("give @a diamond");
						break;
					case BLUE:
						server.sendCommand("effect give @a minecraft:night_vision 60");
						break;
					case MAGENTA:
						server.sendCommand("spreadplayers ~ ~ 100 500 false @a");
						break;
					case PINK:
						server.sendCommand("execute at @a run summon minecraft:sheep ~ ~ ~ {CustomName:\"\\\"Pink Sheep\\\"\",Color:6,CustomNameVisible:1,Health:100,Glowing:1,NoGravity:1,PersistenceRequired:1,Attributes:[{Name:\"generic.max_health\",Base:100F}]}");
						break;
					case WHITE:
						server.sendCommand("effect give @a minecraft:wither 10");
						break;
					case GREEN:
						server.sendCommand("effect give @a minecraft:instant_health 10");
						break;
					case YELLOW:
						server.sendCommand("effect give @a minecraft:absorption 25");
						break;
					default:
						server.sendCommand("say Somebody changed the color of light no." + light.getId() + ". Who might it be? ¯\\_(ツ)_/¯");
						break;
					}
					
				}	
			}
		}
	}
}
