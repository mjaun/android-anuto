
class Towers {

  //TODO Use an array of towers
  constructor(waveScene) {
    this.tower = new Tower(1, 1, 1500);
    waveScene.addChild(this.tower);
  }

  attack(wave) {
    //TODO find enemy accoding to strategy: first, weakest, etc...
    
    //Make the tower aim and shoot the enemy
    this.tower.aim(wave.enemy)
  }

}
