ws = new WebSocket("ws://localhost:8887");

points = []

ws.onmessage = function (event) {
  data = JSON.parse(event.data);
  if (data.points) {
      points = data.points;
  }
};

var lastTime;
function setup() {
    var cnv = createCanvas(windowWidth, windowHeight);
    cnv.parent('game');
    frameRate(30);
    lastTime = millis();
}


function update() {
   var time = millis();
   points.forEach(function(p) {
     dt = (time - lastTime);
     p.x += p.vx * dt;
     p.y += p.vy * dt;
   });
   lastTime = time;
}

function draw() {
    //ws.send("this is a test");
    update();
    background(255);
    fill(0);
    points.forEach(function (p) {
        ellipse(p.x, p.y, 2, 2);
    });
}