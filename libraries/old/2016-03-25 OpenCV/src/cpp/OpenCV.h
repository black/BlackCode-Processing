
#include "hypermedia_video_OpenCV.h"
#include "cv.h"
#include "highgui.h"
#include <stdio.h>
#include <string.h>
#include <iostream>
#include <algorithm>



#define MAX_NUMOF_BLOBS 256 //128

#define CAPTURE		4
#define SOURCE		hypermedia_video_OpenCV_SOURCE
#define BUFFER		hypermedia_video_OpenCV_BUFFER
#define MEMORY		hypermedia_video_OpenCV_MEMORY
#define ROI			hypermedia_video_OpenCV_ROI

#define GRAY		hypermedia_video_OpenCV_GRAY
#define RGB			hypermedia_video_OpenCV_RGB

using namespace std;


int width;
int height;
int interpolation_method = CV_INTER_LINEAR;

IplImage * buffer;
IplImage * source;
IplImage * memory;

CvHaarClassifierCascade * cascade;

CvCapture * capture;

typedef struct {
	float		area;
	float		length;
	CvRect		rect;
	CvPoint		centroid;
	bool		hole;
	int			nb_pts;
	CvPoint		* pts;
} Blob;


// the buffer color space
// could be RGB, GRAY
int color_space;

int nb_blobs;
Blob * blobs;

void copy( IplImage * src, int srcx, int srcy, int srcw, int srch, int destx, int desty, int destw, int desth );
static int qsort_blobarea_comparator( const void * elem1, const void * elem2 );

