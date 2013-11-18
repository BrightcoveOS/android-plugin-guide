Brightcove Plugin Guide for Android
===================================

This guide illustrates writing Brightcove Player for Android plugins.
At a high level, a plugin integrates with the player by listening for
and emitting events.  A plugin can listen to events from the player
and from other plugins.  A plugin can emit events for the player and
for other plugins.

A plugin should register with the SDK when they are instantiated.  To
register a plugin should emit a `REGISTER_PLUGIN` event with a
`PLUGIN_NAME` property.  For example:

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(Event.PLUGIN_NAME, "my custom plugin");
    eventEmitter.emit(EventType.REGISTER_PLUGIN, properties);

Video playback typically goes through a standard lifecycle event flow:

![alt text](https://docs.google.com/drawings/d/1OCLpdRzqua6teFVz1LHCaFXTs4rcNPf5dwU4Djo6MVc/pub?w=1134&amp;h=1228 "Event Flow")

A plugin can listen for events, which initiate some action, in order to
change the default behavior of the player.  These events include:

1. `WILL_CHANGE_VIDEO`
2. `SET_VIDEO`
3. `SET_SOURCE`
4. `PLAY`
5. `PAUSE`
6. `SEEK_TO`
7. `STOP`

The default behavior can be changed by preventing the default
listeners from receiving the event and/or by stopping propagation of
the event to non-default listeners.  Preventing the default listeners
from receiving the event is accomplished by calling the
`preventDefault()` method.  Event propagation can be stopped by
calling the `stopPropagation()` method.  If only `preventDefault()` is
called, the rest of the non-default listeners will be notified.  If
only `stopPropagation()` is called, the default listeners will still
be notified, but the rest of the non-default listeners will be
skipped.  A plugin can also use these methods to pause the normal
event flow and insert additional behavior, like initializing the
plugin.  A plugin can resume the event flow by emitting the original
event again.

A plugin can also listen for events, which signal the completion of an
action.  These events are typically used by analytics plugins.  The
events include:

1. `DID_CHANGE_LIST`
2. `DID_SELECT_SOURCE`
3. `DID_PAUSE`
4. `DID_PLAY`
5. `DID_SEEK_TO`
6. `DID_SET_SOURCE`
7. `DID_STOP`
8. `PROGRESS`
9. `COMPLETED`

A plugin, which desires to interrupt the video content playback,
should use `WILL_INTERRUPT_CONTENT` and `WILL_RESUME_CONTENT`.  These
events are typically used by advertising pluings.  A plugin should
emit `WILL_INTERRUPT_CONTENT` to request that playback be suspended,
if it is currently playing, and to request that the video view be made
invisibile.  A plugin should emit `WILL_RESUME_CONTENT` to request
that the video view be made visible again.  The `WILL_RESUME_CONTENT`
event should include an `ORIGINAL_EVENT` property which will be
emitted after the video view is made visible.  The `ORIGINAL_EVENT`
should be a `PLAY` event to resume playback, a `CUE_POINT` event to
continue cue point processing, or a `COMPLETED` event to complete
playback.  A `SKIP_CUE_POINTS` property should be added to the
`ORIGINAL_EVENT` to prevent recursive cue point processing.

Many plugins will want to listen for `CUE_POINT` events.  There are
three types of cue points, `BEFORE`, `POINT_IN_TIME`, and `AFTER`.
`BEFORE` cue points are emitted just before playback begins.
`POINT_IN_TIME` cue points are emitted when playback reaches the cue
point's position.  `AFTER` cue points are emitted just after playback
completes.  A plugin should use `WILL_INTERRUPT_CONTENT` and
`WILL_RESUME_CONTENT` events to interrupt and resume content playback
when handling a cue point event.  In the case of before and after cue
points, the event will include an `ORIGINAL_EVENT` property, with
either a `PLAY` event or a `COMPLETED` event.  Cue point events also
include a `CUE_POINTS` property with the batch of cue points.
`START_TIME` and `END_TIME` properties define the cue point time
range.

The sample directory includes an Android Studio based project with two
modules, SamplePlugin and SamplePluginApplication.  The SamplePlugin
module is an example plugin, which can be used as the basis for
writing new plugins.  The SamplePluginApplication module shows how
plugins are incorprated into an Brightcove video application and can
be used to test the SamplePlugin or new plugins.

The steps to writing a plugin include:

1. Copy the sample plugin to a new repository.
2. Change the package and class names.
3. Update the TAG initializer.
4. Edit the @Emits and @ListensFor annotations to reflect the events
   which the plugin emits and listens for.
5. Modify the constructor as necessary.
6. Replace or remove the videoView related logic as necessary.
7. Modify initializeListeners() by adding and/or subtracting event
   listeners as necessary.
8. Compile and distribute your plugin as a .jar file for Eclipse
   and/or a .aar file for Android Studio.

For Brightcove Player SDK for Android documentation and downloads,
see:

[http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html)