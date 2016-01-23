class DateStamp {
  int d = day();     
  int mth = month(); 
  int y = year();
  int s = second();  // Values from 0 - 59
  int m = minute();  // Values from 0 - 59
  int h = hour();

  String[] month = {
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  };

  String createStamp() { 
    return d +" " + month[mth]+" "+y +"    "+ ((h>12)?h-12:h) +":"+ nf(m, 2);
  }
}

