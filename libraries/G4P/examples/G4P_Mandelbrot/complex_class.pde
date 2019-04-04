/**
 * A basic complex number class with just enough 
 * functionality to create Mandelbrot plots.
 */
class Complex {
  public double real;
  public double img;

  Complex() {
    real = img = 0.0f;
  }

  public Complex(float r, float i) {
    super();
    this.real = r;
    this.img = i;
  }

  public void set(double colX, double rowY) {
    real = colX;
    img = rowY;
  }

  public Complex add(Complex c) {
    real += c.real;
    img += c.img;
    return this;
  }

  public void mult(Complex c) {
    double nReal = real * c.real - img * c.img;
    double nImg = real * c.img + img * c.real;
    real = nReal;
    img = nImg;
  }

  public Complex squared() {
    double nReal = (real - img)*(real + img);
    double nImg = 2 * real * img;
    real = nReal;
    img = nImg;  
    return this;
  }

  public double sizeSquared() {
    return real * real + img * img;
  }
}
