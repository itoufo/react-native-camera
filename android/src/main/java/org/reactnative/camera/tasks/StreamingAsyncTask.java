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
import android.hardware.Camera;

public class StreamingAsyncTask extends android.os.AsyncTask<Void, Void, String> {
  private byte[] mImageData;
  private int mWidth;
  private int mHeight;
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
      int orientation,
      Rect rect
  ) {
    mImageData = imageData;
    mWidth = width;
    mHeight = height;
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
    Bitmap bmp = convertYuvToJpeg(mImageData, mWidth, mHeight);
    bmp.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
    byte[] baos = byteArrayOutputStream.toByteArray();
//    StringBuilder dataBuilder = new StringBuilder();
//    dataBuilder.append(Base64.encodeToString(baos, Base64.DEFAULT));
//    String result = dataBuilder.toString();
    return Base64.encodeToString(baos, Base64.DEFAULT);
  }

  public Bitmap convertYuvToJpeg(byte[] data, int width, int height) {
    YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
    Matrix matrix = new Matrix();
    matrix.postRotate(90);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int quality = 20; //set quality
    image.compressToJpeg(new Rect(0, 0, width, height), quality, baos);//this line decreases the image quality
    byte[] jdata = baos.toByteArray();
    BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
    bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);

    Log.d("width", String.valueOf(width));
    Log.d("height", String.valueOf(height));
    Log.d("out width", String.valueOf(bitmapFatoryOptions.outWidth));
    Log.d("out height", String.valueOf(bitmapFatoryOptions.outHeight));

    return Bitmap.createBitmap(bmp, 0, 0, bitmapFatoryOptions.outWidth, bitmapFatoryOptions.outHeight, matrix, true);
  }

  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);
    if (result != null) {
      mDelegate.onStreaming(result, mHeight, mWidth);
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
