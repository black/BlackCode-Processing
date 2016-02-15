
import hog.*;
import javax.swing.JFileChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

void setup() {

	// Standard settings (these are optimal settings)
	int window_width=64;
	int window_height=128;
	int bins = 9;
	int cell_size = 8;
	int block_size = 2;
	boolean signed = false;
	int overlap = 0;
	int stride=16;
	int number_of_resizes=5;

	JOptionPane pane = new JOptionPane("Is this a model with overlap?");
	Object[] options = new String[] { "Yes", "No" };
	pane.setOptions(options);
	JDialog dialog = pane.createDialog(new JFrame(), "Dilaog");
	dialog.show();
	Object obj = pane.getValue();
	if( obj=="Yes")
		overlap=1;
	System.out.println("Overlap: "+overlap);

	HOG_Factory hog=HOG.createInstance();

	GradientsComputation gc=hog.createGradientsComputation();
	Voter voter=MagnitudeItselfVoter.createMagnitudeItselfVoter();
	HistogramsComputation hc=hog.createHistogramsComputation( bins, cell_size, cell_size, signed, voter);
	Norm norm=L2_Norm.createL2_Norm(0.1);
	BlocksComputation bc=hog.createBlocksComputation(block_size, block_size, overlap, norm);
	DescriptorComputation dc=hog.createDescriptorComputation();
	Detector detector=hog.createDetector(gc, hc, bc, dc, window_width, window_height, stride, number_of_resizes);


	System.out.print("Select a model...");
	final JFileChooser fc = new JFileChooser();
	String model_string = null;
	if( fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION ){
		model_string=fc.getSelectedFile().toString();
		System.out.println(model_string);
	}else{
		System.out.println("ERROR: Select a model!!!");
		System.exit(1);
	}
	// Select the model:
	try{
		detector.load_model(model_string);
	} catch (IOException e) {
		e.printStackTrace();
	}

	System.out.print("Select an image...");
	final JFileChooser fc2 = new JFileChooser();
	String image_string = null;
	if( fc2.showOpenDialog(null)==JFileChooser.APPROVE_OPTION ){
		image_string=fc2.getSelectedFile().toString();
		System.out.println(image_string);
	}else{
		System.out.println("ERROR: Select an image!!!");
		System.exit(1);
	}

	PImage img = loadImage(image_string);
	size(img.width,img.height);
	image(img,0,0);

	try {   
		ArrayList<Point_3D> detected_points = detector.detect(img, this);
		Hog_Printer.print_results_onvideo(this, detected_points, window_width, window_height, Hog_Printer.RED);
	} catch (InterruptedException ex) {
		ex.printStackTrace();
	}

}
