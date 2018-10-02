package org.reactnative.camera.tasks;

import android.graphics.Rect;

import com.google.zxing.Result;

public interface StreamingAsyncTaskDelegate {
  void onStreaming(String base64, int height, int width, Rect rect);
  void onStreamingTaskCompleted();
}
