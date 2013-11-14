Brightcove Plugin Guide for Android
===================================

This is a guide for writing plugins for the Brightcove Player for
Android.  At a high level, plugins are implemented by listening to
events emitted by the Brightcove Player and other plugins and by
emitting events for the Brightcove Player and other plugins to use.

In order to register a plugin, plugins should emit a REGISTER_PLUGIN
event with a PLUGIN_NAME property.  For example:

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(Event.PLUGIN_NAME, "my custom plugin");
    eventEmitter.emit(EventType.REGISTER_PLUGIN, properties);

Video playback typically goes through a standard lifecycle event flow:

![alt text](https://docs.google.com/drawings/d/1OCLpdRzqua6teFVz1LHCaFXTs4rcNPf5dwU4Djo6MVc/pub?w=1134&amp;h=1228 "Event Flow")

Plugins can listen for any of the initiating events
(WILL_CHANGE_VIDEO, SET_VIDEO, SET_SOURCE, PLAY, PAUSE, SEEK_TO, and
STOP) and override the default behavior by calling preventDefault()
and/or stop propagation by calling stopPropagation().  If only
preventDefault() is called, the rest of the non-default listeners will
be notified.  If only stopPropagation() is called, the rest of the
non-default listeners will be skipped, but the default listeners will
still be notified.  Plugins can use these methods to pause the normal
event flow and insert additional behavior, like requesting advertising
information when a source is set.  Plugins can resume the event flow
by emitting the original event again.

For plugins, which desire to periodically interrupt the video content,
there are two important events, WILL_INTERRUPT_CONTENT and
WILL_RESUME_CONTENT.  Plugins should emit WILL_INTERRUPT_CONTENT to
request that playback be suspended if it's currently playing and that
the video view be made invisibile.  Plugins should emit
WILL_RESUME_CONTENT to request that the video view be make visible.
The WILL_RESUME_CONTENT event should include an ORIGINAL_EVENT
property which will be emitted after the video view is visible again.
The ORIGINAL_EVENT can be a PLAY event to resume playback, a CUE_POINT
event to continue cue point processing, or a COMPLETED event to
complete playback.  A SKIP_CUE_POINTS property should be added to the
ORIGINAL_EVENT to prevent recursive cue point processing.

Many plugins will want to listen to CUE_POINT events.  There are three
types of cue points, BEFORE, POINT_IN_TIME, and AFTER.  Before cue
points are emitted before playback begins.  Point in time cue points
are emitted when playback reaches the cue point's position.  After cue
points are emitted after playback completes.  Plugins should use
WILL_INTERRUPT_CONTENT and WILL_RESUME_CONTENT events to interrupt and
resume content playback when handling a cue point event.  In the case
of before and after cue points, the event will include an
ORIGINAL_EVENT property, with either a PLAY event or a COMPLETED
event.  Cue point events also include a CUE_POINTS property with the
batch of cue points.  START_TIME and END_TIME properties define the
cue point time range.

The sample directory includes an Android Studio based project with two
modules, SamplePlugin and SamplePluginApplication.  The SamplePlugin
module is an example plugin, which can be used as the basis for
writing new plugins.  The SamplePluginApplication module shows how
plugins are incorprated into an Brightcove video application and can
be used to test the SamplePlugin or new plugins.

For Brightcove Player SDK for Android documentation and downloads,
see:

[http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html)