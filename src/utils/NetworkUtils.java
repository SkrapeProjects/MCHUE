package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils 
{
	public static String request(String url, String requestMethod /* I could've used an enum */, String requestBody)
	{
		try {
			
			HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
			
			connection.setRequestMethod(requestMethod);
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);
			
			if (requestBody != null)
			{
				try (OutputStream os = connection.getOutputStream()) 
				{
					byte[] bytes = requestBody.getBytes();
					os.write(bytes, 0, bytes.length);
					os.close();
				}
			}
			
			try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) 
			{
			    StringBuilder response = new StringBuilder();
			    String responseLine;
			    while ((responseLine = br.readLine()) != null)
			        response.append(responseLine.trim());
			    br.close();
			    return response.toString();
			}
		
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
}
