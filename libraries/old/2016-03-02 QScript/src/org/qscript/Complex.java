/*
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.qscript;

/**
 * Immutable class used to represent complex numbers.<br>
 * The original authors and origin of this class has been 
 * lost in time but it has been modified to make it 
 * immutable by Peter Lager 2014
 *
 * @author Unknown
 */
public final class Complex {

	static Complex Z_0_1i = new Complex(0.0, 1.0);
	static Complex Z_0_1i_neg = negative(Z_0_1i);

	public final double real;
	public final double imag;

	/**
	 * Returns the absolute value or magnitude of a complex number
	 */
	public static double abs(Complex z) {
		return Math.sqrt(z.real*z.real + z.imag*z.imag);
	}

	/**
	 * Returns the acos of z
	 */
	public static Complex acos(Complex z) {
		Complex acos_z;
		Complex temp1 = new Complex();
		temp1 = sqrt(sub(mult(z, z), 1.0));
		acos_z = Z_0_1i_neg.mult(log(add(z,temp1)));
		if (acos_z.real >= 0) {
			return acos_z;
		}
		else {
			return negative(acos_z);
		}
	}

	/** 
	 * Returns the acosh of z
	 */
	public static Complex acosh(Complex z) {
		Complex acosh_z;
		Complex temp1 = new Complex();
		temp1 = sqrt(sub(mult(z, z), 1.0));
		acosh_z = log(add(z, temp1));
		if (acosh_z.real >= 0.0) {
			return acosh_z;
		}
		else {
			return negative(acosh_z);
		}
	}

	/**
	 * Returns the sum of 2 complex numbers
	 */
	public static Complex add(Complex z1, Complex z2) {
		return new Complex(z1.real + z2.real, z1.imag + z2.imag);
	}

	/**
	 * Returns the sum of a complex and a real number
	 */
	public static Complex add(Complex z, double s) {
		return new Complex(z.real + s, z.imag);
	}

	/**
	 * Returns the sum of a real and a complex number
	 */
	public static Complex add(double s, Complex z) {
		return new Complex(s + z.real, z.imag);
	}

	/**
	 * Returns the arg of a complex number
	 */
	public static double arg(Complex z) {
		return Math.atan2(z.imag, z.real);
	}

	/**
	 * Returns the asin of z
	 */
	public static Complex asin(Complex z) {
		Complex temp1, temp2;// = new Complex();
		temp2 = sqrt(sub(1,mult(z,z)));
		temp1 = mult(Z_0_1i, z);
		return Z_0_1i_neg.mult(log(add(temp1, temp2)));
	}

	/** 
	 * Returns the asinh of z
	 */
	public static Complex asinh(Complex z) {
		Complex asinh_z;
		asinh_z = sqrt(add(mult(z, z), 1.0));
		return log(add(z, asinh_z));
	}

	/** 
	 * Returns the atan of z
	 */
	public static Complex atan(Complex z) {
		Complex temp1, temp2;
		temp1 = mult(0.5, Z_0_1i);
		temp2 = log(div(add(Z_0_1i, z), sub(Z_0_1i, z)));
		return mult(temp1,temp2);
	}

	/** 
	 * Returns the atanh of z
	 */
	public static Complex atanh(Complex z) {
		Complex temp1;
		temp1 = div(add(1,z), sub(1.0, z));
		return mult(0.5, log(temp1));
	}

	/** 
	 * Returns the conjugate of z
	 */
	public static Complex conj(Complex z) {
		return new Complex(z.real, -z.imag);
	}

	/**
	 * Returns the cos of z
	 */
	public static Complex cos(Complex z) {
		Complex temp1, temp2;
		temp2 = mult(0.5, exp(negative(Z_0_1i.mult(z))));
		temp1 = mult(0.5, exp(Z_0_1i.mult(z)));
		return add(temp1, temp2);
	}

	/**
	 * Returns the cosh of z
	 */
	public static Complex cosh(Complex z) {
		Complex temp1;
		temp1 = add(exp(z), exp(negative(z)));
		return div(temp1, 2.0);
	}

	/**
	 * Returns the cot of z
	 */
	public static Complex cot(Complex z) {
		return div(cos(z), sin(z));
	}

	/**
	 * Returns the coth of z
	 */
	public static Complex coth(Complex z) {
		return new Complex(div(cosh(z), sinh(z)));
	}

	/**
	 * Returns the cosec of z
	 */
	public static Complex cosec(Complex z) {
		Complex retval = new Complex();
		retval = div(1.0, sin(z));
		return retval;
	}

	/**
	 *  Returns the cosech of z
	 */
	public static Complex cosech(Complex z) {
		Complex retval = new Complex(div(1,sinh(z)));
		return retval;
	}

	/**
	 * Returns the result of dividing z1 by z2
	 */
	public static Complex div(Complex z1, Complex z2) {
		Complex top = z1.mult(conj(z2));
		double bottom = mag(z2);
		return top.div(bottom);
	}

	/**
	 * Returns the result of dividing z by a scalar
	 */
	public static Complex div(Complex z, double s) {
		return new Complex(z.real/s,z.imag/s);
	}

	/**
	 * Returns the result of dividing a scalar by z
	 */
	public static Complex div(double s, Complex z) {
		Complex top = conj(z).mult(s);
		double bottom = mag(z);
		return top.div(bottom);
	}

	/**
	 * Returns e to the power z
	 */
	public static Complex exp(Complex z) {
		Complex c, d;
		double a, b;

		a = Math.exp(z.real);
		b = Math.cos(z.imag);
		c = Z_0_1i.mult(Math.sin(z.imag));
		d = add(b, c);
		return mult(a, d);
	}

	/**
	 * Linear interpretation between 2 complex numbers
	 */
	public static Complex lerp(Complex c1, Complex c2, double t){
		double real = c1.real + t * (c2.real - c1.real);
		double imag = c1.imag + t * (c2.imag - c1.imag);
		return new Complex(real, imag);
	}
	
	/**
	 * Returns the natural log of z
	 */
	public static Complex log(Complex z) {
		if (z.real < 0 && z.imag == 0) {
			return add(Math.log(abs(z)), Z_0_1i.mult(Math.PI));
		}
		else {
			return add(Math.log(abs(z)), Z_0_1i.mult(arg(z)));
		}
	}

	/**
	 * Returns z1 multiplied by z2
	 */
	public static Complex mult(Complex z1, Complex z2) {
		return new Complex(z1.real * z2.real - z1.imag * z2.imag,
				z1.real * z2.imag + z1.imag * z2.real);
	}

	/**
	 * Returns a complex number multiplied by a scalar
	 */
	public static Complex mult(Complex z, double s) {
		return new Complex(z.real * s, z.imag * s);
	}

	/**
	 * Returns a scalar multiplied by a complex number 
	 */
	public static Complex mult(double s, Complex z) {
		return new Complex(s * z.real, s * z.imag);
	}

	/**
	 * Returns the negative value of z
	 */
	public static Complex negative(Complex z) {
		return new Complex(-z.real, -z.imag);
	}

	/**
	 * Returns a Complex number which is the value  of z
	 * normalised so the magnitude is 1
	 */
	public static Complex norm(Complex z) {
		double mag = z.real * z.real + z.imag * z.imag;
		return new Complex(z.real/mag, z.imag/mag);
	}

	public static double mag(Complex z) {
		return z.real * z.real + z.imag * z.imag;
	}

	/**
	 * Returns z1 raised to the power of z2
	 */
	public static Complex pow(Complex z1, Complex z2) {
		return exp(mult(z2, log(z1)));
	}

	/**
	 * Returns z raised to the power of a scalar
	 */
	public static Complex pow(Complex z, double s) {
		return exp(mult(s, log(z)));
	}

	/**
	 * Returns the sec of z
	 */
	public static Complex sec(Complex z) {
		return div(1.0, cos(z));
	}

	/**
	 * Returns the sech of z
	 */
	public static Complex sech(Complex z) {
		return new Complex(div(1.0, cosh(z)));
	}

	/**
	 * Returns the sin of z
	 */
	public static Complex sin(Complex z) {
		Complex temp1, temp2;
		temp2 = mult(0.5, Z_0_1i_neg.mult(exp(Z_0_1i_neg.mult(z))));
		temp1 = mult(0.5, negative(Z_0_1i.mult(exp(Z_0_1i.mult(z)))));
		return sub(temp1, temp2);
	}

	/**
	 * Returns the sinh of z
	 */
	public static Complex sinh(Complex z) {
		Complex temp = sub(exp(z), exp(negative(z)));
		return div(temp, 2.0);
	}

	/**
	 * Returns the square root of z
	 */
	public static Complex sqrt(Complex z) {
		double re, im;
		re = Math.sqrt(0.5*(abs(z) + z.real));
		im = Math.sqrt(0.5*(abs(z) - z.real));
		if (z.imag >= 0)
			return new Complex(re, im);
		else
			return new Complex(re, -im);
	}

	/**
	 * Returns difference between z1 and z2
	 */
	public static Complex sub(Complex z1, Complex z2) {
		return new Complex(z1.real - z2.real, z1.imag - z2.imag);
	}

	/**
	 * Returns the z - scalar value
	 */
	public static Complex sub(Complex z, double s) {
		return new Complex(z.real - s, z.imag);
	}

	/**
	 * Returns the scalar value - z
	 */
	public static Complex sub(double s, Complex z) {
		return new Complex(s - z.real, -z.imag);
	}

	/**
	 * Returns the tan of z
	 */
	public static Complex tan(Complex z) {
		return div(sin(z), cos(z));
	}

	/**
	 * Returns the tanh of z
	 */
	public static Complex tanh(Complex z) {
		return div(sinh(z), cosh(z));
	}

	// #################################################################
	// Instance members
	// #################################################################
	
	/**
	 * Constructor 
	 */
	public Complex(){
		real = imag = 0;
	}

	/**
	 * Constructor
	 */
	public Complex(Complex z) {
		real = z.real;
		imag = z.imag;
	}

	/**
	 * Constructor
	 */
	public Complex(double real) {
		this.real = real;
		imag = 0;
	}

	/**
	 * Constructor
	 */
	public Complex(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}

	/**
	 * Add a complex value to this and return result
	 */
	public Complex add(Complex z) {
		return new Complex(real + z.real, imag + z.imag);
	}

	/**
	 * Add a scalar to this and return result
	 */	
	public Complex add(double s) {
		return new Complex(real + s, imag);
	}

	/**
	 * Divide this by a complex number and return result
	 */
	public Complex div(Complex z) {
		Complex answer;
		double bottom = mag(z);
		Complex top = mult(conj(z));
		answer = top.div(bottom);
		return answer;
	}

	/**
	 * Divide this by a scalar and return result
	 */
	public Complex div(double s) {
		return new Complex(real/s, imag/s);
	}

	/**
	 * Compare 2 complex numbers for equality
	 */
	public boolean equals(Complex z) {
		if (real == z.real && imag == z.imag)
			return true;
		else
			return false;
	}

	/**
	 * Multiply this by z and return the result
	 */
	public double mag() {
		return Math.sqrt(real * real + imag * imag);
	}
	
	/**
	 * Multiply this by z and return the result
	 */
	public Complex mult(Complex z) {
		return new Complex(real * z.real - imag * z.imag,
				real * z.imag + imag * z.real);
	}

	/**
	 * Multiply this by a scalar and return result
	 */
	public Complex mult(double s) {
		return new Complex(real * s, imag * s);
	}

	/**
	 * Return a complex number of length 1 and the same angle
	 * @return
	 */
	public Complex norm(){
		double mag = Math.sqrt(real * real + imag * imag);
		return new Complex(real/mag, imag/mag);
	}

	/**
	 * Subtract z from this and return the result
	 */
	public Complex sub(Complex z) {
		return new Complex(real - z.real, imag - z.imag);
	}

	/**
	 * Subtract a scalar from this and return result
	 */
	public Complex sub(double s) {
		return new Complex(real - s, imag);
	}

	/**
	 * Compares two complex numbers for equality
	 */
	@Override
	public boolean equals(Object obj){
		if(obj == null || !(obj instanceof Complex))
			return false;
		Complex v = (Complex)obj;
		return real == v.real && imag == v.imag;
	}

	/**
	 * Creates a string representation of this complex number
	 */
	@Override
	public String toString(){
		return "{ " + real + (imag < 0 ? " - " : " + ") + Math.abs(imag) + "i }";
	}

}
