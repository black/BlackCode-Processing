// FaceDetect.h: interface for the CFaceDetect class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_FACEDETECT_H__8E076058_8A41_4AEA_9BB4_55ED6871A4E1__INCLUDED_)
#define AFX_FACEDETECT_H__8E076058_8A41_4AEA_9BB4_55ED6871A4E1__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <jni.h>
#include "pFaceDetect_PFaceDetect.h"
#include "cv.h"
#include "highgui.h"
#include "cxcore.h"

class CFaceDetect
{

private:
	CvHaarClassifierCascade *pCascade;
	CvMemStorage *pStorage;
	int capW;
	int capH;

public:
	CvSeq *pFaces;
	double scale;

	CFaceDetect(void);
	virtual ~CFaceDetect(void);
	void init(const char* _p, int _w, int _h);
	void check(int* _p);
};

#endif // !defined(AFX_FACEDETECT_H__8E076058_8A41_4AEA_9BB4_55ED6871A4E1__INCLUDED_)
