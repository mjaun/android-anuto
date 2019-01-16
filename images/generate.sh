#!/bin/bash

inkscape -f icon.svg -e icon.png -a 0:0:512:512 -w 512 -h 512
inkscape -f icon.svg -e ../app/src/main/res/mipmap-mdpi/icon.png -a 0:0:512:512 -w 48 -h 48
inkscape -f icon.svg -e ../app/src/main/res/mipmap-hdpi/icon.png -a 0:0:512:512 -w 72 -h 72
inkscape -f icon.svg -e ../app/src/main/res/mipmap-xhdpi/icon.png -a 0:0:512:512 -w 96 -h 96
inkscape -f icon.svg -e ../app/src/main/res/mipmap-xxhdpi/icon.png -a 0:0:512:512 -w 144 -h 144

inkscape -f icon_bg.svg -e ../app/src/main/res/mipmap-mdpi/icon_bg.png -a 0:0:108:108 -w 48 -h 48
inkscape -f icon_bg.svg -e ../app/src/main/res/mipmap-hdpi/icon_bg.png -a 0:0:108:108 -w 72 -h 72
inkscape -f icon_bg.svg -e ../app/src/main/res/mipmap-xhdpi/icon_bg.png -a 0:0:108:108 -w 96 -h 96
inkscape -f icon_bg.svg -e ../app/src/main/res/mipmap-xxhdpi/icon_bg.png -a 0:0:108:108 -w 144 -h 144

inkscape -f icon_fg.svg -e ../app/src/main/res/mipmap-mdpi/icon_fg.png -a 0:0:108:108 -w 48 -h 48
inkscape -f icon_fg.svg -e ../app/src/main/res/mipmap-hdpi/icon_fg.png -a 0:0:108:108 -w 72 -h 72
inkscape -f icon_fg.svg -e ../app/src/main/res/mipmap-xhdpi/icon_fg.png -a 0:0:108:108 -w 96 -h 96
inkscape -f icon_fg.svg -e ../app/src/main/res/mipmap-xxhdpi/icon_fg.png -a 0:0:108:108 -w 144 -h 144