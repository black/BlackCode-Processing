/* Processing QRCode Library
 * Daniel Shiffman, 6/26/2007
 * Based on code by Tom Igoe
 * Generate images from: http://qrcode.kaywa.com/
 * Get QRCode java library from: http://qrcode.sourceforge.jp/
 * Requires: qrcode.jar
 */

package qrcodeprocessing;

import java.awt.image.BufferedImage;
import jp.sourceforge.qrcode.codec.data.QRCodeImage;

/*
  The decoder needs an image class that implements QRCodeImage,
  which is part of the decoder library.  This class just allows
  you to pass a BufferedImage that implements QRCodeImage
*/
class QRImage implements QRCodeImage {
  BufferedImage image;

  public QRImage(BufferedImage image) {
    this.image = image;
  }

  public int getWidth() {
    return image.getWidth();
  }

  public int getHeight() {
    return image.getHeight();
  }

  public int getPixel(int x, int y) {
    return image.getRGB(x, y);
  }

}

