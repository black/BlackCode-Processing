/**
 * unPlayer 0.8.2
 *
 * @author Alvaro Lopez, Juan Baquero
 * 
 * http://code.google.com/p/unplayer/
 **/

package unplayer.videoplayer;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import codeanticode.gsvideo.GSMovie;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import unplayer.videoplayer.util.StreamRemoteUtil;
import unplayer.videoplayer.util.TextUtil;
import unplayer.videoplayer.util.TimeUtil;

public class VideoPlayer implements VideoConstants{

	public VideoPlayer(PApplet pa, String url,boolean title,boolean cache) {
		parent = pa;
		parent.registerMouseEvent(this);
		parent.registerKeyEvent(this);
		urlVideo = url;
		showTitle(title);
		aspectRatio(true);
		parent.registerDispose(this);
		cacheDown=cache;
		
		if(url==null || url.equals(""))
			setupNoVideo();
		else
			setup();
	}
	
	public VideoPlayer(PApplet pa, String url,boolean title) {
		this(pa,url,title,true);
	}
	
	public VideoPlayer(PApplet pa, String url) {
		this(pa,url,true,true);
	}
	
	public VideoPlayer(PApplet pa) {
		this(pa,null,true,true);
	}
	
	public void loadVideo(String url)
	{
		urlVideo=url;
		setup();
	}
	
	public void closeVideo()
	{
		if(myMovie!=null)
		{
			myMovie.stop();
			myMovie.dispose();
		}
		
		urlVideo=null;
		myMovie=null;
		
		if(sru!=null) sru.close();
		sru=null;
		
		setupNoVideo();
	}
	
	public void closePlayer()
	{
		if(myMovie!=null)
		{
			myMovie.stop();
			myMovie.dispose();
			myMovie=null;
		}
		
		if(sru!=null) sru.close();
		sru=null;
	}
		
	private void setup()
	{
		 font = parent.createFont("Verdana", 12);
		 
		 //SI ES UNA URL DE INTERNET
	 
		 if(cacheDown && StreamRemoteUtil.isHttpStream(urlVideo))
		 {
			 if(sru!=null) sru.close();
			 sru = new StreamRemoteUtil(parent);
			 sru.getHttpStream(urlVideo);//DESCARGAR EL VIDEO
			 sru.start();
			 setupNoVideo();
			 msgVideo = "Download video cache...";
		 }
		 else if(cacheDown && parent.online && !StreamRemoteUtil.isAbsolutePath(urlVideo)) // SI ES UN APPLET Y NO ES UNA DIRECCION ABSOLUTA
		 {
			 if(sru!=null) sru.close();
			 sru = new StreamRemoteUtil(parent);
			 sru.getVideoStream(urlVideo);//DESCARGAR EL VIDEO 
			 sru.start();
			 setupNoVideo();
			 msgVideo = "Download video cache...";
		 }else
		 {
			 title = TextUtil.getFile(urlVideo);
			 sru=null;
			 initVideo();
		 }
	}
	
	private void initVideo()
	{	 
		 myMovie = new GSMovie(parent, urlVideo);
		 
		 myMovie.loop();

		 movieState=ST_PLAY; // ESTADO PLAY
		 movieLoop=true;
		 movieSound=true;
		 keyCommand=false;
		 
		 volume=80;
		 charge=false; //NO HA CARGADO AUN
		 
		 changeButtonDown = false;
		 changeButtonUp = true;
		 
		 heigthReal = 300;
		 widthReal = 400;
		 
		 widthText = TextUtil.getwidth(font, fontSize, title);
	}
	
	private void setupNoVideo()
	{
		 font = parent.createFont("Verdana", 12);

		 movieState=ST_NO_VIDEO;
		 keyCommand=false;
		 volume=80;
		 charge=false; //NO HA CARGADO AUN
		 
		 changeButtonDown = false;
		 changeButtonUp = true;
		 
		 heigthReal = 300;
		 widthReal = 400;
		 
		 widthText = TextUtil.getwidth(font, fontSize, "UNPlayer");
		 title ="UNPlayer";
		 msgVideo = "No Video";
	}
	
	public void video(int x, int y,int widthV,int heighV) {
			
		//System.out.println(myMovie.time() +" "+ myMovie.duration() +" "+myMovie.frame()+" "+myMovie.length());
		if(keyCommand) runVideo();
		
		parent.textMode(PConstants.SCREEN);
		int height =0;
		int width = 0;
		int contX = x;
		int contY;
		
		float timeNow;  //TIME
		float timeTotal;  //DURATION
		int timeSp; //ESPACIO QUE TOMA LA BARRA DE TIEMPO
		
		boolean resize = false;
		boolean available =true;
		
		//SI LA VARIABLE sru ES DIFERNTE DE NULA INDICA QUE
		//SE ESTA DESCARGANDO UN VIDEO, 
		
		if(myMovie==null && sru!=null && sru.load)
		{
			title = TextUtil.getFile(urlVideo);
			urlVideo = sru.getFile();
			initVideo();
		}

		if(myMovie!=null)
		{
			height = myMovie.height;
			width = myMovie.width;
			timeNow = (long)myMovie.time();
			timeTotal = (long)myMovie.duration();

		}else
		{

			height = 0;
			width = 0;
			timeNow = 0;
			timeTotal = 0;
		}
		
		if(!charge) // VERIFICACION DE CARGA DE VIDEO POR PRIMERA VEZ
		{	
			if(height>1) 
			{
				 heigthReal = height;
				 widthReal = width;
				 charge=true;
			}else
			{
				if(!showCharge) return;
			}
		}
		

		if(height<=1) // VERIFICACION DE CARGA DE VIDEO
		{
			if(timeNow<0) timeNow = 0;
			if(timeTotal<0) timeTotal = 0;
			
			if(movieState==ST_PLAY && charge){  //SI ESTABA ANTES EN STOP
				
				height = heigthReal;
				width = widthReal;
			}else
			{
				height = 300;
				width = 400;
			}
			available=false;
			
			if(myMovie!=null) msgVideo = "Cargando Video...";
		}
	
		if(movieState==ST_STOP)
		{
			available=false;
			msgVideo = "";
		}
		
		
		if(widthV>0)
		{
			if(showTitle)
				height = heighV-sizeButton-sizeTitle;
			else
				height = heighV-sizeButton;
			width = widthV;
			resize=true;
		}
		
		contY = y+height;
		
		buttonPressed = BP_NO_PRESS; //0 indica que no  esta presionado
		
	//TITULO NOMBRE DEL ARCHIVO
		
		if(showTitle)
		{
			parent.fill(0);
		    parent.stroke(0);
			parent.rect(x, y, width, sizeTitle);
			parent.fill(200);
		    //parent.stroke(200);
			parent.textFont(font,fontSize);
			
			if(widthText<width)
				parent.text(title,x+(width-widthText)/2,y+(sizeTitle/2)+(fontSize/2));
			else
				parent.text(title,x,y+4,width,sizeTitle);
			contY = contY+sizeTitle;
			y=y+sizeTitle;
		}
		
	// CAMBIO DE TAMANYO DE BOTONES SI ES NECESARIO
		
		if(width<300)
		{
			if(!changeButtonDown) downButtons();
		}else
		{
			if(!changeButtonUp) upButtons();
		}
		
	// -- VIDEO --
		if(myMovie!=null && available)
		{
			try
			{
				parent.fill(0);
				parent.stroke(0);
				parent.rect(x, y, width, height);
				
				if(width==myMovie.height && width==myMovie.width)
				{
					parent.image(myMovie, x,y);
				} 
				else if(resize)
				{
					if(aspectRatio)
					{

						if(myMovie.height==height && myMovie.width<width)
						{
							parent.image(myMovie, x +(width-myMovie.width)/2,y); //MANTIENE ASPECTRATIO SIN RESIZE CON DELAY EN X
						}else if(myMovie.width==width && myMovie.height<height)
						{
							parent.image(myMovie, x,y+(height-myMovie.height)/2); //MANTIENE ASPECTRATIO SIN RESIZE CON DELAY EN Y
						}else
						{
							int temp = (myMovie.height*width)/myMovie.width; //SE HALLA EL ALTO EN RELACION AL ANCHO
							
							if(temp>height)
							{
								temp = (myMovie.width*height)/myMovie.height; //SE HALLA EL ANCHO EN RELACION AL ALTO
								parent.image(myMovie, x+(width-temp)/2,y,temp,height);//MANTIENE ASPECTRATIO CON RESIZE CON DELAY EN X
							}else
							{
								parent.image(myMovie, x,y+(height-temp)/2,width,temp);//MANTIENE ASPECTRATIO CON RESIZE CON DELAY EN Y
							}
						}
					}else
					{
						parent.image(myMovie, x,y,width, height);
					}
				}
				else
					parent.image(myMovie, x,y);
				
				
				
			}catch(Exception ex)
			{
				// CUADRO NEGRO CON MENSAJE DE ERROR
				System.out.print("ERROR AL CARGAR IMAGEN");
				parent.fill(0);
				parent.stroke(0);
				parent.rect(x, y, width, height);
				msgVideo = "Error al cargar imagen";
				printMsg(x,y,width,height);
			}
		}else
		{
			// CUADRO NEGRO CON MENSAJE DE CARAGANDO
			parent.fill(0);
			parent.stroke(0);
			parent.rect(x, y, width, height);
			printMsg(x,y,width,height);
		}
		
		
		
	//-- PLAY --
		
		//VERIFICA SI EL MOUSE ESTA DENTRO DEL BOTON PLAY
		
		if (parent.mouseX >= contX && parent.mouseX <= contX+sizeButton
				&& parent.mouseY >= contY&& parent.mouseY <= contY+sizeButton) {
			
			if (parent.mousePressed) {
				buttonPressed=BP_PLAY;
				parent.fill(200);
			}else
			{
				parent.fill(150);
			}
		
		}else
		{
			parent.fill(100);
		}
		
		parent.stroke(0);
		parent.rect(contX, contY, sizeButton, sizeButton);
		
		//VERIFICA SI ESTA EN PLAY O EN PAUSE
		
		parent.fill(0);
		parent.noStroke();
		
		if(movieState==0) //ESTA EN PLAY
		{
			parent.rect(contX+spaceInside+1, contY+spaceInside, 4, sizeButton-(spaceInside*2)+1);
			
			parent.rect(contX+spaceInside+7, contY+spaceInside, 4, sizeButton-(spaceInside*2)+1);
			
		}else
		{
		
			parent.triangle(contX+spaceInside, contY+spaceInside,
					contX+spaceInside, contY+sizeButton-spaceInside+1, 
					contX+sizeButton-spaceInside+2, contY+(sizeButton/2)+1);
		}
		
		

		
	// -- STOP --
		
		contX = contX+sizeButton;
		
		if (parent.mouseX >= contX && parent.mouseX <= contX+sizeButton
				&& parent.mouseY >= contY&& parent.mouseY <= contY+sizeButton) {
			
			if (parent.mousePressed) {
				buttonPressed=BP_STOP;
				parent.fill(200);
			}else
			{
				parent.fill(150);
			}
		
		}else
		{
			parent.fill(100);
		}

		parent.stroke(0);
		parent.rect(contX, contY, sizeButton, sizeButton);
		
		//CUADRADO DEL STOP
		
		parent.fill(0);
		parent.noStroke();
		parent.rect(contX+spaceInside+1, contY+spaceInside+1, 
				    sizeButton-(spaceInside*2)-1, sizeButton-(spaceInside*2)-1);
		
		//RESET
		
		contX = contX+sizeButton;
		
		if (parent.mouseX >= contX && parent.mouseX <= contX+sizeButton
				&& parent.mouseY >= contY&& parent.mouseY <= contY+sizeButton) {
			
			if (parent.mousePressed) {
				buttonPressed=BP_RESET;
				parent.fill(200);
			}else
			{
				parent.fill(150);
			}
		
		}else
		{
			parent.fill(100);
		}
		
		parent.stroke(0);
		parent.rect(contX, contY, sizeButton, sizeButton);
		
		//TRIANGULO DEL RESET
		
		parent.fill(0);
		parent.noStroke();
		parent.rect(contX+spaceInside, contY+spaceInside, 
				    2, sizeButton-(spaceInside*2)+1);
		
		parent.triangle(contX+spaceInside+3, contY+(sizeButton/2)+1,
						contX+sizeButton-spaceInside+1, contY+spaceInside, 
						contX+sizeButton-spaceInside+1, contY+sizeButton-spaceInside+1);
		
		//TIME
		
		String time = TimeUtil.getTime((long)timeNow, (long)timeTotal);
		
		if(time.length()>14) timeSp = timeSpaceLg;
		else timeSp = timeSpace;
		
		
		//BARRA DE NAVEGACION
		
		contX = contX+sizeButton;
		
		parent.stroke(0);
		parent.fill(100);
		
		parent.rect(contX, contY, 
					x+width-contX-timeSp-volumeSpace, sizeButton);
		
		int widthBar = x+width-contX-timeSp-volumeSpace-(spaceInside*3);
		int heigthBar = (sizeButton/3)+1;
		
		contX = contX+(int)(spaceInside*1.5);
		
		parent.fill(200);
		parent.rect(contX, contY+heigthBar-1, 
				    widthBar, heigthBar);
		
		int widthTime;
		
		if(timeTotal!=0)
			widthTime= (int)((timeNow*widthBar)/timeTotal);
		else
			widthTime =0;
		
		if(widthTime<0) widthTime=0;
		
		parent.fill(0,0,128);
		parent.noStroke();
		parent.rect(contX, contY+heigthBar-1, 
				widthTime, heigthBar);
		
		//SI HACE CLICK DENTRO DE LA BARRA DE NAVEGACION
		
		if (parent.mousePressed && parent.mouseX >= contX && parent.mouseX <= contX+widthBar
		    && parent.mouseY >= contY+heigthBar-1&& parent.mouseY <= contY+sizeButton-heigthBar) {
			
			if(myMovie!=null)
			{
				myMovie.jump((float)((double)(parent.mouseX-contX)*(double)timeTotal/(double)widthBar));
				movieCommand = CM_JUMP;
				keyCommand=true;
			}
		}
		
	// -- BARRA DE VOLUMEN --
		
		contX = x+width-timeSp-volumeSpace;
		
		parent.fill(100);
		parent.stroke(0);
		
		parent.rect(contX, contY, 
				volumeSpace, sizeButton);
		
		//TRIANGULO DEL VOLUMEN
		
		contX = contX +spaceInside;
		
		int widthVolume = volumeSpace-(spaceInside*2)+1;
		int realVolume =(int)(((double)volume*(double)widthVolume)/(double)100);
		
		parent.fill(180);
		
		parent.noStroke();
		
		parent.fill(180);
		parent.rect(contX, contY+spaceInside,
				    widthVolume, sizeButton-(spaceInside*2)+1);
		
		parent.fill(0,0,64);
		parent.rect(contX, contY+spaceInside,
				    realVolume, sizeButton-(spaceInside*2)+1);
		
		parent.noStroke();
		parent.fill(100);
		parent.triangle(contX, contY+sizeButton-spaceInside+1,
				        contX+widthVolume+1, contY+spaceInside, 
				        contX, contY+spaceInside);
		
		parent.stroke(0);
		parent.noFill();
		parent.triangle(contX, contY+sizeButton-spaceInside+1,
				        contX+widthVolume, contY+spaceInside, 
				        contX+widthVolume, contY+sizeButton-spaceInside+1);
		
		//SI HACE CLICK DENTRO DE LA BARRA DE VOLUMEN
		
		if (parent.mousePressed && parent.mouseX >= contX && parent.mouseX <= contX+widthVolume
		    && parent.mouseY >= contY+spaceInside&& parent.mouseY <= contY+sizeButton-spaceInside) {
			
			volume = (int)((double)(parent.mouseX-contX)*(double)100/(double)widthVolume);
			if(myMovie!=null)
				myMovie.volume((double)volume/(double)100);	
		}
		
		
	// -- BARRA DE TIEMPO --
		
		contX = x+width-timeSp;
		parent.fill(100);
		parent.stroke(0);
		
		parent.rect(contX, contY, 
				timeSp, sizeButton);
		
		parent.fill(0);
		
		parent.textFont(font,fontSize);
		parent.text(time,contX+spaceInside,contY+(sizeButton/2)+(fontSize/2));
	}
	
	public void video(int x, int y) {
		video(x,y,-1,-1);
	}
	
	private void printMsg(int x,int y,int width,int height)
	{
		parent.fill(255);
		int msgText = TextUtil.getwidth(font, fontSize, msgVideo);
		parent.textFont(font,fontSize);
		parent.text(msgVideo,x+(width-msgText)/2,y+(height/2)+(fontSize/2));
	}
	
	public void mouseEvent(MouseEvent event) { 
		 
		  if (event.getID() ==MouseEvent.MOUSE_RELEASED) { 
			  
			  if(movieState==ST_NO_VIDEO) return; //NO SE HA CARGADO NINGUN VIDEO
			  
			    keyCommand=true;
				if (buttonPressed==BP_PLAY)
				{
					if(movieState==ST_PLAY) //ESTA EN PLAY LO DEJA EN PAUSE
						movieCommand=CM_PAUSE;
					else if(movieState==ST_PAUSE)  //ESTA PAUSE LO DEJA EN PLAY
						movieCommand=CM_PLAY;
					else					//STOP RECARGA EL VIDEO
						movieCommand=CM_RELOAD;

				}
				else if (buttonPressed==BP_STOP){
					movieCommand=CM_STOP;
				}
				else if (buttonPressed==BP_RESET){
					
					if(movieState==ST_PLAY) //ESTA EN PLAY A RESET
						movieCommand=CM_RESET;
					else if(movieState==ST_PAUSE)  //ESTA EN PAUSE A INICIO IMAGEN
						movieCommand=CM_INDEX;

				}else 
					keyCommand=false;
		  } 
		}
	
	private void runVideo(){

		//MENU DEL REPRODUCTOR
		if(movieCommand==CM_INDEX){  		//INICIO
			myMovie.goToBeginning();
			myMovie.play();
			myMovie.pause();
			movieState=ST_PAUSE;
		}
		if(movieCommand==CM_PLAY){		//PLAY
			myMovie.play();
			movieState=ST_PLAY;
		}
		if(movieCommand==CM_PAUSE){		//PAUSE
			myMovie.pause();
			movieState=ST_PAUSE;
		}
		if(movieCommand==CM_RESET){		//RESET
			myMovie.goToBeginning();
		}
		if(movieCommand==CM_LOOP_ON){		//LOOP ON
			myMovie.loop();
		}
		if(movieCommand==CM_LOOP_OFF){		//LOOP OFF
			myMovie.noLoop();
		}
		if(movieCommand==CM_MUTE_ON){		//MUTE ON
			myMovie.volume(0);
			volume =0;
		}
		if(movieCommand==CM_MUTE_OFF){		//MUTE OFF
			myMovie.volume(1.0);
			volume=100;
		}
		if(movieCommand==CM_STOP){		//STOP
			myMovie.goToBeginning();
			myMovie.stop();
			myMovie.dispose();
			movieState=ST_STOP;
		}
		if(movieCommand==CM_RELOAD){		//RELOAD VIDEO
			myMovie = new GSMovie(parent, urlVideo);;
			myMovie.play();
			movieState=ST_PLAY;
		}
		
		if(movieCommand==CM_VOLUME_UP){		//UP VOLUME
			if(volume<100)
			{
				volume=volume+10;
				if(volume >100)
					volume=100;
			}
			myMovie.volume((double)volume/(double)100);	
		}
		
		if(movieCommand==CM_VOLUME_DOWN){		//DOWN VOLUME
			if(volume>0)
			{
				volume=volume-10;
				if(volume <0)
					volume=0;
			}
			myMovie.volume((double)volume/(double)100);	
		}
		
		keyCommand=false;
	}
	
	public void keyEvent(KeyEvent e) {
		
		if(e.getID()==KeyEvent.KEY_TYPED)
		{
			if(movieState==ST_NO_VIDEO) return; //NO SE HA CARGADO NINGUN VIDEO
			
			keyCommand=true;
			
			if(parent.key == 'z' || parent.key == 'Z')			
				movieCommand=CM_PLAY;					
			else if(parent.key == 'x' || parent.key == 'X')				
				movieCommand=CM_PAUSE;
			else if(parent.key == 'c' || parent.key == 'C')				
				movieCommand=CM_RESET;
			else if(parent.key == 'b' || parent.key == 'B'){
				
				if(movieLoop){
					movieCommand=CM_LOOP_ON;
					movieLoop=false;
				}else{
					movieLoop=true;
					movieCommand=CM_LOOP_OFF;	
				}																						
			}
			else if(parent.key == 's' || parent.key == 'S'){
				
				if(movieSound){
					movieCommand=CM_MUTE_ON;
					movieSound=false;
				}else{
					movieSound=true;
					movieCommand=CM_MUTE_OFF;	
				}																						
			}
			else if(parent.key == ';' || parent.key == ',')				
				movieCommand=CM_VOLUME_UP;
			
			else if(parent.key == ':' || parent.key == '.')				
				movieCommand=CM_VOLUME_DOWN;
			else
				keyCommand=false;
		}
	}
	
	public int getHeigth()
	{
		if(showTitle)
			return heigthReal+sizeButton+sizeTitle;
		else
			return heigthReal+sizeButton;
	}
	
	public int getWidth()
	{
		return widthReal;
	}
	
	public void showTitle(boolean title)
	{
		showTitle=title;
	}
	
	private void downButtons()
	{
		sizeTitle = 16;
		sizeButton = 18;
		spaceInside = 4;
		timeSpace =63;
		timeSpaceLg = 90;
		volumeSpace = 25;
		fontSize = 8;
		
		font = parent.createFont("Verdana", fontSize);
		
		widthText = TextUtil.getwidth(font, fontSize, title);
		changeButtonDown = true;
		changeButtonUp = false;
	}
	
	private void upButtons()
	{
		sizeTitle = 20;
		sizeButton = 25;
		spaceInside = 7;
		timeSpace = 89;
		timeSpaceLg = 126;
		volumeSpace = 50;
		fontSize = 11;
		widthText = TextUtil.getwidth(font, fontSize, title);
		font = parent.createFont("Verdana", fontSize);
		
		changeButtonDown = false;
		changeButtonUp = true;
	}
	
	protected void setMsg(String msg)
	{
		msgVideo = msg;
	}
	
	protected void setTitle(String msg)
	{
		title = msg;
		widthText = TextUtil.getwidth(font, fontSize, title);
	}
	
	public void disableChargeScreen()
	{
		showCharge=false;
	}
	
	public void enableChargeScreen()
	{
		showCharge=true;
	}
	
	public boolean videoCharged()
	{
		return charge;
	}
	
	public void aspectRatio(boolean ar)
	{
		aspectRatio = ar;
	}
	
	public void cacheDownload(boolean cache)
	{
		cacheDown = cache;
	}
	
	public void dispose()
	{
		closePlayer();
	}
	
	protected PApplet parent;
	protected GSMovie myMovie;

	//TAMANYO BOTONES
	
	private int sizeTitle = 20;
	private int sizeButton = 25;
	private int spaceInside = 7;
	private int timeSpace = 89;
	private int timeSpaceLg = 126;
	private int volumeSpace = 50;
	private int fontSize = 11;
	
	private PFont font;
	
	//ESTADOS DEL VIDEO
	
	private int buttonPressed;
	
	protected boolean keyCommand;
	protected int movieCommand;
	protected int movieState;			//ST CONSTANTS
	
	private int volume; 
	
	private boolean movieLoop;   //LOOP ON/OFF
	private boolean movieSound;  //MUTE ON/OFF
	private boolean showCharge = true;
	private boolean showTitle;
	private boolean aspectRatio;
	
	private boolean charge;
	
	private String urlVideo;
	private String msgVideo;
	private int widthText;
	private String title;
	
	private int heigthReal;
	private int widthReal;
	
	//RESIZE
	
	private boolean changeButtonDown;
	private boolean changeButtonUp;
	
	//STREAM REMOTE
	private StreamRemoteUtil sru;
	private boolean cacheDown; //Desabilita la descarga del video
	
}
