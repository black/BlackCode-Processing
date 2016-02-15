package rekognition.faces;

import http.requests.PostRequest;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.data.JSONArray;
import processing.data.JSONObject;

import processing.core.PApplet;
//import processing.core.PImage;

public class Rekognition {

	String rekog_key = "";
	String rekog_secret = "";

	PApplet p5;

	String name_space = "default";
	String user_id = "default";

	public static final String api = "http://rekognition.com/func/api/";

	public Rekognition(PApplet p5_, String key, String secret) {
		p5 = p5_;
		rekog_key = key;
		rekog_secret = secret;
	}

	public RFace[] detectFacesPath(String path) {
		File f = new File(p5.sketchPath(path));
		// Now try the data path
		if (!f.exists()) {
			f = new File(p5.dataPath(path));
		}
		// Not worrying about size for now
		/*long size = f.length();
		int maxsize = 1000000;
		if (size > maxsize) {
			String tempPath = "temp/temp.jpg";
			PImage img = p5.loadImage(path);
			float ratio = size/(float)maxsize;
			int w = (int) (img.width/ratio);
			int h = (int) (img.height/ratio);
			img.resize(w,h);
			System.out.println("Too big, resizing to: " + w + "," + h);
			img.save(tempPath);
			return detectFacesPath(tempPath);
		} else {
			return detectFaces(f);
		}*/
		return detectFaces(f);
	}

	public RFace[] detectFacesURL(String url) {
		PostRequest post = new PostRequest(api);
		post.addData("api_key", rekog_key);
		post.addData("api_secret", rekog_secret);

		post.addData("name_space",name_space);
		post.addData("user_id",user_id);

		post.addData("jobs","face_part_gender_emotion_age_glass");

		post.addData("urls", url);
		post.send();
		String content = post.getContent();
		return facesFromJSON(content);
	}


	public RFace[] recognize(String s) {
		Pattern p = Pattern.compile("^http",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		if (m.find()) {
			return recognizeFacesURL(s);
		} else {
			return recognizeFacesPath(s);
		}
	}

	public RFace[] detect(String s) {
		Pattern p = Pattern.compile("^http",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		if (m.find()) {
			return detectFacesURL(s);
		} else {
			return detectFacesPath(s);
		}
	}



	public RFace[] detectFaces(File f) {
		PostRequest post = new PostRequest(api);
		post.addData("api_key", rekog_key);
		post.addData("api_secret", rekog_secret);

		post.addData("name_space",name_space);
		post.addData("user_id",user_id);

		post.addData("jobs","face_part_gender_emotion_age_glass");
		post.addFile("uploaded_file", f);
		post.send();
		String content = post.getContent();
		return facesFromJSON(content);
	}

	public RFace[]  recognizeFacesPath(String path) {
		PostRequest post = new PostRequest(api);
		post.addData("api_key", rekog_key);
		post.addData("api_secret", rekog_secret);

		post.addData("name_space",name_space);
		post.addData("user_id",user_id);

		post.addData("job_list", "face_recognize_part_gender_emotion_age_glass");
		File f = new File(p5.sketchPath(path));

		// Now try data paths
		if (!f.exists()) {
			f = new File(p5.dataPath(path));
		}
		post.addFile("uploaded_file", f);
		post.send();
		String content = post.getContent();

		return facesFromJSON(content);
	}

	public RFace[]  recognizeFacesURL(String url) {
		PostRequest post = new PostRequest(api);
		post.addData("api_key", rekog_key);
		post.addData("api_secret", rekog_secret);

		post.addData("name_space",name_space);
		post.addData("user_id",user_id);

		post.addData("job_list", "face_recognize_part_gender_emotion_age_glass");

		post.addData("urls", url);
		post.send();
		String content = post.getContent();

		return facesFromJSON(content);
	}


	public void addFace(String path, String name) {
		name = name.replaceAll("\\s", "_");

		PostRequest post = new PostRequest(api);
		post.addData("api_key", rekog_key);
		post.addData("api_secret", rekog_secret);

		post.addData("name_space",name_space);
		post.addData("user_id",user_id);

		post.addData("job_list", "face_add_[" + name + "]");

		File f = new File(p5.sketchPath(path));

		// Now try data path
		if (!f.exists()) {
			f = new File(p5.dataPath(path));
		}

		post.addFile("uploaded_file", f);
		post.send();
	}



	public void train() {
		PostRequest post = new PostRequest(api);
		post.addData("api_key", rekog_key);
		post.addData("api_secret", rekog_secret);

		post.addData("name_space",name_space);
		post.addData("user_id",user_id);
		//System.out.println("Train: " + name_space + " " + user_id);

		post.addData("job_list", "face_train");
		post.send();
		//String content = post.getContent();
		//System.out.println(content);	
	}


	public PostRequest createPostRequest() {
		PostRequest post = new PostRequest(api);
		post.addData("api_key", rekog_key);
		post.addData("api_secret", rekog_secret);

		post.addData("name_space",name_space);
		post.addData("user_id",user_id);
		return post;
	}




	public RFace[] facesFromJSON(String content) {
		JSONObject data = p5.parseJSONObject(content);
		JSONArray facearray = data.getJSONArray("face_detection");

		RFace[] faces = new RFace[facearray.size()];
		for (int i = 0; i < faces.length; i++) {
			faces[i] = new RFace();  // Fix to include width and height!
			faces[i].fromJSON(facearray.getJSONObject(i));
		}
		return faces;
	}

	public void setNamespace(String s) {
		name_space = s;
	}

	public void setUserID(String s) {
		user_id = s;
	}


}
