package hog;

public class EasyVoter implements Voter{

	public float vote(float magnitude) {
		// TODO Auto-generated method stub
		return 1;
	}
	
	public static Voter createEasyVoter() {
		return new EasyVoter();
	}

}
