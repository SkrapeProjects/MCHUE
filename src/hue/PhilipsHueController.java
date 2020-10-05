package hue;

import java.util.ArrayList;
import java.util.List;

import utils.JsonUtils;
import utils.NetworkUtils;

public class PhilipsHueController 
{
						 // BRIDGE SETTINGS
	private final String HUE_BRIDGE_IP,
						 USERNAME,
						 // MAIN URL FOR CONTROLLING LIGHTS
						 LIGHTS_CONTROL_URL;
	
	private List<Light> lights;
	
	public PhilipsHueController(String username, String ip)
	{
		USERNAME = username;
		HUE_BRIDGE_IP = ip;
		LIGHTS_CONTROL_URL = "http://" + HUE_BRIDGE_IP + "/api/" + USERNAME + "/lights";
		
		getLightsOnNetwork();
	}
	
	public Light getLightById(int id)
	{
		for (Light light : lights)
			if (light.getId() == id) return light;
		return null;
	}
	
	private void getLightsOnNetwork()
	{
		String output = NetworkUtils.request(LIGHTS_CONTROL_URL, "GET", null);
		List<Integer> ids = JsonUtils.readLightsIds(output);
		lights = new ArrayList<Light>(ids.size());
		System.out.println("[MCHUE] Found " + ids.size() + " lights");
		for (int id : ids)
			addLight(new Light(id));
	}
	
	private void addLight(Light light) 
	{
		getLightInfo(light, false);
		lights.add(light);
	}

	// ONLY THE LIGHT CLASS CAN CALL THIS METHOD
	/* package */ void getLightInfo(Light light, boolean print)
	{
		String output = NetworkUtils.request(LIGHTS_CONTROL_URL + "/" + light.getId(), "GET", null);
		if (print) System.out.println(output);
		JsonUtils.readLightInfo(light, output);
	}
	
	public void updateLight(Light light)
	{
		NetworkUtils.request(LIGHTS_CONTROL_URL + "/" + light.getId() + "/state", "PUT", light.getRequestBody());
	}

	public void updateLightsInfo(boolean print)
	{
		for (Light light : lights)
			getLightInfo(light, print);
	}
	
	public List<Light> getLights()
	{
		return lights;
	}
}
