
#include "OpenCV.h"


// TODO : Canny, Kalman, letter-recognition ?, Motion analysis, motion templates, rectangle detection
// object tracking (CamShift)
// FPS (java->P5)
// remember from buffer, info
// RELEASE IMAGE src IN REMENBER METHOD


/**
*	(docs)
 *	- JNI		: http://java.sun.com/j2se/1.5.0/docs/guide/jni/spec/jniTOC.html
 *	- OpenCV	: http://opencvlibrary.sourceforge.net/
 */



JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_allocate
(
	JNIEnv * env, jobject obj, 
	jint w, jint h
)
{
	
	jclass cls	  = env->GetObjectClass( obj );
	jfieldID wfid = env->GetFieldID( cls, "width", "I");
	jfieldID hfid = env->GetFieldID( cls, "height", "I");
	
	
	// deallocate buffer first
	if ( width && height )
		Java_hypermedia_video_OpenCV_deallocate( env, obj, BUFFER );
	
	
	width  = w;
	height = h;
	env->SetIntField( obj, wfid, w );
	env->SetIntField( obj, hfid, h );
	
	buffer = cvCreateImage( cvSize(width,height), IPL_DEPTH_8U, 3 );
	source = cvCreateImage( cvSize(width,height), IPL_DEPTH_8U, 3 );
	
	color_space = RGB;
	
	
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_deallocate
(
	JNIEnv * env, jobject obj,
	jint type
)
{
	
	// release capture
	if ( capture && ( type==CAPTURE || type==-1 ) ) {
		cvReleaseCapture( &capture );
		capture = NULL;
	}
	
	// release cascade classifier
	if ( type==-1 && cascade ) cvReleaseHaarClassifierCascade( &cascade );
	
	// images : because running methods could use it, 
	// this should be released at the end
	if ( type==BUFFER || type==-1 ) {
		if ( buffer ) cvReleaseImage( &buffer );
		if ( source ) cvReleaseImage( &source );
		if ( memory ) cvReleaseImage( &memory );
	}

}

/**
 * Store the specified image in memory and flip to the specified axis if necessary.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object class
 *
 * @param kind	the image source to be stored : SOURCE or BUFFER
 * @param mode	the axis to flip the image : FLIP_HORIZONTAL, FLIP_VERTICAL, FLIP_BOTH or 
 *				every other value for no flip
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_remember
(
	JNIEnv * env, jobject obj,
	jint kind, jint mode
)
{
	
	// nothing to do without sourceÉ yep, without buffer image too
	// -> exit 
	if ( !source ) return;
	
	// or if kind not match SOURCE or BUFFER
	// -> exit É but display message to take off frustrations
	if ( kind<SOURCE || kind>BUFFER ) {
		std::cout << "Error while using remember() : unknow source" << std::endl;
		return;
	}
	
	
	
	IplImage * src = kind==SOURCE ? source : buffer;
		
		
	// create the memory buffer (if needed)
	// with the target image channels -> should be convert for grayscale manipulation
	if ( !memory ) memory = cvCreateImage( cvSize(width,height), IPL_DEPTH_8U, src->nChannels );
	
	// clear region of interest (if need)
	if ( buffer->roi ) cvResetImageROI( memory );
	
	// store the current buffer
	cvCopy( src, memory, NULL );
	
	// flip the image if require
	if ( mode>=-1 && mode<=1 ) cvFlip( memory, NULL, mode );
	
	// restore the region of interest (if need)
	if ( buffer->roi ) cvSetImageROI( memory, cvGetImageROI(buffer) );
	
	
}


/**
 * Calculates the absolute difference between the image in memory and the actual buffer data.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object class
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_absDiff
( 
	JNIEnv * env, jobject  obj 
)
{
	
	IplImage * tmp;
	
	
	// nothing to do
	// -> exit
	if ( !memory ) return;
	
	
	// working with the same color space
	if ( memory->nChannels==buffer->nChannels ) {
		cvAbsDiff( memory, buffer, buffer );
		return;
	}

	// or convert to color space before 
	switch( color_space ) {
		
		// convert to gray before
		case GRAY :
			tmp = cvCreateImage( cvSize(width,height), IPL_DEPTH_8U, 1 );
			// !!! set tmp image ROI to match image size
			if ( buffer->roi ) cvSetImageROI( tmp, cvGetImageROI(buffer) );
			cvCvtColor( memory, tmp, CV_BGR2GRAY );
			cvAbsDiff( tmp, buffer, buffer );
			cvReleaseImage( &tmp );
			break;
			
		// otherwise
		/*default :
			cvAbsDiff( memory, buffer, buffer );
			break;*/
	}
	
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_threshold
( JNIEnv *jenv, jobject jobj, jfloat jvalue, jfloat max, jint jtype ) 
{
	cvThreshold( buffer, buffer, jvalue, max, jtype );
}

// -------------------------------------------------------------------------------------------------


/**
 * Converts the actual image from one color space to another.
 *
 * @param jenv	the Java environment
 * @param jobj	the Java object
 * @param type	the target color space
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_convert( JNIEnv * jenv, jobject jobj, jint type )
{
	
	// nothing to do
	if ( color_space==type || !buffer ) return;
	
	
	CvRect roi;
	bool hasROI = false;
	
	
	// remember buffer ROI and 
	// release region to process with the entire image
	if ( buffer->roi ) {
		roi = cvGetImageROI( buffer );
		cvResetImageROI( buffer );
		hasROI = true;
	}
	
	
	// because buffer depth could change :
	// make a temporary copy of the actual buffer
	// before de-allocate it
	IplImage * copy = cvCloneImage(buffer);
	cvReleaseImage( &buffer );
	
	
	switch( type ) {
		
		// convert to gray
		case GRAY : 
			buffer = cvCreateImage( cvSize(width,height), IPL_DEPTH_8U, 1 );
			cvCvtColor( copy, buffer, CV_BGR2GRAY );
			break;
		
		// convert to gray
		case RGB : 
			buffer = cvCreateImage( cvSize(width,height), IPL_DEPTH_8U, 3 );
			cvCvtColor( copy, buffer, CV_GRAY2BGR );
			break;
	}
	
	
	
	
	// free memory
	cvReleaseImage( &copy );
	
	// re-affect buffer ROI if exists
	if ( hasROI ) cvSetImageROI( buffer, roi );
	
	// set the actual buffer color space
	color_space = type;
}


/**
 * Restore the actual image data from the original image with the given color space.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 *
 * @param type	the target color space
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_restore
(
	JNIEnv * env, jobject obj,
	jint type
) 
{
	
	
	CvRect roi;
	bool hasROI = false;
	
	
	// remember buffer ROI and 
	// release region to process with the entire image
	if ( buffer->roi ) {
		roi = cvGetImageROI( buffer );
		cvResetImageROI( buffer );
		hasROI = true;
	}
	
	
	// re-allocate  buffer if needed
	if ( type!=color_space ) {
		cvReleaseImage( &buffer );
		buffer = cvCreateImage( cvSize(width,height), IPL_DEPTH_8U, type==RGB ? 3:1 );
	}
	
	
	// copy/convert original data
	switch( type ) {
		case RGB  : cvCopy(source,buffer,NULL); break;
		case GRAY : cvCvtColor(source,buffer,CV_BGR2GRAY); break;
	}
	
	
	
	// re-affect buffer ROI if exists
	if ( hasROI ) cvSetImageROI( buffer, roi );
	
	// set the actual buffer color space
	color_space = type;
}

// -------------------------------------------------------------------------------------------------


/**
 * Detects and returne all blobs found in the current buffered image as hypermedia.video.Blob 
 * object list.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 */
JNIEXPORT jobjectArray JNICALL Java_hypermedia_video_OpenCV_blobs
(
	JNIEnv * env, jobject obj,
	jint minArea, jint maxArea, jint maxBlobs, jboolean findHoles, jint maxVertices
)
{

	CvRect roi = cvGetImageROI( buffer );
	CvPoint point;
	CvSeqReader reader;
	
	
	
	// search for the hypermedia.video.Blob class, constructor and associated fields
	jclass bcls = env->FindClass( "hypermedia/video/Blob" );
	jmethodID bmid = env->GetMethodID( bcls, "<init>", "()V" );
	jfieldID bfid_area = env->GetFieldID( bcls, "area", "F" );
	jfieldID bfid_length = env->GetFieldID( bcls, "length", "F" );
	jfieldID bfid_centroid = env->GetFieldID( bcls, "centroid", "Ljava/awt/Point;" );
	jfieldID bfid_rectangle = env->GetFieldID( bcls, "rectangle", "Ljava/awt/Rectangle;" );
	jfieldID bfid_points = env->GetFieldID( bcls, "points", "[Ljava/awt/Point;" );
	jfieldID bfid_ishole = env->GetFieldID( bcls, "isHole", "Z" );
	
	// search for Java object class and constructor
	jclass pcls = env->FindClass( "java/awt/Point" );
	jclass rcls = env->FindClass( "java/awt/Rectangle" );
	jmethodID pmid = env->GetMethodID( pcls, "<init>", "(II)V" );
	jmethodID rmid = env->GetMethodID( rcls, "<init>", "(IIII)V" );
	
	
	
	// memory storage for analysis
	CvMemStorage * storage = cvCreateMemStorage();
	// to calculate gravity center, area, etcÉ
	CvMoments * moments = (CvMoments*) malloc( sizeof(CvMoments) );
	// the valid contour list
	CvSeq * contours[ MAX_NUMOF_BLOBS ];
	// contour index that will walk through starting at 0 address
	CvSeq * index = 0;
	// the number of coontours found
	int count = 0;
	
	
	
	
	// first : convert rgb buffer to gray (if need)
	if ( buffer->nChannels!=1 ) Java_hypermedia_video_OpenCV_convert( env, obj, GRAY );
	
	// find all contours
	cvFindContours(
		buffer,
		storage,
		&index,
		sizeof(CvContour),
		findHoles ? CV_RETR_LIST : CV_RETR_EXTERNAL
	);
	
	
	
	// list valid blob's contour : included in the miminum and maximum area defined by the user
	// to remove noises or biggest blob
	while( index!=NULL && count<=MAX_NUMOF_BLOBS ) {
		float area = fabs( cvContourArea(index) );
		if ( area>=minArea && area<=maxArea ) contours[ count++ ] = index;
		index = index->h_next;
	}

	// reorder contours from the largest to the smallest 
	if ( count > 0 ) qsort( contours, count, sizeof(CvSeq*), qsort_blobarea_comparator );
	
	// limit the number of blob elements in list
	// should be limited after Sorting elements in array to retrieve all biggest blobs
	count = MIN( count, maxBlobs );
	
	
	
	// define the output list
	jobjectArray out = env->NewObjectArray( count, bcls, NULL );
	
	// convert the CV formatted contour data into our Java Blob object
	for( int i=0; i<count; i++ ) {
		
		
		// calculates all moments up to third order of a polygon or rasterized shape
		cvMoments( contours[i], moments );
		
		// set the maximum number of blob's points definition
		int max = MIN( maxVertices, contours[i]->total );
		
		// retrieve CV blob's properties (area, arc length, bounding rectangle, gravity center)
		float area	 = cvContourArea( contours[i] );
		float length = cvArcLength( contours[i] );
		CvRect rect	 = cvBoundingRect( contours[i] );
		int center_x = (int) ( moments->m10/moments->m00 ) + roi.x;
		int center_y = (int) ( moments->m01/moments->m00 ) + roi.y;
		
		
		// create Java objects : hypermedia.video.Blob, Rectangle, Point, Point array
		jobject blob = env->NewObject( bcls, bmid );
		jobject centroid = env->NewObject( pcls, pmid, center_x, center_y );
		jobject rectangle = env->NewObject( rcls, rmid, rect.x+roi.x, rect.y+roi.y, rect.width, rect.height );
		jobjectArray points = env->NewObjectArray( max, pcls, NULL );
		
		
		
		// initializes process of sequential reading from sequence
		cvStartReadSeq( contours[i], &reader, 0 );
		
		// list contour points
		for( int j=0; j<max; j++ ) {
			CV_READ_SEQ_ELEM( point, reader );
			env->SetObjectArrayElement( points, j, env->NewObject( pcls, pmid, point.x+roi.x, point.y+roi.y ) );
		}
		
		// set java Blob's values
		env->SetFloatField( blob, bfid_area, fabs(area) );
		env->SetFloatField( blob, bfid_length, length );
		env->SetObjectField( blob, bfid_centroid, centroid );
		env->SetObjectField( blob, bfid_rectangle, rectangle );
		env->SetObjectField( blob, bfid_points, points );
		env->SetBooleanField( blob, bfid_ishole, area>0 );
		
		// append the new Blob in the output list
		env->SetObjectArrayElement( out, i, blob );
	}
	

	// release memory
	cvReleaseMemStorage( &storage );
	free( moments );
	
	
	// return the result
	return out;
}

/**
 * Unsupported format or combination of formats ([Start]FindContours support only 8uC1 images)
 *
 * TODO : CV_RETR_TREE
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_findContours 
( JNIEnv *jenv, jobject jobj, jint minArea, jint maxArea, jint maxBlobs, jboolean findHoles, jint maxVertices ) 
{
	
	
	CvMemStorage	* contour_storage;
	CvMoments		* moments;
	CvSeq			* cvSeqBlobs[ MAX_NUMOF_BLOBS ];
	//CvSeq			* contour_list;
	CvSeq			* contour_ptr;
	CvContourRetrievalMode retrieve_mode = findHoles ? CV_RETR_LIST : CV_RETR_EXTERNAL;
	
	
	
	
	// convert rgb buffer to gray
	if ( buffer->nChannels!=1 ) Java_hypermedia_video_OpenCV_convert( jenv, jobj, GRAY );
	
	
	// if we haven't instantiated the blob object yet, instantiate it
	if ( !blobs ) blobs = new Blob[ MAX_NUMOF_BLOBS ];
	
	// because the maximum of vertices is defined by the user,
	// release the last blob's points to prevent memory leak
	if ( nb_blobs ) {
		for( int i=0; i<nb_blobs; i++ ) free( blobs[i].pts );
	}
	
	
	
	// reset the number of found blobs
	nb_blobs = 0;
	
	// put zeros in all the (possible) blob objects
	memset( blobs, 0, sizeof(Blob)*MAX_NUMOF_BLOBS );
	
	// create the moment variable to hold OpenCV moment calculations
	moments = (CvMoments*) malloc( sizeof(CvMoments) );
	
	// create the storage (used by OpenCV for analysis)
	contour_storage = cvCreateMemStorage( 1000 );
	
	// the big one: go find all the contours
	cvFindContours( buffer, contour_storage, &contour_ptr, sizeof(CvContour), retrieve_mode);//, CV_CHAIN_APPROX_SIMPLE );
	
	// this is the index that will walk through starting at 0 address of contour_list
	//contour_ptr = contour_list;
	
	
	
	
	// walking through list until NULL (= end of list)
	while( (contour_ptr!=NULL) ) {
		
		float area = fabs(cvContourArea(contour_ptr, CV_WHOLE_SEQ));
		
		if ( area>=minArea && area<=maxArea ) cvSeqBlobs[ nb_blobs++ ] = contour_ptr;
		if ( nb_blobs>MAX_NUMOF_BLOBS ) break;
		
		contour_ptr = contour_ptr->h_next;
	}
		  
	
	// binary quicksort
	if ( nb_blobs > 0 ) {
		qsort( cvSeqBlobs, nb_blobs, sizeof(CvSeq*), qsort_blobarea_comparator );
	}
	
	
	nb_blobs = MIN( nb_blobs, maxBlobs );
	
	// convert the OpenCV formatted contour data into our Blob array format
	CvRect roi = cvGetImageROI( buffer );
	for( int i=0; i<nb_blobs; i++ ) {
		
		float area  = cvContourArea( cvSeqBlobs[i], CV_WHOLE_SEQ);
		CvRect rect = cvBoundingRect( cvSeqBlobs[i], 0 );
		
		cvMoments( cvSeqBlobs[i], moments );
		
		blobs[i].area			= fabs(area);
		blobs[i].hole			= area > 0;
		blobs[i].length			= cvArcLength(cvSeqBlobs[i]);
		blobs[i].rect.x			= rect.x+roi.x;
		blobs[i].rect.y			= rect.y+roi.y;
		blobs[i].rect.width		= rect.width;
		blobs[i].rect.height	= rect.height;
		blobs[i].centroid.x		= (int) (moments->m10 / moments->m00) + roi.x;
		blobs[i].centroid.y		= (int) (moments->m01 / moments->m00) + roi.y;
		blobs[i].nb_pts			= MIN(maxVertices, cvSeqBlobs[i]->total);
		
		CvPoint point;
		CvSeqReader reader;
		cvStartReadSeq(cvSeqBlobs[i], &reader, 0);
		
		
		blobs[i].pts = new CvPoint[ maxVertices ];
		for(int j=0; j<blobs[i].nb_pts; j++) {
			CV_READ_SEQ_ELEM(point, reader);
			blobs[i].pts[j].x = point.x + roi.x;
			blobs[i].pts[j].y = point.y + roi.y;
		}
		
	}
	
	
	
	// release memory
	cvReleaseMemStorage( &contour_storage );
	free( moments );
	
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_hypermedia_video_OpenCV_blobCount( JNIEnv *jenv, jobject jobj ) 
{
	return nb_blobs;
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT jfloat JNICALL Java_hypermedia_video_OpenCV_area( JNIEnv *jenv, jobject jobj, jint index ) 
{
	
	if ( index>nb_blobs ) return -1;
	return (jfloat) blobs[index].area;
	
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT jfloat JNICALL Java_hypermedia_video_OpenCV_arcLength( JNIEnv *jenv, jobject jobj, jint index ) 
{
	
	if ( index>nb_blobs ) return -1;
	return (jfloat) blobs[index].length;
	
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT jobject JNICALL Java_hypermedia_video_OpenCV_rectangle
( 
	JNIEnv * env, jobject obj,
	jint index 
) 
{
	
	// find the Java Rectangle class object and constructor method
	jclass cls = env->FindClass( "java/awt/Rectangle" );
	jmethodID method = env->GetMethodID( cls, "<init>", "(IIII)V" );
	
	// return a new Java Rectangle
	return env->NewObject(
				cls, 
				method, 
				blobs[index].rect.x,
				blobs[index].rect.y,
				blobs[index].rect.width,
				blobs[index].rect.height
			);
}

/*JNIEXPORT jintArray JNICALL Java_hypermedia_video_OpenCV_rectangle
( 
	JNIEnv * jenv, jobject jobj,
	jint index 
) 
{	
	jint rect[4];
	jintArray jrect;
	
	if ( index>nb_blobs ) return NULL;
	
	
	rect[0] = blobs[index].rect.x;
	rect[1] = blobs[index].rect.y;
	rect[2] = blobs[index].rect.width;
	rect[3] = blobs[index].rect.height;
	
	jrect  = jenv->NewIntArray(4);
	jenv->SetIntArrayRegion( jrect, 0, 4, rect);
	return jrect;
	
}*/

// -------------------------------------------------------------------------------------------------

JNIEXPORT jobject JNICALL Java_hypermedia_video_OpenCV_centroid
(
	JNIEnv * env, jobject obj,
	jint index
) 
{
	
	// find the Java Point class object and constructor method
	jclass cls = env->FindClass( "java/awt/Point" );
	jmethodID method = env->GetMethodID( cls, "<init>", "(II)V" );
	
	// return a new Java Point
	return env->NewObject(
					cls, 
					method, 
					blobs[index].centroid.x,
					blobs[index].centroid.y
			);
}

/*JNIEXPORT jintArray JNICALL Java_hypermedia_video_OpenCV_centroid
(
	JNIEnv * jenv, jobject jobj,
	jint index
) 
{			
	jint centroid[2];
	jintArray jcentroid;
	
	if ( index>nb_blobs ) return NULL;
	
	
	centroid[0] = blobs[index].centroid.x;
	centroid[1] = blobs[index].centroid.y;
	
	jcentroid  = jenv->NewIntArray(2);
	jenv->SetIntArrayRegion( jcentroid, 0, 2, centroid);
	return jcentroid;
	
}*/

// -------------------------------------------------------------------------------------------------

JNIEXPORT jboolean JNICALL Java_hypermedia_video_OpenCV_isHole( JNIEnv *jenv, jobject jobj, jint index )
{
	
	if ( index>nb_blobs ) return NULL;
	
	return blobs[index].hole ? JNI_TRUE : JNI_FALSE;
	
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_hypermedia_video_OpenCV_pointCount( JNIEnv *jenv, jobject jobj, jint index )
{
	
	if ( index>nb_blobs ) return 0;
	return (jint) blobs[index].nb_pts;
	
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT jobjectArray JNICALL Java_hypermedia_video_OpenCV_points
(
	JNIEnv * env, jobject obj,
	jint index
)
{

	
	// find the Java Point class object and constructor method
	jclass cls = env->FindClass( "java/awt/Point" );
	jmethodID method = env->GetMethodID( cls, "<init>", "(II)V" );
	
	
	// create an empty output list for errors
	jobjectArray out = env->NewObjectArray( 0, cls, NULL );
	
	
	// return an empty list if need
	// -> exit
	if ( index>nb_blobs || !blobs[index].nb_pts ) return out;
	
	
	// re-define output list length
	out = env->NewObjectArray( blobs[index].nb_pts, cls, NULL );
	
	
	// convert each point
	for( int i=0; i<blobs[index].nb_pts; i++ ) {
		// construct a new java.awt.Point
		jobject elem = env->NewObject( cls, method, blobs[index].pts[i].x, blobs[index].pts[i].y );
		// place the new elemnt in the output list
		env->SetObjectArrayElement( out, i, elem );
		
	}
	
	// return the result
	return out;
}

/*JNIEXPORT jintArray JNICALL Java_hypermedia_video_OpenCV_points ( JNIEnv * jenv, jobject jobj, jint index )
{			
			
	int jsize =  blobs[index].nb_pts*2;
	jint points[ jsize ];
	jintArray jpoints;
	
	
	
	for( int i=0; i<blobs[index].nb_pts; i++ ) {
		points[(i*2) ] = blobs[index].pts[i].x;
		points[(i*2)+1] = blobs[index].pts[i].y;
	}
	
	jpoints  = jenv->NewIntArray(jsize);
	jenv->SetIntArrayRegion( jpoints, 0, jsize, points);
	return jpoints;

	
}*/

JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_interpolation
(
	JNIEnv * env, jobject obj,
	jint method
)
{
	interpolation_method = (int) method;
}




/*
	////////////////////////////////////////////////////////////////////////////////////////////////
	 
		 MANAGE PIXELS
		 -------------
		 
		 COPY() todo : to optimise the process, return before creating source image while destw or 
		 desth are outside the scope.
	 
	////////////////////////////////////////////////////////////////////////////////////////////////
 */

/**
 * Copies into the buffer a region of a source image at the specified position by resizing the result
 * if required. no interpolation.
 *
 * @param JENV		pointer to native method interface
 * @param JOBJ		the Java object
 *
 * @param pixels	image's data to be copied
 * @param step		full row length, explicitly the image width for non-deformation
 * @param srcx		the source X coordinate
 * @param srcy		the source Y coordinate
 * @param srcw		the source width (image or region)
 * @param srch		the source height (image or region)
 * @param destx		the destination x coordinate
 * @param desty		the destination y coordinate
 * @param destw		the destination width
 * @param desth		the destination height
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_copy__Ljava_lang_String_2IIIIIIII
(
	JNIEnv * env, jobject obj, 
	jstring file, 
	jint srcx, jint srcy, jint srcw, jint srch, 
	jint destx, jint desty, jint destw, jint desth
)
{
	
	
	// nothing to do : without buffer, outside the scope
	// -> exit
	if ( !buffer || destx>width-1 || desty>height-1 ) return;
	
	
	
	// retrieve file absolute path
	jclass cls 	  = env->GetObjectClass( obj );
	jmethodID mid = env->GetMethodID( cls, "absolutePath", "(Ljava/lang/String;)Ljava/lang/String;" );
	jstring path  = (jstring) env->CallObjectMethod( obj, mid, file );
	
	// convert jstring
	const char * usrfile = env->GetStringUTFChars( file, JNI_FALSE );
	const char * abspath = env->GetStringUTFChars( path, JNI_FALSE );
	
	
	// load the image file...
	IplImage * src  = cvLoadImage( abspath, buffer->nChannels/3 );
	
	
	if ( src ) {
		
		if ( srcw==-1 ) srcw = src->width;
		if ( srch==-1 ) srch = src->height;
		if ( destw==-1 ) destw = src->width;
		if ( desth==-1 ) desth = src->height;
		
		copy( src, srcx, srcy, srcw, srch, destx, desty, destw, desth );
		
	}
	else {
		std::cout << "The image file '" << usrfile;
		std::cout << "' can not be found in folders, you must specify the full path instead.";
		std::cout << std::endl;
	}
	
	// free memory
	cvReleaseImage( &src );
	env->ReleaseStringUTFChars( file, usrfile );
	env->ReleaseStringUTFChars( path, abspath );

	
}

/**
 *
 */
void copy
( 
	IplImage * src, 
	int srcx, int srcy, int srcw, int srch, 
	int destx, int desty, int destw, int desth
)
{	
	
	int mindestw = max( destw, 1 );
	int mindesth = max( desth, 1 );
	
	destw = destx>=0 ? min( width, mindestw )  : ( min(width,mindestw)+destx );
	desth = desty>=0 ? min( height, mindesth ) : ( min(height,mindesth)+desty );
	
	
	// nothing to do : without buffer, outside the scope, or without source's region
	// -> exit
	if ( !buffer || destx>width-1 || desty>height-1 || destw<=0 || desth<=0 ) return;
	if ( srcw<=0 || srch<=0 ) return;

	
	
	// load the image file...
	IplImage * dest = cvCreateImage( cvSize(mindestw,mindesth), IPL_DEPTH_8U, buffer->nChannels );
	
	
	
	CvRect roi;
	bool hasROI = false;
	
	
	
	CvRect srcr, destr, bufr;
	
	srcr = cvRect(
		max( (int) srcx, 0 ),
		max( (int) srcy, 0 ),
		min( (int) srcw, (int) src->width ),
		min( (int) srch, (int) src->height )
	);
	
	// copy/scale source region
	cvSetImageROI( src, srcr );
	cvResize( src, dest, interpolation_method );
	
	// copy destination to the actual buffer
	
	
	// remember user ROI
	if ( buffer->roi ) {
		roi = cvGetImageROI( buffer );
		hasROI = true;
	}
	
	// re-define destination dimensions to fit at most to the buffer dimensions
	bufr  = cvRect(
		max( (int) destx, 0 ),
		max( (int) desty, 0 ),
		destw,
		desth
	);
	
	destr = cvRect(
		destx>=0 ? 0 : -destx,
		desty>=0 ? 0 : -desty,
		destx>=0 ? buffer->width  - bufr.x : destw,
		desty>=0 ? buffer->height - bufr.y : desth
	);
	
	cvSetImageROI( dest, destr );
	cvSetImageROI( buffer, bufr );
	cvCopy( dest, buffer );
	
	// re-affect user ROI or clear the created one
	if ( hasROI ) cvSetImageROI( buffer, roi );
	else cvResetImageROI( buffer );
	
	
	// free memory
	cvReleaseImage( &dest );
}


/**
 * Copies into the buffer a region of a source image at the specified position by resizing the result
 * if required. no interpolation.
 *
 * @param JENV		pointer to native method interface
 * @param JOBJ		the Java object
 *
 * @param pixels	image's data to be copied
 * @param step		full row length, explicitly the image width for non-deformation
 * @param srcx		the source X coordinate
 * @param srcy		the source Y coordinate
 * @param srcw		the source width (image or region)
 * @param srch		the source height (image or region)
 * @param destx		the destination x coordinate
 * @param desty		the destination y coordinate
 * @param destw		the destination width
 * @param desth		the destination height
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_copy___3IIIIIIIIII
(
	JNIEnv * JENV, jobject JOBJ, 
	jintArray pixels, jint step, 
	jint srcx, jint srcy, jint srcw, jint srch, 
	jint destx, jint desty, jint destw, jint desth
)
{
	
	
	// nothing to do : without buffer, outside the scope, or without source's region
	// -> exit
	if ( !buffer || destx>width-1 || desty>height-1 || srcw<=0 || srch<=0 ) return;
	
	
	
	// access pixel's values and
	// check in case the VM tried to make a copy.
	int * data = (int*) JENV->GetPrimitiveArrayCritical( pixels, 0 );
	if ( data==NULL ) std::cout << "out of memory"  << std::endl;
	
	// data length
	int srcsize = (int) JENV->GetArrayLength( pixels );
	
	// the destination region size
	int size = step*(srcsize/step);
	
	
	
	// scaled image
	IplImage * src  = cvCreateImage( cvSize(step,srcsize/step), IPL_DEPTH_8U, buffer->nChannels );
	
	
	// add data
	for( int i=0; i<size; i++ ) {
		
		int x = i%step;
		int y = i/step;
		
		// retrieve scaled source and destination pixel's index
		int srci  = x+(y*step);
		int desti = ( x*src->nChannels )+( y*src->widthStep );
		
		// get RGB values
		unsigned char b = data[ srci ] & 0xff;
		unsigned char g = data[ srci ] >> 8 & 0xff;
		unsigned char r = data[ srci ] >> 16 & 0xff;
		
		
		// set RGB pixel's value
		if ( src->nChannels==3 ) {
			src->imageData[ desti ]		= b;
			src->imageData[ desti+1 ]	= g;
			src->imageData[ desti+2 ]	= r;
		}
		// or convert to gray
		else src->imageData[ desti ] = (b*0.114) + (g*0.587) + (r*0.299);
		
	}	
	
	copy( src, srcx, srcy, srcw, srch, destx, desty, destw, desth );
	
	// free memory
	cvReleaseImage( &src );
	JENV->ReleasePrimitiveArrayCritical( pixels, data, 0 );
	
	
	/*int mindestw = max( (int) destw, 1 );
	int mindesth = max( (int) desth, 1 );
	
	
	destw = destx>=0 ? min( width, mindestw )  : ( min(width,mindestw)+destx );
	desth = desty>=0 ? min( height, mindesth ) : ( min(height,mindesth)+desty );
	
	
	// nothing to do : without buffer, outside the scope, or without source's region
	// -> exit
	if ( !buffer || destx>width-1 || desty>height-1 || destw<=0 || desth<=0 ) return;
	if ( srcw<=0 || srch<=0 ) return;
	
	
	
	
	// access pixel's values and
	// check in case the VM tried to make a copy.
	int * data = (int*) JENV->GetPrimitiveArrayCritical( pixels, 0 );
	if ( data==NULL ) std::cout << "out of memory"  << std::endl;
	
	// data length
	int srcsize = (int) JENV->GetArrayLength( pixels );
	
	// the destination region size
	int size = step*(srcsize/step);
	
	
	
	// scaled image
	IplImage * src  = cvCreateImage( cvSize(step,srcsize/step), IPL_DEPTH_8U, buffer->nChannels );
	IplImage * dest = cvCreateImage( cvSize(mindestw,mindesth), IPL_DEPTH_8U, buffer->nChannels );
	
	
	// add data
	for( int i=0; i<size; i++ ) {
		
		int x = i%step;
		int y = i/step;
		
		// retrieve scaled source and destination pixel's index
		int srci  = x+(y*step);
		int desti = ( x*src->nChannels )+( y*src->widthStep );
		
		// get RGB values
		unsigned char b = data[ srci ] & 0xff;
		unsigned char g = data[ srci ] >> 8 & 0xff;
		unsigned char r = data[ srci ] >> 16 & 0xff;
		
		
		// set RGB pixel's value
		if ( src->nChannels==3 ) {
			src->imageData[ desti ]		= b;
			src->imageData[ desti+1 ]	= g;
			src->imageData[ desti+2 ]	= r;
		}
		// or convert to gray
		else src->imageData[ desti ] = (b*0.114) + (g*0.587) + (r*0.299);
		
	}
	
	CvRect roi;
	bool hasROI = false;
	
	
	
	CvRect srcr, destr, bufr;
	
	srcr = cvRect(
				max( (int) srcx, 0 ),
				max( (int) srcy, 0 ),
				min( (int) srcw, (int) src->width ),
				min( (int) srch, (int) src->height )
			);
	
	// copy/scale source region
	cvSetImageROI( src, srcr );
	cvResize( src, dest, interpolation_method );
	
	// copy destination to the actual buffer
		
		
	// remember user ROI
	if ( buffer->roi ) {
		roi = cvGetImageROI( buffer );
		hasROI = true;
	}
	
	// re-define destination dimensions to fit at most to the buffer dimensions
	bufr  = cvRect(
				max( (int) destx, 0 ),
				max( (int) desty, 0 ),
				destw,
				desth
			);
	
	destr = cvRect(
				destx>=0 ? 0 : -destx,
				desty>=0 ? 0 : -desty,
				destx>=0 ? buffer->width  - bufr.x : destw,
				desty>=0 ? buffer->height - bufr.y : desth
			);
	
	cvSetImageROI( dest, destr );
	cvSetImageROI( buffer, bufr );
	cvCopy( dest, buffer );
	
	// re-affect user ROI or clear the created one
	if ( hasROI ) cvSetImageROI( buffer, roi );
	else cvResetImageROI( buffer );

	
	// free memory
	cvReleaseImage( &src );
	cvReleaseImage( &dest );
	JENV->ReleasePrimitiveArrayCritical( pixels, data, 0 );
	
	
	
	 
	// OLD METHOD ---------------------------------------------------------------------------------- 
	 
	// nothing to do
	// -> exit to prevent error
	if ( !buffer ) return;
	
	
	
	// access pixel's values
	jint * data = (jint*) JENV->GetPrimitiveArrayCritical( pixels, 0 );
	
	// data length
	int srcsize = (int) JENV->GetArrayLength( pixels );
	
	// scaling factors
	// source's region dimensions should be less or equal to source dimensions
	double fx = min((int) srcw,(int) widthstep) / float(destw);
	double fy = min((int) srch,(int) (srcsize/widthstep)) / float(desth);
	
	// the destination region size
	int size = destw*desth;
	
	// offsets
	int srcoffset  = max((int)srcx,0)+( max((int)srcy,0)*widthstep);
	int destoffset = (destx*buffer->nChannels)+(desty*buffer->widthStep);
	
	
	
	
	// check in case the VM tried to make a copy.
	if ( data==NULL ) std::cout << "out of memory"  << std::endl;
	
	// update pixels
	for( int i=0; i<size; i++ ) {
		
		int x = i%destw;
		int y = i/destw;
		
		
		// retrieve scaled source and destination pixel's index
		int srci  = ( (int) (x*fx) ) + ( (int) (y*fy) * widthstep )+srcoffset;
		int desti = ( x*buffer->nChannels )+( y*buffer->widthStep )+destoffset;
		
		// do nothing outside imagesÉ of course -> continue
		if ( desti<0 || desti>=(width*height*buffer->nChannels) || srci<0 || srci>=srcsize ) continue;
		// do not use array 'loopback' -> continue
		if ( desti%(width*buffer->nChannels)<(destx*buffer->nChannels) ) continue;		 // left
		if ( desti%(width*buffer->nChannels)>(destx+destw)*buffer->nChannels ) continue; // right
		
		
		// get RGB values
		unsigned char b = data[ srci ] & 0xff;
		unsigned char g = data[ srci ] >> 8 & 0xff;
		unsigned char r = data[ srci ] >> 16 & 0xff;
		
		
		// set RGB pixel's value
		if ( buffer->nChannels==3 ) {
			buffer->imageData[ desti ]	 = b;
			buffer->imageData[ desti+1 ] = g;
			buffer->imageData[ desti+2 ] = r;
		}
		// or convert to gray
		else buffer->imageData[ desti ] = (b*0.114) + (g*0.587) + (r*0.299);
		
	}
	
		
	
	// free memory
	JENV->ReleasePrimitiveArrayCritical( pixels, data, 0 );*/
	
	
}

// -------------------------------------------------------------------------------------------------

JNIEXPORT jintArray JNICALL Java_hypermedia_video_OpenCV_pixels
( JNIEnv * jenv, jobject jobj, jint kind ) 
{
	
	
	
	// GetImageROI
	
	jintArray jpixels;
	int size = width*height;
	jpixels  = jenv->NewIntArray(size);
	jint data[1];
	
	// nothing to fix
	if ( kind==SOURCE && source==NULL ) return jpixels;
	if ( kind==BUFFER && buffer==NULL ) return jpixels;
	if ( kind==MEMORY && memory==NULL ) return jpixels;
	
	
	IplImage * src;
	switch( kind ) {
		case MEMORY	: src = cvCloneImage(memory); break;
		case SOURCE	: src = cvCloneImage(source); break;
		default		: src = cvCloneImage(buffer); break;
	}
	
	
	for( int i=0; i<size; i++ ) {
		
		int x = i%width;
		int y = i/width;
		int p = ( x*src->nChannels ) + ( y*src->widthStep );
		
		unsigned char b = src->imageData[ p ];
		unsigned char g = src->nChannels==1 ? b : src->imageData[ p+1 ];
		unsigned char r = src->nChannels==1 ? b : src->imageData[ p+2 ];
		
		data[0] = 0xff000000 | r << 16 | g << 8 | b;
		jenv->SetIntArrayRegion( jpixels, i, 1, data );
	}
	
	cvReleaseImage( &src );
	
	return jpixels;
	
}

/**
 * Flip the actual buffer around vertical, horizontal or both axises.
 *
 * @param mode	which axis : FLIP_HORIZONTAL, FLIP_VERTICAL or FLIP_BOTH
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_flip
(
	JNIEnv * JENV, jobject JOBJ, jint mode
) 
{

	CvRect roi;
	bool hasROI = false;
	
	
	// remember buffer ROI and 
	// release region to process with the entire image
	if ( buffer->roi ) {
		roi = cvGetImageROI( buffer );
		cvResetImageROI( buffer );
		hasROI = true;
	}

	// flip the image
	cvFlip( buffer, NULL, mode );
	
	// re-affect buffer ROI if exists
	if ( hasROI ) cvSetImageROI( buffer, roi );
	
}



/*
	////////////////////////////////////////////////////////////////////////////////////////////////
		CAPTURE AND MOVIE FILE
	////////////////////////////////////////////////////////////////////////////////////////////////
*/


JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_capture
(
	JNIEnv * env, jobject obj,
	jint w, jint h, jint index
)
{
	
	// deallocate previous capture if need
	// and initialise video stream from camera
	Java_hypermedia_video_OpenCV_deallocate( env, obj, CAPTURE );
	capture = cvCreateCameraCapture( (int)index );
	
	
	// allocate memory on success
	// or display an error message
	if( capture ) Java_hypermedia_video_OpenCV_allocate( env, obj, w, h );
	else std::cout << "Error while starting capture : device " << (int)index << std::endl;
	

	// fonctions not yet implemented
	// cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_WIDTH, (double) width );
	// cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_HEIGHT, (double) height );
	// cvSetCaptureProperty( capture, CV_CAP_PROP_FPS, (double) 30 );
		
	
}



/**
 * Initializes OpenCV for reading the video stream from the specified file.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_loadMovie
(
	JNIEnv * env, jobject obj,
	jstring file, jint w, jint h
)
{
	
	// retrieve file absolute path
	jclass cls 	  = env->GetObjectClass( obj );
	jmethodID mid = env->GetMethodID( cls, "absolutePath", "(Ljava/lang/String;)Ljava/lang/String;" );
	jstring path  = (jstring) env->CallObjectMethod( obj, mid, file );
	
	// convert jstring
	const char * usrfile = env->GetStringUTFChars( file, JNI_FALSE );
	const char * abspath = env->GetStringUTFChars( path, JNI_FALSE );
	
	
	
	// deallocate previous capture if need
	// load the movie with the absolute path...
	Java_hypermedia_video_OpenCV_deallocate( env, obj, CAPTURE );
	capture = cvCreateFileCapture( abspath );
	
	
	
	// define movie size and allocate memory for this capture
	// or display a message on error
	if ( capture ) {
		Java_hypermedia_video_OpenCV_allocate(
				env,
				obj,
				w==-1 ? (jint)cvGetCaptureProperty(capture,CV_CAP_PROP_FRAME_WIDTH) : w,
				h==-1 ? (jint)cvGetCaptureProperty(capture,CV_CAP_PROP_FRAME_HEIGHT): h
	  );
	}
	else {
		std::cout << "The movie file '" << usrfile;
		std::cout << "' can not be found in folders, you must specify the full path instead.";
		std::cout << std::endl;
	}

	
	// free memory
	env->ReleaseStringUTFChars( file, usrfile );
	env->ReleaseStringUTFChars( path, abspath );
	
}

/**
 * Loads an image from the specified file.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 * @param file	the name of file to be loaded
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_loadImage
(
	JNIEnv * env, jobject obj,
	jstring file, jint w, jint h
)
{
	
	// retrieve file absolute path
	jclass cls 	  = env->GetObjectClass( obj );
	jmethodID mid = env->GetMethodID( cls, "absolutePath", "(Ljava/lang/String;)Ljava/lang/String;" );
	jstring path  = (jstring) env->CallObjectMethod( obj, mid, file );
	
	// convert jstring
	const char * usrfile = env->GetStringUTFChars( file, JNI_FALSE );
	const char * abspath = env->GetStringUTFChars( path, JNI_FALSE );

	
	// cvReleaseImage(&image) before image = cvLoadImage() to avoid memory leaks
	// load the image file...
	IplImage * src = cvLoadImage( abspath );
	
	
	// set buffer's memory and copy/scale the new data, 
	// or display a message on error
	if ( src ) {
		
		// allocate
		Java_hypermedia_video_OpenCV_allocate(
				env,
				obj,
				w==-1 ? src->width : w, 
				h==-1 ? src->height : h
		);
		
		// affect original image
		if ( src->width!=w || src->height!=h ) cvResize( src, source, interpolation_method );
		else cvCopy( src, source );
		
		// copy the buffer
		Java_hypermedia_video_OpenCV_restore( env, obj, RGB );
	}
	else {
		std::cout << "The image file '" << usrfile;
		std::cout << "' can not be found in folders, you must specify the full path instead.";
		std::cout << std::endl;
	}
	
	
	// free memory
	cvReleaseImage( &src );
	env->ReleaseStringUTFChars( file, usrfile );
	env->ReleaseStringUTFChars( path, abspath );
	
}

/**
 * Retrieve the specified information from the current capture
 *
 * @param env		pointer to native method interface
 * @param obj		the Java object
 * @param property	the required information type
 */
JNIEXPORT jfloat JNICALL Java_hypermedia_video_OpenCV_info
(
	JNIEnv * env, jobject obj,
	jint property
)
{
	
	// nothing to return without capture 
	if ( !capture ) return -1.;
		
	/*
	 try {
		
		double ch		= cvGetCaptureProperty( capture, CV_CAP_PROP_FRAME_HEIGHT );
		double cw		= cvGetCaptureProperty( capture, CV_CAP_PROP_FRAME_WIDTH );
		double fps		= cvGetCaptureProperty( capture, CV_CAP_PROP_FPS );
		double milli	= cvGetCaptureProperty( capture, CV_CAP_PROP_POS_MSEC );
		double frame	= cvGetCaptureProperty( capture, CV_CAP_PROP_POS_FRAMES );
		double ratio	= cvGetCaptureProperty( capture, CV_CAP_PROP_POS_AVI_RATIO );
		double nbframes	= cvGetCaptureProperty( capture, CV_CAP_PROP_FRAME_COUNT );
		
		// only for cameras
		double brightness	= cvGetCaptureProperty( capture, CV_CAP_PROP_BRIGHTNESS );
		double contrast		= cvGetCaptureProperty( capture, CV_CAP_PROP_CONTRAST );
		double saturation	= cvGetCaptureProperty( capture, CV_CAP_PROP_SATURATION );
		double hue			= cvGetCaptureProperty( capture, CV_CAP_PROP_HUE );
	 
		double fourcc	= cvGetCaptureProperty( capture, CV_CAP_PROP_FOURCC );
		double ch		= cvGetCaptureProperty( capture, CV_CAP_PROP_FORMAT );
		double ch		= cvGetCaptureProperty( capture, CV_CAP_PROP_MODE );
		double ch		= cvGetCaptureProperty( capture, CV_CAP_PROP_GAIN );
		double ch		= cvGetCaptureProperty( capture, CV_CAP_PROP_CONVERT_RGB );
		
		
		std::cout << std::endl;
		std::cout << "movie width: " << cw << std::endl;
		std::cout << "movie height: " << ch << std::endl;
		std::cout << "movie FPS: " << fps << std::endl;
		std::cout << "movie position: " << milli << std::endl;
		std::cout << "movie frame: " << frame << "/" << nbframes << std::endl;
		std::cout << "movie ratio: " << ratio << std::endl;
		std::cout << "capture brightness: " << brightness << std::endl;
		std::cout << "capture contrast: " << contrast << std::endl;
		std::cout << "capture saturation: " << saturation << std::endl;
		std::cout << "capture hue: " << hue << std::endl;
		std::cout << "capture codec: " << fourcc << std::endl;
		std::cout << std::endl;
	}
	catch( ... ) { std::cout << "error" << std::endl; }*/

	return max( 0., cvGetCaptureProperty(capture, property) );
}

/**
 * Grab the next capture image to affect the original buffer.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_read
(
	JNIEnv * env, jobject obj
)
{
	
	// exit to prevent errors if no capture exists
	// or if OpenCV dimensions are not set
	if ( !capture || !width ) return;
	
	
	// grab frame from the video stream
	// WARNING : The returned image should not be released or modified
	IplImage * frame = cvQueryFrame( capture );
	if ( !frame ) return;
	
	
	// scale image if needed
	if ( frame->width!=width || frame->height!=height ) cvResize( frame, source, interpolation_method );
	else cvCopy( frame, source );
	
	// automatically flip Windows bitmap : origin = bottom-left
	if ( frame->origin==1 ) cvFlip( source, NULL, hypermedia_video_OpenCV_FLIP_VERTICAL );
	
	// copy the original image to the buffer
	Java_hypermedia_video_OpenCV_restore( env, obj, RGB );
	
}


/**
 * Jump to a specific image.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_jump
(
	JNIEnv * env, jobject obj,
	jfloat value, jint type
)
{
	if ( capture ) cvSetCaptureProperty( capture, (int)type, (double)value );
}

///////////////////////////// Region of interest ///////////////////////////////////////////////////

/**
 * Sets buffer region of interest to the given rectangle.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 *
 * @param x		the region x coordinate
 * @param y		the region y coordinate
 * @param w		the region width
 * @param h		the region height
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_ROI
(
	JNIEnv * env, jobject obj,
	jint x, jint y , jint w, jint h
)
{
	
	// clear the previous images ROI
	cvResetImageROI( buffer );
	if ( memory ) cvResetImageROI( memory );
	
	// set the new images ROI,
	// except for entire image to deallocate buffer->roi
	if ( x!=0 || y!=0 || w!=width || h!=height ) {
		cvSetImageROI( buffer, cvRect( min((int)x,width-1), min((int)y,height-1), max(1,(int)w), max(1,(int)h) ) );
		if ( memory ) cvSetImageROI( memory, cvGetImageROI(buffer) );
	}
}


/**
 * Invert buffer data colors
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_invert
(
	JNIEnv * env, jobject obj
)
{
	
	if ( buffer ) cvXorS( buffer, cvScalarAll(255), buffer, 0 );
	
}

/**
 * Smooth the image in one of several ways.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 *
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_blur
(
	JNIEnv * env, jobject obj,
	jint type, jint param1, jint param2, jfloat param3, jfloat param4
)
{	
	// nothing to do without buffer
	// or with non-converted image
	if ( !buffer ) return;
	
	
	/** --------------------------------------------------------------------------------------------
		CV errors : Only normalized box filter can be used for 8u->8u transformation
	 -------------------------------------------------------------------------------------------- */
	if ( type==CV_BLUR_NO_SCALE ) return;
	/*if ( type==CV_BLUR_NO_SCALE && buffer->nChannels!=1 ) {
		std::cout << "you must convert the image to GRAY before using the BLUR_NO_SCALE operation";
		std::cout << endl;
		return;
	 }*/
	
	
	
	// define the maximum value relativily to the actual buffer size
	int maximum = ( (width*height) / (abs(width-height)*2) )+1;
	
	// convert pair values to the upper odd values (if need)
	if ( param1%2==0 ) param1++;
	if ( param2!=0 && param2%2==0 ) param2++;
	
	
	// smooth buffer
	cvSmooth(
		buffer,
		buffer,
		(int) type,
		min( max(1,(int)param1), maximum),
		min( max(0,(int)param2), maximum), 
		(double) param3,
		(double) param4
	);
}



/**
 * Adjust the buffer brightness/contrast with the given values.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 *
 * @param brightness	the new brightness value (
 * @param contrast		the new contrast value
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_brightnessContrast
(
	JNIEnv * env, jobject obj,
	jint brightness, jint contrast
)
{
	
	// of course -> exit
	if ( !buffer ) return;
	
	
	
	uchar data[ 256 ];
	CvMat * matrix;
	double delta, a, b;
	
	// create a new matrix header (8-bit unsigned single-channel)
	// to assigns image data
	matrix = cvCreateMatHeader( 1, 256, CV_8UC1 );
    cvSetData( matrix, data, 0 );
	
	
	// set values
	if ( contrast>0 ) {
        delta = (127.*contrast) / 128;
        a = 255. / ( 255.-(delta*2) );
        b = a * (brightness-delta);
    }
    else {
		delta = (-128.*contrast) / 128;
		a = ( 256.-(delta*2) ) / 255.;
		b = ( a*brightness )+delta;
    }

	
	// update data
	for( int i=0; i<256; i++ ) {
		int value = cvRound( (a*i)+b );
		data[i]	= (uchar) min( max(0,value), 255 );
	}
	
	// performs look-up table transform
    cvLUT( buffer, buffer, matrix );
	
	
	// deallocate matrix
	cvReleaseMat( &matrix );
}

////////////////////////////////////// OBJECT DETECTION ////////////////////////////////////////////


/**
 * Load and store file description of a trained cascade classifier for a futur objects detection.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 * @param file	the haar classifier cascade file to be used for object detection
 */
JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_cascade
(
	JNIEnv * env, jobject obj,
	jstring file
)
{
	
	
	if ( !buffer ) return;
	

	// get file absolute path
	jclass cls 	  = env->GetObjectClass( obj );
	jmethodID mid = env->GetMethodID( cls, "absolutePath", "(Ljava/lang/String;)Ljava/lang/String;" );
	jstring path  = (jstring) env->CallObjectMethod( obj, mid, file );
		
	
	// convert file name and file path
	const char * usrfile = env->GetStringUTFChars( file, JNI_FALSE );
	const char * abspath = env->GetStringUTFChars( path, JNI_FALSE );
	
		
	/*	if ( env->ExceptionOccurred() ) {
			std::cout << "fuck" << std::endl; //env->ExceptionDescribe() << std::endl;
			env->ExceptionClear();
		}
		
	std::cout << "file : " << usrfile << std::endl;
	std::cout << "path : " << abspath << std::endl;*/
	
	// load object detection description
	cascade = (CvHaarClassifierCascade*) cvLoad( abspath );
	
	// report error if file description can not be loaded
	if ( !cascade ) {
		std::cout << "The haar classifier cascade file '" << usrfile;
		std::cout << "' can not be found in folders, you must specify the full path instead.";
		std::cout << std::endl;
	}
	
	
	// free memory
	env->ReleaseStringUTFChars( file, usrfile );
	env->ReleaseStringUTFChars( path, abspath );

}

/**
 * Detects object(s) in the current buffer with the actual cascade classifier.
 *
 * @param env	pointer to native method interface
 * @param obj	the Java object
 *
 * @return		a list of all rectangle as java.awt.Rectangle
 */
JNIEXPORT jobjectArray JNICALL Java_hypermedia_video_OpenCV_detect
(
	JNIEnv * env, jobject obj,
	jfloat scale, jint neighbors, jint flags, jint w, jint h
)
{
	
	
	// Find the Java Rectangle class object and constructor method
	jclass cls = env->FindClass( "java/awt/Rectangle" );
	jmethodID method = env->GetMethodID( cls, "<init>", "(IIII)V" );
	
	// create an empty output list for errors
	jobjectArray out = env->NewObjectArray( 0, cls, NULL );
	
	
	
	
	// nothing to do without this elements
	// -> exit
	if ( !buffer || !cascade ) return out;
	
	
	
	
	// allocate memory for calculations
	CvMemStorage * storage = cvCreateMemStorage( 0 );
	
	// there can be more than one face in an image -> create a growable sequence of faces.
	// detect the objects and store them in the sequence
	CvSeq * faces = cvHaarDetectObjects(
						buffer,
						cascade,
						storage,
						(double) scale, 
						(int) neighbors, 
						(int) flags,
						cvSize( (int)w, (int)h ) 
					);
	
	
	
	// define output list length
	out = env->NewObjectArray( faces->total, cls, NULL );
	
	
	// convert each face rectangle
	for( int i=0; i<faces->total; i++ ) {
		// define face rectangle
		CvRect * rect = (CvRect*) cvGetSeqElem( faces, i );
		// Construct a new Java Rectangle
		jobject elem = env->NewObject( cls, method, rect->x, rect->y, rect->width, rect->height );
		// place the new elemnt in the output list
		env->SetObjectArrayElement( out, i, elem );
		
	}
	
	
	// clear the memory storage
    cvReleaseMemStorage( &storage );
	
	// return the result
	return out;
	
	
}


/**
 * Blob's area quicksort comparator : reorder areas from the largest to the smallest.
 * This code was taken from the openframeworks version 0.04 opencv addon example.
 * http://openframeworks.cc/
 */
static int qsort_blobarea_comparator( const void * elem1, const void * elem2 ) {
	
	float a1 = fabs( cvContourArea(*((CvSeq **)elem1)) );
	float a2 = fabs( cvContourArea(*((CvSeq **)elem2)) );
	
	return a1>a2 ? -1 : 1;
}




/*JNIEXPORT void JNICALL Java_hypermedia_video_OpenCV_perspective( JNIEnv * env, jobject jobj ) {

	CvMat* matrix = cvCreateMatHeader( 3, 3, CV_32FC1 );
    cvCreateData( matrix );
	
	cvWarpPerspective( buffer, buffer, matrix, CV_WARP_INVERSE_MAP+CV_WARP_FILL_OUTLIERS, cvScalarAll(0) );
	
	// deallocate matrix
	cvReleaseMat( &matrix );
}*/




