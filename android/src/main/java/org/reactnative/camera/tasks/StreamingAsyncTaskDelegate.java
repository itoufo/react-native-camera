package org.reactnative.camera.tasks;

import com.google.zxing.Result;

public interface StreamingAsyncTaskDelegate {
  void onStreaming(String base64, int height, int width);
  void onStreamingTaskCompleted();
}
