package de.voidplus.dollar;

public class Dimension {
	
	private Float width, height;
	
	protected Dimension( Float _width, Float _height ){
		this.width = _width;
		this.height = _height;
	}
	protected Float getWidth() {
		return width;
	}
	protected Float getHeight() {
		return height;
	}
	
}
