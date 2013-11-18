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
package com.brightcove.player.application;

import android.os.Bundle;
import android.util.Log;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.media.PlaylistListener;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.SourceAwareMetadataObject;
import com.brightcove.player.plugin.sample.SamplePlugin;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class demonstrates how an application incorporates the sample
 * plugin.
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Instantiate the sample plugin.
        new SamplePlugin(brightcoveVideoView.getEventEmitter(), this, brightcoveVideoView);

        // Request a sample playlist from the Brightcove Media API.
        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }

            @Override
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
            }
        });

        // Set up a listener to establish the cue points once the
        // MediaPlayer has been prepared.
        brightcoveVideoView.getEventEmitter().on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Source source = (Source) event.properties.get(Event.SOURCE);
                setupCuePoints(source);
            }
        });
    }

    // Sets up a preroll, midroll at 10 seconds, and a postroll.
    private void setupCuePoints(Source source) {
        String cuePointType = "ad";
        Map<String, Object> properties = new HashMap<String, Object>();
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        // create the preroll
        CuePoint cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType,
                                         Collections.<String, Object>emptyMap());
        properties.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, properties);

        // create the midroll, these don't work with HLS videos due to an Android bug.
        Log.v(TAG, "delivery type: " + source.getProperties().get(SourceAwareMetadataObject.Fields.DELIVERY_TYPE));
        if (!DeliveryType.HLS.equals(source.getProperties().get(SourceAwareMetadataObject.Fields.DELIVERY_TYPE))) {
            cuePoint = new CuePoint(10000, cuePointType, Collections.<String, Object>emptyMap());
            properties.put(Event.CUE_POINT, cuePoint);
            eventEmitter.emit(EventType.SET_CUE_POINT, properties);
        }

        // create the postroll
        cuePoint = new CuePoint(CuePoint.PositionType.AFTER, cuePointType,
                                Collections.<String, Object>emptyMap());
        properties.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, properties);
    }
}
