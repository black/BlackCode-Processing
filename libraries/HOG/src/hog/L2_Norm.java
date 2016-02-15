package hog;

public class L2_Norm implements Norm{
	private double epsilon;
	public L2_Norm(double epsilon) {
		this.epsilon=epsilon;
	}
	//we calculate the L2_Norm of the vector
	public float[] normalize(float[] vector) {
		float norm2=getNorm2(vector);
		for (int i=0; i<vector.length; i++) {
			vector[i]=(float)(vector[i]/Math.sqrt(norm2*norm2+epsilon*epsilon));
		}
		return vector;
	}
	private float getNorm2(float[] vector) {
		float norm2=0;
		for (int i=0; i<vector.length; i++) {
			norm2+=vector[i]*vector[i];
		}
		norm2=(float)Math.sqrt(norm2);
		return norm2;
	}
	
	public static Norm createL2_Norm(double epsilon) {
		return new L2_Norm(epsilon);
	}
	
}
