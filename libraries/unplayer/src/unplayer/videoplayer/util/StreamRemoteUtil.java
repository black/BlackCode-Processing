/**
 * unPlarer 0.8.6
 *
 * @author Alvaro Lopez, Juan Baquero
 * 
 * http://code.google.com/p/unplayer/
 **/

package unplayer.videoplayer.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import processing.core.PApplet;

public class StreamRemoteUtil extends Thread{
	
	public StreamRemoteUtil(PApplet parent)
	{
		load = false;
		this.parent = parent;
	}

	public static boolean isHttpStream(String urlVideo) {

		boolean rsp = false;
		if(urlVideo.indexOf("http://")!=-1) rsp = true;
		if(urlVideo.indexOf("https://")!=-1) rsp = true;
		if(urlVideo.indexOf("ftp://")!=-1) rsp = true;
		return rsp;
	}

	public static boolean isAbsolutePath(String urlVideo) {
		boolean rsp= false;
		
		if(urlVideo.indexOf(":")!=-1) rsp = true;
		
		if(urlVideo.charAt(0)=='/') rsp = true;
		
		return rsp;
	}

	public void run()
	{
		if(http) httpStream();
		else videoStream();
	}
	
	public void getVideoStream(String urlVideo) {
		http=false;
		url=urlVideo;
	}
	
	private void videoStream() {	
		
		String file;
		String fileStream;
		
		
		
		if(PApplet.platform == PApplet.WINDOWS)
		{
			file = System.getProperty("java.io.tmpdir");
			file= file+"unpcache.tmp";
			fileStream=file;
		}else
		{
			file="cache";
			fileStream= "data"+File.separator+file;
		}
		
		videoFile=file;
		
		try
		{
			
			out = parent.createOutput(fileStream);
			bufferedOutput = new BufferedOutputStream(out);

			
		    isReader = parent.createInputRaw(url);
		    bufferedInput = new BufferedInputStream(isReader);

		    int cont=0;//Contador de 100K;
		    
			byte [] array = new byte[30000];
			int leidos = bufferedInput.read(array);
			while (leidos > 0)
			{
				bufferedOutput.write(array,0,leidos);
				leidos=bufferedInput.read(array);
				
				if(cont==3) 
					load=true;
				else
					cont++;
			}

			// Cierre de los ficheros
			load=true;
			bufferedInput.close();
			isReader.close();
			out.close();
			bufferedOutput.close();
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}
	}

	public void getHttpStream(String urlVideo)
	{
		http=true;
		url=urlVideo;
	}
	
	private void httpStream() {
		
		
		String file;
		String fileStream;
		
		if(PApplet.platform == PApplet.WINDOWS && parent.online)
		{
			file = System.getProperty("java.io.tmpdir");
			file= file+"unpcache.tmp";
			fileStream=file;
		}else
		{
			file="cache";
			fileStream= "data"+File.separator+file;
		}
		
		videoFile=file;

		URL newUrl = null;
		
		try
		{
			out = parent.createOutput(fileStream);
			bufferedOutput = new BufferedOutputStream(out);

			
			newUrl = new URL(url);
		    isReader = newUrl.openStream();
		    bufferedInput = new BufferedInputStream(isReader);

		    int cont=0;//Contador de 100K;
		    
			byte [] array = new byte[3000];
			int leidos = bufferedInput.read(array);
			while (leidos > 0)
			{
				bufferedOutput.write(array,0,leidos);
				leidos=bufferedInput.read(array);
				
				if(cont==3) 
					load=true;
				else
					cont++;
			}

			// Cierre de los ficheros
			load=true;
			bufferedInput.close();
			isReader.close();
			out.close();
			bufferedOutput.close();
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}

	}
	
	public void close() {
		
		try {
			isReader.close();
			out.close();
			bufferedOutput.close();
			bufferedInput.close();
			
		} catch (IOException e) {
			
		}
		  
		File filep = parent.dataFile(videoFile);
		int cont =0;
		while(!filep.delete())
		{
			cont++;
			if(cont==4000)
				break;
		}
	}
	
	public String getFile()
	{
		return videoFile;
	}
	
	
	private InputStream isReader = null;
	private BufferedInputStream bufferedInput = null;
	private BufferedOutputStream bufferedOutput;
	private OutputStream out;
	
	private PApplet parent;
	private String videoFile;
	private String url;
	private boolean http;
	public boolean load;
}
