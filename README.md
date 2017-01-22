tilepacker
==========

A Gradle plugin and command line utility for packing individual tile images into tilesets.

Gradle
---------

```gradle
buildscript {
    repositories {
		mavenLocal()
        mavenCentral()
    }
    dependencies {
		classpath 'org.mini2Dx:tilepacker-gradle-plugin:4.1.0'
    }
}
```

Then define a task for packing up your tiles into tilesets. The tilesDirectory is where your tiles and configuration file are.

```gradle
task tilePack(type: org.tilepacker.gradle.TilePackerTask) {
	tilesDirectory file("path/to/tiles/folder")
	//Change to true if you do not want to use previously stored order of tiles
	rewrite false
}
```

Then run the task with the ```--no-daemon``` option.

```bash
gradle tilePack --no-daemon
```

A sample configuration file can be found [here](https://raw.githubusercontent.com/tomcashman/tilepacker/master/config.sample.xml).

Command Line
---------
On the command line, the application takes a single argument - the path to the directory of the tiles and configuration file. The configuraiton file tells tilepacker which settings to apply and which files to pack into tilesets.

A sample configuration file can be found [here](https://raw.githubusercontent.com/tomcashman/tilepacker/master/config.sample.xml).

Windows

```bash
tilepacker-core.bat ./path/to/tiles/folder
```

Mac OS X / Linux

```bash
./tilepacker-core ./path/to/tiles/folder
```


Required Configuration Options
---------
 * ```tileWidth``` - The width of each tile in pixels
 * ```tileHeight``` - The height of each tile in pixels
 * ```tilesetWidth``` - The width of each tileset in pixels
 * ```tilesetHeight``` - The height of each tileset in pixels
 * ```outputFormat```- The output image format (PNG or JPG)
 * ```tiles``` - The list of relative paths to tile images to be packed
 
Optional Configuration Options
---------
 * ```tilePadding``` - The padding in pixels to apply around each tile (0 by default)
 * ```preventTearing``` - If you want to prevent tearing when scaling rendering, TilePacker can pad out each tile by its border pixels (false by default)
  * ```premultiplyAlpha``` - If you want the output image to have premultiplied alpha values (false by default)
   * ```backgroundColor``` - The background color to use in output images

Notes
---------
Older integrated graphics cards can only load textures of 512x512 pixels so it is recommended that tilesets are generated at this maximum size.