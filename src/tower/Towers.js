
class Towers {

  //TODO Use an array of towers
  constructor(waveScene) {
    this.tower = null;

    this.tower = new Tower(1, 1);
    //this.tower.x = 25
    //this.tower.y = 25

    waveScene.addChild(this.tower);
  }

  attack(wave) {
    //TODO find enemy accoding to strategy: first, weakest, etc...
    //console.log("Attack", wave.enemy)
    
    //Make the tower aim and shoot the enemy
    this.tower.aim(wave.enemy)
  }

}
