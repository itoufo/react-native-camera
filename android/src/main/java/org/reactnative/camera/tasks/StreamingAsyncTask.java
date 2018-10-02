package org.reactnative.camera.tasks;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import android.graphics.Matrix;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.util.Log;
import android.util.Base64;

public class StreamingAsyncTask extends android.os.AsyncTask<Void, Void, String> {
  private byte[] mImageData;
  private int imgWidth;
  private int imgHeight;
  private int mScreenWidth;
  private int mScreenHeight;
  private int mOrientation;
  private Rect mRect;
  private StreamingAsyncTaskDelegate mDelegate;
  private Bitmap mBitmap;

  //  note(sjchmiela): From my short research it's ok to ignore rotation of the image.
  public StreamingAsyncTask(
      StreamingAsyncTaskDelegate delegate,
      byte[] imageData,
      int width,
      int height,
      int screenWidth,
      int screenHeight,
      int orientation,
      Rect rect
  ) {
    mImageData = imageData;
    imgWidth = width;
    imgHeight = height;
    mScreenWidth = screenWidth;
    mScreenHeight = screenHeight;
    mRect = rect;
    mDelegate = delegate;
    mOrientation = orientation;
  }

  @Override
  protected String doInBackground(Void... ignored) {
    if (isCancelled() || mDelegate == null) {
      return null;
    }
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Bitmap bmp = convertYuvToJpeg(mImageData);
    bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
    byte[] baos = byteArrayOutputStream.toByteArray();
    return Base64.encodeToString(baos, Base64.DEFAULT);
  }

  public Bitmap convertYuvToJpeg(byte[] data) {
    int correctScreenHeight;
    int correctScreenWidth;
    int screenWidth = mScreenWidth;
    int screenHeight = mScreenHeight;
    float ratio = (float)imgWidth/(float)imgHeight;
    Log.d("ratio", String.valueOf(ratio));

    int width = mRect.width();
    int height = mRect.height();

    if (mOrientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
      Log.d("shape", "横長");
      if (ratio * height < width) { //横長
        correctScreenHeight = (int) (width/ ratio);
        correctScreenWidth = (int) width;
      } else {
        correctScreenWidth = (int) (height * ratio);
        correctScreenHeight = (int) height;
      }
    } else {
      Log.d("shape", "縦長");
      if (ratio * width > height) {
        correctScreenHeight = (int) (width * ratio);
        correctScreenWidth = (int) width;
      } else {
        correctScreenWidth = (int) (height / ratio);
        correctScreenHeight = (int) height;
      }
    }
    float imgPreviewRate = ((float)imgWidth/(float)correctScreenHeight);

    int paddingX2 = (int) ((correctScreenHeight - height) / 2);
    int paddingY2 = (int) ((correctScreenWidth - width) / 2);

    int paddingX3 = (int) (paddingX2*imgPreviewRate);
    int paddingY3 = (int) (paddingY2*imgPreviewRate);

    Log.d("debug", "***********************************");
    Log.d("screenPreviewRate", String.valueOf(imgPreviewRate));

    Log.d("correctScreenWidth", String.valueOf((int)(correctScreenWidth)));
    Log.d("correctScreenHeight", String.valueOf((int)(correctScreenHeight)));

    Log.d("width", String.valueOf((int)(width)));
    Log.d("height", String.valueOf((int)(height)));

    Log.d("paddingX", String.valueOf((int)(paddingX2)));
    Log.d("paddingY", String.valueOf((int)(paddingY2)));

    Log.d("paddingX2", String.valueOf((int)(paddingX2*imgPreviewRate)));
    Log.d("paddingY2", String.valueOf((int)(paddingY2*imgPreviewRate)));
    Log.d("debug", "***********************************");

    float screenImgRatio = (float)correctScreenWidth/(float)imgHeight;

    YuvImage image = new YuvImage(data, ImageFormat.NV21, imgWidth, imgHeight, null);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int quality = 100; //set quality
    image.compressToJpeg(new Rect(0, 0, imgWidth, imgHeight), quality, baos);//this line decreases the image quality
    byte[] jdata = baos.toByteArray();
    BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
    bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);

    Log.d("screenHeight", String.valueOf((int)(screenHeight)));
    Log.d("screenWidth", String.valueOf((int)(screenWidth)));

    Log.d("rect height", String.valueOf((int)(mRect.height())));
    Log.d("rect width", String.valueOf((int)(mRect.width())));

    Log.d("correctScreenWidth", String.valueOf((int)(correctScreenWidth)));
    Log.d("correctScreenHeight", String.valueOf((int)(correctScreenHeight)));

    Log.d("imgWidth", String.valueOf((int)(imgWidth)));
    Log.d("imgHeight", String.valueOf((int)(imgHeight)));
    Log.d("screenImgRatio", String.valueOf(screenImgRatio));

    Log.d("width", String.valueOf(((int)((float)mRect.height()/screenImgRatio))));
    Log.d("height", String.valueOf(((int)((float)mRect.width()/screenImgRatio))));

    Matrix matrix = new Matrix();
    matrix.postRotate(90);
    float scaleRate = 300.0f/(float)imgWidth;
    matrix.preScale(scaleRate, scaleRate);
    Log.d("scaleRate", String.valueOf(scaleRate));
    return Bitmap.createBitmap(bmp,
            paddingX3, paddingY3, imgWidth - paddingX3*2,imgHeight - paddingY3*2,
//            0, 0, 800, 600,
            matrix,
            true
    );
  }

  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);
    if (result != null) {
      mDelegate.onStreaming(result, imgHeight, imgWidth, mRect);
    }
    mDelegate.onStreamingTaskCompleted();
  }

  private BinaryBitmap generateBitmapFromImageData(byte[] imageData, int width, int height) {
    PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
        imageData, // byte[] yuvData
        width, // int dataWidth
        height, // int dataHeight
        0, // int left
        0, // int top
        width, // int width
        height, // int height
        false // boolean reverseHorizontal
    );
    return new BinaryBitmap(new HybridBinarizer(source));
  }
}
