// https://discourse.processing.org/t/fft-of-1d-array-of-1024-measurements/7901/2

// from https://stackoverflow.com/questions/3287518/reliable-and-fast-fft-in-java
// https://web.archive.org/web/20150922044939/http://www.wikijava.org/wiki/The_Fast_Fourier_Transform_in_Java_%28part_1%29
// kll note: that is the complex version, so need 2 input arrays, and the output as complex pairs..

//     rev 0.1 forum https://discourse.processing.org/t/fft-of-1d-array-of-1024-measurements/7901/5
//     rev 0.2 change from double to float
// kll, i have no idea what we loose?

/**
 * @author Orlando Selenu
 *
 */

//kll class FFTbase {
/**
 * The Fast Fourier Transform (generic version, with NO optimizations).
 *
 * @param inputReal
 *            an array of length n, the real part
 * @param inputImag
 *            an array of length n, the imaginary part
 * @param DIRECT
 *            TRUE = direct transform, FALSE = inverse transform
 * @return a new array of length 2n
 */
float[] fft(float[] inputReal, float[] inputImag, boolean DIRECT) {
  // - n is the dimension of the problem
  // - nu is its logarithm in base e
  int n = inputReal.length;

  // If n is a power of 2, then ld is an integer (_without_ decimals)
  float ld = log(n) / log(2.0);

  // Here I check if n is a power of 2. If exist decimals in ld, I quit
  // from the function returning null.
  if (((int) ld) - ld != 0) {
    System.out.println("The number of elements is not a power of 2.");
    return null;
  }

  // Declaration and initialization of the variables
  // ld should be an integer, actually, so I don't lose any information in
  // the cast
  int nu = (int) ld;
  int n2 = n / 2;
  int nu1 = nu - 1;
  float[] xReal = new float[n];
  float[] xImag = new float[n];
  float tReal, tImag, p, arg, c, s;

  // Here I check if I'm going to do the direct transform or the inverse
  // transform.
  float constant;
  if (DIRECT)
    constant = -2 * PI;
  else
    constant = 2 * PI;

  // I don't want to overwrite the input arrays, so here I copy them. This
  // choice adds \Theta(2n) to the complexity.
  for (int i = 0; i < n; i++) {
    xReal[i] = inputReal[i];
    xImag[i] = inputImag[i];
  }

  // First phase - calculation
  int k = 0;
  for (int l = 1; l <= nu; l++) {
    while (k < n) {
      for (int i = 1; i <= n2; i++) {
        p = bitreverseReference(k >> nu1, nu);
        // direct FFT or inverse FFT
        arg = constant * p / n;
        c = cos(arg);
        s = sin(arg);
        tReal = xReal[k + n2] * c + xImag[k + n2] * s;
        tImag = xImag[k + n2] * c - xReal[k + n2] * s;
        xReal[k + n2] = xReal[k] - tReal;
        xImag[k + n2] = xImag[k] - tImag;
        xReal[k] += tReal;
        xImag[k] += tImag;
        k++;
      }
      k += n2;
    }
    k = 0;
    nu1--;
    n2 /= 2;
  }

  // Second phase - recombination
  k = 0;
  int r;
  while (k < n) {
    r = bitreverseReference(k, nu);
    if (r > k) {
      tReal = xReal[k];
      tImag = xImag[k];
      xReal[k] = xReal[r];
      xImag[k] = xImag[r];
      xReal[r] = tReal;
      xImag[r] = tImag;
    }
    k++;
  }

  // Here I have to mix xReal and xImag to have an array (yes, it should
  // be possible to do this stuff in the earlier parts of the code, but
  // it's here to readibility).
  float[] newArray = new float[xReal.length * 2];
  float radice = 1 / sqrt(n);
  for (int i = 0; i < newArray.length; i += 2) {
    int i2 = i / 2;
    // I used Stephen Wolfram's Mathematica as a reference so I'm going
    // to normalize the output while I'm copying the elements.
    newArray[i] = xReal[i2] * radice;
    newArray[i + 1] = xImag[i2] * radice;
  }
  return newArray;
}

/**
 * The reference bitreverse function.
 */
int bitreverseReference(int j, int nu) {
  int j2;
  int j1 = j;
  int k = 0;
  for (int i = 1; i <= nu; i++) {
    j2 = j1 / 2;
    k = 2 * k + j1 - 2 * j2;
    j1 = j2;
  }
  return k;
}
//kll }  // kll disable class thing

// kll now we try to use that in our processing sketch

int zeroy = 110, welle=1, owelle=4, many = 512;
float[] signalr = new float[many];   //signal real
float[] signali = new float[many];   //signal imag // empty

float[] myfft  = new float[2*many];
float off = -0.25f;
float fftmul = 10.0;               // fft graph zoom

void make_signal() {
  float ang = welle*2*PI/many;
  for (int i=0; i<many; i++) signalr[i] = off + 0.5*cos(ang*i)+ 0.1*cos(owelle*ang*i);
}

void draw_signal() {
  stroke(0, 200, 0);
  for (int i=0; i<many; i++) point(10+i, zeroy - 100.0*signalr[i]);
  stroke(200, 0, 0);
  for (int i=0; i<many; i++) line(10 +i, zeroy, 10+i, zeroy - fftmul*myfft[i]);
}

void setup() {
  size(532, 220);
  make_signal();
  myfft = fft(signalr, signali, true);
  for (int i=0; i<10; i++) println("i "+i+" fft "+nf(myfft[i], 1, 1));
}

void draw() {
  background(200, 200, 0);
  draw_signal();
}
