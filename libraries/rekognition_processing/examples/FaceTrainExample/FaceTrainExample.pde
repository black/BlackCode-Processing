// Rekognition for Processing example
// Daniel Shiffman
// https://github.com/shiffman/Rekognition-for-Processing/

// This example also requires HTTP Requests for Processing
// https://github.com/runemadsen/HTTP-Requests-for-Processing

// Also, you need an API key.
// Sign up here: http://rekognition.com/register/
// Make a text file in your data folder called key.txt
// Put your API key on the first line and your API secret on the second line

import http.requests.*;
import rekognition.faces.*;

PImage img;

void setup() {
  size(300, 200);

  // Load the API keys
  String[] keys = loadStrings("key.txt");
  String api_key = keys[0];
  String api_secret = keys[1];

  Rekognition face = new Rekognition(this, api_key, api_secret);

  // Here we tell Rekognition that the face in this image associated with this name
  face.addFace("pitt.jpg", "Pitt");

  // We need a second API call to train Rekognition of whatever faces have been added
  // Here it's one face, then train, but you could add a lot of faces before training
  face.train();
}

void draw() {
  
  // Not doing anything in this example
  noLoop();
}

