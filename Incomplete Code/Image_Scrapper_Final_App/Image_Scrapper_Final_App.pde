void setup() {
  size(200, 250, P3D);
}
int t=7;
void draw() {
  background(-1);
  fill(0); 
  //  http://hentaicdn.com/hentai/12954/1/hcdn0015.jpg
  //  https://g.hitomi.la/galleries/147056/1700.jpg
  //  https://g.hitomi.la/galleries/147056/1700x.jpg
  //  https://g.hitomi.la/galleries/147056/l1700a.jpg
  //  https://g.hitomi.la/galleries/147056/l1700b.jpg
  //  https://g.hitomi.la/galleries/147056/l1701.jpg // 5 - 54 
  //  http://hentaicdn.com/hentai/11298/1/p0006.jpg
  //  https://g.hitomi.la/galleries/775750/14.gif
  //  https://g.hitomi.la/galleries/775750/15.jpg
  //  https://g.hitomi.la/galleries/775750/16.jpg 54 
  String url2 = "https://g.hitomi.la/galleries/147056/l170"+t+".jpg";   // _"+nf(k, 3)+".jpg"; // https://hitomi.la/reader/827053.html#212
  String s3 = download(url2, 3, t);
  text(s3, 10, 40);
  t++;
}


String download(String url, int folder, int file) {
  String str = " ";
  PImage img = null;
  if (loadStrings(url)!=null) {
    img = loadImage(url, "jpg");
    img = loadImage(url, "gif");
    img.save("data/comics"+folder+"/"+file+".jpg");
    str = "found " + file +".jpg";
  } else str = "Not found !..";
  return str;
}

