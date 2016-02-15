/*---

    iGeo - http://igeo.jp

    Copyright (c) 2002-2013 Satoru Sugihara

    This file is part of iGeo.

    iGeo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, version 3.

    iGeo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with iGeo.  If not, see <http://www.gnu.org/licenses/>.

---*/

package igeo.p;

import processing.core.*;
import processing.opengl.*;

import javax.media.opengl.*;

import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import java.util.*;

import java.lang.reflect.*;

//import com.sun.opengl.util.j2d.Overlay;
import com.jogamp.opengl.util.awt.Overlay; // processing 2.0

import igeo.*;
import igeo.gui.*;


/**
   A child class of Processing's PGraphic to draw on Processing using OpenGL.
   This class also manages the IServer to manage all the objects in iGeo.
   
   @author Satoru Sugihara
*/
public class PIGraphicsGL2Test extends PGraphics3D
    implements IGraphics3D{

    /** preserve Procesing's color mode or not */
    static boolean keepColorMode=false;
    
    
    public IPanelI panel;
    public IGraphics3D igg;

    public PGL localPGL;
    
    /** To draw Java2D graphic over OpenGL graphic. */
    public Overlay overlay;
    
    /** Background color of overlay should be transparent. */
    static public Color overlayBG = new Color(0,0,0,0);
    
    /** To show iGeo correctly in Processing's basic mode, this needs to be true. */
    public boolean overwritePAppletFinish=true;
    public boolean finished=false;
    
    /** To show iGeo correctly in Processing's basic mode, this needs to be true. */
    public boolean overwritePAppletLoop=true;
    public boolean looping=true;



    public IView view; // for IGrahpics3D
    /** cache of view.frontDirection() */
    public IVec viewDirection;
    /** cache of view.location() */
    public IVec viewLocation;
    
    public int origColorMode;
    public float origColorModeX, origColorModeY, origColorModeZ, origColorModeA;
    
    public PImage bgImage=null;
    public IColor[][] bgColorCache=new IColor[2][2];
    
    public boolean firstDraw=true;

    /** when number of objects exceeds IConfig.maxObjectNumberForDepthSort, depth sort is overwritten to be disabled */
    public boolean overrideDepthSort=false;
    
    public boolean enableDepthSort;
    
    
    public PIGraphicsGL2Test(){ super(); }
    
    
    /**
       setParent is called by Processing in the initialization process of Processing.
       Here the initialization proces of iGeo is also done.
       @param parent parent PApplet of Processing.
    */
    public void setParent(PApplet parent){
	
	super.setParent(parent);
	
	// initialize root GUI
	panel = new IGridPanel(0,0,parent.getWidth(),parent.getHeight(),2,2);
	panel.setVisible(true); 
	
	panel.setParent(parent);
	//panel.setAdapter(this);
	
	// initialize iGeo 
	IG ig = IG.init(panel);
	ig.server().graphicServer().enableGL(); //
	//ig.setBasePath(parent.sketchPath("")); // not sketchPath
	
	ig.setOnline(parent.online);
	
	if(!parent.online){ // only when running local
	    ig.setBasePath(parent.dataPath("")); // for default path to read/write files
	}
	
	ig.setInputWrapper(new PIInput(parent));
	

	parent.removeMouseListener(parent);
	parent.removeMouseMotionListener(parent);
	parent.removeMouseWheelListener(parent);
	parent.removeKeyListener(parent);
	parent.removeFocusListener(parent);
	//parent.removeComponentListener(parent);
	
	parent.addMouseListener(panel);
	parent.addMouseMotionListener(panel);
	parent.addMouseWheelListener(panel);
	parent.addKeyListener(panel);
	parent.addFocusListener(panel);
	parent.addComponentListener(panel);
	
	
	if(parent.frame!=null){
	    parent.frame.addWindowListener(panel);
	}
	
	
	//igg = new IGraphics();
	//igg = new IGraphicsGL();
	//igg = this;
	//igg = new IGraphicsGL2();
	igg = this;
	
	//noSmooth();
	
	
	if(PIConfig.drawBeforeProcessing){ parent.registerPre(this); }
	else{ parent.registerDraw(this); }
	parent.registerPost(this);
	
	
	if(PIConfig.resizable && parent.frame!=null){ // frame seems to be null in exported applet
	    parent.frame.setResizable(true);
	}
	
	//super.hints[DISABLE_OPENGL_2X_SMOOTH]=true;  //
	//super.hints[ENABLE_OPENGL_4X_SMOOTH]=true;  //

	// added from PIGraphics3D
	enableDepthSort=IConfig.depthSort;
	if(enableDepthSort){ super.hint(ENABLE_DEPTH_SORT); }
	else{ super.hint(DISABLE_DEPTH_SORT); }
	
    }

    public PGL getPGL(){
	try{
	    Class<?> cls = Class.forName("processing.opengl.PGraphicsOpenGL");
	    Field pglField = cls.getField("pgl");
	    if(pglField!=null){
		Object obj = pglField.get(this);
		if(obj!=null && obj instanceof PGL){
		    return (PGL)obj;
		}
	    }
	}
	catch(ClassNotFoundException e){}
	catch(NoSuchFieldException e){}
	catch(Exception e){ e.printStackTrace(); }
	
	IG.err("no PGL found"); //
	return null;
    }
    
    public GL getGL(){
	
	if(localPGL==null){ localPGL = getPGL(); }
	if(localPGL==null){ return null; }
	
	if(localPGL.getClass().getSimpleName().equals("PJOGL")){ // Processing v 2.1
	    try{
		Field glField = localPGL.getClass().getField("gl");
		if(glField!=null){
		    Object obj = glField.get(localPGL);
		    if(obj!=null && obj instanceof GL){
			return (GL)obj;
		    }
		}
	    }
	    catch(NoSuchFieldException e){}
	    catch(Exception e){ e.printStackTrace(); }
	}
	
	try{
	    Field glField = localPGL.getClass().getField("gl");
	    if(glField!=null){
		Object obj = glField.get(localPGL);
		if(obj!=null && obj instanceof GL){ return (GL)obj; }
	    }
	}
	catch(NoSuchFieldException e){}
	catch(Exception e){ e.printStackTrace(); }
	
	IG.err("no GL found"); //
	return null;
    }
    
    public GL2 getGL2(){
	GL gl = getGL();
	if(gl!=null){ return gl.getGL2(); }
	return null;
    }
    
    protected Canvas getCanvas(){
	
	if(localPGL==null){ localPGL = getPGL(); }
	if(localPGL==null){ return null; }
	
	try{
	    Class<?> cls = localPGL.getClass();
	    Method canvasMethod = cls.getMethod("getCanvas");
	    if(canvasMethod != null){ // Processing 2.1
		Object obj = canvasMethod.invoke(localPGL);
		if(obj!=null && obj instanceof Canvas){ return (Canvas)obj; }
	    }
	}
	catch(NoSuchMethodException e){}
	catch(Exception e){ e.printStackTrace(); }
	
	try{
	    Field canvasField = localPGL.getClass().getField("canvas");
	    if(canvasField != null){ // Processing 2.0.3
		Object fieldObj = canvasField.get(localPGL);
		if(fieldObj!=null && fieldObj instanceof Canvas){ return (Canvas)fieldObj; }
	    }
	}
	catch(NoSuchFieldException e){}
	catch(Exception e){ e.printStackTrace(); }
	
	return null;
    }
    
    
    @Override
    public void initPrimary(){
	super.initPrimary();

	Canvas canvas = getCanvas();

	if(canvas!=null){
	    canvas.addMouseListener(panel);
	    canvas.addMouseMotionListener(panel);
	    canvas.addMouseWheelListener(panel);
	    canvas.addKeyListener(panel);
	    canvas.addFocusListener(panel);
	    canvas.addComponentListener(panel);
	}
	
	/*
	pgl.canvas.addMouseListener(panel);
	pgl.canvas.addMouseMotionListener(panel);
	pgl.canvas.addMouseWheelListener(panel);
	pgl.canvas.addKeyListener(panel);
	pgl.canvas.addFocusListener(panel);
	pgl.canvas.addComponentListener(panel);
	*/
    }
    
    
    /* // debug
    public void setGLProperties(){
	
	GL2 gl = pgl.gl.getGL2(); // processing 2.0
	
	gl.glEnable(GL2.GL_MULTISAMPLE); //
	gl.glEnable(GL2.GL_POINT_SMOOTH); //
	gl.glEnable(GL2.GL_LINE_SMOOTH); //
	gl.glEnable(GL2.GL_POLYGON_SMOOTH); //
	
	gl.glEnable(GL2.GL_ALPHA_TEST); //
	//gl.glEnable(GL2.GL_BLEND); //
	//gl.glDisable(GL2.GL_BLEND); //
	//gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); //
	//gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE); //
	
	gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST); //
	gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST); //
	gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST); //
	
	//gl.glEnable(GL2.GL_NORMALIZE); //
	//gl.glEnable(GL2.GL_AUTO_NORMAL); //
	//gl.glShadeModel(GL2.GL_SMOOTH); //
	
	//gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1); //
	
	//gl.glEnable(GL2.GL_DEPTH_TEST); // already enabled in super
	//gl.glDisable(GL2.GL_DEPTH_TEST); // ? for transparency
	
	//gl.glEnable(GL2.GL_LIGHTING); // test!
	//gl.glEnable(GL2.GL_LIGHT1); // test!
	
    }
    */
    
    public void pre(){ drawIG(); }
    public void draw(){ drawIG(); }
    

    /**
       Drawing all the iGeo objects through IPanel.
       Overlay is also used to draw 2D graphics on top of OpenGL 3D graphics.
    */
    public void drawIG(){
	if(panel!=null) panel.draw(this);
    }
    /*
    public synchronized void drawIG(){
	GL gl= getGL();
	if(gl!=null){
	    if(igg instanceof IGraphicsGL){
		((IGraphicsGL)igg).setGL(gl);
	    }
	    super.beginPGL();
	    panel.draw(igg);
	    super.endPGL();
	}
    }
    */
    
    public void setOverlay(){ // doing nothing for now
	/*
	if(pgl.canvas instanceof GLDrawable){
	    //overlay = new Overlay(drawable); // processing 1.5
	    overlay = new Overlay((GLDrawable)pgl.canvas); // processing 2.0
	    igg.setGraphics(overlay.createGraphics());
	    igg.getGraphics().setBackground(overlayBG);
	}
	*/
    }
    
    /*
    public void setSize(int w, int h){
	super.setSize(w,h);
	//setOverlay(); // update overlay
	//panel.setSize(w,h); //
    }
    */
    
    public void post(){
	if(overwritePAppletFinish) parent.finished=finished;
	if(overwritePAppletLoop) if(looping) parent.loop(); else parent.noLoop();
    }
    
    public void loop(){ if(!looping) looping=true; }
    public void noLoop(){ if(looping) looping=false; }
    
    public void start(){ if(finished) finished=false; }
    public void stop(){ if(!finished) finished=true; }

    
    
    
    ///////////////////////////////////////////////////////////////////////////
    // added from PIGraphics3D
    /////////////////////////////////////////////////////////////////////////


    // overriding PGraphics.vertex to avoid error of disappearing lines when their end and start points are at the same location and DEPTH_SORT is enabled. 
    public void vertex(float x, float y, float z) {
	
	// part of this is excluded in PGraphics.vertex(float,float,float)
	if(hints[ENABLE_DEPTH_SORT] &&
	   (shape == POLYGON || shape==LINE_STRIP || shape==LINE)&&
	   vertexCount > 0 &&
	   // vertexCount == shapeFirst &&  // first vertex
	   (Math.abs(vertices[vertexCount-1][X] - x) < EPSILON) &&
	   (Math.abs(vertices[vertexCount-1][Y] - y) < EPSILON) &&
	   (Math.abs(vertices[vertexCount-1][Z] - z) < EPSILON)) {
	    // in case depth sort is enabled and end of previous line is identical with the start of the current line
	    
	    // add a vertex in the regular way as PGraphics.vertex does.
	    vertexCheck();
	    float[] vertex = vertices[vertexCount];
	    curveVertexCount = 0;
	    vertex[X] = x;
	    vertex[Y] = y;
	    vertex[Z] = z;
	    vertex[EDGE] = edge ? 1 : 0;
	    if (fill || textureImage != null) {
		if (textureImage == null) {
		    vertex[R] = fillR;
		    vertex[G] = fillG;
		    vertex[B] = fillB;
		    vertex[A] = fillA;
		}
		else {
		    if (tint) {
			vertex[R] = tintR;
			vertex[G] = tintG;
			vertex[B] = tintB;
			vertex[A] = tintA;
		    }
		    else {
			vertex[R] = 1;
			vertex[G] = 1;
			vertex[B] = 1;
			vertex[A] = 1;
		    }
		}
		vertex[AR] = ambientR;
		vertex[AG] = ambientG;
		vertex[AB] = ambientB;
		vertex[SPR] = specularR;
		vertex[SPG] = specularG;
		vertex[SPB] = specularB;
		//vertex[SPA] = specularA;
		vertex[SHINE] = shininess;
		vertex[ER] = emissiveR;
		vertex[EG] = emissiveG;
		vertex[EB] = emissiveB;
	    }
	    if (stroke) {
		vertex[SR] = strokeR;
		vertex[SG] = strokeG;
		vertex[SB] = strokeB;
		vertex[SA] = strokeA;
		vertex[SW] = strokeWeight;
	    }
	    if (textureImage != null) {
		vertex[U] = textureU;
		vertex[V] = textureV;
	    }
	    vertex[NX] = normalX;
	    vertex[NY] = normalY;
	    vertex[NZ] = normalZ;
	    vertex[BEEN_LIT] = 0;
	    vertexCount++;
	}
	else{ super.vertex(x,y,z); }
    }
    
    
    /*
    // for debug
    protected void render(){
	//IOut.err("point="+super.pointCount+", line="+super.lineCount+", triangle="+super.triangleCount);
	super.render();
    }
    // for debug
    protected void sort(){
	//IOut.err();
	//IOut.err("point="+super.pointCount+", line="+super.lineCount+", triangle="+super.triangleCount);
	super.sort();
    }
    // debug
    protected void endShapeStroke(int mode) {
	//IOut.err();
	switch (shape) {
	case POLYGON:
	    {
		IOut.err("shapeFirst="+shapeFirst+", shapeLast="+shapeLast); //
		// store index of first vertex
		int stop = shapeLast - 1;
		addLineBreak();
		for (int i = shapeFirst; i < stop; i++) { addLine(i, i+1); }
		if (mode == CLOSE) {
		    // draw the last line connecting back to the first point in poly
		    addLine(stop, shapeFirst); //lines[first][VERTEX1]);
		}
	    }
	    break;
	}
    }
    // debug
    protected void addLine(int a, int b) { addLineWithClip2(a, b); }
    protected final void addLineWithClip2(int a, int b) {
	IOut.err();
	float az = vertices[a][VZ];
	float bz = vertices[b][VZ];
	if (az > cameraNear) {
	    if (bz > cameraNear) {
		IOut.err("bz > cameraNear!!!!");
		return;
	    }
	    IOut.err("interpolate");
	    int cb = interpolateClipVertex(a, b);
	    addLineWithoutClip(cb, b);
	    return;
	}
	else {
	    if (bz <= cameraNear) {
		//IOut.err("normal"); //
		addLineWithoutClip(a, b);
		return;
	    }
	    IOut.err("interpolate");
	    int cb = interpolateClipVertex(a, b);
	    addLineWithoutClip(a, cb);
	    return;
	}
    }
    */
    
    
    

    /*******************************************************************************************
     * implementation of IGraphics3D
     *******************************************************************************************/
    
    public IGraphicMode.GraphicType type(){ return IGraphicMode.GraphicType.P3D; }
    
    public IView view(){ return view; }
    
    public PImage createBGImage(int w, int h, IView v){
	
	PImage bgimg = new PImage(w,h);
	float[][][] cl = new float[2][2][3];
	for(int i=0; i<2; i++){
	    for(int j=0; j<2; j++){
		cl[i][j][0] = v.bgColor[i][j].getRed();
		cl[i][j][1] = v.bgColor[i][j].getGreen();
		cl[i][j][2] = v.bgColor[i][j].getBlue();
	    }
	}
	for(int j=0; j<h; j++){
	    float jr = (float)(h-j)/h;
	    for(int i=0; i<w; i++){
		float ir = (float)(w-i)/w;
		int red = (int)(jr*(ir*cl[0][0][0] + (1-ir)*cl[1][0][0]) +
				(1-jr)*(ir*cl[0][1][0] + (1-ir)*cl[1][1][0]));
		int green = (int)(jr*(ir*cl[0][0][1] + (1-ir)*cl[1][0][1]) +
				  (1-jr)*(ir*cl[0][1][1] + (1-ir)*cl[1][1][1]));
		int blue = (int)(jr*(ir*cl[0][0][2] + (1-ir)*cl[1][0][2]) +
				 (1-jr)*(ir*cl[0][1][2] + (1-ir)*cl[1][1][2]));
		bgimg.set(i,j, 0xFF<<24|(red&0xFF)<<16|(green&0xFF)<<8|(blue&0xFF));
	    }
	}
	return bgimg;
	
	
	/*
	PGraphics g = new PGraphics2D();
	g.setSize(w,h);
	g.beginDraw();
	
	// image drawn by QUAD
	g.noStroke();
	g.beginShape(QUADS);
	Color c = v.bgColor[0][0];
	g.fill(c.getRed(),c.getGreen(),c.getBlue());
	g.vertex(0,0);
	c = v.bgColor[1][0];
	g.fill(c.getRed(),c.getGreen(),c.getBlue());
	g.vertex(w,0);
	c = v.bgColor[1][1];
	g.fill(c.getRed(),c.getGreen(),c.getBlue());
	g.vertex(w,h);
	c = v.bgColor[0][1];
	g.fill(c.getRed(),c.getGreen(),c.getBlue());
	g.vertex(0,h);
	g.endShape();
	
	g.endDraw();
	
	return g.get();
	*/
    }
    
    public void drawBG(IView view){
	
	IG.p("firstDraw = "+firstDraw);
	//IG.p("clearBG = "+IConfig.clearBG);
	//IG.p("x = "+screenX+", y = "+screenY+", w = "+super.width+", h = "+super.height); //
	
	if(view.bgColor!=null){
	    // modelview?
	    // projection?
	    // depth test?
	    
	    boolean createImg=false;
	    
	    for(int i=0; i<2; i++){
		for(int j=0; j<2; j++){
		    if(bgColorCache[i][i] != view.bgColor[i][j]){
			bgColorCache[i][j] = view.bgColor[i][j];
			createImg=true;
		    }
		}
	    }
	    if(bgImage==null||createImg||
	       bgImage.width<super.width||bgImage.height<super.height) // added 20120915
		bgImage = createBGImage(super.width,super.height,view); 
	    
	    //try{
	    background(bgImage);
	    
	    //backgroundImpl(bgImage);
	    //}catch(Exception e){
	    //	IG.err("bgImage w = "+bgImage.width+" h = "+bgImage.height); //
	    //	e.printStackTrace();
	    //}
	    
	    /*
	    //super.resetMatrix(); // ?
	    super.ortho();
	    super.noStroke();
	    super.beginShape(QUADS);
	    clr(view.bgColor[0][0]);
	    super.vertex(0,0,-100); //vertex(-0.5f,-1);
	    clr(view.bgColor[1][0]);
	    super.vertex(width,0,-100); //vertex(-0.5f,-1);
	    clr(view.bgColor[1][1]);
	    super.vertex(width,height,-100);
	    clr(view.bgColor[0][1]);
	    super.vertex(0,height,-100);
	    super.endShape();
	    */
	    
	    /*
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glPushMatrix();
	    gl.glLoadIdentity();
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glPushMatrix();
	    gl.glLoadIdentity();
	    gl.glDisable(GL.GL_DEPTH_TEST);
	    //if(mode.isLight()) gl.glDisable(GL.GL_LIGHTING);
	    gl.glBegin(GL.GL_QUADS);
	    gl.glColor3dv(bgColor[0][1],0);
	    gl.glVertex3d(-1.,-1.,0);
	    gl.glColor3dv(bgColor[1][1],0);
	    gl.glVertex3d(1.,-1.,0);
	    gl.glColor3dv(bgColor[1][0],0);
	    gl.glVertex3d(1.,1.,0);
	    gl.glColor3dv(bgColor[0][0],0);
	    gl.glVertex3d(-1.,1.,0);
	    gl.glEnd();
	    gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glPopMatrix();
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glPopMatrix();
	    */
	}
	
    }
    
    
    public void drawView(IView view){
	
	if(IConfig.clearBG || firstDraw){
	    drawBG(view);
	}
	
	viewLocation = view.location();
	viewDirection = view.frontDirection();
	
       // default light
        if(view.mode.isLight()){
	    super.lights();
        }
	
	if(!overrideDepthSort){
	    if(IConfig.depthSort!=enableDepthSort){
		if(IConfig.depthSort){
		    enableDepthSort=true;
		    super.hint(ENABLE_DEPTH_SORT);
		}
		else{
		    enableDepthSort=false;
		    super.hint(DISABLE_DEPTH_SORT);
		}
	    }
	}
	
	if(IConfig.smoothGraphicP3D){
	    super.smooth(); // this makes perspective close up very very heavy.
	}
	
	//super.resetMatrix();
	
	// not sure why this is necessary. but it works.
	super.translate(view.screenWidth/2, view.screenHeight/2, 0);
	
	//IG.p("view w = "+view.screenWidth + ", h = "+view.screenHeight); //
	//IG.p("pane w = "+this.getWidth() + ", h = "+this.getHeight()); //
	
	
	super.scale(1,-1,1);
	
        if(view.axonometric){
            double glWidth = view.screenWidth*view.axonRatio;
            double glHeight = view.screenHeight*view.axonRatio;
	    super.ortho((float)-glWidth/2,(float)glWidth/2,(float)-glHeight/2,(float)glHeight/2,
			(float)view.near,(float)view.far);
        }
        else{
	    super.scale(view.screenHeight);
	    
	    double glHeight = view.near*view.persRatio*2;
            double glWidth = glHeight*view.screenWidth/view.screenHeight;
            super.frustum((float)-glWidth/2,(float)glWidth/2,(float)-glHeight/2,(float)glHeight/2,
			  (float)view.near,(float)view.far);
        }
	
	
	super.applyMatrix((float)view.transformArray[0], (float)view.transformArray[4],
			  (float)view.transformArray[8], (float)view.transformArray[12],
			  (float)view.transformArray[1], (float)view.transformArray[5],
			  (float)view.transformArray[9], (float)view.transformArray[13],
			  (float)view.transformArray[2], (float)view.transformArray[6],
			  (float)view.transformArray[10], (float)view.transformArray[14],
			  (float)view.transformArray[3], (float)view.transformArray[7],
			  (float)view.transformArray[11], (float)view.transformArray[15]);
	
    }
    
    
    // draw each view 
    public synchronized void draw(ArrayList<IGraphicI> objects, IView v){

	view = v;
	if(view.hide) return;
	
	/*
	// test
	ArrayList<IGraphicI> obj = new ArrayList<IGraphicI>();
	for(int i=0; i<IConfig.maxObjectNumberForDepthSort&& i<objects.size(); i++){
	    obj.add(objects.get(i));
	}
	objects = obj;
	*/
	
	// check object num to determin depth sort
	if(enableDepthSort){
	    if(objects!=null &&
	       IConfig.maxObjectNumberForDepthSort>0 &&
	       objects.size() > IConfig.maxObjectNumberForDepthSort){
		if(!overrideDepthSort){
		    overrideDepthSort=true;
		    enableDepthSort=false;
		    super.hint(DISABLE_DEPTH_SORT);
		    IOut.err("number of graphic objects ("+objects.size()+") exceeds IConfig.maxObjectNumberForDepthSort ("+IConfig.maxObjectNumberForDepthSort+")");
		    IOut.err("depth sort is disalbed"); 
		}
	    }
	    else if(IConfig.maxObjectNumberForDepthSort>0){
		if(overrideDepthSort){ overrideDepthSort=false; }
	    }
	}
	
	super.beginDraw();
	
	drawView(view);
	
	IG.err("drawing "+objects.size() +" objects");
	
	if(objects!=null)
	    //for(int i=0; i<objects.size(); i++)
	    for(int i=objects.size()-1; i>=0; i--)
		if(objects.get(i).isVisible()) objects.get(i).draw(this);
	
	if(view.mode().isLight()){ disableLight(); }
	
	/*
	if(g!=null){ // overlay
	    // draw here?
	    // border
	    if(view.pane!=null && 
	       view.pane.borderWidth>0&&
	       (view.pane.parent.width!=view.pane.width ||view.pane.parent.height!=view.pane.height)){
		g.setColor(view.pane.borderColor);
		g.setStroke(view.pane.borderStroke);
		//g.drawRect(view.x,view.y,view.width-1,view.height-1);
		g.drawRect(view.pane.x,view.pane.y,view.pane.width,view.pane.height);
	    }
	}
	*/
	
	super.endDraw();
    }
    
    public boolean firstDraw(){ return firstDraw; }
    public void firstDraw(boolean f){
	firstDraw=f;
	/*
	if(subGraphics!=null){
	    for(int i=0; i<subGraphics.length; i++){
		for(int j=0; j<subGraphics[i].length; j++){
		    if(subGraphics[i][j]!=this){
			subGraphics[i][j].firstDraw(f);
		    }
		}
	    }
	}
	*/
    }
    
    
    /** default color mode for IG */
    public boolean isDefaultColorMode(){
	return super.colorMode == RGB &&
	    super.colorModeX == 255 &&
	    super.colorModeY == 255 &&
	    super.colorModeZ == 255 &&
	    super.colorModeA == 255 ;
	/*
	return super.colorMode == RGB &&
	    super.colorModeX == 1f &&
	    super.colorModeY == 1f &&
	    super.colorModeZ == 1f &&
	    super.colorModeA == 1f ;
	*/
    }
    
    public void pushColorMode(){
	origColorMode = super.colorMode;
	origColorModeX = super.colorModeX;
	origColorModeY = super.colorModeY;
	origColorModeZ = super.colorModeZ;
	origColorModeA = super.colorModeA;
    }
    public void popColorMode(){
	super.colorMode(origColorMode,origColorModeX,origColorModeY,origColorModeZ,origColorModeA);
    }
    public void setColorMode(){
	colorMode(RGB, 255);
    }
    
    public void clr(IColor c){ this.clr(c.red(),c.green(),c.blue(),c.alpha()); }
    /* float 0 - 1 */
    public void clr(float r, float g, float b){ this.clr(r,g,b,1f); }
    /* float 0 - 1 */
    public void clr(float r, float g, float b, float a){
	//IG.err(r+","+g+","+b+","+a); //
	
	if(keepColorMode && !isDefaultColorMode()){
	    pushColorMode();
	    setColorMode();
	    super.fill(r*255,g*255,b*255,a*255);
	    popColorMode();
	}
	else{ super.fill(r*255,g*255,b*255,a*255); }
    }
    /* float 0 - 1 */
    public void clr(float[] rgba){ this.clr(rgba[0],rgba[1],rgba[2],rgba[3]); }
    /** alpha : 0-1 */
    public void clr(IColor c, float alpha){
	this.clr(c.red(),c.green(),c.blue(),alpha);
    }
    
    public void stroke(IColor c){ this.stroke(c.red(),c.green(),c.blue(),c.alpha()); }
    public void stroke(float r, float g, float b, float a){
	if(keepColorMode && !isDefaultColorMode()){
	    pushColorMode();
	    setColorMode();
	    super.stroke(r*255,g*255,b*255,a*255);
	    popColorMode();
	}
	else{ super.stroke(r*255,g*255,b*255,a*255); }
    }
    public void stroke(float r, float g, float b){ this.stroke(r,g,b,1f); }
    public void stroke(float[] rgba){ this.stroke(rgba[0],rgba[1],rgba[2],rgba[3]); }
    public void stroke(IColor c, float alpha){ this.stroke(c.red(),c.green(),c.blue(),alpha); }
    
    public void weight(float w){ /*this.weight=w;*/ super.strokeWeight(w); }
    
    public void diffuse(float r, float g, float b, float a){
	clr(r,g,b,a);
    }
    
    public void diffuse(float r, float g, float b){ this.diffuse(r,g,b,1f); }
    public void diffuse(IColor c){
	this.diffuse(c.red(), c.green(), c.blue(), c.alpha());
    }
    public void diffuse(IColor c,float alpha){
	this.diffuse(c.red(), c.green(), c.blue(), alpha);
    }
    public void diffuse(float[] rgba){
	this.diffuse(rgba[0], rgba[1], rgba[2], rgba[3]);
    }
    
    
    public void ambient(float r, float g, float b, float a){
	if(keepColorMode && !isDefaultColorMode()){
	    pushColorMode();
	    setColorMode();
	    super.ambient(r*255,g*255,b*255);
	    popColorMode();
	}
	else{ super.ambient(r*255,g*255,b*255); }
    }
    public void ambient(float r, float g, float b){ this.ambient(r,g,b,1f); }
    public void ambient(float[] rgba){ this.ambient(rgba[0],rgba[1],rgba[2],rgba[3]); }
    public void ambient(IColor c){ this.ambient(c.red(),c.green(),c.blue(),c.alpha()); }
    public void ambient(IColor c, float alpha){ this.ambient(c.red(),c.green(),alpha); }
    
    public void specular(float r, float g, float b, float a){
	if(keepColorMode && !isDefaultColorMode()){
	    pushColorMode();
	    setColorMode();
	    super.specular(r*255,g*255,b*255);
	    popColorMode();
	}
	else{ super.specular(r*255,g*255,b*255); }
    }
    public void specular(float r, float g, float b){ this.specular(r,g,b,1f); }
    public void specular(float[] rgba){ this.specular(rgba[0],rgba[1],rgba[2],rgba[3]); }
    public void specular(IColor c){ specular(c.red(),c.green(),c.blue(),c.alpha()); }
    public void specular(IColor c, float alpha){ specular(c.red(),c.green(),c.blue(),alpha); }
    
    public void emissive(float r, float g, float b, float a){
	if(keepColorMode && !isDefaultColorMode()){
	    pushColorMode();
	    setColorMode();
	    super.emissive(r*255,g*255,b*255);
	    popColorMode();
	}
	else{ super.emissive(r*255,g*255,b*255); }
    }
    public void emissive(float r, float g, float b){ this.emissive(r,g,b,1f); }
    public void emissive(float[] rgba){ this.emissive(rgba[0],rgba[1],rgba[2],rgba[3]); }
    public void emissive(IColor c){
	emissive(c.red(),c.green(),c.blue(),c.alpha());
    }
    public void emissive(IColor c, float alpha){
	emissive(c.red(),c.green(),c.blue(),alpha);
    }
    
    public void shininess(float s){ super.shininess(s); }
    
    public void enableLight(){ super.lights(); }
    public void disableLight(){ super.noLights(); }
    
    
    //public void pointSize(float sz){ pointSize=sz; }
    public void pointSize(float sz){ weight(sz); } // point is drawn as line
    
    
    public boolean isInFront(IVec p){
	return true;  // debug
	//return p.dif(viewLocation).dot(viewDirection)>0;
    }
    
    
    public void vertex(IVec p){
	vertex((float)p.x, (float)p.y, (float)p.z);
    }
    
    public void vertex(IVec[] p){
	for(int i=0; i<p.length; i++){
	    vertex((float)p[i].x, (float)p[i].y, (float)p[i].z);
	}
    }
    
    public void vertex(IVec[] p, IVec[] n){
	for(int i=0; i<p.length; i++){
	    super.normal((float)n[i].x, (float)n[i].y, (float)n[i].z);
	    vertex((float)p[i].x, (float)p[i].y, (float)p[i].z);
	}
    }
    
    public void normal(IVec n){
	super.normal((float)n.x, (float)n.y, (float)n.z);
    }
    
    public void drawPoint(IVec p){
	super.stroke=true;
	super.fill=false;
	
	super.beginShape(POINTS); // use POINTS for processing 2.0
	//super.beginShape(LINE); // use lines. POINTS in P3D has no control on size.
	//super.beginShape(LINE_STRIP); // use lines. POINTS in P3D has no control on size.
	
	if(IConfig.cullVertexBehindViewInP3D){
	    if(isInFront(p)){ vertex(p); /*vertex(p);*/ }
	}
	else{ vertex(p); /*vertex(p);*/ }
	super.endShape();
	
	/*
	if(IConfig.cullVertexBehindViewInP3D){
	    if(isInFront(p)){
		super.beginShape(POINTS);
		vertex(p);
		super.endShape();
		//super.strokeCap(ROUND); //ROUND doesn't work in P3D
		//drawLines(new IVec[]{p, p});
	    }
	}
	else{
	    super.beginShape(POINTS);
	    vertex(p);
	    super.endShape();
	}
	*/
    }
    
    public void drawPoints(IVec[] p){
	super.stroke=true;
	super.fill=false;
	super.beginShape(POINTS); // use POINTS for processing 2.0
	//super.beginShape(LINES); // use lines. POINTS in P3D has no control on size.
	//super.beginShape(LINE_STRIP); // use lines. POINTS in P3D has no control on size.
	for(int i=0; i<p.length; i++){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i])){ vertex(p[i]); /*vertex(p[i]);*/ }
	    }
	    else{ vertex(p[i]); /*vertex(p[i]);*/ }
	}
	super.endShape();
	
	/*
	super.beginShape(POINTS);
	for(int i=0; i<p.length; i++){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i])){ vertex(p); }
	    }
	    else{ vertex(p); }
	}
	//vertex(p);
	super.endShape();
	*/
    }
    public void drawLines(IVec[] p){
	super.stroke=true;
	super.fill=false;
	super.beginShape(LINES);
	//vertex(p);
	for(int i=0; i<p.length-1; i+=2){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i]) && isInFront(p[i+1])){
		    vertex(p[i]);
		    vertex(p[i+1]);
		}
	    }
	    else{
		vertex(p[i]);
		vertex(p[i+1]);
	    }
	}
	super.endShape();
    }
    public void drawLineStrip(IVec[] p){
	super.stroke=true;
	super.fill=false;
	
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean previousFront=false;
	    boolean isDrawing=false;
	    for(int i=0; i<p.length; i++){
		boolean currentFront = isInFront(p[i]);
		if(currentFront){
		    if(isDrawing){ vertex(p[i]); }
		    else{
			//super.beginShape(POLYGON);
			super.beginShape(LINE_STRIP);
			vertex(p[i]);
			isDrawing=true;
		    }
		}
		else{
		    if(isDrawing){
			//if(previousFront){ vertex(p[i]); }
			//else{ super.endShape(); isDrawing=false; }
			super.endShape(); isDrawing=false;
		    }
		}
		previousFront = currentFront;
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    //super.beginShape(POLYGON);
	    super.beginShape(LINE_STRIP);
	    for(int i=0; i<p.length; i++){ vertex(p[i]); }
	    //vertex(p);
	    super.endShape();
	}
    }
    
    public void drawLineLoop(IVec[] p){
	super.stroke=true;
	super.fill=false;
	
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean[] isFront = new boolean[p.length];
	    boolean isAnyBehind = false;
	    for(int i=0; i<p.length; i++){
		isFront[i] = isInFront(p[i]);
		if(!isFront[i]){ isAnyBehind=true; }
	    }
	    
	    if(isAnyBehind){ // line strip
		
		boolean previousFront=false;
		boolean isDrawing=false;
		for(int i=0; i<=p.length; i++){
		    boolean currentFront = isFront[i%p.length];
		    if(currentFront){
			if(isDrawing){ vertex(p[i%p.length]); }
			else{
			    super.beginShape(POLYGON);
			    vertex(p[i%p.length]);
			    isDrawing=true;
			}
		    }
		    else{
			if(isDrawing){
			    //if(previousFront){ vertex(p[i%p.length]); } 
			    //else{ super.endShape(); isDrawing=false; }
			    // simply end is fine.
			    super.endShape(); isDrawing=false; 
			}
		    }
		    previousFront = currentFront;
		}
		if(isDrawing){ super.endShape(); }
		
	    }
	    else{
		super.beginShape(POLYGON);
		for(int i=0; i<p.length; i++){ vertex(p[i]); }
		super.endShape(CLOSE);
	    }
	}
	else{
	    super.beginShape(POLYGON);
	    for(int i=0; i<p.length; i++){ vertex(p[i]); }
	    super.endShape(CLOSE);
	}
    }
    public void drawPolygon(IVec[] p){
	super.stroke= false;
	super.fill = true;
	super.beginShape(POLYGON);
	for(int i=0; i<p.length; i++){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i])){ vertex(p[i]); }
	    }
	    else{ vertex(p[i]); }
	}
	//vertex(p);
	super.endShape(CLOSE);
    }
    public void drawPolygon(IVec[] p, IVec[] n){
	super.stroke= false;
	super.fill = true;
	super.beginShape(POLYGON);
	for(int i=0; i<p.length; i++){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i])){ normal(n[i]); vertex(p[i]); }
	    }
	    else{ normal(n[i]); vertex(p[i]); }
	}
	//vertex(p,n);
	super.endShape(CLOSE);
    }
    public void drawPolygon(IVec[] p, IColor clr[], float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	super.beginShape(POLYGON);
	if(light){
	    if(alpha<0){
		for(int i=0; i<p.length; i++){
		    ambient(clr[i]);
		    diffuse(clr[i]);
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i])){ vertex(p[i]); }
		    }
		    else{ vertex(p[i]); }
		}
	    }
	    else{
		for(int i=0; i<p.length; i++){
		    ambient(clr[i], alpha);
		    diffuse(clr[i], alpha);
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i])){ vertex(p[i]); }
		    }
		    else{ vertex(p[i]); }
		}
	    }
	}
	else{
	    if(alpha<0){
		for(int i=0; i<p.length; i++){
		    clr(clr[i]);
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i])){ vertex(p[i]); }
		    }
		    else{ vertex(p[i]); }
		}
	    }
	    else{
		for(int i=0; i<p.length; i++){
		    clr(clr[i], alpha);
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i])){ vertex(p[i]); }
		    }
		    else{ vertex(p[i]); }
		}
	    }
	}
	super.endShape(CLOSE);
    }
    public void drawPolygon(IVec[] p, IVec[] n, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	super.beginShape(POLYGON);
	if(light){
	    if(alpha<0){
		for(int i=0; i<p.length; i++){
		    ambient(clr[i]);
		    diffuse(clr[i]);
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i])){ normal(n[i]); vertex(p[i]); }
		    }
		    else{ normal(n[i]); vertex(p[i]); }
		}
	    }
	    else{
		for(int i=0; i<p.length; i++){
		    ambient(clr[i], alpha);
		    diffuse(clr[i], alpha);
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i])){ normal(n[i]); vertex(p[i]); }
		    }
		    else{ normal(n[i]); vertex(p[i]); }
		}
	    }
	}
	else{
	    if(alpha<0){
		for(int i=0; i<p.length; i++){
		    clr(clr[i]);
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i])){ normal(n[i]); vertex(p[i]); }
		    }
		    else{ normal(n[i]); vertex(p[i]); }
		}
	    }
	    else{
		for(int i=0; i<p.length; i++){
		    clr(clr[i], alpha);
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i])){ normal(n[i]); vertex(p[i]); }
		    }
		    else{ normal(n[i]); vertex(p[i]); }
		}
	    }
	}
	super.endShape(CLOSE);
    }
    public void drawQuads(IVec[] p){
	super.stroke= false;
	super.fill = true;
	super.beginShape(QUADS);
	for(int i=0; i<p.length-3; i+=4){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i]) && isInFront(p[i+1]) &&
		   isInFront(p[i+2]) && isInFront(p[i+3])){
		    vertex(p[i]);
		    vertex(p[i+1]);
		    vertex(p[i+2]);
		    vertex(p[i+3]);
		}
	    }
	    else{
		vertex(p[i]);
		vertex(p[i+1]);
		vertex(p[i+2]);
		vertex(p[i+3]);
	    }
	}
	super.endShape();
    }
    public void drawQuads(IVec[] p, IVec[] n){
	super.stroke= false;
	super.fill = true;
	super.beginShape(QUADS);
	for(int i=0; i<p.length-3; i+=4){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i]) && isInFront(p[i+1]) &&
		   isInFront(p[i+2]) && isInFront(p[i+3])){
		    normal(n[i]); vertex(p[i]);
		    normal(n[i+1]); vertex(p[i+1]);
		    normal(n[i+2]); vertex(p[i+2]);
		    normal(n[i+3]); vertex(p[i+3]);
		}
	    }
	    else{
		normal(n[i]); vertex(p[i]);
		normal(n[i+1]); vertex(p[i+1]);
		normal(n[i+2]); vertex(p[i+2]);
		normal(n[i+3]); vertex(p[i+3]);
	    }
	}
	//vertex(p,n);
	super.endShape();
    }
    public void drawQuads(IVec[] p, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	super.beginShape(QUADS);
	if(light){
	    if(alpha<0){
		for(int i=0; i<p.length-3; i+=4){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) &&
			   isInFront(p[i+2]) && isInFront(p[i+3])){
			    ambient(clr[i]);
			    diffuse(clr[i]);
			    vertex(p[i]);
			    ambient(clr[i+1]);
			    diffuse(clr[i+1]);
			    vertex(p[i+1]);
			    ambient(clr[i+2]);
			    diffuse(clr[i+2]);
			    vertex(p[i+2]);
			    ambient(clr[i+3]);
			    diffuse(clr[i+3]);
			    vertex(p[i+3]);
			}
		    }
		    else{
			ambient(clr[i]);
			diffuse(clr[i]);
			vertex(p[i]);
			ambient(clr[i+1]);
			diffuse(clr[i+1]);
			vertex(p[i+1]);
			ambient(clr[i+2]);
			diffuse(clr[i+2]);
			vertex(p[i+2]);
			ambient(clr[i+3]);
			diffuse(clr[i+3]);
			vertex(p[i+3]);
		    }
		}
	    }
	    else{
		for(int i=0; i<p.length-3; i+=4){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) &&
			   isInFront(p[i+2]) && isInFront(p[i+3])){
			    ambient(clr[i], alpha);
			    diffuse(clr[i], alpha);
			    vertex(p[i]);
			    ambient(clr[i+1], alpha);
			    diffuse(clr[i+1], alpha);
			    vertex(p[i+1]);
			    ambient(clr[i+2], alpha);
			    diffuse(clr[i+2], alpha);
			    vertex(p[i+2]);
			    ambient(clr[i+3], alpha);
			    diffuse(clr[i+3], alpha);
			    vertex(p[i+3]);
			}
		    }
		    else{
			ambient(clr[i], alpha);
			diffuse(clr[i], alpha);
			vertex(p[i]);
			ambient(clr[i+1], alpha);
			diffuse(clr[i+1], alpha);
			vertex(p[i+1]);
			ambient(clr[i+2], alpha);
			diffuse(clr[i+2], alpha);
			vertex(p[i+2]);
			ambient(clr[i+3], alpha);
			diffuse(clr[i+3], alpha);
			vertex(p[i+3]);
		    }
		}
	    }
	}
	else{
	    if(alpha<0){
		for(int i=0; i<p.length-3; i+=4){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) &&
			   isInFront(p[i+2]) && isInFront(p[i+3])){
			    clr(clr[i]);
			    vertex(p[i]);
			    clr(clr[i+1]);
			    vertex(p[i+1]);
			    clr(clr[i+2]);
			    vertex(p[i+2]);
			    clr(clr[i+3]);
			    vertex(p[i+3]);
			}
		    }
		    else{
			clr(clr[i]);
			vertex(p[i]);
			clr(clr[i+1]);
			vertex(p[i+1]);
			clr(clr[i+2]);
			vertex(p[i+2]);
			clr(clr[i+3]);
			vertex(p[i+3]);
		    }
		}
	    }
	    else{
		for(int i=0; i<p.length-3; i+=4){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) &&
			   isInFront(p[i+2]) && isInFront(p[i+3])){
			    clr(clr[i], alpha);
			    vertex(p[i]);
			    clr(clr[i+1], alpha);
			    vertex(p[i+1]);
			    clr(clr[i+2], alpha);
			    vertex(p[i+2]);
			    clr(clr[i+3], alpha);
			    vertex(p[i+3]);
			}
		    }
		    else{
			clr(clr[i], alpha);
			vertex(p[i]);
			clr(clr[i+1], alpha);
			vertex(p[i+1]);
			clr(clr[i+2], alpha);
			vertex(p[i+2]);
			clr(clr[i+3], alpha);
			vertex(p[i+3]);
		    }
		}
	    }
	}
	super.endShape();
    }
    public void drawQuads(IVec[] p, IVec[] n, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	super.beginShape(QUADS);
	if(light){
	    if(alpha<0){
		for(int i=0; i<p.length-3; i+=4){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) &&
			   isInFront(p[i+2]) && isInFront(p[i+3])){
			    ambient(clr[i]); diffuse(clr[i]); normal(n[i]); vertex(p[i]);
			    ambient(clr[i+1]); diffuse(clr[i+1]); normal(n[i+1]); vertex(p[i+1]);
			    ambient(clr[i+2]); diffuse(clr[i+2]); normal(n[i+2]); vertex(p[i+2]);
			    ambient(clr[i+3]); diffuse(clr[i+3]); normal(n[i+3]); vertex(p[i+3]);
			}
		    }
		    else{
			ambient(clr[i]); diffuse(clr[i]); normal(n[i]); vertex(p[i]);
			ambient(clr[i+1]); diffuse(clr[i+1]); normal(n[i+1]); vertex(p[i+1]);
			ambient(clr[i+2]); diffuse(clr[i+2]); normal(n[i+2]); vertex(p[i+2]);
			ambient(clr[i+3]); diffuse(clr[i+3]); normal(n[i+3]); vertex(p[i+3]);
		    }
		}
	    }
	    else{
		for(int i=0; i<p.length-3; i+=4){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) &&
			   isInFront(p[i+2]) && isInFront(p[i+3])){
			    ambient(clr[i],alpha); diffuse(clr[i],alpha); normal(n[i]); vertex(p[i]);
			    ambient(clr[i+1],alpha); diffuse(clr[i+1],alpha); normal(n[i+1]); vertex(p[i+1]);
			    ambient(clr[i+2],alpha); diffuse(clr[i+2],alpha); normal(n[i+2]); vertex(p[i+2]);
			    ambient(clr[i+3],alpha); diffuse(clr[i+3],alpha); normal(n[i+3]); vertex(p[i+3]);
			}
		    }
		    else{
			ambient(clr[i],alpha); diffuse(clr[i],alpha); normal(n[i]); vertex(p[i]);
			ambient(clr[i+1],alpha); diffuse(clr[i+1],alpha); normal(n[i+1]); vertex(p[i+1]);
			ambient(clr[i+2],alpha); diffuse(clr[i+2],alpha); normal(n[i+2]); vertex(p[i+2]);
			ambient(clr[i+3],alpha); diffuse(clr[i+3],alpha); normal(n[i+3]); vertex(p[i+3]);
		    }
		}
	    }
	}
	else{
	    if(alpha<0){
		for(int i=0; i<p.length-3; i+=4){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) &&
			   isInFront(p[i+2]) && isInFront(p[i+3])){
			    clr(clr[i]); normal(n[i]); vertex(p[i]);
			    clr(clr[i+1]); normal(n[i+1]); vertex(p[i+1]);
			    clr(clr[i+2]); normal(n[i+2]); vertex(p[i+2]);
			    clr(clr[i+3]); normal(n[i+3]); vertex(p[i+3]);
			}
		    }
		    else{
			clr(clr[i]); normal(n[i]); vertex(p[i]);
			clr(clr[i+1]); normal(n[i+1]); vertex(p[i+1]);
			clr(clr[i+2]); normal(n[i+2]); vertex(p[i+2]);
			clr(clr[i+3]); normal(n[i+3]); vertex(p[i+3]);
		    }
		}
	    }
	    else{
		for(int i=0; i<p.length-3; i+=4){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) &&
			   isInFront(p[i+2]) && isInFront(p[i+3])){
			    clr(clr[i],alpha); normal(n[i]); vertex(p[i]);
			    clr(clr[i+1],alpha); normal(n[i+1]); vertex(p[i+1]);
			    clr(clr[i+2],alpha); normal(n[i+2]); vertex(p[i+2]);
			    clr(clr[i+3],alpha); normal(n[i+3]); vertex(p[i+3]);
			}
		    }
		    else{
			clr(clr[i],alpha); normal(n[i]); vertex(p[i]);
			clr(clr[i+1],alpha); normal(n[i+1]); vertex(p[i+1]);
			clr(clr[i+2],alpha); normal(n[i+2]); vertex(p[i+2]);
			clr(clr[i+3],alpha); normal(n[i+3]); vertex(p[i+3]);
		    }
		}
	    }
	}
	super.endShape();
    }
    public void drawQuadStrip(IVec[] p){
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean previousFront=false;
	    boolean isDrawing=false;
	    for(int i=0; i<p.length-1; i+=2){
		boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
		if(currentFront){
		    if(isDrawing){
			vertex(p[i]);
			vertex(p[i+1]);
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			vertex(p[i]);
			vertex(p[i+1]);
			isDrawing=true;
		    }
		}
		else if(isDrawing){ super.endShape(); isDrawing=false; }
		previousFront = currentFront;
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    super.beginShape(QUAD_STRIP);
	    for(int i=0; i<p.length; i++){ vertex(p[i]); }
	    super.endShape();
	}
    }
    public void drawQuadStrip(IVec[] p, IVec[] n){
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean previousFront=false;
	    boolean isDrawing=false;
	    for(int i=0; i<p.length-1; i+=2){
		boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
		if(currentFront){
		    if(isDrawing){
			normal(n[i]);
			vertex(p[i]);
			normal(n[i+1]);
			vertex(p[i+1]);
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			normal(n[i]);
			vertex(p[i]);
			normal(n[i+1]);
			vertex(p[i+1]);
			isDrawing=true;
		    }
		}
		else if(isDrawing){ super.endShape(); isDrawing=false; }
		previousFront = currentFront;
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    super.beginShape(QUAD_STRIP);
	    for(int i=0; i<p.length; i++){ normal(n[i]); vertex(p[i]); }
	    super.endShape();
	}
    }
    public void drawQuadStrip(IVec[] p, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean previousFront=false;
	    boolean isDrawing=false;
	    if(light){
		if(alpha<0){
		    for(int i=0; i<p.length-1; i+=2){
			boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
			if(currentFront){
			    if(isDrawing){
				ambient(clr[i]);
				diffuse(clr[i]);
				vertex(p[i]);
				ambient(clr[i+1]);
				diffuse(clr[i+1]);
				vertex(p[i+1]);
			    }
			    else{
				super.beginShape(QUAD_STRIP);
				ambient(clr[i]);
				diffuse(clr[i]);
				vertex(p[i]);
				ambient(clr[i+1]);
				diffuse(clr[i+1]);
				vertex(p[i+1]);
				isDrawing=true;
			    }
			}
			else if(isDrawing){ super.endShape(); isDrawing=false; }
			previousFront = currentFront;
		    }
		}
		else{
		    for(int i=0; i<p.length-1; i+=2){
			boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
			if(currentFront){
			    if(isDrawing){
				ambient(clr[i], alpha);
				diffuse(clr[i], alpha);
				vertex(p[i]);
				ambient(clr[i+1], alpha);
				diffuse(clr[i+1], alpha);
				vertex(p[i+1]);
			    }
			    else{
				super.beginShape(QUAD_STRIP);
				ambient(clr[i], alpha);
				diffuse(clr[i], alpha);
				vertex(p[i]);
				ambient(clr[i+1], alpha);
				diffuse(clr[i+1], alpha);
				vertex(p[i+1]);
				isDrawing=true;
			    }
			}
			else if(isDrawing){ super.endShape(); isDrawing=false; }
			previousFront = currentFront;
		    }
		}
	    }
	    else{
		if(alpha<0){
		    for(int i=0; i<p.length-1; i+=2){
			boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
			if(currentFront){
			    if(isDrawing){
				clr(clr[i]);
				vertex(p[i]);
				clr(clr[i+1]);
				vertex(p[i+1]);
			    }
			    else{
				super.beginShape(QUAD_STRIP);
				clr(clr[i]);
				vertex(p[i]);
				clr(clr[i+1]);
				vertex(p[i+1]);
				isDrawing=true;
			    }
			}
			else if(isDrawing){ super.endShape(); isDrawing=false; }
			previousFront = currentFront;
		    }
		}
		else{
		    for(int i=0; i<p.length-1; i+=2){
			boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
			if(currentFront){
			    if(isDrawing){
				clr(clr[i], alpha);
				vertex(p[i]);
				clr(clr[i+1], alpha);
				vertex(p[i+1]);
			    }
			    else{
				super.beginShape(QUAD_STRIP);
				clr(clr[i], alpha);
				vertex(p[i]);
				clr(clr[i+1], alpha);
				vertex(p[i+1]);
				isDrawing=true;
			    }
			}
			else if(isDrawing){ super.endShape(); isDrawing=false; }
			previousFront = currentFront;
		    }
		}
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    super.beginShape(QUAD_STRIP);
	    if(light){
		if(alpha<0){
		    for(int i=0; i<p.length; i++){
			ambient(clr[i]); diffuse(clr[i]); vertex(p[i]);
		    }
		}
		else{
		    for(int i=0; i<p.length; i++){
			ambient(clr[i], alpha); diffuse(clr[i], alpha); vertex(p[i]);
		    }
		}
	    }
	    else{
		if(alpha<0){
		    for(int i=0; i<p.length; i++){ clr(clr[i]); vertex(p[i]); }
		}
		else{
		    for(int i=0; i<p.length; i++){ clr(clr[i], alpha); vertex(p[i]); }
		}
	    }
	    super.endShape();
	}
    }
    public void drawQuadStrip(IVec[] p, IVec[] n, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean previousFront=false;
	    boolean isDrawing=false;
	    if(light){
		if(alpha<0){
		    for(int i=0; i<p.length-1; i+=2){
			boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
			if(currentFront){
			    if(isDrawing){
				ambient(clr[i]);
				diffuse(clr[i]);
				normal(n[i]);
				vertex(p[i]);
				ambient(clr[i+1]);
				diffuse(clr[i+1]);
				normal(n[i+1]);
				vertex(p[i+1]);
			    }
			    else{
				super.beginShape(QUAD_STRIP);
				ambient(clr[i]);
				diffuse(clr[i]);
				normal(n[i]);
				vertex(p[i]);
				ambient(clr[i+1]);
				diffuse(clr[i+1]);
				normal(n[i+1]);
				vertex(p[i+1]);
				isDrawing=true;
			    }
			}
			else if(isDrawing){ super.endShape(); isDrawing=false; }
			previousFront = currentFront;
		}
		}
		else{
		    for(int i=0; i<p.length-1; i+=2){
			boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
			if(currentFront){
			    if(isDrawing){
				ambient(clr[i], alpha);
				diffuse(clr[i], alpha);
				normal(n[i]);
				vertex(p[i]);
				ambient(clr[i+1], alpha);
				diffuse(clr[i+1], alpha);
				normal(n[i+1]);
				vertex(p[i+1]);
			    }
			    else{
				super.beginShape(QUAD_STRIP);
				ambient(clr[i], alpha);
				diffuse(clr[i], alpha);
				normal(n[i]);
				vertex(p[i]);
				ambient(clr[i+1], alpha);
				diffuse(clr[i+1], alpha);
				normal(n[i+1]);
				vertex(p[i+1]);
				isDrawing=true;
			    }
			}
			else if(isDrawing){ super.endShape(); isDrawing=false; }
			previousFront = currentFront;
		    }
		}
	    }
	    else{
		if(alpha<0){
		    for(int i=0; i<p.length-1; i+=2){
			boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
			if(currentFront){
			    if(isDrawing){
				clr(clr[i]);
				normal(n[i]);
				vertex(p[i]);
				clr(clr[i+1]);
				normal(n[i+1]);
				vertex(p[i+1]);
			    }
			    else{
				super.beginShape(QUAD_STRIP);
				clr(clr[i]);
				normal(n[i]);
				vertex(p[i]);
				clr(clr[i+1]);
				normal(n[i+1]);
				vertex(p[i+1]);
				isDrawing=true;
			    }
			}
			else if(isDrawing){ super.endShape(); isDrawing=false; }
			previousFront = currentFront;
		    }
		}
		else{
		    for(int i=0; i<p.length-1; i+=2){
			boolean currentFront = isInFront(p[i]) && isInFront(p[i+1]);
			if(currentFront){
			    if(isDrawing){
				clr(clr[i], alpha);
				normal(n[i]);
				vertex(p[i]);
				clr(clr[i+1], alpha);
				normal(n[i+1]);
				vertex(p[i+1]);
			    }
			    else{
				super.beginShape(QUAD_STRIP);
				clr(clr[i], alpha);
				normal(n[i]);
				vertex(p[i]);
				clr(clr[i+1], alpha);
				normal(n[i+1]);
				vertex(p[i+1]);
				isDrawing=true;
			    }
			}
			else if(isDrawing){ super.endShape(); isDrawing=false; }
			previousFront = currentFront;
		    }
		}
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    super.beginShape(QUAD_STRIP);
	    if(alpha<0){
		for(int i=0; i<p.length; i++){ clr(clr[i]); normal(n[i]); vertex(p[i]); }
	    }
	    else{
		for(int i=0; i<p.length; i++){ clr(clr[i],alpha); normal(n[i]); vertex(p[i]); }
	    }
	    super.endShape();
	}
    }
    public void drawQuadMatrix(IVec[][] pts){
	super.stroke= false;
	super.fill = true;
        for(int i=0; i<pts.length-1; i++){
	    
	    if(IConfig.cullVertexBehindViewInP3D){
		boolean previousFront=false;
		boolean isDrawing=false;
		for(int j=0; j<pts[i].length; j++){
		    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
		    if(currentFront){
			if(isDrawing){
			    vertex(pts[i][j]);
			    vertex(pts[i+1][j]);
			}
			else{
			    super.beginShape(QUAD_STRIP);
			    vertex(pts[i][j]);
			    vertex(pts[i+1][j]);
			    isDrawing=true;
			}
		    }
		    else if(isDrawing){ super.endShape(); isDrawing=false; }
		    previousFront = currentFront;
		}
		if(isDrawing){ super.endShape(); }
	    }
	    else{
		super.beginShape(QUAD_STRIP);
		for(int j=0; j<pts[i].length; j++){
		    vertex(pts[i][j]);
		    vertex(pts[i+1][j]);
		}
		super.endShape();
	    }
        }
    }
    public void drawQuadMatrix(IVec[][] pts, IVec[][] nml){
	
	super.stroke= false;
	super.fill = true;
	
        for(int i=0; i<pts.length-1; i++){
	    
	    if(IConfig.cullVertexBehindViewInP3D){
		boolean previousFront=false;
		boolean isDrawing=false;
		for(int j=0; j<pts[i].length; j++){
		    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
		    if(currentFront){
			if(isDrawing){
			    normal(nml[i][j]);
			    vertex(pts[i][j]);
			    normal(nml[i+1][j]);
			    vertex(pts[i+1][j]);
			}
			else{
			    super.beginShape(QUAD_STRIP);
			    normal(nml[i][j]);
			    vertex(pts[i][j]);
			    normal(nml[i+1][j]);
			    vertex(pts[i+1][j]);
			    isDrawing=true;
			}
		    }
		    else if(isDrawing){ super.endShape(); isDrawing=false; }
		    previousFront = currentFront;
		}
		if(isDrawing){ super.endShape(); }
	    }
	    else{
		super.beginShape(QUAD_STRIP);
		for(int j=0; j<pts[i].length; j++){
		    normal(nml[i][j]);
		    vertex(pts[i][j]);
		    normal(nml[i+1][j]);
		    vertex(pts[i+1][j]);
		}
		super.endShape();
	    }
        }
    }
    public void drawQuadMatrix(IVec[][] pts, IColor[][] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	if(light){
	    if(alpha<0){
		for(int i=0; i<pts.length-1; i++){
		    if(IConfig.cullVertexBehindViewInP3D){
			boolean previousFront=false;
			boolean isDrawing=false;
			for(int j=0; j<pts[i].length; j++){
			    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
			    if(currentFront){
				if(isDrawing){
				    ambient(clr[i][j]);
				    diffuse(clr[i][j]);
				    vertex(pts[i][j]);
				    ambient(clr[i+1][j]);
				    diffuse(clr[i+1][j]);
				    vertex(pts[i+1][j]);
				}
				else{
				    super.beginShape(QUAD_STRIP);
				    ambient(clr[i][j]);
				    diffuse(clr[i][j]);
				    vertex(pts[i][j]);
				    ambient(clr[i+1][j]);
				    diffuse(clr[i+1][j]);
				    vertex(pts[i+1][j]);
				    isDrawing=true;
				}
			    }
			    else if(isDrawing){ super.endShape(); isDrawing=false; }
			    previousFront = currentFront;
			}
			if(isDrawing){ super.endShape(); }
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			for(int j=0; j<pts[i].length; j++){
			    ambient(clr[i][j]);
			    diffuse(clr[i][j]);
			    vertex(pts[i][j]);
			    ambient(clr[i+1][j]);
			    diffuse(clr[i+1][j]);
			    vertex(pts[i+1][j]);
			}
			super.endShape();
		    }
		}
	    }
	    else{
		for(int i=0; i<pts.length-1; i++){
		    if(IConfig.cullVertexBehindViewInP3D){
			boolean previousFront=false;
			boolean isDrawing=false;
			for(int j=0; j<pts[i].length; j++){
			    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
			    if(currentFront){
				if(isDrawing){
				    ambient(clr[i][j], alpha);
				    diffuse(clr[i][j], alpha);
				    vertex(pts[i][j]);
				    ambient(clr[i+1][j], alpha);
				    diffuse(clr[i+1][j], alpha);
				    vertex(pts[i+1][j]);
				}
				else{
				    super.beginShape(QUAD_STRIP);
				    ambient(clr[i][j], alpha);
				    diffuse(clr[i][j], alpha);
				    vertex(pts[i][j]);
				    ambient(clr[i+1][j], alpha);
				    diffuse(clr[i+1][j], alpha);
				    vertex(pts[i+1][j]);
				    isDrawing=true;
				}
			    }
			    else if(isDrawing){ super.endShape(); isDrawing=false; }
			    previousFront = currentFront;
			}
			if(isDrawing){ super.endShape(); }
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			for(int j=0; j<pts[i].length; j++){
			    ambient(clr[i][j], alpha);
			    diffuse(clr[i][j], alpha);
			    vertex(pts[i][j]);
			    ambient(clr[i+1][j], alpha);
			    diffuse(clr[i+1][j], alpha);
			    vertex(pts[i+1][j]);
			}
			super.endShape();
		    }
		}
	    }
	}
	else{
	    if(alpha<0){
		for(int i=0; i<pts.length-1; i++){
		    if(IConfig.cullVertexBehindViewInP3D){
			boolean previousFront=false;
			boolean isDrawing=false;
			for(int j=0; j<pts[i].length; j++){
			    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
			    if(currentFront){
				if(isDrawing){
				    clr(clr[i][j]);
				    vertex(pts[i][j]);
				    clr(clr[i+1][j]);
				    vertex(pts[i+1][j]);
				}
				else{
				    super.beginShape(QUAD_STRIP);
				    clr(clr[i][j]);
				    vertex(pts[i][j]);
				    clr(clr[i+1][j]);
				    vertex(pts[i+1][j]);
				    isDrawing=true;
				}
			    }
			    else if(isDrawing){ super.endShape(); isDrawing=false; }
			    previousFront = currentFront;
			}
			if(isDrawing){ super.endShape(); }
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			for(int j=0; j<pts[i].length; j++){
			    clr(clr[i][j]);
			    vertex(pts[i][j]);
			    clr(clr[i+1][j]);
			    vertex(pts[i+1][j]);
			}
			super.endShape();
		    }
		}
	    }
	    else{
		for(int i=0; i<pts.length-1; i++){
		    if(IConfig.cullVertexBehindViewInP3D){
			boolean previousFront=false;
			boolean isDrawing=false;
			for(int j=0; j<pts[i].length; j++){
			    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
			    if(currentFront){
				if(isDrawing){
				    clr(clr[i][j], alpha);
				    vertex(pts[i][j]);
				    clr(clr[i+1][j], alpha);
				    vertex(pts[i+1][j]);
				}
				else{
				    super.beginShape(QUAD_STRIP);
				    clr(clr[i][j], alpha);
				    vertex(pts[i][j]);
				    clr(clr[i+1][j], alpha);
				    vertex(pts[i+1][j]);
				    isDrawing=true;
				}
			    }
			    else if(isDrawing){ super.endShape(); isDrawing=false; }
			    previousFront = currentFront;
			}
			if(isDrawing){ super.endShape(); }
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			for(int j=0; j<pts[i].length; j++){
			    clr(clr[i][j], alpha);
			    vertex(pts[i][j]);
			    clr(clr[i+1][j], alpha);
			    vertex(pts[i+1][j]);
			}
			super.endShape();
		    }
		}
	    }
	}
    }
    public void drawQuadMatrix(IVec[][] pts, IVec[][] nml, IColor[][] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	if(light){
	    if(alpha<0){
		for(int i=0; i<pts.length-1; i++){
		    if(IConfig.cullVertexBehindViewInP3D){
			boolean previousFront=false;
			boolean isDrawing=false;
			for(int j=0; j<pts[i].length; j++){
			    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
			    if(currentFront){
				if(isDrawing){
				    ambient(clr[i][j]);
				    diffuse(clr[i][j]);
				    normal(nml[i][j]);
				    vertex(pts[i][j]);
				    ambient(clr[i+1][j]);
				    diffuse(clr[i+1][j]);
				    normal(nml[i+1][j]);
				    vertex(pts[i+1][j]);
				}
				else{
				    super.beginShape(QUAD_STRIP);
				    ambient(clr[i][j]);
				    diffuse(clr[i][j]);
				    normal(nml[i][j]);
				    vertex(pts[i][j]);
				    ambient(clr[i+1][j]);
				    diffuse(clr[i+1][j]);
				    normal(nml[i+1][j]);
				    vertex(pts[i+1][j]);
				    isDrawing=true;
				}
			    }
			    else if(isDrawing){ super.endShape(); isDrawing=false; }
			    previousFront = currentFront;
			}
			if(isDrawing){ super.endShape(); }
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			for(int j=0; j<pts[i].length; j++){
			    ambient(clr[i][j]);
			    diffuse(clr[i][j]);
			    normal(nml[i][j]);
			    vertex(pts[i][j]);
			    ambient(clr[i+1][j]);
			    diffuse(clr[i+1][j]);
			    normal(nml[i+1][j]);
			    vertex(pts[i+1][j]);
			}
			super.endShape();
		    }
		}
	    }
	    else{
		for(int i=0; i<pts.length-1; i++){
		    if(IConfig.cullVertexBehindViewInP3D){
			boolean previousFront=false;
			boolean isDrawing=false;
			for(int j=0; j<pts[i].length; j++){
			    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
			    if(currentFront){
				if(isDrawing){
				    ambient(clr[i][j], alpha);
				    diffuse(clr[i][j], alpha);
				    normal(nml[i][j]);
				    vertex(pts[i][j]);
				    ambient(clr[i+1][j], alpha);
				    diffuse(clr[i+1][j], alpha);
				    normal(nml[i+1][j]);
				    vertex(pts[i+1][j]);
				}
				else{
				    super.beginShape(QUAD_STRIP);
				    ambient(clr[i][j], alpha);
				    diffuse(clr[i][j], alpha);
				    normal(nml[i][j]);
				    vertex(pts[i][j]);
				    ambient(clr[i+1][j], alpha);
				    diffuse(clr[i+1][j], alpha);
				    normal(nml[i+1][j]);
				    vertex(pts[i+1][j]);
				    isDrawing=true;
				}
			    }
			    else if(isDrawing){ super.endShape(); isDrawing=false; }
			    previousFront = currentFront;
			}
			if(isDrawing){ super.endShape(); }
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			for(int j=0; j<pts[i].length; j++){
			    ambient(clr[i][j], alpha);
			    diffuse(clr[i][j], alpha);
			    normal(nml[i][j]);
			    vertex(pts[i][j]);
			    ambient(clr[i+1][j], alpha);
			    diffuse(clr[i+1][j], alpha);
			    normal(nml[i+1][j]);
			    vertex(pts[i+1][j]);
			}
			super.endShape();
		    }
		}
	    }
	}
	else{
	    if(alpha<0){
		for(int i=0; i<pts.length-1; i++){
		    if(IConfig.cullVertexBehindViewInP3D){
			boolean previousFront=false;
			boolean isDrawing=false;
			for(int j=0; j<pts[i].length; j++){
			    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
			    if(currentFront){
				if(isDrawing){
				    clr(clr[i][j]);
				    normal(nml[i][j]);
				    vertex(pts[i][j]);
				    clr(clr[i+1][j]);
				    normal(nml[i+1][j]);
				    vertex(pts[i+1][j]);
				}
				else{
				    super.beginShape(QUAD_STRIP);
				    clr(clr[i][j]);
				    normal(nml[i][j]);
				    vertex(pts[i][j]);
				    clr(clr[i+1][j]);
				    normal(nml[i+1][j]);
				    vertex(pts[i+1][j]);
				    isDrawing=true;
				}
			    }
			    else if(isDrawing){ super.endShape(); isDrawing=false; }
			    previousFront = currentFront;
			}
			if(isDrawing){ super.endShape(); }
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			for(int j=0; j<pts[i].length; j++){
			    clr(clr[i][j]);
			    normal(nml[i][j]);
			    vertex(pts[i][j]);
			    clr(clr[i+1][j]);
			    normal(nml[i+1][j]);
			    vertex(pts[i+1][j]);
			}
			super.endShape();
		    }
		}
	    }
	    else{
		for(int i=0; i<pts.length-1; i++){
		    if(IConfig.cullVertexBehindViewInP3D){
			boolean previousFront=false;
			boolean isDrawing=false;
			for(int j=0; j<pts[i].length; j++){
			    boolean currentFront = isInFront(pts[i][j]) && isInFront(pts[i+1][j]);
			    if(currentFront){
				if(isDrawing){
				    clr(clr[i][j], alpha);
				    normal(nml[i][j]);
				    vertex(pts[i][j]);
				    clr(clr[i+1][j], alpha);
				    normal(nml[i+1][j]);
				    vertex(pts[i+1][j]);
				}
				else{
				    super.beginShape(QUAD_STRIP);
				    clr(clr[i][j], alpha);
				    normal(nml[i][j]);
				    vertex(pts[i][j]);
				    clr(clr[i+1][j], alpha);
				    normal(nml[i+1][j]);
				    vertex(pts[i+1][j]);
				    isDrawing=true;
				}
			    }
			    else if(isDrawing){ super.endShape(); isDrawing=false; }
			    previousFront = currentFront;
			}
			if(isDrawing){ super.endShape(); }
		    }
		    else{
			super.beginShape(QUAD_STRIP);
			for(int j=0; j<pts[i].length; j++){
			    clr(clr[i][j], alpha);
			    normal(nml[i][j]);
			    vertex(pts[i][j]);
			    clr(clr[i+1][j], alpha);
			    normal(nml[i+1][j]);
			    vertex(pts[i+1][j]);
			}
			super.endShape();
		    }
		}
	    }
	}
    }
    public void drawTriangles(IVec[] p){
	super.stroke= false;
	super.fill = true;
	super.beginShape(TRIANGLES);
	for(int i=0; i<p.length-2; i+=3){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
		    vertex(p[i]);
		    vertex(p[i+1]);
		    vertex(p[i+2]);
		}
	    }
	    else{
		vertex(p[i]);
		vertex(p[i+1]);
		vertex(p[i+2]);
	    }
	}
	//vertex(p);
	super.endShape();
    }
    public void drawTriangles(IVec[] p, IVec[] n){
	super.stroke= false;
	super.fill = true;
	super.beginShape(TRIANGLES);
	for(int i=0; i<p.length-2; i+=3){
	    if(IConfig.cullVertexBehindViewInP3D){
		if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
		    normal(n[i]); vertex(p[i]);
		    normal(n[i+1]); vertex(p[i+1]);
		    normal(n[i+2]); vertex(p[i+2]);
		}
	    }
	    else{
		normal(n[i]); vertex(p[i]);
		normal(n[i+1]); vertex(p[i+1]);
		normal(n[i+2]); vertex(p[i+2]);
	    }
	}
	//vertex(p,n);
	super.endShape();
    }
    public void drawTriangles(IVec[] p, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	super.beginShape(TRIANGLES);
	if(light){
	    if(alpha<0){
		for(int i=0; i<p.length-2; i+=3){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
			    ambient(clr[i]);
			    diffuse(clr[i]);
			    vertex(p[i]);
			    ambient(clr[i+1]);
			    diffuse(clr[i+1]);
			    vertex(p[i+1]);
			    ambient(clr[i+2]);
			    diffuse(clr[i+2]);
			    vertex(p[i+2]);
			}
		    }
		    else{
			ambient(clr[i]);
			diffuse(clr[i]);
			vertex(p[i]);
			ambient(clr[i+1]);
			diffuse(clr[i+1]);
			vertex(p[i+1]);
			ambient(clr[i+2]);
			diffuse(clr[i+2]);
			vertex(p[i+2]);
		    }
		}
	    }
	    else{
		for(int i=0; i<p.length-2; i+=3){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
			    ambient(clr[i], alpha);
			    diffuse(clr[i], alpha);
			    vertex(p[i]);
			    ambient(clr[i+1], alpha);
			    diffuse(clr[i+1], alpha);
			    vertex(p[i+1]);
			    ambient(clr[i+2], alpha);
			    diffuse(clr[i+2], alpha);
			    vertex(p[i+2]);
			}
		    }
		    else{
			ambient(clr[i], alpha);
			diffuse(clr[i], alpha);
			vertex(p[i]);
			ambient(clr[i+1], alpha);
			diffuse(clr[i+1], alpha);
			vertex(p[i+1]);
			ambient(clr[i+2], alpha);
			diffuse(clr[i+2], alpha);
			vertex(p[i+2]);
		    }
		}
	    }
	}
	else{
	    if(alpha<0){
		for(int i=0; i<p.length-2; i+=3){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
			    clr(clr[i]);
			    vertex(p[i]);
			    clr(clr[i+1]);
			    vertex(p[i+1]);
			    clr(clr[i+2]);
			    vertex(p[i+2]);
			}
		    }
		    else{
			clr(clr[i]);
			vertex(p[i]);
			clr(clr[i+1]);
			vertex(p[i+1]);
			clr(clr[i+2]);
			vertex(p[i+2]);
		    }
		}
	    }
	    else{
		for(int i=0; i<p.length-2; i+=3){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
			    clr(clr[i], alpha);
			    vertex(p[i]);
			    clr(clr[i+1], alpha);
			    vertex(p[i+1]);
			    clr(clr[i+2], alpha);
			    vertex(p[i+2]);
			}
		    }
		    else{
			clr(clr[i], alpha);
			vertex(p[i]);
			clr(clr[i+1], alpha);
			vertex(p[i+1]);
			clr(clr[i+2], alpha);
			vertex(p[i+2]);
		    }
		}
	    }
	}
	super.endShape();
    }
    public void drawTriangles(IVec[] p, IVec[] n, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	super.beginShape(TRIANGLES);
	if(light){
	    if(alpha<0){
		for(int i=0; i<p.length-2; i+=3){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
			    ambient(clr[i]); diffuse(clr[i]); normal(n[i]); vertex(p[i]);
			    ambient(clr[i+1]); diffuse(clr[i+1]); normal(n[i+1]); vertex(p[i+1]);
			    ambient(clr[i+2]); diffuse(clr[i+2]); normal(n[i+2]); vertex(p[i+2]);
			}
		    }
		    else{
			ambient(clr[i]); diffuse(clr[i]); normal(n[i]); vertex(p[i]);
			ambient(clr[i+1]); diffuse(clr[i+1]); normal(n[i+1]); vertex(p[i+1]);
			ambient(clr[i+2]); diffuse(clr[i+2]); normal(n[i+2]); vertex(p[i+2]);
		    }
		}
	    }
	    else{
		for(int i=0; i<p.length-2; i+=3){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
			    ambient(clr[i],alpha); diffuse(clr[i],alpha); normal(n[i]); vertex(p[i]);
			    ambient(clr[i+1],alpha); diffuse(clr[i+1],alpha); normal(n[i+1]); vertex(p[i+1]);
			    ambient(clr[i+2],alpha); diffuse(clr[i+2],alpha); normal(n[i+2]); vertex(p[i+2]);
			}
		    }
		    else{
			ambient(clr[i],alpha); diffuse(clr[i],alpha); normal(n[i]); vertex(p[i]);
			ambient(clr[i+1],alpha); diffuse(clr[i+1],alpha); normal(n[i+1]); vertex(p[i+1]);
			ambient(clr[i+2],alpha); diffuse(clr[i+2],alpha); normal(n[i+2]); vertex(p[i+2]);
		    }
		}
	    }
	}
	else{
	    if(alpha<0){
		for(int i=0; i<p.length-2; i+=3){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
			    clr(clr[i]); normal(n[i]); vertex(p[i]);
			    clr(clr[i+1]); normal(n[i+1]); vertex(p[i+1]);
			    clr(clr[i+2]); normal(n[i+2]); vertex(p[i+2]);
			}
		    }
		    else{
			clr(clr[i]); normal(n[i]); vertex(p[i]);
			clr(clr[i+1]); normal(n[i+1]); vertex(p[i+1]);
			clr(clr[i+2]); normal(n[i+2]); vertex(p[i+2]);
		    }
		}
	    }
	    else{
		for(int i=0; i<p.length-2; i+=3){
		    if(IConfig.cullVertexBehindViewInP3D){
			if(isInFront(p[i]) && isInFront(p[i+1]) && isInFront(p[i+2])){
			    clr(clr[i],alpha); normal(n[i]); vertex(p[i]);
			    clr(clr[i+1],alpha); normal(n[i+1]); vertex(p[i+1]);
			    clr(clr[i+2],alpha); normal(n[i+2]); vertex(p[i+2]);
			}
		    }
		    else{
			clr(clr[i],alpha); normal(n[i]); vertex(p[i]);
			clr(clr[i+1],alpha); normal(n[i+1]); vertex(p[i+1]);
			clr(clr[i+2],alpha); normal(n[i+2]); vertex(p[i+2]);
		    }
		}
	    }
	}
	//vertex(p,n);
	super.endShape();
    }
    public void drawTriangleStrip(IVec[] p){
	if(p.length<3) return;
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean isDrawing = false;
	    //boolean previous1Front = isInFront(p[1]);
	    //boolean previous2Front = isInFront(p[0]);
	    //for(int i=2; i<p.length; i++){
	    boolean previous1Front = false;
	    boolean previous2Front = false;
	    for(int i=0; i<p.length; i++){
		boolean isFront = isInFront(p[i]);
		if(isFront){
		    if(isDrawing){
			vertex(p[i]);
		    }
		    else{
			super.beginShape(TRIANGLE_STRIP);
			vertex(p[i]);
			isDrawing=true;
		    }
		    
		    /*
		    if(!isDrawing){
			super.beginShape(TRIANGLE_STRIP);
			vertex(p[i-2]);
			vertex(p[i-1]);
			isDrawing=true;
		    }
		    vertex(p[i]);
		    */
		}
		else{
		    if(isDrawing){
			super.endShape();
			isDrawing=false;
		    }
		    /*
		    if(previous1Front || previous2Front){ vertex(p[i]); }
		    else if(isDrawing){
			super.endShape();
			isDrawing=false;
		    }
		    */
		}
		previous2Front = previous1Front;
		previous1Front = isFront;
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    super.beginShape(TRIANGLE_STRIP);
	    vertex(p);
	    super.endShape();
	}
    }
    public void drawTriangleStrip(IVec[] p, IVec[] n){
	if(p.length<3) return;
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean isDrawing = false;
	    //boolean previous1Front = isInFront(p[1]);
	    //boolean previous2Front = isInFront(p[0]);
	    //for(int i=2; i<p.length; i++){
	    boolean previous1Front = false; 
	    boolean previous2Front = false;
	    for(int i=0; i<p.length; i++){
		boolean isFront = isInFront(p[i]);
		if(isFront){
		    if(isDrawing){
			normal(n[i]);
			vertex(p[i]);
		    }
		    else{
			super.beginShape(TRIANGLE_STRIP);
			normal(n[i]);
			vertex(p[i]);
			/*
			super.beginShape(TRIANGLE_STRIP);
			normal(n[i-2]);
			vertex(p[i-2]);
			normal(n[i-1]);
			vertex(p[i-1]);
			isDrawing=true;
			*/
		    }
		}
		else{
		    if(isDrawing){
			super.endShape();
			isDrawing=false;
		    }
		    /*
		    if(previous1Front || previous2Front){
			normal(n[i]);
			vertex(p[i]);
		    }
		    else if(isDrawing){
			super.endShape();
			isDrawing=false;
		    }
		    */
		}
		previous2Front = previous1Front;
		previous1Front = isFront;
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    super.beginShape(TRIANGLE_STRIP);
	    vertex(p,n);
	    super.endShape();
	}
    }
    public void drawTriangleStrip(IVec[] p, IColor[] clr, float alpha, boolean light){
	if(p.length<3) return;
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean isDrawing = false;
	    boolean previous1Front = false;
	    boolean previous2Front = false;
	    if(light){
		if(alpha<0){
		    for(int i=0; i<p.length; i++){
			boolean isFront = isInFront(p[i]);
			if(isFront){
			    if(isDrawing){
				ambient(clr[i]);
				diffuse(clr[i]);
				vertex(p[i]);
			    }
			    else{
				super.beginShape(TRIANGLE_STRIP);
				ambient(clr[i]);
				diffuse(clr[i]);
				vertex(p[i]);
				isDrawing=true;
			}
			}
			else{
			    if(isDrawing){
				super.endShape();
				isDrawing=false;
			    }
			}
			previous2Front = previous1Front;
			previous1Front = isFront;
		    }
		}
		else{
		    for(int i=0; i<p.length; i++){
			boolean isFront = isInFront(p[i]);
			if(isFront){
			    if(isDrawing){
				ambient(clr[i],alpha);
				diffuse(clr[i],alpha);
				vertex(p[i]);
			    }
			    else{
				super.beginShape(TRIANGLE_STRIP);
				ambient(clr[i], alpha);
				diffuse(clr[i], alpha);
				vertex(p[i]);
				isDrawing=true;
			    }
			}
			else{
			    if(isDrawing){
				super.endShape();
				isDrawing=false;
			    }
			}
			previous2Front = previous1Front;
			previous1Front = isFront;
		    }
		}
	    }
	    else{
		if(alpha<0){
		    for(int i=0; i<p.length; i++){
			boolean isFront = isInFront(p[i]);
			if(isFront){
			    if(isDrawing){
				clr(clr[i]);
				vertex(p[i]);
			    }
			    else{
				super.beginShape(TRIANGLE_STRIP);
				clr(clr[i]);
				vertex(p[i]);
				isDrawing=true;
			    }
			}
			else{
			    if(isDrawing){
				super.endShape();
				isDrawing=false;
			    }
			}
			previous2Front = previous1Front;
			previous1Front = isFront;
		    }
		}
		else{
		    for(int i=0; i<p.length; i++){
			boolean isFront = isInFront(p[i]);
			if(isFront){
			    if(isDrawing){
				clr(clr[i],alpha);
				vertex(p[i]);
			    }
			    else{
				super.beginShape(TRIANGLE_STRIP);
				clr(clr[i], alpha);
				vertex(p[i]);
				isDrawing=true;
			    }
			}
			else{
			    if(isDrawing){
				super.endShape();
				isDrawing=false;
			    }
			}
			previous2Front = previous1Front;
			previous1Front = isFront;
		    }
		}
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    super.beginShape(TRIANGLE_STRIP);
	    if(light){
		if(alpha<0){
		    ambient(clr[0]); // ?
		    diffuse(clr[0]); // ?
		}
		else{
		    ambient(clr[0], alpha); // ?
		    diffuse(clr[0], alpha); // ?
		}
	    }
	    else{
		if(alpha<0){
		    clr(clr[0]); // ?
		}
		else{
		    clr(clr[0], alpha); // ?
		}
		
	    }
	    vertex(p);
	    super.endShape();
	}
    }
    
    public void drawTriangleStrip(IVec[] p, IVec[] n, IColor[] clr, float alpha, boolean light){
	if(p.length<3) return;
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    boolean isDrawing = false;
	    boolean previous1Front = false; 
	    boolean previous2Front = false;
	    if(light){
		if(alpha<0){
		    for(int i=0; i<p.length; i++){
			boolean isFront = isInFront(p[i]);
			if(isFront){
			    if(isDrawing){
				ambient(clr[i]);
				diffuse(clr[i]);
				normal(n[i]);
				vertex(p[i]);
			    }
			    else{
				super.beginShape(TRIANGLE_STRIP);
				ambient(clr[i]);
				diffuse(clr[i]);
				normal(n[i]);
				vertex(p[i]);
			    }
			}
			else{
			    if(isDrawing){
				super.endShape();
				isDrawing=false;
			    }
			}
			previous2Front = previous1Front;
			previous1Front = isFront;
		    }
		}
		else{
		    for(int i=0; i<p.length; i++){
			boolean isFront = isInFront(p[i]);
			if(isFront){
			    if(isDrawing){
				ambient(clr[i],alpha);
				diffuse(clr[i],alpha);
				normal(n[i]);
				vertex(p[i]);
			    }
			    else{
				super.beginShape(TRIANGLE_STRIP);
				ambient(clr[i],alpha);
				diffuse(clr[i],alpha);
				normal(n[i]);
				vertex(p[i]);
			    }
			}
			else{
			    if(isDrawing){
				super.endShape();
				isDrawing=false;
			    }
			}
			previous2Front = previous1Front;
			previous1Front = isFront;
		    }
		}
	    }
	    else{
		if(alpha<0){
		    for(int i=0; i<p.length; i++){
			boolean isFront = isInFront(p[i]);
			if(isFront){
			    if(isDrawing){
				clr(clr[i]);
				normal(n[i]);
				vertex(p[i]);
			    }
			    else{
				super.beginShape(TRIANGLE_STRIP);
				clr(clr[i]);
				normal(n[i]);
				vertex(p[i]);
			    }
			}
			else{
			    if(isDrawing){
				super.endShape();
				isDrawing=false;
			    }
			}
			previous2Front = previous1Front;
			previous1Front = isFront;
		    }
		}
		else{
		    for(int i=0; i<p.length; i++){
			boolean isFront = isInFront(p[i]);
			if(isFront){
			    if(isDrawing){
				clr(clr[i],alpha);
				normal(n[i]);
				vertex(p[i]);
			    }
			    else{
				super.beginShape(TRIANGLE_STRIP);
				clr(clr[i],alpha);
				normal(n[i]);
				vertex(p[i]);
			    }
			}
			else{
			    if(isDrawing){
				super.endShape();
				isDrawing=false;
			    }
			}
			previous2Front = previous1Front;
			previous1Front = isFront;
		    }
		}
	    }
	    if(isDrawing){ super.endShape(); }
	}
	else{
	    super.beginShape(TRIANGLE_STRIP);
	    if(light){
		if(alpha<0){
		    ambient(clr[0]); // ?
		    diffuse(clr[0]); // ?
		}
		else{
		    ambient(clr[0],alpha); // ?
		    diffuse(clr[0],alpha); // ?
		}
	    }
	    else{
		if(alpha<0){
		    clr(clr[0]); // ?
		}
		else{
		    clr(clr[0],alpha); // ?
		}
	    }
	    vertex(p,n);
	    super.endShape();
	}
    }
    public void drawTriangleFan(IVec[] p){
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    super.beginShape(TRIANGLE_FAN);
	    vertex(p[0]);
	    boolean firstFront=isInFront(p[0]);
	    boolean prevFront= firstFront;
	    for(int i=1; i<p.length; i++){
		boolean currentFront = isInFront(p[i]);
		if(firstFront||prevFront||currentFront){ vertex(p[i]); }
		prevFront=currentFront;
	    }
	    super.endShape();
	}
	else{
	    super.beginShape(TRIANGLE_FAN);
	    vertex(p);
	    super.endShape();
	}
    }
    public void drawTriangleFan(IVec[] p, IVec[] n){
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    super.beginShape(TRIANGLE_FAN);
	    normal(n[0]);
	    vertex(p[0]);
	    boolean firstFront=isInFront(p[0]);
	    boolean prevFront= firstFront;
	    for(int i=1; i<p.length; i++){
		boolean currentFront = isInFront(p[i]);
		if(firstFront||prevFront||currentFront){
		    normal(n[i]); vertex(p[i]);
		}
		prevFront=currentFront;
	    }
	    super.endShape();
	}
	else{
	    super.beginShape(TRIANGLE_FAN);
	    vertex(p,n);
	    super.endShape();
	}
    }
    
    public void drawTriangleFan(IVec[] p, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    super.beginShape(TRIANGLE_FAN);
	    if(light){
		if(alpha<0){
		    ambient(clr[0]);
		    diffuse(clr[0]);
		    vertex(p[0]);
		    boolean firstFront=isInFront(p[0]);
		    boolean prevFront= firstFront;
		    for(int i=1; i<p.length; i++){
			boolean currentFront = isInFront(p[i]);
			if(firstFront||prevFront||currentFront){
			    ambient(clr[i]); diffuse(clr[i]); vertex(p[i]);
			}
			prevFront=currentFront;
		    }
		}
		else{
		    ambient(clr[0],alpha);
		    diffuse(clr[0],alpha);
		    vertex(p[0]);
		    boolean firstFront=isInFront(p[0]);
		    boolean prevFront= firstFront;
		    for(int i=1; i<p.length; i++){
			boolean currentFront = isInFront(p[i]);
			if(firstFront||prevFront||currentFront){
			    ambient(clr[i],alpha); diffuse(clr[i],alpha); vertex(p[i]);
			}
			prevFront=currentFront;
		    }
		}
	    }
	    else{
		if(alpha<0){
		    clr(clr[0]);
		    vertex(p[0]);
		    boolean firstFront=isInFront(p[0]);
		    boolean prevFront= firstFront;
		    for(int i=1; i<p.length; i++){
			boolean currentFront = isInFront(p[i]);
			if(firstFront||prevFront||currentFront){ clr(clr[i]); vertex(p[i]); }
			prevFront=currentFront;
		    }
		}
		else{
		    clr(clr[0],alpha);
		    vertex(p[0]);
		    boolean firstFront=isInFront(p[0]);
		    boolean prevFront= firstFront;
		    for(int i=1; i<p.length; i++){
			boolean currentFront = isInFront(p[i]);
			if(firstFront||prevFront||currentFront){ clr(clr[i],alpha); vertex(p[i]); }
			prevFront=currentFront;
		    }
		}
	    }
	    super.endShape();
	}
	else{
	    super.beginShape(TRIANGLE_FAN);
	    if(alpha<0){
		clr(clr[0]); // ?
	    }
	    else{
		clr(clr[0],alpha); // ?
	    }
	    vertex(p);
	    super.endShape();
	}
    }
    public void drawTriangleFan(IVec[] p, IVec[] n, IColor[] clr, float alpha, boolean light){
	super.stroke= false;
	super.fill = true;
	if(IConfig.cullVertexBehindViewInP3D){
	    super.beginShape(TRIANGLE_FAN);
	    if(light){
		if(alpha<0){
		    ambient(clr[0]);
		    diffuse(clr[0]);
		    normal(n[0]);
		    vertex(p[0]);
		    boolean firstFront=isInFront(p[0]);
		    boolean prevFront= firstFront;
		    for(int i=1; i<p.length; i++){
			boolean currentFront = isInFront(p[i]);
			if(firstFront||prevFront||currentFront){
			    ambient(clr[i]); diffuse(clr[i]); normal(n[i]); vertex(p[i]);
			}
		    prevFront=currentFront;
		    }
		}
		else{
		    ambient(clr[0],alpha);
		    diffuse(clr[0],alpha);
		    normal(n[0]);
		    vertex(p[0]);
		    boolean firstFront=isInFront(p[0]);
		    boolean prevFront= firstFront;
		    for(int i=1; i<p.length; i++){
			boolean currentFront = isInFront(p[i]);
			if(firstFront||prevFront||currentFront){
			    ambient(clr[i],alpha); diffuse(clr[i],alpha); normal(n[i]); vertex(p[i]);
			}
			prevFront=currentFront;
		    }
		}
	    }
	    else{
		if(alpha<0){
		    clr(clr[0]);
		    normal(n[0]);
		    vertex(p[0]);
		    boolean firstFront=isInFront(p[0]);
		    boolean prevFront= firstFront;
		    for(int i=1; i<p.length; i++){
			boolean currentFront = isInFront(p[i]);
			if(firstFront||prevFront||currentFront){
			    clr(clr[i]); normal(n[i]); vertex(p[i]);
			}
		    prevFront=currentFront;
		    }
		}
		else{
		    clr(clr[0],alpha);
		    normal(n[0]);
		    vertex(p[0]);
		    boolean firstFront=isInFront(p[0]);
		    boolean prevFront= firstFront;
		    for(int i=1; i<p.length; i++){
			boolean currentFront = isInFront(p[i]);
			if(firstFront||prevFront||currentFront){
			    clr(clr[i],alpha); normal(n[i]); vertex(p[i]);
			}
			prevFront=currentFront;
		    }
		}
	    }
	    super.endShape();
	}
	else{
	    super.beginShape(TRIANGLE_FAN);
	    if(light){
		if(alpha<0){
		    ambient(clr[0]); //?
		    diffuse(clr[0]); //?
		}
		else{
		    ambient(clr[0],alpha); //?
		    diffuse(clr[0],alpha); //?
		}
	    }
	    else{
		if(alpha<0){
		    clr(clr[0]); //?
		}
		else{
		    clr(clr[0],alpha); //?
		}
	    }
	    vertex(p,n);
	    super.endShape();
	}
    }
    

}

