#ANUTO Another Ugly Tower Defense
A project for the embedded android module at Bern university of applied sciences.

##Description
ANUTO is yet another tower defense game for android. However it features astonishing hand-drawn graphics and a smooth gameplay. Originally it was built to run on a beaglebone black based embedded system (BFH cape).

![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/screen1.png "Overview")

##The Game
The game principle is simple. Just like in each other TD your job is to prevent the enemies from getting to the map exit. To do so we provided you a small set of neat towers with different properties. However there are also different types of enemies. During the gameplay the enemies will emerge in waves. Each wave consists of different groups of enemies. So try to prepare your defense lines accordingly.

You have a certain amount lives and you lose one life for each enemy who sneaks through your defense. If the counter goes below zero the game is over. Of course there is a credit system which prevents you from filling up the map with towers. You'll also get credits from finished waves and fallen enemies.

##How to play
Your inventory resides at the bottom of the screen. Each tower is marked with a price tag. If the tag is red you don't have enough credits to buy this tower. To place the tower simply drag it onto the map and drop it on a free location. A green circle indicates the range of the tower. If you change your mind while placing the tower simply drop the tower in the inventory.

There is a status bar at the top of the screen indicating the amount of credits available, the number of lives left and the number of the current wave.

Press the button to the right of your inventory to call in the next wave. Currently you have to wait until the current wave is finished in order to call the next one. Once the game is finished you can restart it using the button which appears in the center of the screen. An option to restart the game at any time will be implemented soon.

Good luck and have fun!

###Enemies
####The Basic Enemy
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/basic_enemy.png "basic enemy")

| Property | Quantity |
|:--------:|:--------:|
| Health   | 1000     |
| Speed    | 2        |
| Reward   | 10       |

The basic enemy is not that strong but commonly emerges in great quantity.

####The Wobbly Enemy
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/blob_enemy.png "blob enemy")

| Property | Quantity |
|:--------:|:--------:|
| Health   | 2000     |
| Speed    | 1        |
| Reward   | 20       |

The wobbly enemy is very slow but has a huge amout of healthpoints compared to the basic enemy. Also it will attack in small groups.

####The Sprinter
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/sprinter_enemy.png "sprinter enemy")

| Property | Quantity |
|:--------:|:--------:|
| Health   | 500      |
| Speed    | 3        |
| Reward   | 5        |

The sprinter is the fastest enemy. It is said that this enemy even outruns rockets with ease. Luckily his healthpoints are very low.

###Towers
####The Cannon
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/basic_tower.png "cannon")

| Property | Quantity |
|:--------:|:--------:|
| Value    | 100      |
| Reload   | fast     |
| Range    | large    |

The basic cannon is fast at aim fires quickly, but doesn't deal a lot of damage at all. However the cannonballs will follow their targets, which is pretty cool for a cannon isn't it?

####The Rocket Launcher
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/rocket_tower.png "rocket launcher")

| Property | Quantity |
|:--------:|:--------:|
| Value    | 300      |
| Reload   | slow     |
| Range    | normal   |

The rocket launcher will fire rockets which explode on impact. A rocket will deal a fairly large amount of area damage to a group of nearby enemies.

####The Laser Tower
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/laser_tower.png "laser tower")

| Property | Quantity |
|:--------:|:--------:|
| Value    | 700      |
| Reload   | middle   |
| Range    | insane   |

The laser basically mows through everything in front and behind his target. It is recommended to use this ability on straight tracks.

###Maps
Currently there is only one map included in the game. Since maps are based on XML files it should be straight forward to create new ones, so feel free to do so.

###Color Sensor Support
There is support for a i2c based color sensor which will make your "next wave" button a little bit fancier. It's only available on the ColorsensorDev branch and you'll need a beaglebone black kit with BFH cape in order to use it.

Note that this branch will not receive any further updates since we would like to make the game available for anyone who has an Android mobile phone.

##How to Install
The whole repository consists of an Android Studio project and the game is still under heavy development. So the easiest way to install the game at this point would probably be to import the project into Android Studio and load it to your phone/beaglebone. If you got a beaglebone with BFH cape make sure you choose the ColorsensorDev branch in order to use the color sensor.

If you received a signed APK file simply issue **adb install [path to apk file]** to install the game.

##Contributors
- oojeiph   https://github.com/oojeiph
- id101010  https://github.com/id101010
