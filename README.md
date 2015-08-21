#ANUTO Another Ugly Tower Defense
A project for the embedded android module at Bern university of applied sciences.

##Description
ANUTO is yet another tower defense game for android. However it features astonishing hand-drawn graphics and a smooth gameplay.

![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/screen1.png "Overview")

##The Game
The game principle is simple. Just like in each other TD your job is to prevent the enemies from getting to the map exit. To do so we provided you a small set of neat towers with different properties. However there are also different types of enemies. During the gameplay the enemies will emerge in waves. Each wave consists of different groups of enemies. So try to prepare your defense lines accordingly.

You have a certain amount lives and you lose one life for each enemy who sneaks through your defense. If the counter goes below zero the game is over. Of course there is a credit system which prevents you from filling up the map with towers. You'll also get credits from finished waves and fallen enemies.

##How to play
Your inventory resides at the top of the screen. Each tower is marked with a price tag. If the tag is red you don't have enough credits to buy this tower. To place the tower simply drag it onto the map and drop it on a free location. A green circle indicates the range of the tower. If you change your mind while placing the tower simply drop the tower in the inventory. Double tap any placed tower to view it's properties, set aim strategies or buy upgrades.

The status bar indicates the amount of credits available, the number of lives left and the number of the current wave.

Press the button to the right of your inventory to call in the next wave.

Good luck and have fun!

###Maps
Currently there is only one map included in the game. Since maps are based on XML files it should be straight forward to create new ones, so feel free to do so.

##How to Install
The whole repository consists of an Android Studio project and the game is still under heavy development. So the easiest way to install the game at this point would probably be to import the project into Android Studio and load it to your phone/beaglebone. If you got a beaglebone with BFH cape make sure you choose the ColorsensorDev branch in order to use the color sensor.

If you received an APK file simply issue **adb install [path to apk file]** to install the game.

