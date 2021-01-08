import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

class FileAppender extends TimerTask
{
	private File loggingFile ;
	private List<String> requests;
	public FileAppender(List<String> requests)
	{
		this.requests=requests;
	}
	public void run()
	{
				try
				{


				loggingFile=new File("..\\logFiles\\Log.dat");
				RandomAccessFile randomAccessFile=new RandomAccessFile(loggingFile,"rw");//append mode ka dekhna he
				for(String request: requests)
				{
					randomAccessFile.seek(randomAccessFile.length());
					randomAccessFile.writeBytes(request+"\n");
					// System.out.println(this.requests.size());
				}
				this.requests.clear();
				randomAccessFile.close();
			
				

				}catch(Exception exception)
				{
					System.out.println("Add to file exception: "+exception);
				}	
			}
		}


class LoggingServer
{
private int portNumber;
private String deviceID;
private String dateTime;
private String nature;
private String payLoad;
private List<String> requests;
public  Socket socket;
// private OutputStream outputStream;
// private OutputStreamWriter outputStreamWriter;
// private InputStream inputStream;
// private InputStreamReader inputStreamReader;
//private StringBuffer stringBuffer=new StringBuffer();

private File loggingFile;
private ServerSocket serverSocket;
public LoggingServer(int portNumber)
{
	this.portNumber=portNumber;
	this.deviceID="";
	this.dateTime="";
	this.nature="";
	this.payLoad="";
	this.requests=null;
	this.requests=new Vector<>();
	startListening();
}

// public void addToFile()
// {
// 	try
// 	{


// 	if(this.requests.size()>=5)
// 	{
// 		loggingFile=new File("..\\logFiles\\Log.dat");
// 		RandomAccessFile randomAccessFile=new RandomAccessFile(loggingFile,"rw");//append mode ka dekhna he
// 		for(String request: requests)
// 		{
// 			randomAccessFile.writeBytes(request+"\n");
// 			// System.out.println(this.requests.size());
// 		}
// 		this.requests.clear();
// 		randomAccessFile.close();
// 	}
	

// 	}catch(Exception exception)
// 	{
// 		System.out.println("Add to file exception: "+exception);
// 	}
// }

public void setRequests(String request)
{
	this.requests.add(request);
	//addToFile();
}
public String getRequest()
{
	return this.requests.remove(requests.size()-1);
}
public void getAllRequests()		//change the return type to List<String>
{
	for(String request: requests)System.out.println(request);
}


private void startListening()
{
	try
	{
		serverSocket=new ServerSocket(this.portNumber);
		Timer timer=new Timer();
		FileAppender f=new FileAppender(this.requests);
		timer.schedule(f,0,5000);

		while(true)
		{
			System.out.println("Server is start listening at port "+this.portNumber);
			socket=serverSocket.accept();
			RequestAcceptor rs=new RequestAcceptor(socket,this);
			//System.out.println("Yaha tak chala");
		}
	}catch(Exception exception)
	{
		System.out.println("start listening  exception : "+exception.getMessage());
	}
}

public static void main(String data[])
{
	LoggingServer server=new LoggingServer(Integer.parseInt(data[0]));
}

}
class RequestAcceptor extends Thread
{
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private StringBuffer stringBuffer;
	private Socket socket;
	private LoggingServer server;
	public RequestAcceptor(Socket socket,LoggingServer server)
	{
		this.socket=socket;
		this.server=server;
		start();
	}
	public void run()
	{
		try
		{
			inputStream=socket.getInputStream();
			inputStreamReader=new InputStreamReader(inputStream);
			stringBuffer=new StringBuffer();
			int x;
			while(true)
			{
				x=inputStreamReader.read();
				if(x=='#' || x==-1)break;
				stringBuffer.append((char)x);
			}
			
			String request=stringBuffer.toString();
			// System.out.println("Request arrived and processed: "+request);
			socket.close();
			this.server.setRequests(request);
			//System.out.println("Get chali: "+this.server.getRequest());
			// this.server.getAllRequests();


		}catch(Exception exception)
		{
			System.out.println("Request exceptor exception : "+exception);
		}
	}

}