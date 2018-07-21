
//Aliases
let Application = PIXI.Application,
    Container = PIXI.Container,
    loader = PIXI.loader,
    resources = PIXI.loader.resources,
    TextureCache = PIXI.utils.TextureCache,
    Sprite = PIXI.Sprite;

//Create a Pixi Application
let app = new Application({
    width: 256,
    height: 256,
    antialiasing: true,
    transparent: false,
    resolution: 1
    
  }
);

app.renderer.view.style.position = "absolute";
app.renderer.view.style.display = "block";
app.renderer.autoResize = true;
app.renderer = PIXI.autoDetectRenderer(
		window.innerWidth,
		window.innerHeight,
		{view:document.getElementById("game-canvas")}
	);


loader
  .add(["images/cat.png", "images/plateau.png", "images/background1.png", "images/background2.png"])
  .on("progress", loadProgressHandler)
  .load(setup);

//Define any variables that are used in more than one function
//let cat, state;

function loadProgressHandler(loader, resource) {

  //Display the file `url` currently being loaded
  console.log("loading: ", resource.name, resource.url);

  //Display the percentage of files currently loaded
  console.log("progress: " + loader.progress + "%");

}

//Define any variables that are used in more than one function
let wave, state;


function setup() {

  console.log("All files loaded");

  //Create the `gameScene` group
  gameScene = new Container();
  app.stage.addChild(gameScene);
  
  waveScene = new Container();
  app.stage.addChild(waveScene);
  
  gameOverScene = new Container();
  app.stage.addChild(gameOverScene);
  gameOverScene.visible = false;

  let plateauTexture = PIXI.utils.TextureCache["images/plateau.png"];
  let backgroundTexture = PIXI.utils.TextureCache["images/background1.png"];

  plateau = new Sprite(resources["images/plateau.png"].texture);
  plateau.x = 0;
  plateau.y = 0;
  plateau.width = 25;
  plateau.height = 25;
  
//TODO move to map class
var x,y = 0;
for (var row = 0; row < map.length; row++) {
  for (var col = 0; col < map[row].length; col++) {

    y = plateau.height * row
    x = plateau.width * col
    let sprite =  new PIXI.Sprite(map[row][col] == "0" ? backgroundTexture:plateauTexture);
    sprite.x = x
    sprite.y = y
    sprite.scale.x = .18
    sprite.scale.y = .18
    gameScene.addChild(sprite);
  }
}
  
  //Make the enemies
  //Create the health bar
  //Add some text for the game over message
  //Create a `gameOverScene` group
  //Assign the player's keyboard controllers
  wave = new Wave(waveScene, 1);
//  cat = new Sprite(resources["images/cat.png"].texture);
//  cat.x = 25*2
//  cat.y = 0
//  cat.scale.x = .4
//  cat.scale.y = .4
//  waveScene.addChild(cat);
  

  //set the game state to `play`
  state = waveLoop;
 
  //Start the game loop
  app.ticker.add(delta => gameLoop(delta));
}


function nextWave() {
  console.log("Next Wave");
  //setup the wave
  
  
  //set the game state to `play`
  //state = wave;
}

function gameLoop(delta){

  //Update the current game state:
  state(delta);
}


function play(delta) {

  //Use the cat's velocity to make it move
  //cat.vx = 1
  //cat.vy = 1
  //cat.x += cat.vx;
  //cat.y += cat.vy
}

function waveLoop(delta) {

  wave.move()
  // cat.vx = 1
  //cat.vy = 1
  //Use the current enemy set for wave
  //cat.x += cat.vx;
  
  //if
  
  //cat.y += cat.vy
  //console.log("cat xy", cat.x, cat.y);
}


function end() {
  //All the code that should run at the end of the game
  
}
