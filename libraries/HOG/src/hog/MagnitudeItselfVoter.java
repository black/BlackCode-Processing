package hog;

public class MagnitudeItselfVoter implements Voter{

	public float vote(float magnitude) {
		return magnitude;
	}
	
	public static Voter createMagnitudeItselfVoter() {
		return new MagnitudeItselfVoter();
	}

}
