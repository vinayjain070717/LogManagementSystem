import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
class MyThread extends Thread
{
	private ThreadedClient tc;
	public MyThread(ThreadedClient t)
	{
		this.tc=t;
	}
	public void run()
	{
		this.tc.sendRequests();
	}
}
class ThreadedClient
{
	private String applicationName,deviceName;
	private String nature;
	public ThreadedClient(String applicationName,String deviceName)
	{
		this.applicationName=applicationName;
		this.deviceName=deviceName;
	}
	public synchronized void sendRequests()
	{
		try
		{
			for(int i=0;i<500;i++)
			{
			Thread.sleep(10);
			OutputStream outputStream;
			OutputStreamWriter outputStreamWriter;
			InputStream inputStream;
			InputStreamReader inputStreamReader;
			int portNumber=2000;
			String serverName="localhost";

			Timestamp currentTime;
			// deviceID=UUID.randomUUID().toString();
			currentTime=new Timestamp(new java.util.Date().getTime());
			String payLoad="Payload "+i;
			 String request=this.applicationName+","+this.deviceName+","+currentTime.toString()+",Request,"+payLoad+"#";
			//String request=String.valueOf(i);
			System.out.println(request);
			Socket socket=new Socket(serverName,portNumber);
			outputStream=socket.getOutputStream();
			outputStreamWriter=new OutputStreamWriter(outputStream);
			outputStreamWriter.write(request);
			outputStreamWriter.flush();
			}
		}catch(Exception exception)
		{
			System.out.println("Thread me exception : "+exception.getMessage());
		}

	}
}
class ThreadedClientMain
{


public static void main(String data[])
{
try
{
	ThreadedClient tc=new ThreadedClient(data[0],data[1]);
	MyThread t=new MyThread(tc);
	t.start();

}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}