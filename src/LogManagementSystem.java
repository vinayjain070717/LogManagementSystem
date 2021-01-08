import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
class LogManagementSystem
{
	public static void main(String gg[])
	{
		Resource r=new Resource();
		LoggingServer l=new LoggingServer(Integer.parseInt(gg[0]),r);
		l.start();
		RequestProcessor rp=new RequestProcessor(r);
		rp.start();
	}
}
class Resource
{
	private List<Socket> requests;
	private boolean b=false;
	public Resource()
	{
		this.requests= Collections.synchronizedList(new ArrayList<Socket>()); 
		// this.requests=new ArrayList<Socket>(); 
	}
	public void setRequests(Socket request)
	{
		this.requests.add(request);
	}
	public Socket getRequest()
	{
		Socket s=this.requests.remove(0);
		return s;
	}
	public int getSize()
	{
		return this.requests.size();
	}
}
class LoggingServer extends Thread
{
private int portNumber;
private String deviceID;
private String dateTime;
private String nature;
private String payLoad;
public  Socket socket;
private File loggingFile;
private ServerSocket serverSocket;

private Resource r;
public LoggingServer(int portNumber,Resource r)
{
	this.portNumber=portNumber;
	this.deviceID="";
	this.dateTime="";
	this.nature="";
	this.payLoad="";
	this.r=r;
}

public void run()
{
	startListening();
}
public void startListening()
{
	try
	{
		serverSocket=new ServerSocket(this.portNumber);
		while(true)
		{
			System.out.println("Server is start listening at port "+this.portNumber);
			socket=serverSocket.accept();
			this.r.setRequests(socket);
		}
	}catch(Exception exception)
	{
		try
		{
		socket.close();
		}catch(IOException ioe)
		{
			System.out.println("IO Exception in start Listening "+ioe);
		}
		System.out.println("start listening  exception : "+exception);
	}
}
}
class RequestProcessor extends Thread
{
	// public List<Socket> requests;
	private Resource r;
	public RequestProcessor(Resource r)
	{
		this.r=r;
		// this.requests=requests;
	}
	public void run()
	{
		startProcessing();
	}
	public void startProcessing()
	{
		try
		{
			while(true)
			{
				 // Thread.sleep(10);
				// System.out.println(this.r.getSize());
				if(this.r.getSize()==0) continue;
				System.out.println("Request processing");
				Socket socket=this.r.getRequest();
				InputStream inputStream=socket.getInputStream();
				InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
				StringBuffer stringBuffer=new StringBuffer();
				int x;
				while(true)
				{
					x=inputStreamReader.read();
					if(x=='#' || x==-1)break;
					stringBuffer.append((char)x);
				}
				String request=stringBuffer.toString();
				System.out.println("Request arrived and processed: "+request);
				socket.close();
				// request=requestParser(request);
				File loggingFile=new File("..\\logFiles\\Log.dat");
				RandomAccessFile randomAccessFile=new RandomAccessFile(loggingFile,"rw");//append mode ka dekhna he
				randomAccessFile.seek(randomAccessFile.length());
				randomAccessFile.writeBytes(request+"\r\n");
				randomAccessFile.close();

			}
		}catch(Exception exception)
		{
			System.out.println("Request processer exception "+exception);
		}
	}
	// private String requestParser(String request)
	// {
	// 	String [] arguments=request.split(",",5);
	// 	String time="";
	// 	time=arguments[1];
	// 	convertDateAndTimeToTimeStamp(time);
	// 	File file=new File("conf.dat");
	// 	RandomAccessFile raf=new RandomAccessFile(file,"rw");
	// 	String line=raf.readLine();
	// 	int index1,index2;
	// 	index1=line.indexOf("\"");
	// 	index2=line.indexOf("\"");

	// 	//System.out.println("converted time stamp"+convertDateAndTimeToTimeStamp(time));
	// }
	private Timestamp convertDateAndTimeToTimeStamp(String dateAndTime)
	{
	int indexOfDot=dateAndTime.indexOf('.');
	dateAndTime=dateAndTime.substring(0,indexOfDot);
	// System.out.println("Simple date and time"+dateAndTime);
	SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Timestamp timeStamp=null;
	try
	{
	java.util.Date dateToBeWritten=sd.parse(dateAndTime);
	timeStamp=new Timestamp(dateToBeWritten.getTime());
	// System.out.println("Time stamp"+timeStamp);
	}catch(Exception exception)
	{
	System.out.println("cool cool "+exception.getMessage());
	}
	return timeStamp;
	}

}
