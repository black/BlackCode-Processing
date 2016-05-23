/**
 * This implementation of Sobel Edge Detection is stripped down
 * and adapted from the implementation by Ian Gorse at:
 * http://www.openprocessing.org/visuals/?visualID=2301
 * Ian Gorse credits http://www.pages.drexel.edu/~weg22/edge.html
 * for description and source
**/

class SobelEdgeDetector
{
  // Sobel Edge Detection strandard, this applies the edge
  // detection algorithm across the entire image and returns an array
  // of pixels, with all edges as white (color(255)), and all else black (color(0))
  public int[] findEdgesAll(PImage img, int tolerance)
  {
    int[] buf = new int[img.width*img.height];
    // 3x3 Sobel Mask for X
    int GX[][] = {{-1, 0, 1}, 
                  {-2, 0, 2}, 
                  {-1, 0, 1}};
    // 3x3 Sobel Mask for Y
    int GY[][] = {{1, 2, 1}, 
                  {0, 0, 0}, 
                  {-1, -2, -1}};
    int sumRx = 0;
    int sumGx = 0;
    int sumBx = 0;
    int sumRy = 0;
    int sumGy = 0;
    int sumBy = 0;

    for (int y = 0; y < img.height; y++) {
      for (int x = 0; x < img.width; x++) {
        if (y != 0 && y != img.height - 1 && x != 0 && x != img.width - 1) {
          // Convolve across the X axis and return gradiant aproximation
          for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
              color col =  img.get(x+i, y+j);
              sumRx += red(col)*GX[i+1][j+1];
              sumGx += green(col)*GX[i+1][j+1];
              sumBx += blue(col)*GX[i+1][j+1];
            }
          }

          // Convolve across the Y axis and return gradiant aproximation
          for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
              color col =  img.get(x+i, y+j);
              sumRy += red(col)*GY[i+1][j+1];
              sumGy += green(col)*GY[i+1][j+1];
              sumBy += blue(col)*GY[i+1][j+1];
            }
          }

          int finalSumR = abs(sumRx) + abs(sumRy);
          int finalSumG = abs(sumGx) + abs(sumGy);
          int finalSumB = abs(sumBx) + abs(sumBy);

          // I only want to return a black or a white value, here I determine the greyscale value,
          // and if it is above a tolerance, then set the colour to white
          float gray = (finalSumR + finalSumG + finalSumB) / 3;
          int c = color(0);
          if (gray > tolerance)
            c = color(255);

          buf[x + y*img.width] = c;
          sumRx = sumGx = sumBx = sumRy = sumGy = sumBy = 0;
        }
      }
    } 
    return buf;
  }
}

