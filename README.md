JAVI - Java Audio/Video Interface
=================================
Media integration for Java.

Website: [http://dev.ivybits.tk/projects/javi](http://dev.ivybits.tk/projects/javi)
<br/>
Issues: [http://dev.ivybits.tk/projects/javi/issues](http://dev.ivybits.tk/projects/javi/issues)
<br/>
Automatically generated documentation for JAVI in Javadoc format can be found at [http://docs.ivybits.tk/javadocs/javi/](http://docs.ivybits.tk/javadocs/javi/)


Usage
-----
###With Swing
JAVI integrates seamlessly with Swing through the `SwingMediaPanel`.
```java
File videoFile = ...
// Open a media instance
Media media = MediaFactory.open(videoFile);

// Load media into panel
SwingMediaPanel videoPanel = new SwingMediaPanel(media);

// Select a video & audio stream to play
videoPanel.setVideoStream(media.videoStreams().get(0));
videoPanel.setAudioStream(media.audioStreams().get(0));

// Create a simple panel to house player
JFrame display = new JFrame("JAVI Test");
display.setLayout(new BorderLayout());
display.add(BorderLayout.CENTER, videoPanel);
display.setSize(640, 480);
display.setVisible(true);

// Start video playback
videoPanel.start();
```
It's that simple!
<br/>
You may also be interested in [JPlay](https://github.com/IvyBits/JAVI/blob/master/src/main/java/tk/ivybits/javi/JPlay.java), a one-class media player demonstrating the usage of JAVI.

###With your graphics library of choice
`SwingMediaPanel` handles a bit of the backend, but if you wish to use a graphics library other than Swing, you'll need to utilise `MediaStream`.

```java
File videoFile = ...
// Open a media instance
Media media = MediaFactory.open(videoFile);

MediaStream stream = media
        .stream() // Begin setup
        // Specify audio handler; may be left out.
        .audio(new MediaHandler<byte[]>() {
            @Override
            public void handle(byte[] buffer) {
                // buffer is in signed 16-bit little-endian PCM format.
                // frequency & channel count can be obtained from a AudioStream instance.
            }
        })
        // Specify video handler; may be left out.
        .video(new MediaHandler<BufferedImage>() {
            @Override
            public void handle(BufferedImage buffer, long duration) {
                // buffer contains frame data
                // duration is the duration this frame should elapse before this frame;
                // taking into consideration audio-video sync.
            }

            @Override
            public void end() {
                // Notification on stream end
            }
        })
        // Create stream.
        // At least one handler must be specified, or an exception is thrown.
        .create();

// Now we can start the stream synchronously
stream.run();
// Or asynchronously
new Thread(stream).start();
```

As a Maven Dependency
---------------------
The easiest way to get started with JAVI is with it as a dependency in your [Maven](http://maven.apache.org/download.html) project.
You must first add our Maven repository, then add JAVI as a dependency.

```xml
<dependencies>
    <dependency>
        <groupId>tk.ivybits.javi</groupId>
        <artifactId>javi</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>IvyBits</id>
        <url>http://maven.ivybits.tk</url>
    </repository>
</repositories>
```

Coding and Pull Requests Guidelines
-----------------------------------
* We follow the [Oracle coding conventions](http://www.oracle.com/technetwork/java/codeconv-138413.html).
  * 4 spaces; not tabs.
  * No trailing whitespaces.
  * No 80 column limit.
* Pull requests should be tested before submission.
* Any additions in API should be properly documented.
* Avoid unnecessary class coupling where possible.
* Don't include IDE-specific files, class files, jar files, and whatnot; that is what the .gitignore is for!
