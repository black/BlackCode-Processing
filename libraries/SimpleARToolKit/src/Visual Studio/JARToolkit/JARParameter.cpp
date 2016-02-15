// jARToolKit Version 2.0
//
// A Java-binding to the Augmented Reality library ARToolKit
// supporting GL4Java, JOGL and Java3D
//
// Copyright (C) 2004 Jörg Stöcklein <ozone_abandon@sf.net>
//					  Tim Schmidt <tisch@sf.net>
//
// www: jartoolkit.sf.net
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

#include <jni.h>
#include "JARParameter.h"

// AR Toolkit Includes Begin
#include <AR/param.h>
#include <AR/ar.h>
#include <AR/arMulti.h>
#include <AR/config.h>

#include "common.h"
extern ARParam	*wparam;
extern ARParam	*cparam;
/*
* Class:     JARParameter
* Method:    JARParamChangeSize
* Signature: (II)I
*/
JNIEXPORT jint JNICALL Java_com_clab_artoolkit_port_JARParameter_JARParamChangeSize(JNIEnv *env, jobject obj, jint xsize, jint ysize)
{
	if(wparam==NULL)
		return -1;
	if(cparam!=NULL)
		delete cparam;

	cparam = new ARParam();
	jint retval = arParamChangeSize( wparam, xsize, ysize, cparam );

	return retval;
}


/*
* Class:     JARParameter
* Method:    JARParamLoad
* Signature: (Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_com_clab_artoolkit_port_JARParameter_JARParamLoad(JNIEnv *env, jobject obj, jstring jfilename)
{
	if(wparam!=NULL)
		delete wparam;
	wparam = new ARParam();

	const char *filename = env->GetStringUTFChars(jfilename, 0);

	jint retval = arParamLoad((char *)filename, 1, wparam);
	env->ReleaseStringUTFChars(jfilename, filename);

	return retval;
}
