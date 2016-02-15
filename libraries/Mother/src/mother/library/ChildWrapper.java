package mother.library;
import processing.core.*; 
import processing.opengl.*;

import javax.media.opengl.*;

import foetus.Foetus;
import foetus.FoetusParameter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 */
public class ChildWrapper extends SynthContainer {
//	private Logger logger = null;
	
	PApplet 		m_Child; 
	Mother  		r_Mother;		
	boolean 		m_RenderBillboard = true;
	String 			m_Name;
	String			m_TypeName;
	Foetus 			foetusField;
	
	public PApplet 	Child()								{ return m_Child;	}
	public boolean 	GetRenderBillboard()				{ return m_RenderBillboard; }
	public void	   	SetRenderBillboard(boolean rB)		{ m_RenderBillboard = rB; }
	
	public String 	GetName() 							{ return m_Name; }
	public void 	SetName(String name) 				{ m_Name = name; }
	
	public String GetTypeName() { return m_TypeName; }
	
	public Foetus 	getFoetusField()					{ return foetusField; }
	
	public void 	setFoetusField(Foetus foetusField)	{ this.foetusField = foetusField; }

	/**
	 *  ChildWrapper CONSTRUCTOR
	 */
	public ChildWrapper(PApplet child, String name, String typeName, boolean billboard, Mother mother) {	
		r_Mother				= mother;
		m_Name 					= name;
		m_TypeName				= typeName;
		m_RenderBillboard 		= billboard;
		m_Child 				= child;
	}
		
	/**
	 * METHODS
	 */
	
	public void draw(boolean stereo) {		
		if(m_Child.g != null) {
//			logger.info("Before Draw: " + m_Name);
				
			m_Child.frameCount	= r_Mother.GetParent().frameCount;
			
			ArrayList<FoetusParameter> params = this.foetusField.getParameters();
			
			for(int pi = 0; pi < params.size(); pi++) {
				params.get(pi).tick();
			}
			
			PGraphicsOpenGL pgl 	= (PGraphicsOpenGL) m_Child.g;		
			
			PGL 			opengl 	= pgl.beginPGL();
			GL2 			gl2 	= ((PJOGL)opengl).gl.getGL2();
			
			gl2.glClear(GL.GL_DEPTH_BUFFER_BIT);
		    
			// PREMULTIPLIED ALPHA BLENDING
			opengl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
			opengl.blendFuncSeparate(PGL.ONE, PGL.ONE_MINUS_SRC_ALPHA, PGL.ONE, PGL.ZERO);
			
			m_Child.g.pushMatrix();				
			m_Child.draw();
			m_Child.g.popMatrix();
			pgl.blendMode(PConstants.BLEND);	
			pgl.endPGL();	
			
//			logger.info("After Draw: " + m_Name);
		}
		else {
//			System.out.println("Applet thread not yet initialized, g == null");
			return;
		}
	}		
}