/*
 * Copyright (c) 2011-2015, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.processing;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.Planar;
import processing.core.PImage;

/**
 * Functions for converting between BoofCV and Processing image data types
 *
 * @author Peter Abeles
 */
public class ConvertProcessing {

	public static void convertFromRGB( PImage input , ImageBase output ) {
		if( output instanceof GrayU8 ) {
			convert_RGB_U8(input,(GrayU8)output);
		} else if( output instanceof GrayF32) {
			convert_RGB_F32(input, (GrayF32) output);
		} else if( output instanceof Planar ) {
			Class bandType = ((Planar)output).getBandType();
			if( bandType == GrayF32.class ) {
				convert_RGB_PF32(input,(Planar)output);
			} else if( bandType == GrayU8.class ) {
				convert_RGB_PU8(input, (Planar) output);
			}
		}
	}

	public static void convert_RGB_F32( PImage input , GrayF32 output ) {
		output.reshape(input.width,input.height);

		int indexInput = 0;
		for( int y = 0; y < input.height; y++ ) {
			int indexOut = output.startIndex + y*output.stride;
			for( int x = 0; x < input.width; x++ ,indexInput++,indexOut++) {
				int value = input.pixels[indexInput];

				int r = ( value >> 16 ) & 0xFF;
				int g = ( value >> 8 ) & 0xFF;
				int b = value & 0xFF;

				output.data[indexOut] = (r+g+b)/3.0f;
			}
		}
	}

	public static void convert_RGB_U8( PImage input , GrayU8 output ) {
		output.reshape(input.width,input.height);

		int indexInput = 0;
		for( int y = 0; y < input.height; y++ ) {
			int indexOut = output.startIndex + y*output.stride;
			for( int x = 0; x < input.width; x++ ,indexInput++,indexOut++) {
				int value = input.pixels[indexInput];

				int r = ( value >> 16 ) & 0xFF;
				int g = ( value >> 8 ) & 0xFF;
				int b = value & 0xFF;

				output.data[indexOut] = (byte)((r+g+b)/3);
			}
		}
	}

	public static void convert_F32_RGB( GrayF32 input , PImage output ) {

		int indexOutput = 0;
		for( int y = 0; y < input.height; y++ ) {
			int indexInput = input.startIndex + y*input.stride;
			for( int x = 0; x < input.width; x++ ,indexOutput++,indexInput++) {
				int value = (int)input.data[indexInput];

				output.pixels[indexOutput] = 0xFF << 24 | value << 16 | value << 8 | value ;
			}
		}
	}

	public static void convert_U8_RGB( GrayU8 input , PImage output ) {

		int indexOutput = 0;
		for( int y = 0; y < input.height; y++ ) {
			int indexInput = input.startIndex + y*input.stride;
			for( int x = 0; x < input.width; x++ ,indexOutput++,indexInput++) {
				int value = input.data[indexInput]&0xFF;

				output.pixels[indexOutput] = 0xFF << 24 | value << 16 | value << 8 | value ;
			}
		}
	}

	public static void convert_PF32_RGB(Planar<GrayF32> input , PImage output ) {

		GrayF32 red = input.getBand(0);
		GrayF32 green = input.getBand(1);
		GrayF32 blue = input.getBand(2);

		int indexOutput = 0;
		for( int y = 0; y < input.height; y++ ) {
			int indexInput = input.startIndex + y*input.stride;
			for( int x = 0; x < input.width; x++ ,indexOutput++,indexInput++) {
				int r = (int)red.data[indexInput];
				int g = (int)green.data[indexInput];
				int b = (int)blue.data[indexInput];

				output.pixels[indexOutput] = 0xFF << 24 | r << 16 | g << 8 | b ;
			}
		}
	}

	public static void convert_PU8_RGB(Planar<GrayU8> input , PImage output ) {

		GrayU8 red = input.getBand(0);
		GrayU8 green = input.getBand(1);
		GrayU8 blue = input.getBand(2);

		int indexOutput = 0;
		for( int y = 0; y < input.height; y++ ) {
			int indexInput = input.startIndex + y*input.stride;
			for( int x = 0; x < input.width; x++ ,indexOutput++,indexInput++) {
				int r = (red.data[indexInput]&0xFF);
				int g = (green.data[indexInput]&0xFF);
				int b = (blue.data[indexInput]&0xFF);

				output.pixels[indexOutput] = 0xFF << 24 | r << 16 | g << 8 | b;
			}
		}
	}

	public static void convert_RGB_PF32(PImage input , Planar<GrayF32> output ) {
		output.reshape(input.width,input.height);

		GrayF32 red = output.getBand(0);
		GrayF32 green = output.getBand(1);
		GrayF32 blue = output.getBand(2);

		int indexInput = 0;
		for( int y = 0; y < input.height; y++ ) {
			int indexOutput = output.startIndex + y*output.stride;
			for( int x = 0; x < input.width; x++ ,indexOutput++,indexInput++) {
				int value = input.pixels[indexInput];

				red.data[indexOutput] = (value>>16)&0xFF;
				green.data[indexOutput] = (value>>8)&0xFF;
				blue.data[indexOutput] = value&0xFF;
			}
		}
	}

	public static void convert_RGB_PU8(PImage input , Planar<GrayU8> output ) {
		output.reshape(input.width,input.height);

		GrayU8 red = output.getBand(0);
		GrayU8 green = output.getBand(1);
		GrayU8 blue = output.getBand(2);

		int indexInput = 0;
		for( int y = 0; y < input.height; y++ ) {
			int indexOutput = output.startIndex + y*output.stride;
			for( int x = 0; x < input.width; x++ ,indexOutput++,indexInput++) {
				int value = input.pixels[indexInput];

				red.data[indexOutput] = (byte)(value>>16);
				green.data[indexOutput] = (byte)(value>>8);
				blue.data[indexOutput] = (byte)value;
			}
		}
	}
}
