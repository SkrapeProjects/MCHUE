package mcserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ServerProcess 
{
	private static final File EXECUTION_DIR = new File("server");
	private static final String LAUNCH_COMMAND = "java -jar -Xmx2048M -Xms512M server.jar nogui";
	
	private Process serverProc;
	private BufferedWriter serverInput;
	private BufferedReader serverOutput;
	
	public ServerProcess()
	{
		try {
		
			serverProc = Runtime.getRuntime().exec(LAUNCH_COMMAND, new String[] {}, EXECUTION_DIR); //(new ProcessBuilder(LAUNCH_COMMAND)).start();
			serverInput = new BufferedWriter(new OutputStreamWriter(serverProc.getOutputStream()));
			serverOutput = new BufferedReader(new InputStreamReader(serverProc.getInputStream()));
		
		} catch (IOException e) {
		
			e.printStackTrace();
			System.exit(-1);
			
		}
	}
	
	public boolean sendCommand(String command)
	{
		try {
			
			serverInput.write(command, 0, command.length());
			serverInput.newLine();
			serverInput.flush();
			return true;
			
		} catch (IOException e) {
		
			e.printStackTrace();
			return false;
			
		}
	}
	
	public String getOutput()
	{
		try {
			
			return serverOutput.readLine();
		
		} catch (IOException e) {
		
			e.printStackTrace();
			return null;
			
		}
	}
	
	public boolean isRunning()
	{
		return serverProc.isAlive();
	}
	
	public void cleanUp()
	{
		try {
			
			serverOutput.close();
			serverInput.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.exit(1);
			
		}
	}

	public void waitForCompleteShutdown() 
	{
		try {
			serverProc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
