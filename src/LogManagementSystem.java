import java.util.regex.*;
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
		// DSMaintainer ds=new DSMaintainer();
		// ds.printDataStructure();
		// ds.populateDataStructure();
		Resource r=new Resource();
		LoggingServer l=new LoggingServer(Integer.parseInt(gg[0]),r);
		l.start();
		RequestProcessor rp=new RequestProcessor(r);
		rp.start();
		// UtilityFunction u=new UtilityFunction();
		// u.convertDateAndTimeToTimeStamp("2020-01-08 13:00:18.635");
	}
}
class Resource
{
	private List<String> requests;
	private boolean b=false;
	public Resource()
	{
		this.requests= Collections.synchronizedList(new ArrayList<>()); 
		// this.requests=new ArrayList<Socket>(); 
	}
	public void setRequests(String request)
	{
		this.requests.add(request);
	}
	public String getRequest()
	{
		String s=this.requests.remove(0);
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
			socket=serverSocket.accept();
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
			socket.close();
			this.r.setRequests(request);
		}
	}catch(Exception exception)
	{
		System.out.println("start listening  exception : "+exception);
		try
		{
		socket.close();
		}catch(IOException ioe)
		{
			System.out.println("IO Exception in start Listening "+ioe);
		}
	}
}
}
class RequestProcessor extends Thread
{
	private Resource r;
	// private DSMaintainer dsMaintainer;
	public RequestProcessor(Resource r)
	{
		// this.dsMaintainer=dsMaintainer;
		this.r=r;
	}
	public void run()
	{
		startProcessing();
	}
	public synchronized void startProcessing()
	{
		try
		{
			while(true)
			{
				Thread.sleep(10);
				if(this.r.getSize()==0) continue;
				String request=this.r.getRequest();
				System.out.println("Request arrived and processed: "+request);
				RequestParser3 rp=new RequestParser3();
				String fileToBeInserted=rp.parseRequest(request);

				// dsMaintainer.updateDataStructure(fileToBeInserted,request);
				// List<String> logs=dsMaintainer.retrieveLogs("app1","device3");


			}
		}catch(Exception exception)
		{
			System.out.println("Request processer exception "+exception);
			startProcessing();
		}
	}

}

//Updations
class DSMaintainer
{
	private HashMap<String,HashMap<String,HashSet<String>>> applications;
	private HashMap<String,HashSet<String>> devices;
	private HashSet<String> deviceContainingFiles;
	private String path;
	public DSMaintainer()
	{
		path="..\\logFiles";
		applications=new HashMap<>();
		populateDataStructure();
	}
	public void printDataStructure()
	{
		applications.forEach((k,v)->{
			System.out.println("Application "+k);
			v.forEach((k1,v1)->{
				System.out.println("Device Name "+k1);
				System.out.print("File Names Containing devices : ");
				v1.forEach((v2)->{
					System.out.print(v2+" ");
				});
				System.out.println();
			});
		});
	}
	public synchronized void populateDataStructure()
	{
			try
			{
					File fileName=new File("..\\logFiles");
					File [] applicationFolders=fileName.listFiles();
					for(File folder: applicationFolders)
					{	
						File deviceFileName=new File("..\\logFiles\\"+folder.getName()+"\\");
						// File deviceFileName=new File(folder.getName()+"\\");

						devices=new HashMap<>();
						File [] deviceFiles=deviceFileName.listFiles();
						RandomAccessFile raf;
						for(File deviceFile: deviceFiles)
						{
							raf=new RandomAccessFile(deviceFile,"r");
							String line;
							while(raf.getFilePointer()<raf.length())
							{
								line=raf.readLine();
								String [] arguments=line.split(",",6);
								String deviceName=arguments[1];
								if(!devices.containsKey(deviceName))
								{
									deviceContainingFiles=new HashSet<String>();
									deviceContainingFiles.add(deviceFile.getName());
									devices.put(deviceName,deviceContainingFiles);
								}
								else
								{
									devices.get(deviceName).add(deviceFile.getName());
								}
							}//file reading loop ends
						}//device files iteration loop ends
						applications.put(folder.getName(),devices);
					}//application folders iteration loop ends
			
			}catch(Exception exception)
			{
			System.out.println("DS Exception in populate data Strucutre"+exception);
			}
		}

		public synchronized void updateDataStructure(String fileName,String request)
		{

			String [] arguments=request.split(",",6);
			String applicationName=arguments[0].toUpperCase();
			String deviceName=arguments[1];
			String path="..\\logFiles\\";
			if(this.applications.containsKey(applicationName))
			{
				path=path+applicationName+"\\";
				HashMap<String,HashSet<String>> tempDevices;
				tempDevices=this.applications.get(applicationName);
				if(tempDevices.containsKey(deviceName))
				{
					HashSet<String> tempDeviceContainingFiles;
					tempDeviceContainingFiles=tempDevices.get(deviceName);
					tempDeviceContainingFiles.add(fileName);
				}
				else
				{
					deviceContainingFiles.add(fileName);
					devices.put(deviceName,deviceContainingFiles);
				}
			}
			else
			{
				deviceContainingFiles=new HashSet<>();
				deviceContainingFiles.add(fileName);
				devices=new HashMap<>();
				devices.put(deviceName,deviceContainingFiles);
				applications.put(applicationName,devices);
			}
			// printDataStructure();
		}
		public synchronized List<String> retrieveLogs(String applicationName,String deviceId)
		{
			List<String> logRecords=new LinkedList<>();
			try
			{
				// System.out.println("Full ds"+applications);
				applicationName=applicationName.toUpperCase();
			if(!applications.containsKey(applicationName))
			{
			//application name is incorrect				
			}
			HashMap<String,HashSet<String>> tempDevices=applications.get(applicationName);
			// System.out.println("Temp devices"+tempDevices);
			if(!tempDevices.containsKey(deviceId))
			{
				//device name is incorrect
			}
			HashSet<String> tempDeviceContainingFiles=tempDevices.get(deviceId);
			// System.out.println("hashset size "+tempDeviceContainingFiles.size());
			// System.out.println("tempDeviceContainingFiles "+tempDeviceContainingFiles);
			String str="";
			Iterator it=tempDeviceContainingFiles.iterator();
			while(it.hasNext())
			{
				String fileName=String.valueOf(it.next());
				System.out.println(fileName);
				File file=new File(this.path+"\\"+applicationName+"\\"+fileName);
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				fis.close();
				str = new String(data, "UTF-8");
				Pattern p = Pattern.compile("(("+deviceId+").*)");
			    Matcher m = p.matcher(str);
			    while(m.find())
			    {
			      String theGroup = m.group(1);
			      // System.out.println(theGroup);
			      logRecords.add(theGroup);
			    }
			}
			}catch(Exception exception)
			{
				System.out.println("Retrive Logs exception "+exception);
			}
			return logRecords;

		}
		public void retrieveLogsByDeviceId(String deviceId)
		{

		}

}





class RequestParser3
{
	public String parseRequest(String request)
	{
		String fileNameToBeInserted="";
		try
		{
			String line, applicationName, requestTime;
			String [] arguments;
			File folderName;
			UtilityFunctions utilityFunction=new UtilityFunctions();
			line=request;
			arguments=line.split(",",6);
			applicationName=arguments[0].toUpperCase();
			requestTime=arguments[2];
			boolean applicationNameFound=false;
			long fileTimeMilliSeconds,requestTimeInMilliSeconds;
			File [] appList=new File("..\\logFiles").listFiles();
			for(File f:appList)
			{
				if(f.getName().equals(applicationName))
				{
					applicationNameFound=true;
					break;
				}
			}
			if(applicationNameFound==false)
			{
				folderName=new File("..\\logFiles\\"+applicationName);
				folderName.mkdir();
			}
			// else System.out.println("File found"+applicationName);
			folderName=new File("..\\logFiles\\"+applicationName+"\\");
			requestTimeInMilliSeconds=utilityFunction.convertDateAndTimeToTimeStamp(requestTime);
			File [] logFileList=folderName.listFiles();
			boolean createNewFileDecision=true;
			for(File f:logFileList)
			{
				String fileNameToCheck=f.getName();
				fileNameToCheck=fileNameToCheck.replaceAll("S"," ");

				fileNameToCheck=fileNameToCheck.replaceAll("T",":");
				fileNameToCheck=fileNameToCheck.replaceAll("H","-");
				fileNameToCheck=fileNameToCheck.substring(0,fileNameToCheck.indexOf("."));
				fileTimeMilliSeconds=utilityFunction.convertDateAndTimeToTimeStamp(fileNameToCheck);
				// System.out.println(fileTimeMilliSeconds);
				long difference=requestTimeInMilliSeconds-fileTimeMilliSeconds;
				// System.out.println(difference);
				boolean decision=utilityFunction.decisionToCreateNewFile(difference);
				if(!decision){
					createNewFileDecision=false;
					System.out.println("Updating File name "+f.getName());
					requestTime=f.getName();
					break;
				}
			}
			if(createNewFileDecision) //System.out.println("Create new File");
			{
				requestTime=requestTime.replaceAll(" ","S");
				requestTime=requestTime.replaceAll(":","T");
				requestTime=requestTime.replaceAll("-","H");
				requestTime=requestTime.substring(0,requestTime.indexOf("."));
				System.out.println("New File");
				fileNameToBeInserted=requestTime+".dat";
				File newFile=new File("..\\logFiles\\"+applicationName+"\\"+fileNameToBeInserted);
				RandomAccessFile r2=new RandomAccessFile(newFile,"rw");
				r2.writeBytes(line+"\n");
				r2.close();
			}
			else //System.out.println("Update Existing file");
			{
				fileNameToBeInserted=requestTime;
				RandomAccessFile r2=new RandomAccessFile(new File("..\\logFiles\\"+applicationName+"\\"+requestTime),"rw");
				r2.seek(r2.length());
				r2.writeBytes(line+"\n");
				r2.close();
			}

		}catch(Exception exception)
		{
			System.out.println("RequestParser3 : "+exception.getMessage());
		}
	return fileNameToBeInserted;
	}

}
class UtilityFunctions
{
	public boolean decisionToCreateNewFile(long difference)
	{
		try
		{
			File file=new File("conf.dat");
			RandomAccessFile r=new RandomAccessFile(file,"rw");
			String stringToSearch=r.readLine();
			r.close();
			Pattern pattern=Pattern.compile("\"([A-Z]+)\"");
			Matcher matcher=pattern.matcher(stringToSearch);
			String accessTime="";
			if(matcher.find())
			{
				accessTime=matcher.group(1);
			}
			// System.out.println(accessTime);
			if(accessTime.equalsIgnoreCase("hourly") && difference/(3600000)<=0) return false;
			if(accessTime.equalsIgnoreCase("daily") && difference/(3600000*24)<=0) return false;
			if(accessTime.equalsIgnoreCase("weekly") && difference/(3600000*24*7)<=0) return false;
			if(accessTime.equalsIgnoreCase("monthly") && difference/(3600000*24*28)<=0) return false;
		}catch(Exception exception)
		{
			System.out.println("decisionToCreateNewFile() method exception: "+exception.getMessage());
		}
		return true;
	}
	public long convertDateAndTimeToTimeStamp(String dateAndTime)
	{
		int indexOfDot=dateAndTime.indexOf('.');
		if(indexOfDot>0)
			dateAndTime=dateAndTime.substring(0,indexOfDot);
		SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeInMilliseconds=0;
		try
		{
			java.util.Date dateToBeWritten=sd.parse(dateAndTime);
			timeInMilliseconds=dateToBeWritten.getTime();
		}catch(Exception exception)
		{
			System.out.println("cool cool "+exception.getMessage());
		}
		return timeInMilliseconds;
	}
}