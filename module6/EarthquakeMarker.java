package module6;

import java.util.Comparator;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 *
 */
// TODO: Implement the comparable interface
public abstract class EarthquakeMarker extends CommonMarker implements Comparable<EarthquakeMarker>
{
	
	// Did the earthquake occur on land?  This will be set by the subclasses.
	protected boolean isOnLand;

	// The radius of the Earthquake marker
	// You will want to set this in the constructor, either
	// using the thresholds below, or a continuous function
	// based on magnitude. 
	protected float radius;
	
	
	// constants for distance
	protected static final float kmPerMile = 1.6f;
	
	/** Greater than or equal to this threshold is a moderate earthquake */
	public static final float THRESHOLD_MODERATE = 5;
	/** Greater than or equal to this threshold is a light earthquake */
	public static final float THRESHOLD_LIGHT = 4;

	/** Greater than or equal to this threshold is an intermediate depth */
	public static final float THRESHOLD_INTERMEDIATE = 70;
	/** Greater than or equal to this threshold is a deep depth */
	public static final float THRESHOLD_DEEP = 300;

	// ADD constants for colors
	
	// @S 
	public static boolean isHovered = false;
	//@S
	private int rank;
	
	public void setRank(int num){
		this.rank = num;
	}
	
	public int getRank(){
		return rank;
	}
	
	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
		
	
	// constructor
	public EarthquakeMarker (PointFeature feature) 
	{
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2*magnitude );
		setProperties(properties);
		this.radius = 1.75f*getMagnitude();
	}
	
	// TODO: Add the method:
	public int compareTo(EarthquakeMarker marker){
		return Float.compare(-(this.getMagnitude()), -(marker.getMagnitude()));
	}
	
//	public static Comparator<EarthquakeMarker> EarthquakeCompare = new Comparator<EarthquakeMarker>(){
//		public int compare(EarthquakeMarker e1, EarthquakeMarker e2){
//			return Float.compare(-(e1.getMagnitude()), -(e2.getMagnitude()));
//		}
//	};
	
	// calls abstract method drawEarthquake and then checks age and draws X if needed
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		// save previous styling
		pg.pushStyle();
			
		// determine color of marker from depth
		colorDetermine(pg);
		
		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);
		
		// IMPLEMENT: add X over marker if within past day		
		String age = getStringProperty("age");
		if ("Past Hour".equals(age) || "Past Day".equals(age)) {
			
			pg.strokeWeight(2);
			int buffer = 2;
			pg.line(x-(radius+buffer), 
					y-(radius+buffer), 
					x+radius+buffer, 
					y+radius+buffer);
			pg.line(x-(radius+buffer), 
					y+(radius+buffer), 
					x+radius+buffer, 
					y-(radius+buffer));
			
		}
		
		// reset to previous styling
		pg.popStyle();
		
	}

	/** Show the title of the earthquake if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
		String title = getTitle();
		//@S
		title = title + ", Rank = " + Integer.toString(this.getRank());
		pg.pushStyle();
		
		pg.rectMode(PConstants.CORNER);
		
		pg.stroke(110);
		pg.fill(255,255,255);
		//@S
		if(x+pg.textWidth(title)<pg.width){
			pg.rect(x, y + 15, pg.textWidth(title) +6, 18, 5);
			
			pg.textAlign(PConstants.LEFT, PConstants.TOP);
			pg.fill(0);
			pg.text(title, x + 3 , y +18);
		}
		else{
			int m = pg.width;
			int g = (int) pg.textWidth(title);
			int n = (int) (pg.textWidth(title)/(pg.width-x));
			n+=2;
			pg.rect(x, y + 15, pg.textWidth(title)/(n-1) +6, 18*(n+1), 5);
			
			pg.textAlign(PConstants.LEFT, PConstants.TOP);
			pg.fill(0);
			pg.text(title, x + 3 , y +18, pg.textWidth(title)/(n-1), 18*(n+1));
		}
		
		
		pg.popStyle();
		
	}

	
	/**
	 * Return the "threat circle" radius, or distance up to 
	 * which this earthquake can affect things, for this earthquake.   
	 * DISCLAIMER: this formula is for illustration purposes
	 *  only and is not intended to be used for safety-critical 
	 *  or predictive applications.
	 */
	public double threatCircle() {	
		double miles = 20.0f * Math.pow(1.8, 2*getMagnitude()-5);
		double km = (miles * kmPerMile);
		return km;
	}
	
	// determine color of marker from depth
	// We use: Deep = red, intermediate = blue, shallow = yellow
	private void colorDetermine(PGraphics pg) {
		if(!isHovered){
			float depth = getDepth();
			
			if (depth < THRESHOLD_INTERMEDIATE) {
				pg.fill(255, 255, 0);
			}
			else if (depth < THRESHOLD_DEEP) {
				pg.fill(0, 0, 255);
			}
			else {
				pg.fill(255, 0, 0);
			}
		}
		//@S
		else{
			if(this.getIsBefore()){
				pg.fill(157,4,204);  
			}
			else
				pg.fill(227,176,242);
		}
	}
	
	
	/** toString
	 * Returns an earthquake marker's string representation
	 * @return the string representation of an earthquake marker.
	 */
	public String toString()
	{
		return getTitle();
	}
	/*
	 * getters for earthquake properties
	 */
	
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}
	
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}
	
	public String getTitle() {
		return (String) getProperty("title");	
		
	}
	
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}
	
	public boolean isOnLand()
	{
		return isOnLand;
	}
	

	
	
}
