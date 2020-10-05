package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SetupUtils 
{
	private static final String CONFIG_FILE_PATH = "config",
								USERNAME_STRING = "mchue#server";
	
	private static String USERNAME, BRIDGE_IP_ADDRESS;
	
	public static void startup()
	{
		try {
		
			File file = new File(CONFIG_FILE_PATH);
		
			if (file.isFile() && file.exists())
				loadConfigFile(file);
			else 
				setup(file);
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
	}
	
	public static void exit()
	{
		try {
			
			saveConfigFile(new File(CONFIG_FILE_PATH));
		
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
	}

	private static void setup(File file) 
	{
		System.out.println
		("\n\n -------------------------------------------- MCHUE SETUP --------------------------------------------");
		
		Scanner sc = new Scanner(System.in);
		boolean success = false;
		
		do {
			
			boolean correct = false;
			String ip = "localhost:80";
			
			do {
				
				System.out.println(" |                                                                                                   |\n" + 
								   " | Please enter your Philiphs Hue Bridge IP address (separate port number with ':' if necessary):    |");
				System.out.print("   -> ");
				ip = sc.nextLine();
				System.out.println("\n |---------------------------------------------------------------------------------------------------|\n" +
								     " | Are you sure this is the correct ip? [Y/n]                                                        |");
				System.out.print("   -> ");
				correct = (sc.nextLine().toLowerCase().equals("y")) ? true : false;
				
			} while (!correct);
			
			System.out.println("\n |---------------------------------------------------------------------------------------------------|\n"
							   + " | Press the link button on your Philiphs Hue Bridge then press enter                                |");
			
			sc.nextLine();
			
			success = createUser(ip);
		
		} while (!success);
		
		sc.close();
		
		try {
			
			saveConfigFile(file);
		
		} catch (IOException e) {
		
			e.printStackTrace();
		
		}
	}

	private static void loadConfigFile(File file) throws IOException 
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		
		while((line = reader.readLine()) != null)
		{
			String name = getSection(line);
			
			switch(name)
			{
			case "USERNAME":
				USERNAME = getValue(line);
				break;
			case "IP":
				BRIDGE_IP_ADDRESS = getValue(line);
				break;
			}
		}
		
		reader.close();
	}

	// YES, I'VE MADE A STUPID CUSTOM FILE FORMAT JUST FOR DIS, SUCK IT
	
	private static String getValue(String line) 
	{
		return line.split("\\:")[1].substring(line.indexOf("<-|") + 3, line.indexOf("|->"));
	}

	private static String getSection(String line) 
	{
		return line.split("\\:")[0].substring(line.indexOf("[") + 1, line.indexOf("]"));
	}

	private static void saveConfigFile(File file) throws IOException 
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write("[USERNAME]:<-|" + USERNAME + "|->\n");
		writer.write("[IP]:<-|" + BRIDGE_IP_ADDRESS + "|->");
		writer.close();
	}
	
	private static boolean createUser(String ip)
	{
		String output = NetworkUtils.request(ip + "/api", "POST", "{\"devicetype\":\"" + USERNAME_STRING + "\"}");
		
		if (output.contains("success"))
		{
			USERNAME = JsonUtils.parseUsername(output);
			return true;
		}
		
		return false;
	}

	public static String getUsername() 
	{
		return USERNAME;
	}

	public static String getBridgeIp() 
	{
		return BRIDGE_IP_ADDRESS;
	}
}
