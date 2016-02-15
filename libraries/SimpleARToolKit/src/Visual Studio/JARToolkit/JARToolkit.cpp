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
#include "JARToolkit.h"

// AR Toolkit Includes Begin
#include <AR/param.h>
#include <AR/ar.h>
#include <AR/gsub.h>
#include <AR/arMulti.h>
#include <AR/config.h>

extern ARParam*			wparam;
extern ARParam*			cparam;
extern ARMarkerInfo*	marker_info;
extern int				marker_num;

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARInitCparam
* Signature: ()I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_initCparam(JNIEnv *, jobject)
{
	if(cparam==NULL)
		return -1;
	return arInitCparam( cparam );
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARLoadPattern
* Signature: (Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_loadPattern(JNIEnv *env, jobject, jstring jfilename)
{
	const char *filename = env->GetStringUTFChars(jfilename, 0);

	return arLoadPatt((char *)filename);
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARDetectMarker
* Signature: ([II)[I
*/
JNIEXPORT jintArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_detectMarker___3II(JNIEnv *env , jobject, jintArray image, jint thresh)
{
	jsize len = env->GetArrayLength(image);
	marker_num = 0;

	jint *buffer = env->GetIntArrayElements(image, 0);

	if( arDetectMarker((ARUint8 *)buffer, thresh, &marker_info, &marker_num) < 0 )
		return 0;

	env->ReleaseIntArrayElements(image, buffer, 0);
	jintArray ids = env->NewIntArray(marker_num);
	if(marker_num==0)
		return ids;

	buffer = env->GetIntArrayElements(ids, 0);

	for(int i=0 ; i<marker_num ; i++)
	{
		buffer[i] = marker_info[i].id;
	}

	env->ReleaseIntArrayElements(ids, buffer, 0);
	return ids;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARDetectMarker
* Signature: (JI)[I
*/
JNIEXPORT jintArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_detectMarker__JI(JNIEnv *env, jobject, jlong image, jint thresh)
{
	marker_num = 0;

	if( arDetectMarker((ARUint8*)image, thresh, &marker_info, &marker_num) < 0 )
		return 0;
	jintArray ids = env->NewIntArray(marker_num);
	if(marker_num==0)
		return ids;

	jint *buffer = env->GetIntArrayElements(ids, 0);

	for(int i=0 ; i<marker_num ; i++)
	{
		buffer[i] = marker_info[i].id;
	}

	env->ReleaseIntArrayElements(ids, buffer, 0);
	return ids;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARDetectMarkerLite
* Signature: ([II)[I
*/
JNIEXPORT jintArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_detectMarkerLite___3II(JNIEnv *env, jobject, jintArray image, jint thresh)
{
	jsize len = env->GetArrayLength(image);
	marker_num=0;

	jint *buffer = env->GetIntArrayElements(image, 0);

	if( arDetectMarkerLite((ARUint8 *)buffer, thresh, &marker_info, &marker_num) < 0 )
		return 0;
	env->ReleaseIntArrayElements(image, buffer, 0);
	jintArray ids = env->NewIntArray(marker_num);
	if(marker_num==0)
		return ids;

	buffer = env->GetIntArrayElements(ids, 0);

	for(int i=0 ; i<marker_num ; i++)
	{
		buffer[i] = marker_info[i].id;
	}

	env->ReleaseIntArrayElements(ids, buffer, 0);
	return ids;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARDetectMarkerLite
* Signature: (JI)[I
*/
JNIEXPORT jintArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_detectMarkerLite__JI(JNIEnv *env, jobject, jlong image, jint thresh)
{
	marker_num = 0;

	if( arDetectMarkerLite((ARUint8 *)image, thresh, &marker_info, &marker_num) < 0 )
		return 0;
	jintArray ids = env->NewIntArray(marker_num);
	if(marker_num==0)
		return ids;

	jint *buffer = env->GetIntArrayElements(ids, 0);

	for(int i=0 ; i<marker_num ; i++)
	{
		buffer[i] = marker_info[i].id;
	}

	env->ReleaseIntArrayElements(ids, buffer, 0);
	return ids;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARGetCamTransMatrix
* Signature: ()[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrix__(JNIEnv *env, jobject)
{
	jdoubleArray matrix = env->NewDoubleArray(16);

	jdouble *buffer = env->GetDoubleArrayElements(matrix, 0);

	//	argConvGLcpara( cparam, 100.0, 10000.0, buffer );
	argConvGLcpara(cparam, AR_GL_CLIP_NEAR, AR_GL_CLIP_FAR, buffer);

	env->ReleaseDoubleArrayElements(matrix, buffer, 0);
	return matrix;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getCamTransMatrix
* Signature: ([D)V
*/
JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrix___3D(JNIEnv *env, jobject, jdoubleArray inMatrix)
{
	jdouble *matrix = env->GetDoubleArrayElements(inMatrix, 0);

	//	argConvGLcpara( cparam, 100.0, 10000.0, matrix );
	argConvGLcpara(cparam, AR_GL_CLIP_NEAR, AR_GL_CLIP_FAR, matrix);

	env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getCamTransMatrixJava3D
* Signature: ()[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrixJava3D__(JNIEnv *env, jobject)
{
	jdoubleArray matrix = env->NewDoubleArray(16);

	jdouble *buffer = env->GetDoubleArrayElements(matrix, 0);

	ARParam test;
	test.xsize = cparam->xsize;
	test.ysize = cparam->ysize;

	// Center shound be in the middle
	test.dist_factor[0] = test.xsize/2.0;
	test.dist_factor[1] = test.ysize/2.0;

	test.dist_factor[2] = cparam->dist_factor[2];
	test.dist_factor[3] = cparam->dist_factor[3];

	test.mat[0][0] = cparam->mat[0][0];
	test.mat[0][1] = cparam->mat[0][1];
	test.mat[0][2] = test.xsize/2.0;
	test.mat[0][3] = cparam->mat[0][3];
	test.mat[1][0] = cparam->mat[1][0];
	test.mat[1][1] = cparam->mat[1][1];
	test.mat[1][2] = test.ysize/2.0;
	test.mat[1][3] = cparam->mat[1][3];
	test.mat[2][0] = cparam->mat[2][0];
	test.mat[2][1] = cparam->mat[2][1];
	test.mat[2][2] = cparam->mat[2][2];
	test.mat[2][3] = cparam->mat[2][3];

	//	argConvGLcpara( &test, 100.0, 10000.0, buffer );
	argConvGLcpara(&test, AR_GL_CLIP_NEAR, AR_GL_CLIP_FAR, buffer);

	buffer[11]  = -buffer[11];
	buffer[14]  = -buffer[14];

	env->ReleaseDoubleArrayElements(matrix, buffer, 0);
	return matrix;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getCamTransMatrixJava3D
* Signature: ([D)V
*/
JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrixJava3D___3D(JNIEnv *env, jobject, jdoubleArray inMatrix)
{
	jdouble *matrix = env->GetDoubleArrayElements(inMatrix, 0);

	ARParam test;
	test.xsize = cparam->xsize;
	test.ysize = cparam->ysize;

	// Center shound be in the middle
	test.dist_factor[0] = test.xsize/2.0;
	test.dist_factor[1] = test.ysize/2.0;

	test.dist_factor[2] = cparam->dist_factor[2];
	test.dist_factor[3] = cparam->dist_factor[3];

	test.mat[0][0] = cparam->mat[0][0];
	test.mat[0][1] = cparam->mat[0][1];
	test.mat[0][2] = test.xsize/2.0;
	test.mat[0][3] = cparam->mat[0][3];
	test.mat[1][0] = cparam->mat[1][0];
	test.mat[1][1] = cparam->mat[1][1];
	test.mat[1][2] = test.ysize/2.0;
	test.mat[1][3] = cparam->mat[1][3];
	test.mat[2][0] = cparam->mat[2][0];
	test.mat[2][1] = cparam->mat[2][1];
	test.mat[2][2] = cparam->mat[2][2];
	test.mat[2][3] = cparam->mat[2][3];

	//	argConvGLcpara( &test, 100.0, 10000.0, matrix );
	argConvGLcpara(&test, AR_GL_CLIP_NEAR, AR_GL_CLIP_FAR, matrix);

	matrix[11]  = -matrix[11];
	matrix[14]  = -matrix[14];

	env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getCamTransMatrixJava3D
* Signature: (DD)[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrixJava3D__DD(JNIEnv *env, jobject, jdouble corrx, jdouble corry)
{
	jdoubleArray matrix = env->NewDoubleArray(16);

	jdouble *buffer = env->GetDoubleArrayElements(matrix, 0);

	ARParam test;
	test.xsize = cparam->xsize;
	test.ysize = cparam->ysize;

	// Center shound be in the middle
	test.dist_factor[0] = test.xsize/2.0 + corrx;
	test.dist_factor[1] = test.ysize/2.0 + corry;

	test.dist_factor[2] = cparam->dist_factor[2];
	test.dist_factor[3] = cparam->dist_factor[3];

	test.mat[0][0] = cparam->mat[0][0];
	test.mat[0][1] = cparam->mat[0][1];
	test.mat[0][2] = test.xsize/2.0 + corrx;
	test.mat[0][3] = cparam->mat[0][3];
	test.mat[1][0] = cparam->mat[1][0];
	test.mat[1][1] = cparam->mat[1][1];
	test.mat[1][2] = test.ysize/2.0 + corry;
	test.mat[1][3] = cparam->mat[1][3];
	test.mat[2][0] = cparam->mat[2][0];
	test.mat[2][1] = cparam->mat[2][1];
	test.mat[2][2] = cparam->mat[2][2];
	test.mat[2][3] = cparam->mat[2][3];

	//	argConvGLcpara( &test, 100.0, 10000.0, buffer );
	argConvGLcpara(&test, AR_GL_CLIP_NEAR, AR_GL_CLIP_FAR, buffer);

	buffer[11]  = -buffer[11];
	buffer[14]  = -buffer[14];

	env->ReleaseDoubleArrayElements(matrix, buffer, 0);
	return matrix;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getCamTransMatrixJava3D
* Signature: ([DDD)V
*/
JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrixJava3D___3DDD(JNIEnv *env, jobject, jdoubleArray inMatrix, jdouble corrx, jdouble corry)
{
	jdouble *matrix = env->GetDoubleArrayElements(inMatrix, 0);

	ARParam test;
	test.xsize = cparam->xsize;
	test.ysize = cparam->ysize;

	// Center shound be in the middle
	test.dist_factor[0] = test.xsize/2.0 + corrx;
	test.dist_factor[1] = test.ysize/2.0 + corry;

	test.dist_factor[2] = cparam->dist_factor[2];
	test.dist_factor[3] = cparam->dist_factor[3];

	test.mat[0][0] = cparam->mat[0][0];
	test.mat[0][1] = cparam->mat[0][1];
	test.mat[0][2] = test.xsize/2.0 + corrx;
	test.mat[0][3] = cparam->mat[0][3];
	test.mat[1][0] = cparam->mat[1][0];
	test.mat[1][1] = cparam->mat[1][1];
	test.mat[1][2] = test.ysize/2.0 + corry;
	test.mat[1][3] = cparam->mat[1][3];
	test.mat[2][0] = cparam->mat[2][0];
	test.mat[2][1] = cparam->mat[2][1];
	test.mat[2][2] = cparam->mat[2][2];
	test.mat[2][3] = cparam->mat[2][3];

	//	argConvGLcpara( &test, 100.0, 10000.0, matrix );
	argConvGLcpara(&test, AR_GL_CLIP_NEAR, AR_GL_CLIP_FAR, matrix);

	matrix[11]  = -matrix[11];
	matrix[14]  = -matrix[14];

	env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARGetTransMatrix
* Signature: (IIFF)[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrix__IIFF(JNIEnv *env, jobject, jint patternID, jint patt_width, jfloat patt_centerX, jfloat patt_centerY)
{
	double patt_trans[3][4];
	int j, i;
	double patt_center[2] = {patt_centerX, patt_centerY};

	jdoubleArray matrix = env->NewDoubleArray(16);

	jdouble *buffer = env->GetDoubleArrayElements(matrix, 0);

	for(i=1 ; i<15 ; i++)
		buffer[i] = 0.0;

	buffer[0] = 1.0;
	buffer[5] = 1.0;
	buffer[10] = 1.0;
	buffer[15] = 1.0;

	for (j = 0; j < marker_num; j++)
	{
		if (patternID == marker_info[j].id)
			break;
	}

	if (j >= marker_num)
	{
		env->ReleaseDoubleArrayElements(matrix, buffer, 0);
		return matrix;
	}

	if (arGetTransMat(&marker_info[j], patt_center, patt_width, patt_trans) < 0)
	{
		env->ReleaseDoubleArrayElements(matrix, buffer, 0);
		return matrix;
	}
	/*
	for( j = 0; j < 3; j++ )
	{
	for( i = 0; i < 4; i++ )
	{
	buffer[i*4+j] = patt_trans[j][i];
	}
	}
	*/
	argConvGlpara(patt_trans, buffer);
	env->ReleaseDoubleArrayElements(matrix, buffer, 0);
	return matrix;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getTransMatrix
* Signature: ([DIIFF)Z
*/
JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrix___3DIIFF(JNIEnv *env, jobject, jdoubleArray inMatrix, jint patternID, jint patt_width, jfloat patt_centerX, jfloat patt_centerY)
{
	double patt_trans[3][4];
	int j, i;
	double patt_center[2] = {patt_centerX, patt_centerY};

	jdouble *matrix = env->GetDoubleArrayElements(inMatrix, 0);

	for (j = 0; j < marker_num; j++)
	{
		if (patternID == marker_info[j].id)
			break;
	}

	if (j >= marker_num)
	{
		env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
		return false;
	}

	if (arGetTransMat(&marker_info[j], patt_center, patt_width, patt_trans) < 0)
	{
		env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
		return false;
	}
	/*
	for( j = 0; j < 3; j++ )
	{
	for( i = 0; i < 4; i++ )
	{
	matrix[i*4+j] = patt_trans[j][i];
	}
	}

	matrix[0*4+3] = matrix[1*4+3] = matrix[2*4+3] = 0.0;
	matrix[3*4+3] = 1.0;
	*/
	argConvGlpara(patt_trans, matrix);
	env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
	return true;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARGetTransMatrixCont
* Signature: (IIFF[D)[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixCont__IIFF_3D(JNIEnv *env, jobject, jint patternID, jint patt_width, jfloat patt_centerX, jfloat patt_centerY, jdoubleArray conv)
{
	double patt_trans[3][4];
	int k, j, i;
	double patt_center[2] = {patt_centerX, patt_centerY};
	double prev_conv[3][4];

	jdoubleArray matrix = env->NewDoubleArray(16);

	jdouble *buffer = env->GetDoubleArrayElements(matrix, 0);

	for (i=1 ;i<15; i++)
		buffer[i] = 0.0;

	buffer[0] = 1.0;
	buffer[5] = 1.0;
	buffer[10] = 1.0;
	buffer[15] = 1.0;

	k = -1;
	for (j = 0; j < marker_num; j++)
	{
		if (patternID == marker_info[j].id)
		{
			if (k = -1) k = j;
			else if (marker_info[k].cf < marker_info[j].cf ) k = j;
		}
	}

	if (k == -1) 
	{
		env->ReleaseDoubleArrayElements(matrix, buffer, 0);
		return matrix;
	}

	jdouble *pre_conv = env->GetDoubleArrayElements(conv, 0);
	for (j=0; j < 3; j++)
	{
		for (i=0; i < 4; i++)
		{
			prev_conv[j][i] = pre_conv[i*4+j];
		}
	}
	env->ReleaseDoubleArrayElements(conv, pre_conv, 0);
	if (arGetTransMatCont(&marker_info[k], prev_conv, patt_center,patt_width, patt_trans) < 0)
	{
		env->ReleaseDoubleArrayElements(matrix, buffer, 0);
		return matrix;
	}
	/*
	for( j = 0; j < 3; j++ )
	{
	for( i = 0; i < 4; i++ )
	{
	buffer[i*4+j] = patt_trans[j][i];
	}
	}

	buffer[0*4+3] = buffer[1*4+3] = buffer[2*4+3] = 0.0;
	buffer[3*4+3] = 1.0;
	*/
	argConvGlpara(patt_trans, buffer);
	env->ReleaseDoubleArrayElements(matrix, buffer, 0);
	return matrix;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getTransMatrixCont
* Signature: ([DIIFF[D)Z
*/
JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixCont___3DIIFF_3D(JNIEnv *env, jobject, jdoubleArray inMatrix, jint patternID, jint patt_width, jfloat patt_centerX, jfloat patt_centerY, jdoubleArray conv)
{
	double patt_trans[3][4];
	int k, j, i;
	double patt_center[2] = {patt_centerX, patt_centerY};
	double prev_conv[3][4];

	jdouble *matrix = env->GetDoubleArrayElements(inMatrix, 0);

	k = -1;
	for (j = 0; j < marker_num; j++)
	{
		if (patternID == marker_info[j].id)
		{
			if (k = -1) k = j;
			else if (marker_info[k].cf < marker_info[j].cf ) k = j;
		}
	}

	if (k == -1) 
	{
		env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
		return false;
	}

	jdouble *pre_conv = env->GetDoubleArrayElements(conv, 0);
	for (j=0; j<3; j++)
	{
		for( i=0; i<4; i++)
		{
			prev_conv[j][i] = pre_conv[i*4+j];
		}
	}
	env->ReleaseDoubleArrayElements(conv, pre_conv, 0);

	if (arGetTransMatCont(&marker_info[k], prev_conv, patt_center, patt_width, patt_trans) < 0)
	{
		env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
		return false;
	}

	/*
	for( j = 0; j < 3; j++ )
	{
	for( i = 0; i < 4; i++ )
	{
	matrix[i*4+j] = patt_trans[j][i];
	}
	}

	matrix[0*4+3] = matrix[1*4+3] = matrix[2*4+3] = 0.0;
	matrix[3*4+3] = 1.0;
	*/
	argConvGlpara(patt_trans, matrix);
	env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
	return true;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getTransMatrixJava3D
* Signature: (IIFF)[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixJava3D__IIFF(JNIEnv *env, jobject, jint patternID, jint patt_width, jfloat patt_centerX, jfloat patt_centerY)
{
	double patt_trans[3][4];
	int j, i;
	double patt_center[2] = {patt_centerX, patt_centerY};

	jdoubleArray matrix = env->NewDoubleArray(16);

	jdouble *buffer = env->GetDoubleArrayElements(matrix, 0);

	for(i=1 ; i<15 ; i++)
		buffer[i] = 0.0;

	buffer[0] = 1.0;
	buffer[5] = 1.0;
	buffer[10] = 1.0;
	buffer[15] = 1.0;

	for( j = 0; j < marker_num; j++ )
	{
		if( patternID == marker_info[j].id )
			break;
	}

	if( j >= marker_num )
	{
		env->ReleaseDoubleArrayElements(matrix, buffer, 0);
		return matrix;
	}

	if( arGetTransMat(&marker_info[j], patt_center, patt_width, patt_trans) < 0 )
	{
		env->ReleaseDoubleArrayElements(matrix, buffer, 0);
		return matrix;
	}

	for( j = 0; j < 3; j++ )
	{
		for( i = 0; i < 4; i++ )
		{
			buffer[i*4+j] = patt_trans[j][i];
		}
	}
	buffer[1] = -buffer[1];
	buffer[4] = -buffer[4];
	buffer[6] = -buffer[6];
	buffer[9] = -buffer[9];
	buffer[13] = -buffer[13];

	env->ReleaseDoubleArrayElements(matrix, buffer, 0);
	return matrix;
}
/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getTransMatrixJava3D
* Signature: ([DIIFF)Z
*/
JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixJava3D___3DIIFF(JNIEnv *env, jobject, jdoubleArray inMatrix, jint patternID, jint patt_width, jfloat patt_centerX, jfloat patt_centerY)
{
	double patt_trans[3][4];
	int j, i;
	double patt_center[2] = {patt_centerX, patt_centerY};

	jdouble *matrix = env->GetDoubleArrayElements(inMatrix, 0);

	for( j = 0; j < marker_num; j++ )
	{
		if( patternID == marker_info[j].id )
			break;
	}

	if( j >= marker_num )
	{
		env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
		return false;
	}

	if( arGetTransMat(&marker_info[j], patt_center, patt_width, patt_trans) < 0 )
	{
		env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
		return false;
	}

	for( j = 0; j < 3; j++ )
	{
		for( i = 0; i < 4; i++ )
		{
			matrix[i*4+j] = patt_trans[j][i];
		}
	}

	matrix[1] = -matrix[1];
	matrix[4] = -matrix[4];
	matrix[6] = -matrix[6];
	matrix[9] = -matrix[9];
	matrix[13] = -matrix[13];

	matrix[0*4+3] = matrix[1*4+3] = matrix[2*4+3] = 0.0;
	matrix[3*4+3] = 1.0;

	env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
	return true;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getTransMatrixContJava3D
* Signature: (IIFF[D)[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixContJava3D__IIFF_3D(JNIEnv *env, jobject, jint patternID, jint patt_width, jfloat patt_centerX, jfloat patt_centerY, jdoubleArray conv)
{
	double patt_trans[3][4];
	int j, i;
	double patt_center[2] = {patt_centerX, patt_centerY};
	double prev_conv[3][4];

	jdoubleArray matrix = env->NewDoubleArray(16);

	jdouble *buffer = env->GetDoubleArrayElements(matrix, 0);

	for(i=1 ; i<15 ; i++)
		buffer[i] = 0.0;

	buffer[0] = 1.0;
	buffer[5] = 1.0;
	buffer[10] = 1.0;
	buffer[15] = 1.0;

	for( j = 0; j < marker_num; j++ )
	{
		if( patternID == marker_info[j].id )
			break;
	}

	if( j >= marker_num )
	{
		env->ReleaseDoubleArrayElements(matrix, buffer, 0);
		return matrix;
	}

	jdouble *pre_conv = env->GetDoubleArrayElements(conv, 0);
	for(j=0 ; j<3 ; j++)
	{
		for(i=0 ; i<4 ; i++)
		{
			prev_conv[j][i] = pre_conv[i*4+j];
		}
	}
	env->ReleaseDoubleArrayElements(conv, pre_conv, 0);

	if( arGetTransMatCont(&marker_info[j], prev_conv, patt_center,patt_width, patt_trans) < 0 )
	{
		env->ReleaseDoubleArrayElements(matrix, buffer, 0);
		return matrix;
	}

	for( j = 0; j < 3; j++ )
	{
		for( i = 0; i < 4; i++ )
		{
			buffer[i*4+j] = patt_trans[j][i];
		}
	}

	buffer[1] = -buffer[1];
	buffer[4] = -buffer[4];
	buffer[6] = -buffer[6];
	buffer[9] = -buffer[9];
	buffer[13] = -buffer[13];

	buffer[0*4+3] = buffer[1*4+3] = buffer[2*4+3] = 0.0;
	buffer[3*4+3] = 1.0;

	env->ReleaseDoubleArrayElements(matrix, buffer, 0);
	return matrix;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    getTransMatrixContJava3D
* Signature: ([DIIFF[D)Z
*/
JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixContJava3D___3DIIFF_3D(JNIEnv *env, jobject, jdoubleArray inMatrix, jint patternID, jint patt_width, jfloat patt_centerX, jfloat patt_centerY, jdoubleArray conv)
{
	double patt_trans[3][4];
	int j, i;
	double patt_center[2] = {patt_centerX, patt_centerY};
	double prev_conv[3][4];

	jdouble *matrix = env->GetDoubleArrayElements(inMatrix, 0);

	for( j = 0; j < marker_num; j++ )
	{
		if( patternID == marker_info[j].id )
			break;
	}

	if( j >= marker_num )
	{
		env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
		return false;
	}

	jdouble *pre_conv = env->GetDoubleArrayElements(conv, 0);
	for(j=0 ; j<3 ; j++)
	{
		for(i=0 ; i<4 ; i++)
		{
			prev_conv[j][i] = pre_conv[i*4+j];
		}
	}
	env->ReleaseDoubleArrayElements(conv, pre_conv, 0);

	if( arGetTransMatCont(&marker_info[j], prev_conv, patt_center,patt_width, patt_trans) < 0 )
	{
		env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
		return false;
	}

	for( j = 0; j < 3; j++ )
	{
		for( i = 0; i < 4; i++ )
		{
			matrix[i*4+j] = patt_trans[j][i];
		}
	}

	matrix[1] = -matrix[1];
	matrix[4] = -matrix[4];
	matrix[6] = -matrix[6];
	matrix[9] = -matrix[9];
	matrix[13] = -matrix[13];

	matrix[0*4+3] = matrix[1*4+3] = matrix[2*4+3] = 0.0;
	matrix[3*4+3] = 1.0;

	env->ReleaseDoubleArrayElements(inMatrix, matrix, 0);
	return true;
}
/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARFreePattern
* Signature: (I)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_freePattern(JNIEnv *, jobject, jint pattNo)
{
	return arFreePatt(pattNo);
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARActivatePattern
* Signature: (I)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_activatePattern(JNIEnv *, jobject, jint pattNo)
{
	return arActivatePatt(pattNo);
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARDeactivatePattern
* Signature: (I)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_deactivatePattern(JNIEnv *, jobject, jint pattNo)
{
	return arDeactivatePatt(pattNo);
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARParamLoad
* Signature: (Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_paramLoad(JNIEnv *env, jobject, jstring jfilename)
{
	if(wparam!=NULL)
		delete wparam;
	wparam = new ARParam();

	const char *filename = env->GetStringUTFChars(jfilename, 0);

	jint retval = arParamLoad((char *)filename, 1, wparam);
	env->ReleaseStringUTFChars(jfilename, filename);

	return retval;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARParamChangeSize
* Signature: (II)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_paramChangeSize(JNIEnv *env, jobject, jint xsize, jint ysize)
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
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    multiReadConfigFile
* Signature: (Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiReadConfigFile(JNIEnv *env, jobject, jstring jfilename)
{
	return -1;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    multiGetTransMat
* Signature: (II)[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiGetTransMat__II(JNIEnv *env, jobject, jint jmultipatternid, jint markernumber)
{
	return NULL;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    multiGetTransMat
* Signature: ([DII)Z
*/
JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiGetTransMat___3DII(JNIEnv *env, jobject, jdoubleArray inMatrix, jint jmultipatternid, jint markernumber)
{
	return false;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    multiGetTransMatJava3D
* Signature: (II)[D
*/
JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiGetTransMatJava3D__II(JNIEnv *env, jobject, jint jmultipatternid, jint markernumber)
{
	return NULL;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    multiGetTransMatJava3D
* Signature: ([DII)Z
*/
JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiGetTransMatJava3D___3DII(JNIEnv *env, jobject, jdoubleArray inMatrix, jint jmultipatternid, jint markernumber)
{
	return false;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    multiPatternActivate
* Signature: (I)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiPatternActivate(JNIEnv *env, jobject, jint jmultipatternid)
{
	return -1;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    arMultiDeactivate
* Signature: (I)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_arMultiDeactivate(JNIEnv *env, jobject, jint jmultipatternid)
{
	return -1;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    arMultiFreeConfig
* Signature: (I)I
*/
JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_arMultiFreeConfig(JNIEnv *env, jobject, jint jmultipatternid)
{
	return -1;
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARParamDisplay
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_paramDisplay(JNIEnv *, jobject)
{
	arParamDisp( cparam );
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARUtilTimer
* Signature: ()D
*/
JNIEXPORT jdouble JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_utilTimer(JNIEnv *, jclass)
{
	return arUtilTimer();
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARUtilTimerReset
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_utilTimerReset(JNIEnv *, jclass)
{
	arUtilTimerReset();
}

/*
* Class:     com_clab_artoolkit_port_JARToolkit
* Method:    JARUtilSleep
* Signature: (I)V
*/
JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_utilSleep(JNIEnv *, jclass, jint msec)
{
	arUtilSleep( msec );
}
