package megamu.redblue;

import processing.core.*;

// TODO: add in non P3D specific commands!

public class RedBlue extends PGraphics3D {

	PGraphics3D left, right;

	float divergence;
	
	float eyeX, eyeY, eyeZ;
	float centerX, centerY, centerZ;
	float upX, upY, upZ;
	float fov = PI/3.0f;
	
	public RedBlue(int iwidth, int iheight, PApplet parent) {
		super(iwidth, iheight, parent);
		left = (PGraphics3D) parent.createGraphics(iwidth, iheight, P3D);
		right = (PGraphics3D) parent.createGraphics(iwidth, iheight, P3D);
		setDivergence(1.0f);
	}

	public void setDivergence(float amount) {
		divergence = amount;
		eyeballs();
	}

	public float getDivergence() {
		return divergence;
	}

	protected void eyeballs(){
		float dx = eyeX - centerX;
		float dy = eyeY - centerY;
		float dz = eyeZ - centerZ;
		
		float diverge = -divergence/(fov*RAD_TO_DEG);

		float divergeX = (dy * upZ - upY * dz) * diverge;
		float divergeY = (dz * upX - upZ * dx) * diverge;
		float divergeZ = (dx * upY - upX * dy) * diverge;

		if (left != null) {
			left.camera(eyeX - divergeX, eyeY - divergeY, eyeZ - divergeZ,
					centerX, centerY, centerZ, upX, upY, upZ);
			right.camera(eyeX + divergeX, eyeY + divergeY, eyeZ + divergeZ,
					centerX, centerY, centerZ, upX, upY, upZ);
		}
	}

	public void resize(int iwidth, int iheight) { // ignore
		super.resize(iwidth, iheight);
		if (left != null) {
			left.resize(iwidth, iheight);
			right.resize(iwidth, iheight);
		}
	}

	public void beginDraw() {
		super.beginDraw();
		left.beginDraw();
		right.beginDraw();
	}

	public void endDraw() {
		left.endDraw();
		right.endDraw();

		// do pixel dance
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0xFF000000 | (left.pixels[i] & 0xFF0000)
					| (right.pixels[i] >> 8 & 0xFF00)
					| (right.pixels[i] & 0xFF);
		}

		super.endDraw();
	}

	public void defaults() {
		super.defaults();
		left.defaults();
		right.defaults();
	}

	// ////////////////////////////////////////////////////////////

	public void beginShape(int kind) {
		left.beginShape(kind);
		right.beginShape(kind);
	}

	public void normal(float nx, float ny, float nz) {
		left.normal(nx, ny, nz);
		right.normal(nx, ny, nz);
	}

	public void texture(PImage image) {
		left.texture(image);
		right.texture(image);
	}

	public void vertex(float x, float y) {
		left.vertex(x, y);
		right.vertex(x, y);
	}

	public void vertex(float x, float y, float u, float v) {
		left.vertex(x, y, u, v);
		right.vertex(x, y, u, v);
	}

	public void vertex(float x, float y, float z) {
		left.vertex(x, y, z);
		right.vertex(x, y, z);
	}

	public void vertex(float x, float y, float z, float u, float v) {
		left.vertex(x, y, z, u, v);
		right.vertex(x, y, z, u, v);
	}

	public void bezierVertex(float x2, float y2, float z2, float x3, float y3,
			float z3, float x4, float y4, float z4) {
		left.bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4);
		right.bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4);
	}

	public void endShape(int mode) {
		left.endShape(mode);
		right.endShape(mode);
	}

	// ////////////////////////////////////////////////////////////
	// BASIC SHAPES

	public void point(float x, float y, float z) {
		left.point(x, y, z);
		right.point(x, y, z);
	}

	/**
	 * Compared to the implementation in PGraphics, this adds normal().
	 */
	public void triangle(float x1, float y1, float x2, float y2, float x3,
			float y3) {
		left.triangle(x1, y1, x2, y2, x3, y3);
		right.triangle(x1, y1, x2, y2, x3, y3);
	}

	/**
	 * Compared to the implementation in PGraphics, this adds normal().
	 */
	public void quad(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4) {
		left.quad(x1, y1, x2, y2, x3, y3, x4, y4);
		right.quad(x1, y1, x2, y2, x3, y3, x4, y4);
	}

	// ////////////////////////////////////////////////////////////
	// BOX

	public void box(float w, float h, float d) {
		left.box(w, h, d);
		right.box(w, h, d);
	}

	// ////////////////////////////////////////////////////////////
	// SPHERE

	public void sphereDetail(int res) {
		left.sphereDetail(res);
		right.sphereDetail(res);
	}

	public void sphere(float r) {
		left.sphere(r);
		right.sphere(r);
	}

	// ////////////////////////////////////////////////////////////
	// CURVES

	// ////////////////////////////////////////////////////////////
	// MATRIX TRANSFORMATIONS

	public void translate(float tx, float ty, float tz) {
		super.translate(tx, ty, tz);
		left.translate(tx, ty, tz);
		right.translate(tx, ty, tz);
	}

	public void rotateX(float angle) {
		super.rotateX(angle);
		left.rotateX(angle);
		right.rotateX(angle);
	}

	public void rotateY(float angle) {
		super.rotateY(angle);
		left.rotateY(angle);
		right.rotateY(angle);
	}

	public void rotateZ(float angle) {
		super.rotateZ(angle);
		left.rotateZ(angle);
		right.rotateZ(angle);
	}

	public void rotate(float angle, float v0, float v1, float v2) {
		super.rotate(angle, v0, v1, v2);
		left.rotate(angle, v0, v1, v2);
		right.rotate(angle, v0, v1, v2);
	}

	public void scale(float x, float y, float z) {
		super.scale(x, y, z);
		left.scale(x, y, z);
		right.scale(x, y, z);
	}

	// ////////////////////////////////////////////////////////////
	// TRANSFORMATION MATRIX

	public void pushMatrix() {
		super.pushMatrix();
		left.pushMatrix();
		right.pushMatrix();
	}

	public void popMatrix() {
		super.popMatrix();
		left.popMatrix();
		right.popMatrix();
	}

	public void resetMatrix() {
		super.resetMatrix();
		left.resetMatrix();
		right.resetMatrix();
	}

	public void applyMatrix(float n00, float n01, float n02, float n03,
			float n10, float n11, float n12, float n13, float n20, float n21,
			float n22, float n23, float n30, float n31, float n32, float n33) {

		super.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21,
				n22, n23, n30, n31, n32, n33);

		left.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22,
				n23, n30, n31, n32, n33);

		right.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21,
				n22, n23, n30, n31, n32, n33);
	}

	public void loadMatrix() {
		super.loadMatrix();
		left.loadMatrix();
		right.loadMatrix();
	}

	// ////////////////////////////////////////////////////////////
	// CAMERA and PERSPECTIVE

	public void beginCamera() {
		super.beginCamera();
		left.beginCamera();
		right.beginCamera();
	}

	public void endCamera() {
		super.endCamera();
		left.endCamera();
		right.endCamera();
	}

	public void camera(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ, float upX, float upY, float upZ) {

		super
				.camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY,
						upZ);
		
		this.eyeX = eyeX;
		this.eyeY = eyeY;
		this.eyeZ = eyeZ;

		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;

		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;
		
		eyeballs();
	}

	public void ortho(float left, float right, float bottom, float top,
			float near, float far) {
		super.ortho(left, right, bottom, top, near, far);
		this.left.ortho(left, right, bottom, top, near, far);
		this.right.ortho(left, right, bottom, top, near, far);
	}

	public void perspective(float fov, float aspect, float zNear, float zFar) {
		super.perspective(fov, aspect, zNear, zFar);
		
		this.fov = fov;
		
		if (left != null) {
			left.perspective(fov, aspect, zNear, zFar);
			right.perspective(fov, aspect, zNear, zFar);
		}
		
		eyeballs();
	}

	public void frustum(float left, float right, float bottom, float top,
			float znear, float zfar) {
		super.frustum(left, right, bottom, top, znear, zfar);
		if (this.left != null) {
			this.left.frustum(left, right, bottom, top, znear, zfar);
			this.right.frustum(left, right, bottom, top, znear, zfar);
		}
	}

	// ////////////////////////////////////////////////////////////

	public void ambient(int rgb) {
		left.ambient(rgb);
		right.ambient(rgb);
	}

	public void ambient(float gray) {
		left.ambient(gray);
		right.ambient(gray);
	}

	public void ambient(float x, float y, float z) {
		left.ambient(x, y, z);
		right.ambient(x, y, z);
	}

	// ////////////////////////////////////////////////////////////

	public void specular(int rgb) {
		left.specular(rgb);
		right.specular(rgb);
	}

	public void specular(float gray) {
		left.specular(gray);
		right.specular(gray);
	}

	public void specular(float gray, float alpha) {
		left.specular(gray, alpha);
		right.specular(gray, alpha);
	}

	public void specular(float x, float y, float z) {
		left.specular(x, y, z);
		right.specular(x, y, z);
	}

	public void specular(float x, float y, float z, float a) {
		left.specular(x, y, z, a);
		right.specular(x, y, z, a);
	}

	public void shininess(float shine) {
		left.shininess(shine);
		right.shininess(shine);
	}

	// ////////////////////////////////////////////////////////////

	public void emissive(int rgb) {
		left.emissive(rgb);
		right.emissive(rgb);
	}

	public void emissive(float gray) {
		left.emissive(gray);
		right.emissive(gray);
	}

	public void emissive(float x, float y, float z) {
		left.emissive(x, y, z);
		right.emissive(x, y, z);
	}

	// ////////////////////////////////////////////////////////////

	public void lights() {
		left.lights();
		right.lights();
	}

	public void noLights() {
		left.noLights();
		right.noLights();
	}

	public void ambientLight(float r, float g, float b, float x, float y,
			float z) {
		left.ambientLight(r, g, b, x, y, z);
		right.ambientLight(r, g, b, x, y, z);
	}

	public void directionalLight(float r, float g, float b, float nx, float ny,
			float nz) {
		left.directionalLight(r, g, b, nx, ny, nz);
		right.directionalLight(r, g, b, nx, ny, nz);
	}

	public void pointLight(float r, float g, float b, float x, float y, float z) {
		left.pointLight(r, g, b, x, y, z);
		right.pointLight(r, g, b, x, y, z);
	}

	public void spotLight(float r, float g, float b, float x, float y, float z,
			float nx, float ny, float nz, float angle, float concentration) {
		left.spotLight(r, g, b, x, y, z, nx, ny, nz, angle, concentration);
		right.spotLight(r, g, b, x, y, z, nx, ny, nz, angle, concentration);
	}

	public void lightFalloff(float constant, float linear, float quadratic) {
		left.lightFalloff(constant, linear, quadratic);
		right.lightFalloff(constant, linear, quadratic);
	}

	public void lightSpecular(float x, float y, float z) {
		left.lightSpecular(x, y, z);
		right.lightSpecular(x, y, z);
	}

	// ////////////////////////////////////////////////////////////
	// BACKGROUND

	public void background(PImage image) {
		left.background(image);
		right.background(image);
	}

	// ////////////////////////////////////////////////////////////
	// PGRAPHICS generic
	public void hint(int which) {
		left.hint(which);
		right.hint(which);
	}

	public void unhint(int which) {
		left.unhint(which);
		right.unhint(which);
	}

	public void textureMode(int mode) {
		left.textureMode(mode);
		right.textureMode(mode);
	}

	public void bezierVertex(float x2, float y2, float x3, float y3, float x4,
			float y4) {
		bezierVertex(x2, y2, Float.MAX_VALUE, x3, y3, Float.MAX_VALUE, x4, y4,
				Float.MAX_VALUE);
	}

	/**
	 * See notes with the curve() function.
	 */
	public void curveVertex(float x, float y) {
		left.curveVertex(x, y);
		right.curveVertex(x, y);
	}

	/**
	 * See notes with the curve() function.
	 */
	public void curveVertex(float x, float y, float z) {
		left.curveVertex(x, y, z);
		right.curveVertex(x, y, z);
	}

	/** This feature is in testing, do not use or rely upon its implementation */
	public void breakShape() {
		left.breakShape();
		right.breakShape();
	}

	// ////////////////////////////////////////////////////////////
	// SIMPLE SHAPES WITH ANALOGUES IN beginShape()

	public void point(float x, float y) {
		left.point(x, y);
		right.point(x, y);
	}

	public void line(float x1, float y1, float x2, float y2) {
		left.line(x1, y1, x2, y2);
		right.line(x1, y1, x2, y2);
	}

	public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
		left.line(x1, y1, z1, x2, y2, z2);
		right.line(x1, y1, z1, x2, y2, z2);
	}

	// ////////////////////////////////////////////////////////////
	// RECT

	public void rectMode(int mode) {
		left.rectMode(mode);
		right.rectMode(mode);
	}

	public void rect(float x1, float y1, float x2, float y2) {
		left.rect(x1, y1, x2, y2);
		right.rect(x1, y1, x2, y2);
	}

	// ////////////////////////////////////////////////////////////
	// ELLIPSE AND ARC

	public void ellipseMode(int mode) {
		left.ellipseMode(mode);
		right.ellipseMode(mode);
	}

	public void ellipse(float a, float b, float c, float d) {
		left.ellipse(a, b, c, d);
		right.ellipse(a, b, c, d);
	}

	public void arc(float a, float b, float c, float d, float start, float stop) {
		left.arc(a, b, c, d, start, stop);
		right.arc(a, b, c, d, start, stop);
	}

	// ////////////////////////////////////////////////////////////
	// BEZIER

	public void bezierDetail(int detail) {
		super.bezierDetail(detail);
		left.bezierDetail(detail);
		right.bezierDetail(detail);
	}

	// ////////////////////////////////////////////////////////////
	// CATMULL-ROM CURVE

	public void curveDetail(int detail) {
		super.curveDetail(detail);
		left.curveDetail(detail);
		right.curveDetail(detail);
	}

	public void curveTightness(float tightness) {
		super.curveTightness(tightness);
		left.curveTightness(tightness);
		right.curveTightness(tightness);
	}

	// ////////////////////////////////////////////////////////////
	// IMAGE

	public void image(PImage image, float x, float y) {
		left.image(image, x, y);
		right.image(image, x, y);
	}

	public void image(PImage image, float a, float b, float c, float d, int u1,
			int v1, int u2, int v2) {
		left.image(image, a, b, c, d, u1, v1, u2, v2);
		right.image(image, a, b, c, d, u1, v1, u2, v2);
	}

	// ////////////////////////////////////////////////////////////
	// TEXT/FONTS

	public void textAlign(int alignX, int alignY) {
		left.textAlign(alignX, alignY);
		right.textAlign(alignX, alignY);
	}

	public float textAscent() {
		return left.textAscent();
	}

	public float textDescent() {
		return left.textDescent();
	}

	public void textFont(PFont which) {
		left.textFont(which);
		right.textFont(which);
	}

	public void textLeading(float leading) {
		left.textLeading(leading);
		right.textLeading(leading);
	}

	public void textMode(int mode) {
		left.textMode(mode);
		right.textMode(mode);
	}

	public void textSize(float size) {
		left.textSize(size);
		right.textSize(size);
	}

	// ........................................................

	public float textWidth(char c) {
		return left.textWidth(c);
	}

	public float textWidth(String str) {
		return left.textWidth(str);
	}

	// ........................................................

	public void text(char c) {
		left.text(c);
		right.text(c);
	}

	public void text(char c, float x, float y) {
		left.text(c, x, y);
		right.text(c, x, y);
	}

	public void text(char c, float x, float y, float z) {
		left.text(c, x, y, z);
		right.text(c, x, y, z);
	}

	public void text(String str, float x, float y) {
		left.text(str, x, y);
		right.text(str, x, y);
	}

	/**
	 * Same as above but with a z coordinate.
	 */
	public void text(String str, float x, float y, float z) {
		left.text(str, x, y, z);
		right.text(str, x, y, z);
	}

	public void text(String str, float x1, float y1, float x2, float y2) {
		left.text(str, x1, y1, x2, y2);
		right.text(str, x1, y1, x2, y2);
	}

	public void text(String s, float x1, float y1, float x2, float y2, float z) {
		left.text(s, x1, y1, x2, y2, z);
		right.text(s, x1, y1, x2, y2, z);
	}

	// ////////////////////////////////////////////////////////////
	// COLOR

	public void colorMode(int mode) {
		super.colorMode(mode);
		left.colorMode(mode);
		right.colorMode(mode);
	}

	public void colorMode(int mode, float maxX, float maxY, float maxZ,
			float maxA) {
		super.colorMode(mode, maxX, maxY, maxZ, maxA);
		left.colorMode(mode, maxX, maxY, maxZ, maxA);
		right.colorMode(mode, maxX, maxY, maxZ, maxA);
	}

	// ////////////////////////////////////////////////////////////

	public void strokeWeight(float weight) {
		left.strokeWeight(weight);
		right.strokeWeight(weight);
	}

	public void noStroke() {
		left.noStroke();
		right.noStroke();
	}

	public void stroke(int rgb) {
		left.stroke(rgb);
		right.stroke(rgb);
	}

	public void stroke(int rgb, float alpha) {
		left.stroke(rgb, alpha);
		right.stroke(rgb, alpha);
	}

	public void stroke(float gray) {
		left.stroke(gray);
		right.stroke(gray);
	}

	public void stroke(float gray, float alpha) {
		left.stroke(gray, alpha);
		right.stroke(gray, alpha);
	}

	public void stroke(float x, float y, float z) {
		left.stroke(x, y, z);
		right.stroke(x, y, z);
	}

	public void stroke(float x, float y, float z, float a) {
		left.stroke(x, y, z, a);
		right.stroke(x, y, z, a);
	}

	// ////////////////////////////////////////////////////////////

	public void noTint() {
		left.noTint();
		right.noTint();
	}

	public void tint(int rgb) {
		left.tint(rgb);
		right.tint(rgb);
	}

	public void tint(int rgb, float alpha) {
		left.tint(rgb, alpha);
		right.tint(rgb, alpha);
	}

	public void tint(float gray) {
		left.tint(gray);
		right.tint(gray);
	}

	public void tint(float gray, float alpha) {
		left.tint(gray, alpha);
		right.tint(gray, alpha);
	}

	public void tint(float x, float y, float z) {
		left.tint(x, y, z);
		right.tint(x, y, z);
	}

	public void tint(float x, float y, float z, float a) {
		left.tint(x, y, z, a);
		right.tint(x, y, z, a);
	}

	// ////////////////////////////////////////////////////////////

	public void noFill() {
		left.noFill();
		right.noFill();
	}

	public void fill(int rgb) {
		left.fill(rgb);
		right.fill(rgb);
	}

	public void fill(int rgb, float alpha) {
		left.fill(rgb, alpha);
		right.fill(rgb, alpha);
	}

	public void fill(float gray) {
		left.fill(gray);
		right.fill(gray);
	}

	public void fill(float gray, float alpha) {
		left.fill(gray, alpha);
		right.fill(gray, alpha);
	}

	public void fill(float x, float y, float z) {
		left.fill(x, y, z);
		right.fill(x, y, z);
	}

	public void fill(float x, float y, float z, float a) {
		left.fill(x, y, z, a);
		right.fill(x, y, z, a);
	}

	// ////////////////////////////////////////////////////////////

	public void background(int rgb) {
		left.background(rgb);
		right.background(rgb);
	}

	public void background(int rgb, float alpha) {
		left.background(rgb, alpha);
		right.background(rgb, alpha);
	}

	public void background(float gray) {
		left.background(gray);
		right.background(gray);
	}

	public void background(float gray, float alpha) {
		left.background(gray, alpha);
		right.background(gray, alpha);
	}

	public void background(float x, float y, float z) {
		left.background(x, y, z);
		right.background(x, y, z);
	}

	public void background(float x, float y, float z, float a) {
		left.background(x, y, z, a);
		right.background(x, y, z, a);
	}

	// ////////////////////////////////////////////////////////////

	public void mask(int alpha[]) { // ignore
		left.mask(alpha);
		right.mask(alpha);
	}

	public void mask(PImage alpha) { // ignore
		left.mask(alpha);
		right.mask(alpha);
	}

}
