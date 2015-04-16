#ANUTO Another Ugly Tower Defense
A project for the embedded android module at bern university of applied sciences.

##Description
ANUTO is yet another tower defense game for android. However it features astonishing hand-drawn graphics and a smooth gameplay.
Originally it was built to run on a beaglebone black based embedded system.

![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/screen1.png "Overview")

##The Game
The game principle is simple. Just like in each other TD your job is to prevent the enemies from getting to the map exit.
To do so we provided you a small set of neat towers with different properties. However there are also different types of enemies.
During the gameplay the enemies will emerge in waves. Each wave consists of different groups of enemies. So try to prepare your defense lines accordingly. 
Of course there is a moneysystem which prevents you from filling up the map with towers. You'll also get money from each fallen enemy.

###Enemies
####The basic enemy
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/basic_enemy.png "basic enemy")

| Property | Quantity |
|:--------:|:--------:|
| Health   | 1000     |
| Speed    | 2        |
| Reward   | 10       |

The basic enemy is not that strong but commonly emerges in great quantity.

####The wobbly enemy
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/blob_enemy.png "blob enemy")

| Property | Quantity |
|:--------:|:--------:|
| Health   | 2000     |
| Speed    | 1        |
| Reward   | 20       |

The wobbly enemy is very slow but has a huge amout of healthpoints compared to the basic enemy. Also it will attack in small groups.

####The sprinter
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/sprinter_enemy.png "sprinter enemy")

| Property | Quantity |
|:--------:|:--------:|
| Health   | 500      |
| Speed    | 3        |
| Reward   | 5        |

The sprinter is the fastest enemy. It is said that this enemy even outruns rocketswith ease. Luckily his healthpoints are very low.

###Towers
####The cannon
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/basic_tower.png "cannon")

| Property | Quantity |
|:--------:|:--------:|
| Value    | 100      |
| Reload   | fast     |

The basic cannon is fast at aim fires quickly, but doesn't deal a lot of damage at all. However the cannonballs will follow their targets, which is pretty cool for a cannon isn't it?

####The rocket launcher
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/rocket_tower.png "rocket launcher")

| Property | Quantity |
|:--------:|:--------:|
| Value    | 300      |
| Reload   | slow     |

The rocket launcher will fire rockets which explode on impact. A rocket will deal a fairly large amount of area damage to a group of nearby enemies.

####The laser
![alt text](https://raw.githubusercontent.com/oojeiph/android-anuto/master/images/laser_tower.png "laser tower")

| Property | Quantity |
|:--------:|:--------:|
| Value    | 700      |
| Reload   | middle   |

The laser basically mows through everything in front and behind his target. It is recommended to use this ability on straight tracks.

###Maps
Currently there is only one map included in the game. But maps are based on xml and should be very easy to build.

##How to Install
The whole repository consists of an android studio project and the game is still under heavy development. So the easiest way to install the game at this point would probably be to import the project into android studio and load it to your phone/beaglebone.

However you can also clone the repository and issue **adb install ./app/build/outputs/apk/app-debug.apk** from within the repository folder to install the game. 

###Installation for the beaglebone
To get the latest version of the game including the colorsensor support, make sure to clone the ColorsensorDev-branch.

##Imoprtant
###Colorsensor support
There is support for a i2c based color sensor which will change some game colors for you. But its only available on the ColorsensorDev branch and you'll need a bfh beaglebone black kit in order to use it.

##Contributors
- oojeiph   https://github.com/oojeiph
- id101010  https://github.com/id101010
