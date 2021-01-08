import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
class Client3
{


public static void main(String data[])
{
try
{
	String applicationName=data[0];
	String deviceID=data[1];
	String payload=data[2];	
	OutputStream outputStream;
	OutputStreamWriter outputStreamWriter;
	InputStream inputStream;
	InputStreamReader inputStreamReader;
	int portNumber=2000;
	String serverName="localhost";

	// String deviceID;
	Timestamp currentTime;
	// if(i%100==0)j++;
//	Random random=new Random();
	// String applicationName="Application"+applicationName;
	// deviceID=UUID.randomUUID().toString();
	// deviceID="Device "+deviceID;
	currentTime=new Timestamp(new java.util.Date().getTime());
	String request=applicationName+","+deviceID+","+currentTime.toString()+","+"Request1"+","+payload+"#";
	//String request=String.valueOf(i);
	System.out.println(request);
	Socket socket=new Socket(serverName,portNumber);
	outputStream=socket.getOutputStream();
	outputStreamWriter=new OutputStreamWriter(outputStream);
	outputStreamWriter.write(request);
	outputStreamWriter.flush();


}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}