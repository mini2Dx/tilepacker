tilepacker
==========

A Gradle plugin and command line utility for packing individual tile images into tilesets.

Gradle
---------

Command Line
---------
On the command line, the application takes a single argument - the path to the configuration file. The configuraiton file tells tilepacker which settings to apply and which files to pack into tilesets.

A sample configuration file can be found here.

```bash
java -jar tilepacker-core-standalone.jar ./path/to/config.xml
```

take individual tile images from files in the source folder /input/ and generate tilesets into the target folder /output/. Each tile is 32 pixels by 32 pixels (images larger than this will be split into multiple tiles). The maximum size for the tilesets will be 512 pixels by 512 pixels. The source and target images will be PNG format (other formats can be JPG or TGA).

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