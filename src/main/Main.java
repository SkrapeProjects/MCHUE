package main;

import java.util.Scanner;

import hue.PhilipsHueController;
import mcserver.EventManager;
import mcserver.ServerProcess;
import utils.SetupUtils;

public class Main 
{
	private ServerProcess server;
	private Thread serverThread, updateThread;
	private PhilipsHueController phc;
	
	private boolean printUpdate = false;
	
	public Main()
	{	
		SetupUtils.startup();
		phc = new PhilipsHueController(SetupUtils.getUsername(), SetupUtils.getBridgeIp());
		server = new ServerProcess();
		startThreads();
		getInput();
	}
	
	private void startThreads()
	{
		serverThread = new Thread
		(
			new Runnable() 
			{
				@Override
				public void run() 
				{
					String line;
					while(server.isRunning())
					{
						if ((line = server.getOutput()) != null)
							EventManager.parseOutput(line, phc);
					}
					
					server.cleanUp();
				}
			}
		);
		
		updateThread = new Thread
		(
			new Runnable() 
			{
				@Override
				public void run() 
				{
					try {
						
						while (server.isRunning())
						{
							if (printUpdate) System.out.println("[MCHUE] Updating lights info");
							phc.updateLightsInfo(printUpdate);
							updateThread.sleep(1000);
							EventManager.checkForEvents(server, phc);
						}
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
						
					}
				}
			}
		);
		
		if (System.getProperty("os.name").contains("Mac")) {
		
			serverThread.run();
			updateThread.run();
		
		} else {
		
			serverThread.start();
			updateThread.start();
		
		}
	}
	
	private void getInput()
	{
		Scanner sc = new Scanner(System.in);
		String line;
		while (server.isRunning())
		{
			line = sc.nextLine();
			if (line.equals("toggleUpdateOutput")) {
			
				printUpdate = !printUpdate;
			
			} else {
				
				server.sendCommand(line);
				if (line.equals("stop"))
					break;
			
			}
		}
		sc.close();
		exit();
	}
	
	private void exit()
	{
		try {
			
			server.waitForCompleteShutdown();
			serverThread.join();
			updateThread.join();
			SetupUtils.exit();
			System.exit(0);
		
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			System.exit(2);
		
		}
	}
	
	public static void main(String[] args) 
	{
		new Main();
	}
}
