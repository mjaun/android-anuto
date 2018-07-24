//let Sprite = PIXI.Sprite;

class Tower extends PIXI.Container {

  constructor(col, row) {
    super()
    //super(resources["images/carrot.png"].texture); // call the super class
    
    
    this.sprite = new PIXI.Sprite(resources["images/bunny.png"].texture);
    this.rectangle = new PIXI.Graphics();
     
    var x = col * PLATEAU_WIDTH
    var y = row * PLATEAU_HEIGHT
    
    // move the sprite to the center of the screen
    this.sprite.position.x = x + PLATEAU_WIDTH/2;
    this.sprite.position.y = y + PLATEAU_HEIGHT/2;
    
    this.sprite.scale.x = .9 //TODO remove when there are proper textures
    this.sprite.scale.y = .7

    
    this.rectangle.beginFill(0x66CCFF);
    this.rectangle.drawRect(x, y, PLATEAU_WIDTH, PLATEAU_HEIGHT);
    this.rectangle.endFill();
    
    this.addChild(this.rectangle)
    this.addChild(this.sprite)

    this.sprite.anchor.x = 0.5;
    this.sprite.anchor.y = 0.5;
 
  }

  aim(enemy) {
    // just for fun, let's rotate mr tower a little
    var dist_Y = enemy.position.y - this.sprite.position.x;
    var dist_X = enemy.position.x - this.sprite.position.y;
    var angle = Math.atan2(dist_Y,dist_X);
    var degrees = angle * 180 / Math.PI;
    console.log("Rotate angle to aim enemy ", degrees);
    this.sprite.rotation = angle
  }

}