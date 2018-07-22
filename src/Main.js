
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
  plateau.width = PLATEAU_WIDTH;
  plateau.height = PLATEAU_HEIGHT;
  
//TODO move to map class
var x,y = 0;
for (var row = 0; row < map.length; row++) {
  for (var col = 0; col < map[row].length; col++) {

    y = plateau.height * row
    x = plateau.width  * col
    let sprite =  new PIXI.Sprite(map[row][col] === '0' ? plateauTexture:backgroundTexture);
    sprite.x = x
    sprite.y = y
    sprite.scale.x = .18 //TODO remove when there are proper textures
    sprite.scale.y = .18
    gameScene.addChild(sprite);
  }
}
  
  //TODO Make the enemies
  //TODO Create the health bar
  //TODO Add some text for the game over message
  //TODO Create a `gameOverScene` group
  //TODO Assign the player's keyboard controllers
  wave = new Wave(waveScene, 1);

  //set the game state to `play`
  state = waveLoop;
 
  //Start the game loop
  app.ticker.add(delta => gameLoop(delta));
}


function gameLoop(delta){

  //Update the current game state:
  state(delta);
}

function waveLoop(delta) {
  //Move the wave
  wave.move(map)
}


function end() {
  //All the code that should run at the end of the game
  
}

//Function called from javascript form
function nextWave() {
  console.log("Next Wave");
  //setup the wave
  
  
  //set the game state to `play`
  //state = wave;
}