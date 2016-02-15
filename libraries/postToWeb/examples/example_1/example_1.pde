import processing.pdf.*;
import org.seltar.Bytes2Web.*;
PDFToWeb pdf;

void setup()
{
  size(800,600);
  pdf = new PDFToWeb(this);
}


void draw()
{
  background(0);
  stroke(255);
  line(random(width),random(height),random(width),random(height));
  pdf.addPage(); // if you want each frame to be on it's own page
}

void keyPressed()
{
  String url = "http://yourdomain.com/Upload.php";
  if(key == 'p'){
    if(!pdf.isRecording()){
      pdf.startRecording();
    }else{
      pdf.save("pdf");
      pdf.post("test",url,"pdf-test",true);
    }
  }
  if(key == 'j'){
    ImageToWeb img = new ImageToWeb(this);
    img.save("jpg",true);
    img.post("test",url,"jpg-test",true,img.getBytes(g));
  }
  if(key == 't'){
    ImageToWeb img = new ImageToWeb(this);
    img.setType(ImageToWeb.TIFF);
    img.save("tiff",true);
    img.post("test",url,"tiff-test",true);
  }
  if(key == 'n'){
    ImageToWeb img = new ImageToWeb(this);
    img.setType(ImageToWeb.PNG);
    img.save("png",true);
    img.post("test",url,"png-test",true);
  }
  if(key == 'x'){
    ByteToWeb bytes = new ByteToWeb(this);
    String text = "Hello World!";
    //bytes.save("hello.txt",text.getBytes());
    bytes.post("test",url,"hello.txt",true,text.getBytes());
  }
  if(key == 'g'){
    ImageToWeb img = new ImageToWeb(this);
    img.setType(ImageToWeb.GIF);
    img.save("gif",true);
    img.post("test",url,"gif-test",true);
  }
}


			