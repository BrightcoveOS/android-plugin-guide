/**
 * Copyright (C) 2013 Brightcove Inc. All Rights Reserved. No use, copying or distribution of this
 * work may be made except in accordance with a valid license agreement from Brightcove Inc. This
 * notice must be included on all copies, modifications and derivatives of this work.
 *
 * Brightcove Inc MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BRIGHTCOVE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * "Brightcove" is a registered trademark of Brightcove Inc.
 */
package com.brightcove.player.plugin.sample;

import android.content.Context;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.VideoView;
import com.brightcove.player.event.AbstractComponent;
import com.brightcove.player.event.Component;
import com.brightcove.player.event.Emits;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.event.ListensFor;
import java.util.HashMap;
import java.util.Map;

@Emits(events = {
        EventType.WILL_INTERRUPT_CONTENT,
        EventType.WILL_RESUME_CONTENT,
})
@ListensFor(events = {
        EventType.COMPLETED,
        EventType.CUE_POINT,
        EventType.DID_PAUSE,
        EventType.DID_PLAY,
        EventType.DID_SEEK_TO,
        EventType.DID_SET_SOURCE,
        EventType.DID_SET_VIDEO,
        EventType.DID_STOP,
        EventType.PROGRESS,
})
/**
 * This class is an example of how plugins can listen for events and
 * how events can be emitted to interrupt and resume video playback.
 * It also logs the properties of commonly useful events.
 */
public class SamplePlugin extends AbstractComponent
    implements Component, OnCompletionListener, OnErrorListener {

    public static final String TAG = SamplePlugin.class.getSimpleName();

    private VideoView videoView;
    private Context context;
    private ViewGroup viewGroup;

    // Used to resume PLAY or COMPLETED after the ads are finished.
    private Event originalEvent;

    public SamplePlugin(EventEmitter emitter, Context context, ViewGroup viewGroup) {
        super(emitter, SamplePlugin.class);
        this.context = context;
        this.viewGroup = viewGroup;

        Log.d(TAG, "Initializing " + TAG);
        initializeListeners();
    }

    private void initializeListeners() {
        addListener(EventType.COMPLETED, new OnCompletedListener());
        addListener(EventType.CUE_POINT, new OnCuePointListener());
        addListener(EventType.DID_PAUSE, new OnDidPauseListener());
        addListener(EventType.DID_PLAY, new OnDidPlayListener());
        addListener(EventType.DID_SEEK_TO, new OnDidSeekToListener());
        addListener(EventType.DID_SET_SOURCE, new OnDidSetSourceListener());
        addListener(EventType.DID_SET_VIDEO, new OnDidSetVideoListener());
        addListener(EventType.DID_STOP, new OnDidStopListener());
        addListener(EventType.PROGRESS, new OnProgressListener());
    }

    private class OnCuePointListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnCuePointListener: " + event.properties);

            // Store the original event, so it can be emitted again
            // upon resume.
            originalEvent = (Event) event.properties.get(Event.ORIGINAL_EVENT);

            eventEmitter.emit(EventType.WILL_INTERRUPT_CONTENT);

            // Simulate playing a video advertisement.
            videoView = new VideoView(context);
            FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                             ViewGroup.LayoutParams.MATCH_PARENT,
                                             Gravity.CENTER);
            viewGroup.addView(videoView, layoutParams);
            videoView.setVideoPath("http://solutions.brightcove.com/truggles/temp/fishtank.mp4");
            videoView.setOnCompletionListener(SamplePlugin.this);
            videoView.setOnErrorListener(SamplePlugin.this);
            videoView.start();
        }
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.v(TAG, "onCompletion");
        resume();
    }

    public boolean onError(MediaPlayer mediaPlayer, int frameworkError, int implementationError) {
        Log.v(TAG, "onError: " + frameworkError + ", " + implementationError);
        resume();
        return true;
    }

    private void resume() {
        try {
            videoView.suspend();
        } catch (Exception exception) {
            // ignore
        }

        viewGroup.removeView(videoView);

        if (originalEvent == null) {
            originalEvent = new Event(EventType.PLAY);
            originalEvent.properties.put(Event.SKIP_CUE_POINTS, true);
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Event.ORIGINAL_EVENT, originalEvent);
        eventEmitter.emit(EventType.WILL_RESUME_CONTENT, properties);
    }

    private class OnDidSetSourceListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnDidSetSourceListener: " + event.properties);
        }
    }

    private class OnDidSetVideoListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnDidSetVideoListener: " + event.properties);
        }
    }

    private class OnDidPlayListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnDidPlayListener: " + event.properties);
        }
    }

    private class OnDidPauseListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnDidPauseListener: " + event.properties);
        }
    }

    private class OnDidSeekToListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnDidSeekToListener: " + event.properties);
        }
    }

    private class OnDidStopListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnDidStopListener: " + event.properties);
        }
    }

    private class OnProgressListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnProgressListener: " + event.properties);
        }
    }

    private class OnCompletedListener implements EventListener {
        @Override
        public void processEvent(Event event) {
            Log.v(TAG, "OnCompletedListener: " + event.properties);
        }
    }
}
