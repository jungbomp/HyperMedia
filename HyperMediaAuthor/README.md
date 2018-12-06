# HyperMedia Author

This is an implementation of hyperlink media authoring tool on Java 1.7, OpenCV (bytedeco). The User Interface is implementd
with Java Swing UI. It is based on a series of images and corresponding .WAV file. Video playing parts draw each image at 30
fps. The WAV file is basically recoreded corresponding 30 fps images. Sample images and .WAV file attached in media directory.

![Sample screen shut](screen_shut/screenshut01.png)

The repository includes:
* Source code
* Sample Media contents
* Sample screen shuts

The media contents consist of series of image files. Each image file is .RGB where the resolution is 352x288 containing 352x288 red bytes, followed by 352x288 green bytes, floowed by 352x288 blue bytes.

Autio Format is .WAV which is 16 bits per sample, smpling rate of 44,100 samples per second.


### Compile & Run

```bash
$ cd HyperMediaAuthor
$ mvn package
$ java -jar target/HyperMediaAuthor.jar
```

### Clean

```bash
$ cd HyperMediaAuthor
$ mvn clean
```

### Status

Version 1.0

