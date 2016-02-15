// FaceDetect.cpp: implementation of the CFaceDetect class.
//
//////////////////////////////////////////////////////////////////////

#include "FaceDetect.h"
#include <iostream>

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CFaceDetect::CFaceDetect()
{

}

CFaceDetect::~CFaceDetect()
{

}

void CFaceDetect::init(const char* _p, int _w, int _h)
{	
	pFaces = NULL;
	pStorage = cvCreateMemStorage(0);
	pCascade = (CvHaarClassifierCascade *) cvLoad(_p,0,0,0);

	capW = _w;
	capH = _h;
	scale = 1.2;
}

void CFaceDetect::check(int* pixels)
{
	IplImage* img = cvCreateImage(cvSize(capW,capH),IPL_DEPTH_8U,3);

	int sz = capW*capH;
	int step = img->widthStep;
	int chnl = img->nChannels;
 
	for (int i=0;i<sz;i++) {
		unsigned int px = pixels[i];
		unsigned char r = (px>>16) & 0x000000ff;
		unsigned char g = (px>>8) & 0x000000ff;
		unsigned char b = (px) & 0x000000ff;
		int row = i/img->width;
		int col = i%img->width;
		unsigned char* tmp = &((unsigned char*)(img->imageData + row*step))[col*chnl];
		
		tmp[0] = b;
		tmp[1] = g;
		tmp[2] = r;
	}
	
	IplImage* grayImg = cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_8U,1);
	IplImage* smallImg = cvCreateImage(cvSize(cvRound(img->width/scale),cvRound(img->height/scale)),
		IPL_DEPTH_8U,1);

	cvCvtColor(img,grayImg,CV_BGR2GRAY);
	cvResize(grayImg,smallImg,CV_INTER_LINEAR);
	cvEqualizeHist(smallImg, smallImg);
	cvClearMemStorage(pStorage);

	pFaces = NULL;
	pFaces = cvHaarDetectObjects(smallImg, pCascade, pStorage,
		1.2, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(30,30));

	cvReleaseImage(&img);
	cvReleaseImage(&grayImg);
	cvReleaseImage(&smallImg);
}
