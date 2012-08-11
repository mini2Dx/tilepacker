tilepacker
==========

A command line utility for packing individual tile images into tilesets.

Usage
---------
The following command will take individual tile images from files in the source folder /input/ and generate tilesets into the target folder /output/. Each tile is 32 pixels by 32 pixels (images larger than this will be split into multiple tiles). The maximum size for the tilesets will be 512 pixels by 512 pixels. The source and target images will be PNG format (other formats can be JPG or TGA).

java -jar tilepacker-core.jar -w 512 -h 512 -f PNG -tw 32 -th 32 -s /input/ -t /output/

Notes
---------
Older integrated graphics cards can only load textures of 512x512 pixels so it is recommended that tilesets are generated at this maximum size.