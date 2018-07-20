class Wave {

  //An array to store all the blob monsters
  //blobs = [];
  //cat;

  constructor(waveScene, mWaveNumber) {
    this.mWaveNumber = mWaveNumber;
    //this.blobs = [];
    this.cat = null;
    
    this.cat = new Sprite(resources["images/cat.png"].texture);
    this.cat.x = 25*2
    this.cat.y = 0
    this.cat.scale.x = .4
    this.cat.scale.y = .4
    waveScene.addChild(this.cat);
  }

  getWaveNumber() {
    console.log(this.mWaveNumber);
  }
  
  move(){
    console.log("Advance ");
  
    if (this.collision(this.cat.x, this.cat.y)){
      console.log("Pared");
    }
  
    //move down until collision
    this.cat.vy = 1
    //Use the current enemy set for wave
    this.cat.y += this.cat.vy;
    
    console.log("cat xy", this.cat.x, this.cat.y);
  }
  
  collision(x, y){
    
    
    return false;
  }

}