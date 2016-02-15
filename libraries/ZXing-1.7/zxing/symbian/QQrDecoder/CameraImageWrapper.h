#ifndef CAMERAIMAGE_H
#define CAMERAIMAGE_H

#include <QImage>
#include <QString>
#include <zxing/LuminanceSource.h>

using namespace zxing;

class CameraImageWrapper : public LuminanceSource
{
public:
    CameraImageWrapper();
    CameraImageWrapper(CameraImageWrapper& otherInstance);
    ~CameraImageWrapper();
    
    int getWidth() const;
    int getHeight() const;
    
    unsigned char getPixel(int x, int y) const;
    unsigned char* copyMatrix() const;
    
    void setImage(QString fileName);
    void setImage(QImage newImage);
    QImage grayScaleImage(QImage::Format f);
    QImage getOriginalImage();
  
private:
    QImage image;
};

#endif //CAMERAIMAGE_H
