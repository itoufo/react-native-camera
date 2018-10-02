package org.reactnative.camera.events;

import android.graphics.Rect;
import android.support.v4.util.Pools;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import org.reactnative.camera.StreamingViewManager;

public class StreamingEvent extends Event<StreamingEvent> {
  private static final Pools.SynchronizedPool<StreamingEvent> EVENTS_POOL =
      new Pools.SynchronizedPool<>(3);

  private String mBase64;
  private int mHeight;
  private int mWidth;
  private Rect mRect;

  private StreamingEvent() {}

  public static StreamingEvent obtain(int viewTag, String base64, int height, int width, Rect rect) {
    StreamingEvent event = EVENTS_POOL.acquire();
    if (event == null) {
      event = new StreamingEvent();
    }
    event.init(viewTag, base64, height, width, rect);
    return event;
  }

  private void init(int viewTag, String base64, int height, int width, Rect rect) {
    super.init(viewTag);
    mBase64 = base64;
    mHeight = height;
    mWidth = width;
    mRect = rect;
  }

  @Override
  public String getEventName() {
    return StreamingViewManager.Events.EVENT_ON_STREAMING.toString();
  }

  @Override
  public void dispatch(RCTEventEmitter rctEventEmitter) {
    rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
  }

  private WritableMap serializeEventData() {
    WritableMap event = Arguments.createMap();
    float imgWidth = 300f;
    event.putInt("target", getViewTag());
    event.putString("imagedata", mBase64);
    event.putInt("width", (int)imgWidth);
    event.putInt("height", (int)(imgWidth / (float)mRect.width() * (float)mRect.height()));
//    event.putInt("width", 300);
//    event.putInt("height", 400);
    return event;
  }
}
