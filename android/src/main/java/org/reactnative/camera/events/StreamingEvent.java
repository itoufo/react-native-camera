package org.reactnative.camera.events;

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

  private StreamingEvent() {}

  public static StreamingEvent obtain(int viewTag, String base64) {
    StreamingEvent event = EVENTS_POOL.acquire();
    if (event == null) {
      event = new StreamingEvent();
    }
    event.init(viewTag, base64);
    return event;
  }

  private void init(int viewTag, String base64) {
    super.init(viewTag);
    mBase64 = base64;
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
    event.putInt("target", getViewTag());
    event.putString("data", mBase64);
    return event;
  }
}
