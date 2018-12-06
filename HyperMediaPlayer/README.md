# HyperMedia Player

This is an implementation of [hyperlink media](https://en.wikipedia.org/wiki/Hypervideo) playing tool on Java 1.7, OpenCV (bytedeco). The User Interface is implementd with Java Swing UI. It is based on a series of images and corresponding .WAV file. Video playing parts draw each image at 30 fps. The WAV file is basically recoreded corresponding 30 fps images. Sample images and .WAV file attached in media directory.

![Sample screen shot](screenshot.png)

The repository includes:
* Source code
* Sample Media contents
* Sample screen shots

The media contents consist of series of image files. Each image file is .RGB where the resolution is 352x288 containing 352x288 red bytes, followed by 352x288 green bytes, floowed by 352x288 blue bytes.

Autio Format is .WAV which is 16 bits per sample, smpling rate of 44,100 samples per second.

The tool plays link metafile which is json file containing origin of hyperlink and destination. Once link area is selected on a frame, it jumps to linked video and play.

![Demo](demo.mp4)


### Compile & Run

```bash
$ cd HyperMediaPlayer
$ mvn package
$ java -jar target/HyperMediaPlayer.jar
```

### Clean

```bash
$ cd HyperMediaPlayer
$ mvn clean
```

### Status

Version 1.0

