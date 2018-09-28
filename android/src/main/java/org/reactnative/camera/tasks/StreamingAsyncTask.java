package org.reactnative.camera.tasks;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import java.util.Base64;

public class StreamingAsyncTask extends android.os.AsyncTask<Void, Void, String> {
  private byte[] mImageData;
  private int mWidth;
  private int mHeight;
  private StreamingAsyncTaskDelegate mDelegate;

  //  note(sjchmiela): From my short research it's ok to ignore rotation of the image.
  public StreamingAsyncTask(
      StreamingAsyncTaskDelegate delegate,
      byte[] imageData,
      int width,
      int height
  ) {
    mImageData = imageData;
    mWidth = width;
    mHeight = height;
    mDelegate = delegate;
  }

  @Override
  protected String doInBackground(Void... ignored) {
    if (isCancelled() || mDelegate == null) {
      return null;
    }
    String result = Base64.getEncoder().encodeToString(mImageData);
    return result;
  }

  private byte[] rotateImage(byte[]imageData,int width, int height) {
    byte[] rotated = new byte[imageData.length];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        rotated[x * height + height - y - 1] = imageData[x + y * width];
      }
    }
    return rotated;
  }
  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);
    if (result != null) {
      mDelegate.onStreaming(result);
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
