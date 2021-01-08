import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import org.json.simple.*;
import com.google.gson.*;

class LogRecords
{
	public String key;
	public List<String> value;
}

public class RetrievalClient
{


public static void main(String data[])
{
OutputStream outputStream;
OutputStreamWriter outputStreamWriter;
InputStream inputStream;
InputStreamReader inputStreamReader;
int portNumber=3000;
String serverName="localhost";
String applicationName=data[0];
String deviceName=data[1];
String date=data[2];
try
{
String request=applicationName+","+deviceName+","+date+"#";
System.out.println(request);
Socket socket=new Socket(serverName,portNumber);
outputStream=socket.getOutputStream();
outputStreamWriter=new OutputStreamWriter(outputStream);
outputStreamWriter.write(request);
outputStreamWriter.flush();
inputStream=socket.getInputStream();
inputStreamReader=new InputStreamReader(inputStream);
StringBuffer sb=new StringBuffer();
int x;
while(true)
{
	x=inputStreamReader.read();
	if(x==-1) break;
	sb.append((char)x);
}
String jsonString=sb.toString();
Gson gson=new Gson();
LogRecords lr=gson.fromJson(jsonString,LogRecords.class);
socket.close();
System.out.println(jsonString);
System.out.println(lr);

}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}