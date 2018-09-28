package org.reactnative.camera.events;

import android.support.v4.util.Pools;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.reactnative.camera.StreamingViewManager;

public class StreamingCompletedEvent extends Event<StreamingCompletedEvent> {
  private static final Pools.SynchronizedPool<StreamingCompletedEvent> EVENTS_POOL =
      new Pools.SynchronizedPool<>(3);

  private StreamingCompletedEvent() {}

  public static StreamingCompletedEvent obtain(int viewTag) {
    StreamingCompletedEvent event = EVENTS_POOL.acquire();
    if (event == null) {
      event = new StreamingCompletedEvent();
    }
    event.init(viewTag);
    return event;
  }

  protected void init(int viewTag) {
    super.init(viewTag);
  }

  @Override
  public String getEventName() {
    return StreamingViewManager.Events.EVENT_ON_STREAMING_COMPLETED.toString();
  }

  @Override
  public void dispatch(RCTEventEmitter rctEventEmitter) {
    rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
  }

  private WritableMap serializeEventData() {
    WritableMap event = Arguments.createMap();
    return event;
  }
}
