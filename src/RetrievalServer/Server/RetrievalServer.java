import java.util.regex.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import org.json.simple.*;
import com.google.gson.*;
class LogRecords
{
	public String key;
	public List<String> value;
}

public class RetrievalServer
{
	public static void main(String gg[])
	{
		Resource r=new Resource();
		MainServer ms=new MainServer(r,Integer.parseInt(gg[0]));
		ms.start();
		FileServer fw=new FileServer(r);
		fw.start();
	}
}
class Resource
{
	private List<String> requests;
	private List<Socket> sockets;
	private boolean b=false;
	public Resource()
	{
		this.requests= Collections.synchronizedList(new ArrayList<>()); 
		this.sockets= Collections.synchronizedList(new ArrayList<>()); 
		// this.requests=new ArrayList<Socket>(); 
	}
	public void setSocket(Socket socket)
	{
		this.sockets.add(socket);
	}
	public void setRequests(String request)
	{
		this.requests.add(request);
	}
	public Socket getSocket()
	{
		return this.sockets.remove(0);
	}
	public String getRequest()
	{
		String s=this.requests.remove(0);
		return s;
	}
	public int getSizeOfSocket()
	{
		return this.sockets.size();
	}
	public int getSizeOfRequest()
	{
		return this.requests.size();
	}
}
class MainServer extends Thread
{
	private Resource resource;
	private int portNumber;
	public MainServer(Resource resource,int portNumber)
	{
		this.resource=resource;
		this.portNumber=portNumber;
	}
	public void run()
	{
		startListening();
	}
	public void startListening()
	{
		try
		{
			ServerSocket serverSocket=new ServerSocket(this.portNumber);
			while(true)
			{
				System.out.println("Retrival Server is start listening at port "+this.portNumber);
				Socket socket=serverSocket.accept();
				InputStream is=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is);
				StringBuffer sb=new StringBuffer();
				int x;
				while(true)
				{
					x=isr.read();
					if(x==-1 || x=='#') break;
					sb.append((char)x);
				}
				String request=sb.toString();
				this.resource.setRequests(request);
				this.resource.setSocket(socket);
				System.out.println(socket);
			}
		}catch(Exception exception)
		{
			System.out.println("Main Server exeption "+exception);
		}
	}
}
class FileServer extends Thread
{
	private Resource resource;
	private String filePath="..\\..\\..\\logFiles";
	public FileServer(Resource resource)
	{
		this.resource=resource;
	}
	public void run()
	{
		try
		{
			while(true)
			{
				if(this.resource.getSizeOfRequest()<=0) continue;
				String request=this.resource.getRequest();
				System.out.println(request);
				String [] arguments=request.split(",",5);
				String applicationName=arguments[0];
				String deviceName=arguments[1];
				String[] date=arguments[2].split("-",4);
				String year=date[0];
				String month=date[1];
				String day=date[2];
				String path=new File("..").getCanonicalPath();//for going out of del folder 
				// System.out.println("1 "+path);
				File file=new File(filePath+"\\");
				// System.out.println("2 "+file.getName());
				File[] applicationFolders=file.listFiles();
				boolean applicationExists=false;
				List<String> logRecords=new LinkedList<>();
				LogRecords lr=new LogRecords();
				for(File f:applicationFolders)
				{
					if(applicationName.equalsIgnoreCase(f.getName()))
					{
						applicationExists=true;
						// System.out.println("3");
						File logFilesName=new File(filePath+"\\"+applicationName+"\\");
						File[] logFiles=logFilesName.listFiles();
						for(File lf:logFiles)
						{
							Pattern pattern=Pattern.compile("^("+year+"."+month+"."+day+".*)$");
							Matcher m=pattern.matcher(lf.getName());
							// System.out.println(lf.getName());
							if(m.find())
							{
								// System.out.println("4");
								//Enter into file
								FileInputStream fis = new FileInputStream(lf);
								byte[] data = new byte[(int) lf.length()];
								fis.read(data);
								fis.close();
								String str = new String(data, "UTF-8");
								Pattern pattern2 = Pattern.compile("(("+deviceName+").*)");
							    Matcher matcher = pattern2.matcher(str);
							    while(matcher.find())
							    {
									// System.out.println("5");
							      String theGroup = matcher.group(1);
							      // System.out.println(theGroup);
							      logRecords.add(theGroup);
							    }//while ends
							}//if ends
						}//for ends
						lr.key="Log :";
						lr.value=logRecords;
						break;
					}
				}
					if(!applicationExists)
					{
						// obj.put("log Records","No such application name found")
						System.out.println("No such application name found");
					}
					Socket socket=this.resource.getSocket();
					System.out.println(socket);
					OutputStream os=socket.getOutputStream();
					OutputStreamWriter osw=new OutputStreamWriter(os);
					Gson gson=new GsonBuilder().create();	//done
					osw.write(gson.toJson(lr));
					osw.flush();
					socket.close();
				}
			
		}catch(Exception exception)
		{
			System.out.println("File walker exception : "+exception);
		}
	}

}