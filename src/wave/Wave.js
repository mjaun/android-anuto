class Wave {

  // TODO Make enemies into an array
  //enemies = [];
  //enemy;

  constructor(waveScene, mWaveNumber) {
    this.mWaveNumber = mWaveNumber;
    this.enemy = null;

    this.enemy = new Sprite(resources["images/cat.png"].texture);
    this.enemy.x = 25 * 2 + 1
    this.enemy.y = 0 + 1
    this.enemy.scale.x = .35 //TODO remove when there are proper textures
    this.enemy.scale.y = .35

    //move down by default until first collision
    this.enemy.vy = 1
    this.enemy.vx = 0

    waveScene.addChild(this.enemy);
  }

  getWaveNumber() {
    console.log("WaveNumber", this.mWaveNumber);
  }

  move(map) {
    //move the enemy
    this.enemy.y += this.enemy.vy
    this.enemy.x += this.enemy.vx
    
    //TODO check enemy makes it outside the map

    //check if the next move has crossed boundaries 
    if (this.contain(this.enemy, map)) {
      console.log("enemy hits wall");
      //determine where to go next
      var col = Math.floor(this.enemy.x / PLATEAU_WIDTH);
      var row = Math.floor(this.enemy.y / PLATEAU_HEIGHT);
      console.log("where to go next...", map[row][col]);
      switch (map[row][col]) {
        case 'R':
          this.enemy.vy = 0
          this.enemy.vx = 1
          break;
        case 'D':
          this.enemy.vy = 1
          this.enemy.vx = 0
          break;
        case 'L':
          this.enemy.vy = 0
          this.enemy.vx = -1
          break;
        case 'U':
          this.enemy.vy = -1
          this.enemy.vx = 0
          break;
      }
    }
  }

  contain(sprite, map) {
    //if sprite moving down
    if (sprite.vy > 0) {
      var col = Math.floor(sprite.x / PLATEAU_WIDTH);
      var row = Math.floor((sprite.y + sprite.height) / PLATEAU_HEIGHT);
    } else if (sprite.vx > 0) {
      var col = Math.floor((sprite.x + sprite.width) / PLATEAU_WIDTH);
      var row = Math.floor(sprite.y / PLATEAU_HEIGHT);
    } else if (sprite.vx < 0) {
      var col = Math.floor((sprite.x - 1) / PLATEAU_WIDTH);
      var row = Math.floor(sprite.y / PLATEAU_HEIGHT);
    }else if (sprite.vy < 0) {
      var col = Math.floor(sprite.x / PLATEAU_WIDTH);
      var row = Math.floor((sprite.y - 1) / PLATEAU_HEIGHT);
    }

    return map[row][col] === '0'
  }

}