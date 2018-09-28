package org.reactnative.camera;

import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StreamingViewManager extends ViewGroupManager<StreamingCameraView> {
  public enum Events {
    EVENT_CAMERA_READY("onCameraReady"),
    EVENT_ON_MOUNT_ERROR("onMountError"),
    EVENT_ON_BAR_CODE_READ("onBarCodeRead"),
    EVENT_ON_STREAMING("onStreaming"),
    EVENT_ON_STREAMING_COMPLETED("onStreamingCompleted"),
    EVENT_ON_FACES_DETECTED("onFacesDetected"),
    EVENT_ON_BARCODES_DETECTED("onGoogleVisionBarcodesDetected"),
    EVENT_ON_FACE_DETECTION_ERROR("onFaceDetectionError"),
    EVENT_ON_BARCODE_DETECTION_ERROR("onGoogleVisionBarcodeDetectionError"),
    EVENT_ON_TEXT_RECOGNIZED("onTextRecognized"),
    EVENT_ON_PICTURE_SAVED("onPictureSaved");

    private final String mName;

    Events(final String name) {
      mName = name;
    }

    @Override
    public String toString() {
      return mName;
    }
  }

  private static final String REACT_CLASS = "StreamingCamera";

  @Override
  public void onDropViewInstance(StreamingCameraView view) {
    view.stop();
    super.onDropViewInstance(view);
  }


  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  protected StreamingCameraView createViewInstance(ThemedReactContext themedReactContext) {
    return new StreamingCameraView(themedReactContext);
  }

  @Override
  @Nullable
  public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
    MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
    for (Events event : Events.values()) {
      builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
    }
    return builder.build();
  }

  @ReactProp(name = "type")
  public void setType(StreamingCameraView view, int type) {
    view.setFacing(type);
  }

  @ReactProp(name = "ratio")
  public void setRatio(StreamingCameraView view, String ratio) {
    view.setAspectRatio(AspectRatio.parse(ratio));
  }

  @ReactProp(name = "flashMode")
  public void setFlashMode(StreamingCameraView view, int torchMode) {
    view.setFlash(torchMode);
  }

  @ReactProp(name = "autoFocus")
  public void setAutoFocus(StreamingCameraView view, boolean autoFocus) {
    view.setAutoFocus(autoFocus);
  }

  @ReactProp(name = "focusDepth")
  public void setFocusDepth(StreamingCameraView view, float depth) {
    view.setFocusDepth(depth);
  }

  @ReactProp(name = "zoom")
  public void setZoom(StreamingCameraView view, float zoom) {
    view.setZoom(zoom);
  }

  @ReactProp(name = "whiteBalance")
  public void setWhiteBalance(StreamingCameraView view, int whiteBalance) {
    view.setWhiteBalance(whiteBalance);
  }
    
  @ReactProp(name = "pictureSize")
  public void setPictureSize(StreamingCameraView view, String size) {
    view.setPictureSize(size.equals("None") ? null : Size.parse(size));
  }

  @ReactProp(name = "barCodeTypes")
  public void setBarCodeTypes(StreamingCameraView view, ReadableArray barCodeTypes) {
    if (barCodeTypes == null) {
      return;
    }
    List<String> result = new ArrayList<>(barCodeTypes.size());
    for (int i = 0; i < barCodeTypes.size(); i++) {
      result.add(barCodeTypes.getString(i));
    }
    view.setBarCodeTypes(result);
  }
  @ReactProp(name = "streamingEnabled")
  public void setStreaming(StreamingCameraView view, boolean streamingEnabled) {
    view.setShouldStreaming(streamingEnabled);
  }
}
