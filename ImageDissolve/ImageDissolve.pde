//PImage img;
//ArrayList<ShapeImage> poop = new  ArrayList();
//int m=0;
//boolean moved;
//void setup() {
//  size(600, 600);
//  img = loadImage("https://i.pinimg.com/736x/e7/2a/8f/e72a8f54076e7575925301caf4254667--seo-kpop-girls.jpg");
//  for (int j=0; j<height; j+=50) {
//    for (int i=0; i<width; i+=50) {
//      ShapeImage clip = new ShapeImage(img, i, j);
//      poop.add(clip);
//    }
//  }
//}
//
//void draw() {
//  background(-1, 100);
//  translate(25, 25);
//  for (int i=0; i<poop.size (); i++) {
//    ShapeImage clip = poop.get(i); 
//    clip.show(m);
//    if (moved)clip.move(m);
//  }
//  m++;
//}
//
//void mousePressed() {
//  moved = true;
//}
//
//
//class ShapeImage {
//  PImage main, clip;
//  float x, y, w, ang, xspeed, yspeed, angspeed;
//  ShapeImage(PImage main, int x, int y) {
//    this.x = x;
//    this.y = y;
//    w = 50;
//    clip = main.get((int)x, (int)y, (int)w, (int)w);
//    xspeed = random(-1, 1);
//    yspeed = random(-1, 1);
//    angspeed = random(-PI/100, PI/100);
//  }
//  void show(int k) { 
//    pushMatrix();
//    translate(x, y);
//    rotate(ang);
//    imageMode(CENTER);
//    image(clip, 0, 0);
//    popMatrix();
//  }
//
//  void move(int k) {
//    x = x + xspeed;
//    y = y + yspeed;
//    ang = ang + angspeed;
//  }
//}


ArrayList < ShapeImage > poop = new ArrayList();

 var img, m = 0,
   moved = false;

 function setup() {
   createCanvas(600, 600);
   img = loadImage("https://i.pinimg.com/736x/e7/2a/8f/e72a8f54076e7575925301caf4254667--seo-kpop-girls.jpg");
 }

 function draw() {
   background(255, 0);
   translate(25, 25);
   for (var i = 0; i < poop.size(); i++) {
     ShapeImage clip = poop.get(i);
     clip.show(m);
     if (moved) clip.move(m);
   }
   m++;
 }

 function ShapeImage(main, x, y) {
   var main, clip;
   var x, y, w, ang, xspeed, yspeed, angspeed;
   this.main = main;
   this.x = x;
   this.y = y;
 }

 ShapeImage.prototype.initialize = function() {
   this.clip = main.get(x, y, w, w);
   xspeed = random(-1, 1);
   yspeed = random(-1, 1);
   angspeed = random(-PI / 100, PI / 100);
 }

 ShapeImage.prototype.show = function() {
   pushMatrix();
   translate(x, y);
   rotate(ang);
   imageMode(CENTER);
   image(clip, 0, 0);
   popMatrix();
 }

 ShapeImage.prototype.move = function() {
   x = x + xspeed;
   y = y + yspeed;
   ang = ang + angspeed;
 }
