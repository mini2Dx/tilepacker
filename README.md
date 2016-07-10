tilepacker
==========

A Gradle plugin and command line utility for packing individual tile images into tilesets.

Gradle
---------

First, include the mini2Dx repositories in your buildscripts section. This is where tilepacker is deployed to.

```gradle
buildscript {
    repositories {
		mavenLocal()
        mavenCentral()
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
        maven { url "http://maven.mini2dx.org/content/repositories/thirdparty/" }
        maven { url "http://maven.mini2dx.org/content/repositories/releases/" }
    }
    dependencies {
		classpath 'org.tilepacker:tilepacker-gradle-plugin:3.0.0'
    }
}
```

Then define a task for packing up your tiles into tilesets.

```gradle
task tilePack(type: org.tilepacker.gradle.TilePackerTask) {
	configFile file('config.xml')
}
```

Then run the task with the ```--no-daemon``` option.

```bash
gradle tilePack --no-daemon
```

A sample configuration file can be found [here](https://raw.githubusercontent.com/tomcashman/tilepacker/master/config.sample.xml).

Command Line
---------
On the command line, the application takes a single argument - the path to the configuration file. The configuraiton file tells tilepacker which settings to apply and which files to pack into tilesets.

A sample configuration file can be found [here](https://raw.githubusercontent.com/tomcashman/tilepacker/master/config.sample.xml).

Windows

```bash
tilepacker-core.bat ./path/to/config.xml
```

Mac OS X / Linux

```bash
./tilepacker-core ./path/to/config.xml
```


Configuration Options
---------
 * ```tileWidth``` - The width of each tile in pixels
 * ```tileHeight``` - The height of each tile in pixels
 * ```tilesetWidth``` - The width of each tileset in pixels
 * ```tilesetHeight``` - The height of each tileset in pixels
 * ```tilePadding``` - The padding in pixels to apply around each tile
 * ```outputFormat```- The output image format (PNG, JPG or TGA)
 * ```preventTearing``` - If you want to prevent tearing when scaling rendering, TilePacker can pad out each tile by its border pixels
 * ```tiles``` - The list of relative paths to tile images to be packed

Notes
---------
Older integrated graphics cards can only load textures of 512x512 pixels so it is recommended that tilesets are generated at this maximum size.