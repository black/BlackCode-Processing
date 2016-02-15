package hog;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import processing.core.PApplet;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import utility.Matrix;


/*
 * The current class is used to do detecting operations in a window of the image.
 */
public class DetectionWindow extends Thread {
	
	private PixelGradientVector[][] pgv;
	private Rect image_rect;
	private Point_3D detection_point;
	private HistogramsComputation hc;
	private BlocksComputation bc;
	private DescriptorComputation dc;
	private svm_model model;
	private ArrayList<Point_3D> detection_list;
	Semaphore wait_for_all;

	public DetectionWindow(Semaphore wait_for_all,PixelGradientVector pgv[][],HistogramsComputation hc,BlocksComputation bc,
			DescriptorComputation dc,Rect image_rect,Point_3D detection_point,svm_model model,ArrayList<Point_3D> detection_list) {
		this.pgv=pgv;
		this.wait_for_all=wait_for_all;
		this.image_rect=image_rect;
		this.detection_point=detection_point;
		this.hc=hc;
		this.bc=bc;
		this.dc=dc;
		this.model=model;
		this.detection_list=detection_list;
	}
	
	public void run() {
		
		PixelGradientVector sub_pgv[][]=Matrix.getSubMatrix(pgv, image_rect.getX(), image_rect.getWidth(), image_rect.getY(), image_rect.getHeight());
		Histogram[][] histograms = hc.computeHistograms(sub_pgv);
		Block[][] unnormalized_blocks = bc.computeBlocks(histograms);
		Block[][] normalized_blocks = bc.normalizeBlocks(unnormalized_blocks);
		float[] descriptor = dc.computeDescriptor(normalized_blocks);
		
		//creating svm_node[]
		svm_node nodes[] = new svm_node[descriptor.length];
		for( int j=0; j<descriptor.length; j++ ){
			nodes[j] = new svm_node();
			nodes[j].index=j+1;
			nodes[j].value=descriptor[j];
		}
		
		//predicting the decision for this detection window
		double predict[] = new double[1];
		svm.svm_predict_values(model, nodes, predict);
		
		detection_point.setWeight(predict[0]);
		
		if (predict[0]>0) {
			synchronized (detection_list) {
				detection_list.add(detection_point);
			}
		}
		
		//predicting the decision for this detection window
		//double predict = svm.svm_predict(model, nodes);
		
		
		//We add the detection window to the list of all positive detection window found in the image at different scale.*/
		/*if( predict==+1 ){
			synchronized (detection_list) {
				detection_list.add(detection_point);
			}
		}*/
		
		
		
		//release the semaphore wait_for_all
		wait_for_all.release();
	}

}
