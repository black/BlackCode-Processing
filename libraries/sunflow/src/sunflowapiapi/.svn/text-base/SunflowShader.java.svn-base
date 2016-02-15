package sunflowapiapi;

import java.awt.Color;

import org.sunflow.SunflowAPI;

/**
 * This class is the container for sunflow shaders
 * It makes it easyer handling and setting them from processing
 * 
 * @author chwarnow
 */
public class SunflowShader {
	public final String SHADER_AMBIENT_OCCLUSION = "ambient_occlusion";
	public final String SHADER_TEXTURED_AMBIENT_OCCLUSION = "textured_ambient_occlusion";
	public final String SHADER_CONSTANT = "constant";
	public final String SHADER_DIFFUSE = "diffuse";
	public final String SHADER_TEXTURED_DIFFUSE = "textured_diffuse";
	public final String SHADER_GLASS = "glass";
	public final String SHADER_MIRROR = "mirror";
	public final String SHADER_PHONG = "phong";
	public final String SHADER_TEXTURED_PHONG = "textured_phong";
	public final String SHADER_SHINY_DIFFUSE = "shiny_diffuse";
	public final String SHADER_TEXTURED_SHINY_DIFFUSE = "textured_shiny_diffuse";
	public final String SHADER_UBER = "uber";
	public final String SHADER_WARD = "ward";
	public final String SHADER_TEXTURED_WARD = "textured_ward";
	public final String SHADER_WIREFRAME = "wireframe";
	
	public String type = SHADER_AMBIENT_OCCLUSION;
	
	
	/* TODO: implement color setting in an elegant way */
	private  final String COLORSPACE_SRGB_NONLINEAR = "sRGB nonlinear";
	private final String COLORSPACE_SRGB_LINEAR = "sRGB linear";
	private final String COLORSPACE_XYZ = "XYZ";
	private String colorSpace = COLORSPACE_SRGB_NONLINEAR;
	
	private String defaultName = "shaderName_";
	private int nameID = 0;
	public String currentName = "default_shader_";
	
	/* sunflow values */
	private Color bright = new Color(1.0f, 1.0f, 1.0f);
	private Color dark = new Color(0.0f, 0.0f, 0.0f);
	private Color color = new Color(1.0f, 1.0f, 1.0f);
	private int samples = 4;
	private float maxDist = 1.0f;
	private String texture = "";
	private float eta = 3.0f;
	private float absorptionDistance = 5.0f;
	private Color absorptionColor = new Color(0.5f, 0.55f, 0.5f);
	private Color diffuse = new Color(1.0f, 1.0f, 1.0f);
	private Color specular = new Color(1.0f, 1.0f, 1.0f);
	private float power = 1.0f;
	private float shiny = 1.0f;
	private String diffuseTexture = "";
	private String specularTexture = "";
	private float diffuseBlend = 1.0f;
	private float specularBlend = 1.0f;
	private float glossyness = 1.0f;
	private float roughnessX = 1.0f;
	private float roughnessY = 1.0f;
	private Color lineColor = new Color(1.0f, 1.0f, 1.0f);
	private Color fillColor = new Color(1.0f, 1.0f, 1.0f);
	private float width = 1.0f;
	
	SunflowAPI sunflow;
	
	/**
	 * default constructor
	 * sets ambient occlusion
	 */
	public SunflowShader(SunflowAPI sunflow_) {
		sunflow = sunflow_;
	}
	
	/**
	 * sets the current shader
	 */
	public void applyCurrentShader(float r, float g, float b) {
		if(type == SHADER_AMBIENT_OCCLUSION) setAmbientOcclusionShader(new Color(r, g, b), dark, samples, maxDist);
		else if(type == SHADER_TEXTURED_AMBIENT_OCCLUSION) setAmbientOcclusionShader(new Color(r, g, b), dark, samples, maxDist, texture);
		else if(type == SHADER_CONSTANT) setConstantShader(new Color(r, g, b));
		else if(type == SHADER_DIFFUSE) setDiffuseShader(new Color(r, g, b));
		else if(type == SHADER_TEXTURED_DIFFUSE) setDiffuseShader(new Color(r, g, b), texture);
		else if(type == SHADER_GLASS) setGlassShader(new Color(r, g, b), eta, absorptionDistance, absorptionColor);
		else if(type == SHADER_MIRROR) setMirrorShader(new Color(r, g, b));
		else if(type == SHADER_PHONG) setPhongShader(new Color(r, g, b), specular, power, samples);
		else if(type == SHADER_TEXTURED_PHONG) setPhongShader(new Color(r, g, b), specular, power, samples, texture);
		else if(type == SHADER_SHINY_DIFFUSE) setShinyDiffuseShader(new Color(r, g, b), shiny);
		else if(type == SHADER_TEXTURED_SHINY_DIFFUSE) setShinyDiffuseShader(new Color(r, g, b), shiny, texture);
		else if(type == SHADER_UBER) setUberShader(new Color(r, g, b), specular, diffuseTexture, specularTexture, diffuseBlend, specularBlend, glossyness, samples);
		else if(type == SHADER_WARD) setWardShader(new Color(r, g, b), specular, roughnessX, roughnessY, samples);
		else if(type == SHADER_TEXTURED_WARD) setWardShader(new Color(r, g, b), specular, roughnessX, roughnessY, samples, texture);
		else if(type == SHADER_WIREFRAME) setWireframeShader(new Color(r, g, b), fillColor, width);
	}
	
	// internal shader setting
	
	/**
	 * Sets Ambient Occlusion Shader
	 * @param bright Highlight Color
	 * @param dark Dark Color
	 * @param samples Detail, the higher the slower and smoother
	 * @param maxDist ?
	 */
	public void setAmbientOcclusionShader(Color bright, Color dark, int samples, float maxDist) {
		// save internal status
		this.bright = bright;
		this.dark = dark;
		this.samples = samples;
		this.maxDist = maxDist;
		
		type = SHADER_AMBIENT_OCCLUSION;
		//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("bright", colorSpace, bright.getRed()/(float)255, bright.getGreen()/(float)255, bright.getBlue()/(float)255);
		sunflow.parameter("dark", colorSpace, dark.getRed()/(float)255, dark.getGreen()/(float)255, dark.getBlue()/(float)255);
		sunflow.parameter("samples", samples);
		sunflow.parameter("maxdist", maxDist);

//		set shader
		sunflow.shader(currentName, SHADER_AMBIENT_OCCLUSION);
	}

	/**
	 * Sets Ambient Occlusion Shader
	 * @param bright Highlight Color
	 * @param dark Dark Color
	 * @param samples Detail, the higher the slower and smoother
	 * @param maxDist ?
	 * @param texture Path to texture file
	 */
	public void setAmbientOcclusionShader(Color bright, Color dark, int samples, float maxDist, String texture) {
		// save internal status
		this.bright = bright;
		this.dark = dark;
		this.samples = samples;
		this.maxDist = maxDist;
		this.texture = texture;
		
		type = SHADER_TEXTURED_AMBIENT_OCCLUSION;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("bright", colorSpace, bright.getRed()/(float)255, bright.getGreen()/(float)255, bright.getBlue()/(float)255);
		sunflow.parameter("dark", colorSpace, dark.getRed()/(float)255, dark.getGreen()/(float)255, dark.getBlue()/(float)255);
		sunflow.parameter("samples", samples);
		sunflow.parameter("maxdist", maxDist);
		sunflow.parameter("texture", texture);

//		set shader
		sunflow.shader(currentName, SHADER_TEXTURED_AMBIENT_OCCLUSION);
	}

	/**
	 * Sets constant shader
	 * @param color Color
	 */
	public void setConstantShader(Color color) {
		// save internal status
		this.color = color;
		
		type = SHADER_CONSTANT;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("color", colorSpace, color.getRed()/(float)255, color.getGreen()/(float)255, color.getBlue()/(float)255);

//		set shader
		sunflow.shader(currentName, SHADER_CONSTANT);
	}

	/**
	 * Sets Diffuse Shader
	 * @param color Color
	 */
	public void setDiffuseShader(Color color) {
		// save internal status
		this.color = color;
		
		type = SHADER_DIFFUSE;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, color.getRed()/(float)255, color.getGreen()/(float)255, color.getBlue()/(float)255);

//		set shader
		sunflow.shader(currentName, SHADER_DIFFUSE);
	}

	/**
	 * Sets Diffuse Shader
	 * @param color Color
	 * @param texture Path to texture file
	 */
	public void setDiffuseShader(Color color, String texture) {
		// save internal status
		this.color = color;
		this.texture = texture;
		
		type = SHADER_TEXTURED_DIFFUSE;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, color.getRed()/(float)255, color.getGreen()/(float)255, color.getBlue()/(float)255);
		sunflow.parameter("texture", texture);

//		set shader
		sunflow.shader(currentName, SHADER_TEXTURED_DIFFUSE);
	}

	/**
	 * Sets Glass Shader
	 * @param color Color
	 * @param eta ?
	 * @param absorptionDistance ?
	 * @param absorptionColor Color
	 */
	public void setGlassShader(Color color, float eta, float absorptionDistance, Color absorptionColor) {
		// save internal status
		this.color = color;
		this.eta = eta;
		this.absorptionDistance = absorptionDistance;
		this.absorptionColor = absorptionColor;
		
		type = SHADER_GLASS;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("color", colorSpace, color.getRed()/(float)255, color.getGreen()/(float)255, color.getBlue()/(float)255);
		sunflow.parameter("eta", eta);
		sunflow.parameter("absorption.distance", absorptionDistance);
		sunflow.parameter("absorption.color", colorSpace, absorptionColor.getRed()/(float)255, absorptionColor.getGreen()/(float)255, absorptionColor.getBlue()/(float)255);

//		set shader
		sunflow.shader(currentName, SHADER_GLASS);
	}

	/**
	 * Sets Mirror Shader
	 * @param color Color
	 */
	public void setMirrorShader(Color color) {
		// save internal status
		this.color = color;
		type = SHADER_MIRROR;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("color", colorSpace, color.getRed()/(float)255, color.getGreen()/(float)255, color.getBlue()/(float)255);

//		set shader
		sunflow.shader(currentName, SHADER_MIRROR);
	}

	/**
	 * Sets Phong Shader
	 * @param diffuse Diffuse Color
	 * @param specular Specular Color
	 * @param power ?
	 * @param samples Detail, the higher the slower and smoother
	 */
	public void setPhongShader(Color diffuse, Color specular, float power, int samples) {
		// save internal status
		this.diffuse = diffuse;
		this.specular = specular;
		this.power = power;
		this.samples = samples;
		type = SHADER_PHONG;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, diffuse.getRed()/(float)255, diffuse.getGreen()/(float)255, diffuse.getBlue()/(float)255);
		sunflow.parameter("specular", colorSpace, specular.getRed()/(float)255, specular.getGreen()/(float)255, specular.getBlue()/(float)255);
		sunflow.parameter("power", power);
		sunflow.parameter("samples", samples);

//		set shader
		sunflow.shader(currentName, SHADER_PHONG);
	}

	/**
	 * Sets Phong Shader
	 * @param diffuse Diffuse Color
	 * @param specular Specular Color
	 * @param power ?
	 * @param samples Detail, the higher the slower and smoother
	 * @param texture Path to texture file
	 */
	public void setPhongShader(Color diffuse, Color specular, float power, int samples, String texture) {
		// save internal status
		this.diffuse = diffuse;
		this.specular = specular;
		this.power = power;
		this.samples = samples;
		type = SHADER_TEXTURED_PHONG;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, diffuse.getRed()/(float)255, diffuse.getGreen()/(float)255, diffuse.getBlue()/(float)255);
		sunflow.parameter("specular", colorSpace, specular.getRed()/(float)255, specular.getGreen()/(float)255, specular.getBlue()/(float)255);
		sunflow.parameter("power", power);
		sunflow.parameter("samples", samples);
		sunflow.parameter("texture", texture);

//		set shader
		sunflow.shader(currentName, SHADER_TEXTURED_PHONG);
	}

	/**
	 * Sets Shiny Diffuse Shader
	 * @param color Color
	 * @param shiny shinyness, the bigger the more
	 */
	public void setShinyDiffuseShader(Color color, float shiny) {
		// save internal status
		this.color = color;
		this.shiny = shiny;
		type = SHADER_SHINY_DIFFUSE;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, color.getRed()/(float)255, color.getGreen()/(float)255, color.getBlue()/(float)255);
		sunflow.parameter("shiny", shiny);

//		set shader
		sunflow.shader(currentName, SHADER_SHINY_DIFFUSE);
	}

	/**
	 * Sets Shiny Diffuse Shader
	 * @param color Color
	 * @param shiny shinyness, the bigger the more
	 * @param texture Path to texture file
	 */
	public void setShinyDiffuseShader(Color color, float shiny, String texture) {
		// save internal status
		this.color = color;
		this.shiny = shiny;
		this.texture = texture;
		
		type = SHADER_TEXTURED_SHINY_DIFFUSE;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, color.getRed()/(float)255, color.getGreen()/(float)255, color.getBlue()/(float)255);
		sunflow.parameter("shiny", shiny);
		sunflow.parameter("texture", texture);

//		set shader
		sunflow.shader(currentName, SHADER_TEXTURED_SHINY_DIFFUSE);
	}

	/**
	 * Sets Uber Shader
	 * @param diffuse Diffuse Color
	 * @param specular Specular Color
	 * @param diffuseTexture Diffuse Texture
	 * @param specularTexture Specular Texture
	 * @param diffuseBlend Diffuse Blendamount
	 * @param specularBlend Specular Blendamount
	 * @param glossyness glossyness
	 * @param samples samples
	 */
	public void setUberShader(Color diffuse, Color specular, String diffuseTexture, String specularTexture, float diffuseBlend, float specularBlend, float glossyness, int samples) {
		// save internal status
		this.diffuse = diffuse;
		this.specular = specular;
		this.diffuseTexture = diffuseTexture;
		this.specularTexture = specularTexture;
		this.diffuseBlend = diffuseBlend;
		this.specularBlend = specularBlend;
		this.glossyness = glossyness;
		this.samples = samples;
		
		type = SHADER_UBER;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, diffuse.getRed()/(float)255, diffuse.getGreen()/(float)255, diffuse.getBlue()/(float)255);
		sunflow.parameter("specular", colorSpace, specular.getRed()/(float)255, specular.getGreen()/(float)255, specular.getBlue()/(float)255);
		sunflow.parameter("diffuse.texture", diffuseTexture);
		sunflow.parameter("specular.texture", specularTexture);
		sunflow.parameter("diffuse.blend", diffuseBlend);
		sunflow.parameter("specular.blend", specularBlend);
		sunflow.parameter("glossyness", glossyness);
		sunflow.parameter("samples", samples);

//		set shader
		sunflow.shader(currentName, SHADER_UBER);
	}

	/**
	 * Sets Anisotropic Ward Shader
	 * @param diffuse Diffuse Color
	 * @param specular Specular Color
	 * @param roughnessX Roughness in x axis
	 * @param roughnessY Roughness in y axis
	 * @param samples Detail, the more the slower and smoother
	 */
	public void setWardShader(Color diffuse, Color specular, float roughnessX, float roughnessY, int samples) {
		// save internal status
		this.diffuse = diffuse;
		this.specular = specular;
		this.roughnessX = roughnessX;
		this.roughnessY = roughnessY;
		this.samples = samples;
		
		type = SHADER_WARD;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, diffuse.getRed()/(float)255, diffuse.getGreen()/(float)255, diffuse.getBlue()/(float)255);
		sunflow.parameter("specular", colorSpace, specular.getRed()/(float)255, specular.getGreen()/(float)255, specular.getBlue()/(float)255);
		sunflow.parameter("roughnessX", roughnessX);
		sunflow.parameter("roughnessY", roughnessY);
		sunflow.parameter("samples", samples);

//		set shader
		sunflow.shader(currentName, SHADER_WARD);
	}

	/**
	 * Sets Anisotropic Ward Shader
	 * @param diffuse Diffuse Color
	 * @param specular Specular Color
	 * @param roughnessX Roughness in x axis
	 * @param roughnessY Roughness in y axis
	 * @param samples Detail, the more the slower and smoother
	 * @param texture Path to texture file
	 */
	public void setWardShader(Color diffuse, Color specular, float roughnessX, float roughnessY, int samples, String texture) {
		// save internal status
		this.diffuse = diffuse;
		this.specular = specular;
		this.roughnessX = roughnessX;
		this.roughnessY = roughnessY;
		this.samples = samples;
		this.texture = texture;
		
		type = SHADER_TEXTURED_WARD;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("diffuse", colorSpace, diffuse.getRed()/(float)255, diffuse.getGreen()/(float)255, diffuse.getBlue()/(float)255);
		sunflow.parameter("specular", colorSpace, specular.getRed()/(float)255, specular.getGreen()/(float)255, specular.getBlue()/(float)255);
		sunflow.parameter("roughnessX", roughnessX);
		sunflow.parameter("roughnessY", roughnessY);
		sunflow.parameter("samples", samples);
		sunflow.parameter("texture", texture);

//		set shader
		sunflow.shader(currentName, SHADER_TEXTURED_WARD);
	}

	/**
	 * Sets Wireframe Shader
	 * @param lineColor line color
	 * @param fillColor fill color
	 * @param width stroke width ?
	 */
	public void setWireframeShader(Color lineColor, Color fillColor, float width) {
		// save internal status
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.width = width;
		
		type = SHADER_WIREFRAME;
//		save name for use with primitives
		currentName = defaultName + nameID++;

//		set parameter
		sunflow.parameter("line", colorSpace, lineColor.getRed()/(float)255, lineColor.getGreen()/(float)255, lineColor.getBlue()/(float)255);
		sunflow.parameter("fill", colorSpace, fillColor.getRed()/(float)255, fillColor.getGreen()/(float)255, fillColor.getBlue()/(float)255);
		sunflow.parameter("width", width);

//		set shader
		sunflow.shader(currentName, SHADER_WIREFRAME);
	}
}
