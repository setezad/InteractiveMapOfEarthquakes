package guimodule;

import processing.core.PApplet;

public class MyDisplay extends PApplet {
	public void setup(){
		size(400,400);
		background(200,200,200);
	}
	public void draw(){
		fill(255,217,27);
		ellipse(50,50,70,70);
		fill(0,0,0);
		ellipse(35,35,10,10);
		fill(0,0,0);
		ellipse(65,35,10,10);
		noFill();
		// arc or curve
		curve(30,50, 45,70, 55,70  ,70,50);
		
	}
	

}
