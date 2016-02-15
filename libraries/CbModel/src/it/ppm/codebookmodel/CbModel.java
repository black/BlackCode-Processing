package it.ppm.codebookmodel;

import processing.core.*;
import java.lang.Math;
import java.util.LinkedList;
import java.lang.Thread;

/**
  *@author Federico Bartoli
  *@author Mattia Masoni

 */
/**
 *This class implent Background Subraction's algorithm trough model based on CodeBook.
 *
 */
public class CbModel extends Thread{

	private PApplet myParent;
	private static final float version=0.1f;
	private CodeBook[][] Cb;
	private  LinkedList<PImage> Image_List;
	private PImage Image;
	private int 	width;
	private int 	height;
	private int 	n_frame;
    private boolean learn_state;
	private boolean end_learn;
	private boolean active_process;
	private boolean wait_process;
	private int		color_foreground;
    private int	    color_background;
    private float 	percent_tm;
    private float   tm;
	private float 	alpha;
	private float 	beta;
	private float[] epsilon;
	

	private int  matchPixel(int cord_x,int cord_y,float xt[],float brightness_xt,int index_epsilon){

		if(Cb[cord_x][cord_y].size()<1)
			return -1;
		for(int i=0;i<Cb[cord_x][cord_y].size();i++){ 
			if( colorDist(xt,Cb[cord_x][cord_y].get(i).vm)<=epsilon[index_epsilon] && compareBrightness(brightness_xt,Cb[cord_x][cord_y].get(i).aux[0],Cb[cord_x][cord_y].get(i).aux[1],xt)==true)
				return i;
		}
		return -1;

	}
	private double colorDist(float xt[],float vm[]){

		float p_squared=0;
		float norm_squared_xt=0;
		float norm_squared_vm=0;
		float scalar_squared=0;
		
		for(int i=0;i<3;i++){
			norm_squared_xt+=xt[i]*xt[i];
			norm_squared_vm+=vm[i]*vm[i];
			scalar_squared+=xt[i]*vm[i];
		}
		scalar_squared=scalar_squared * scalar_squared;
		p_squared=scalar_squared/norm_squared_vm;
		return Math.sqrt((double)norm_squared_xt-p_squared);
	
	}
	private boolean compareBrightness(float brightness_xt,float brightness_inf_vm,float brightness_sup_vm,float xt[]){

		float brightness_low=alpha*brightness_sup_vm;
		float brightness_hi=0;
		float norm_xt=0;

		for(int i=0;i<3;i++)
			norm_xt+=xt[i]*xt[i];
		norm_xt=(float)Math.sqrt(norm_xt);
		
		if( (brightness_hi=beta*brightness_sup_vm) >brightness_inf_vm/alpha)
			brightness_hi=brightness_inf_vm/alpha;
		
		if( (brightness_low<= norm_xt) && (norm_xt <=brightness_hi))
			return true;
		else
			return false;
		
	}
    private void resetModel(int width,int height,float alpha,float beta,float epsilon_1,float epsilon_2,float percent_tm,int color_background,int color_foreground){
		
		this.width=width;
		this.height=height;
		n_frame=0;
		
		learn_state=true;
		end_learn=false;
    	active_process=false;
    	wait_process=false;

    	this.color_background=color_background;
    	this.color_foreground=color_foreground;

    	this.percent_tm=percent_tm;
		this.alpha=alpha;
		this.beta=beta;
		epsilon=new float[2];
		epsilon[0]=epsilon_1;
		epsilon[1]=epsilon_2;
		
		Image_List=new LinkedList<PImage>();
	
		Cb=new CodeBook[width][height];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++)
				Cb[x][y]=new CodeBook();
		}

    } 
	private void buildCodebook(int cord_x,int cord_y,int color){

		float[] xt=new float[3];
		float brightness_xt;
		float[] aux=new float[6];
		int index;

		xt[0]=myParent.red(color);		
		xt[1]=myParent.green(color); 	
    	xt[2]=myParent.blue(color);		

    	brightness_xt=xt[0]+xt[1]+xt[2];
 		
		if((index=matchPixel(cord_x,cord_y,xt,brightness_xt,0))<0){
			aux[0]=aux[1]=brightness_xt;
			aux[2]=1;
			aux[3]=n_frame-1;
			aux[4]=aux[5]=n_frame;
			
			Cb[cord_x][cord_y].add(xt,aux);
		}
		else{
			Cb[cord_x][cord_y].get(index).vm[0]=(Cb[cord_x][cord_y].get(index).aux[2]*Cb[cord_x][cord_y].get(index).vm[0]+xt[0])/(Cb[cord_x][cord_y].get(index).aux[2]+1);	
			Cb[cord_x][cord_y].get(index).vm[1]=(Cb[cord_x][cord_y].get(index).aux[2]*Cb[cord_x][cord_y].get(index).vm[1]+xt[1])/(Cb[cord_x][cord_y].get(index).aux[2]+1);
			Cb[cord_x][cord_y].get(index).vm[2]=(Cb[cord_x][cord_y].get(index).aux[2]*Cb[cord_x][cord_y].get(index).vm[2]+xt[2])/(Cb[cord_x][cord_y].get(index).aux[2]+1);
	    	
	    	if(Cb[cord_x][cord_y].get(index).aux[0]>brightness_xt)
	    		Cb[cord_x][cord_y].get(index).aux[0]=brightness_xt;
			
	    	if(Cb[cord_x][cord_y].get(index).aux[1]<brightness_xt)
	    		Cb[cord_x][cord_y].get(index).aux[1]=brightness_xt;

	    	Cb[cord_x][cord_y].get(index).aux[2]=Cb[cord_x][cord_y].get(index).aux[2]+1;

	    	if(Cb[cord_x][cord_y].get(index).aux[3]<=n_frame-Cb[cord_x][cord_y].get(index).aux[5])
	    		Cb[cord_x][cord_y].get(index).aux[3]=n_frame-Cb[cord_x][cord_y].get(index).aux[5];
	    	
	    	Cb[cord_x][cord_y].get(index).aux[5]=n_frame;	    	
		}
			
	}
	private void passTestState_(){
	
		this.setPriority(MAX_PRIORITY);
				
		tm=(float)(n_frame*percent_tm);			
		
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				for(int z=0;z<Cb[x][y].size();z++){
					if(Cb[x][y].get(z).aux[3]<(n_frame-Cb[x][y].get(z).aux[5]+Cb[x][y].get(z).aux[4]-1))
						Cb[x][y].get(z).aux[3]=n_frame-Cb[x][y].get(z).aux[5]+Cb[x][y].get(z).aux[4]-1;
					if(Cb[x][y].get(z).aux[3]>tm)
						Cb[x][y].remove(z);
				}
			}
		}
		
	}
	private boolean isBackground(int cord_x,int cord_y,int color){
		
		float[] xt=new float[3];
		float brightness_xt;
		
		xt[0]=myParent.red(color);		
		xt[1]=myParent.green(color);	
    	xt[2]=myParent.blue(color);		
		
    	brightness_xt=xt[0]+xt[1]+xt[2];
		
		if(matchPixel(cord_x, cord_y, xt, brightness_xt,1)>=0 )
			return true;	
		else
			return false;
		
	}
    private void updateModel_(PImage Image)throws LearningStateException,WrongSizeException{

		n_frame+=1;
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				buildCodebook(x,y,Image.pixels[y*width+x]);
				
			}
		}

	}

    /**
     * 	Construct a new object CbModel with the specified dimension of the video.This function set class in too "Learning" state.
     * @param theParent pointer to the caller.
     * @param width video's width.
     * @param height video's height.
     */
    public CbModel(PApplet theParent,int width,int height) {
		
    	myParent = theParent;
		resetModel(width,height,0.4f,1.2f,0.2f,100f,0.9f,0,0xffffffff);
			
	}
	/**
	 * 	Construct a new object CbModel whit the specified parameters of the video.This function set class in too "Learning" state.
	 * @param theParent pointer to the caller.
	 * @param width video's width.
	 * @param height video's height.
	 * @param alpha brightness'parameter ,between 0.4-0.7 .
	 * @param beta brightness'parameter,tipically between 1.1-1.5 .
	 * @param epsilon_1 max tollerance of brightenns's deviation and the color of a pixel(in order too recognise a pixel from another) phase's "Learning".
	 * @param epsilon_2 max tollerance of brightenns's deviation and the color of a pixel(in order too recognise a pixel from another) phase's "Testing".
	 * @param percent_tm temporal threshold's parameter,tipically is  0.7.
	 * @param color_background background pixel's color.
	 * @param color_foreground foreground pixel's color.
	 */
    public CbModel(PApplet theParent,int width,int height,float alpha,float beta,float epsilon_1,float epsilon_2,float percent_tm,int color_background,int color_foreground){
		
    	myParent = theParent;
		resetModel(width,height,alpha,beta,epsilon_1,epsilon_2,percent_tm,color_background,color_foreground);

	}
    /**
     * 	Method of building/uploading of the model contained in the object,this will evenctually start a thread,if not yet operating,
     * that build the model according too previous image passed.
     * @param Image Image to build/uploading the model.
     * @throws LearningStateException Exception that may appears if this method is called during Testing phase.
     * @throws WrongSizeException Exception that may appears if Image has different dimension from those setted in the object.
     */
    public void updateModel(PImage Image)throws LearningStateException,WrongSizeException{

    	if(!learn_state || end_learn)
			throw new LearningStateException();
	
    	if(Image.width!=width || Image.height!=height)
			throw new WrongSizeException();

    	if(!active_process){
			active_process=true;
			this.setPriority(MIN_PRIORITY);
			this.start();
		}
		
    	synchronized (Image_List) {
			Image_List.add(Image);
			if(Image_List.size()==1 && wait_process)
				Image_List.notify();
		}
		
    }
    public void run(){
 
    	while(true){
    		synchronized(Image_List){
		   		if(Image_List.size()==0){	    			
		   			if(end_learn){
		   				passTestState_();
						learn_state=false;
		   				break;
		   			}
		   			try{
						wait_process=true;
						Image_List.wait();
					}
					catch (InterruptedException e) {
					}
					wait_process=false;	
		   			if(end_learn){
		   				passTestState_();
						learn_state=false;
		   				break;
		   			}
		   		}
    		}
	   		synchronized(Image_List){
	   			Image=Image_List.removeFirst();
	   		}
		   	try{
				updateModel_(Image);
			}
			catch(LearningStateException Err){
        		
        	}
        	catch(WrongSizeException Err){
        		
        	}
		}
    	
    }
	/**
	 * 	Returns  an image where each pixel'color of image is:
	 * 	color_background(default black) if such a pixel has been recognized by the model like a pixel's background
	 *  color_foreground (default white) if such a pixel has been recognized by the model like a pixel's foreground.

	 * @param Image Image that must be processed by the model.
	 * @return the image that represent background's pixels and foreground's pixels. 
	 * @throws WrongSizeException Exception that may appears if Image has different dimension from those setted in the object.
	 * @throws LearningStateException Exception that may appears if this method is called during Testing phase.
	 */
    public PImage getDifferenceImage (PImage Image)throws WrongSizeException,LearningStateException{

		PImage Back_Forg_Image; 
		if (!end_learn || learn_state)
			throw new LearningStateException();
		
		if(Image.width!=width || Image.height!=height)
			throw new WrongSizeException();

		Back_Forg_Image=new PImage(width,height,1);
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				if(isBackground(x,y,Image.pixels[y*width+x]))
					Back_Forg_Image.pixels[y*width+x]=color_background;
				else
					Back_Forg_Image.pixels[y*width+x]=color_foreground;
			}
		}
		return Back_Forg_Image;
		
	}
	/**
	 * 	Returns the pixels identified as belonging to the background.
	 * @param Image Image that must be processed by the model.
	 * @return the background's pixels.
	 * @throws WrongSizeException Exception that may appears if this method is called during Testing phase.
	 * @throws LearningStateException Exception that may appears if this method is called during Testing phase.
	 */
    public Point_List getBackgroundPoint(PImage Image)throws WrongSizeException,LearningStateException{

		Point_List Points;
		if (!end_learn || learn_state)
			throw new LearningStateException();
		
		if(Image.width!=width || Image.height!=height)
			throw new WrongSizeException();

		Points=new Point_List();
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				if(isBackground(x,y,Image.pixels[y*width+x]))
					Points.addPoint(x, y);
			}
		}
		return Points;
		
	}
/**
 * 	Returns the pixels identified as belonging to the foreground.
 * @param Image Image that must be processed by the model.
 * @return foreground's pixels.
 * @throws WrongSizeException Exception that may appears if Image has different dimension from those setted in the object.
 * @throws LearningStateException Exception that may appears if this method is called during Testing phase.
 */
    public Point_List getForegroundPoint(PImage Image)throws WrongSizeException,LearningStateException{

		Point_List Points;
		if (!end_learn || learn_state)
			throw new LearningStateException();
		
		if(Image.width!=width || Image.height!=height)
			throw new WrongSizeException();

		Points=new Point_List();
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				if(!isBackground(x,y,Image.pixels[y*width+x]))
					Points.addPoint(x, y);
			}
		}
		return Points;
		
	}
	/**
	 * 	Set epsilon_2' s parameter value.
	 * @param epsilon a new max threshold of brightenns's deviation and the color of a pixel.
	 */
    public void setEpsilon2(float epsilon){
	
    	this.epsilon[1]=epsilon;
	
    }
	/**
	 * 	The mand of status changement from "Learning" to "Testing",obviously this transit should not be immediate,this depend from 
	 *   thrend execution.
	 *
	 */
	public void setTestState(){
		
		if(!end_learn){
			end_learn=true;
			if(!active_process)
				learn_state=false;
			else{
				synchronized(Image_List){
					Image_List.notify();
				}
			}
		}
	
	}
	/**
	 * 	The method return too effective status of the object:true if in the Testinf state,false if in "learning" state.
	 * @return true if the CbModel is in the state' Test ,false otherwise.
	 */
	public boolean isTestState(){
		
		return (end_learn && !learn_state);
		
	} 
	/**
	 * 	Returns the library's version.
	 * @return the library's version.
	 */
	public float getVersion(){
		
		return version;
	
	}
	/**
	 * 	Returns alpha's parameter value.	
	 * @return alpha's parameter value (parameter set in the constructor).
	 */
	public float getAlpha(){
	
		return alpha;
	
	}
	/**
	 * 	Returns beta's parameter value.	
	 * @return beta's parameter value (parameter set in the constructor).
	 */
	public float getBeta(){
		
		return beta;
	
	}
	/**
	 * 	Returns epsilon_1's parameter value.	
	 * @return epsilon_1's parameter value (parameter set in the constructor).
	 */
	public float getEpsilon1(){
		
		return epsilon[0];
	
	}
	/**
	 * 	Returns epsilon_2's parameter value.	
	 * @return 	epsilon_2's parameter value(parameter that can be set when running through the method setEpsilon2()).
	 */
	public float getEpsilon2(){

		return epsilon[1];
	
	}
	/**
	 * 	Returns percent_tm 's parameter value.	
	 * @return percent_tm 's parameter value (parameter set in the constructor).
	 */
	public float getTm(){
		
		return percent_tm;
	
	}
	
}
