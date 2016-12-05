package module6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;
import de.fhpotsdam.unfolding.utils.ScreenPosition;			//@S

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	//@S NEW IN MODULE 6
	Object[] sortedEarthquakes;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Microsoft.RoadProvider());
			// Google.GoogleMapProvider()  Microsoft.RoadProvider()
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
//		earthquakesURL = "test1.atom";
		earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
//		earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 	
	    //@S
	    sortAndPrint(5);
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	    
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		
	}
	
	
	// TODO: Add the method:
	//   private void sortAndPrint(int numToPrint)
	// and then call that method from setUp
	private void sortAndPrint(int numToPrint){
		// why does it need casting ?????
		// MARKER is an INTERFACE!!
		//EarthquakeMarker[] eq =  (EarthquakeMarker[]) quakeMarkers.toArray();
		// correct code: following two lines
		sortedEarthquakes = quakeMarkers.toArray();
		Arrays.sort(sortedEarthquakes);

		//printing based on the value of numToPrint
		if(numToPrint>sortedEarthquakes.length){
			numToPrint = sortedEarthquakes.length;
		}
		// setting the ranks for extension
		for(int i=0;i<sortedEarthquakes.length;i++){
			for(Marker m:quakeMarkers){
				if(m.equals(sortedEarthquakes[i])){
					((EarthquakeMarker)m).setRank(i+1);
					break;
				}
			}
		}
		for(int i=0;i<numToPrint;i++){
			System.out.println(sortedEarthquakes[i].toString());
		}

	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
			// @S
			EarthquakeMarker.isHovered = false;
			//@S
			changeBackColorFlag(quakeMarkers);
		}
		selectMarkerIfHover(quakeMarkers);
		// add the method here 
		colorEarthquakesWhileHover(sortedEarthquakes);
		selectMarkerIfHover(cityMarkers);
		//loop();
	}
	
	//@S
	private void changeBackColorFlag(List<Marker> markers){
		for(Marker m:markers){
			((CommonMarker)m).setIsBefore(false);
		}
	}
	
	// @S
	private void colorEarthquakesWhileHover(Object[] sortedEq){
		if(lastSelected!= null){
			EarthquakeMarker.isHovered = true;
			boolean flag = false;
			for(int i=0;i<sortedEq.length;i++){
				if( !((CommonMarker)sortedEq[i]).isSelected()){
					// markers preceding the selected one
					if(!flag){
						((CommonMarker)sortedEq[i]).setIsBefore(true);
					}
				}
				// markers following the selected one
				else
					flag = true;
			}
		}
		else
			return;
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		//@S
		for(Marker marker:markers){
			if(marker.isInside(map, mouseX, mouseY)){
				marker.setSelected(true);
				lastSelected = (CommonMarker) marker;
				return;
			}
		}
		//@USD
//		for (Marker m : markers) 
//		{
//			CommonMarker marker = (CommonMarker)m;
//			if (marker.isInside(map,  mouseX, mouseY)) {
//				lastSelected = marker;
//				marker.setSelected(true);
//				return;
//			}
//		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
//			checkEarthquakesForClick();
//			if (lastClicked == null) {
//				checkCitiesForClick();
//			}
//		}
		
		//@S
		boolean isCity = false;
		boolean isEarthq = false;
		for(Marker cmarker:cityMarkers){
			if(cmarker.isInside(map, mouseX, mouseY))
				isCity = true;
		}
		for(Marker emarker:quakeMarkers){
			if(emarker.isInside(map, mouseX, mouseY))
				isEarthq = true;;
		}
		threatCircleWhileCityChosen(isCity,quakeMarkers,cityMarkers);
		// if it's not a city marker
		threatCircleWhileEarthquakeChosen(isEarthq,quakeMarkers,cityMarkers);

//		if(!isCity && !isEarthq){
//			unhideMarkers();
//		}

	} 
	}
	//@S
	private void threatCircleWhileEarthquakeChosen(boolean isEq,List<Marker> qMarkers, List<Marker> cMarkers){
		if(isEq){
			double dist = 0;
			double threatrad = 0;
			for(Marker emarker:qMarkers){
				if(emarker.isInside(map,mouseX,mouseY)){
					lastClicked = (CommonMarker)emarker; //changed now dec 3
				// decide which city markers to keep
					for(Marker cmarker:cMarkers){
						dist = getDistance(cmarker,mouseX,mouseY);
						threatrad = ((EarthquakeMarker)emarker).threatCircle();
						if(dist>threatrad){
							cmarker.setHidden(true);
						}
					}
				}
				else 
					emarker.setHidden(true);
		}
	}
		else
			return;
		}
	//@S
	private void threatCircleWhileCityChosen(boolean isCity,List<Marker> qMarkers, List<Marker> cMarkers){
		if(isCity){
			double dist = 0;
			double threatrad = 0;
			for(Marker emarker:qMarkers){
				dist = getDistance(emarker,mouseX,mouseY);
				threatrad = ((EarthquakeMarker)emarker).threatCircle();
				if(dist>threatrad){
					emarker.setHidden(true);
				}
			}
			// hide all other cities
			for(Marker cmarker:cMarkers){
				if(!(cmarker.isInside(map, mouseX, mouseY))){
					cmarker.setHidden(true);
				}
				else
					lastClicked = (CommonMarker)cmarker; //changed now dec 3
			}
		}
	  else
		  return;
	}
	//@S
	public double getDistance(Marker marker,float x, float y){
		double dist = 0;
		ScreenPosition markerPos = ((SimplePointMarker) marker).getScreenPosition(map);
		dist = Math.sqrt(Math.pow((markerPos.x-x), 2)+Math.pow((markerPos.y-y),2));
		return dist;
	}
		
		
		
		
	
	
	
	
	
	
	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : quakeMarkers) {
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					if (quakeMarker.getDistanceTo(marker.getLocation()) 
							> quakeMarker.threatCircle()) {
						quakeMarker.setHidden(true);
					}
				}
				return;
			}
		}		
	}
	
	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 420);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		//@S
		fill(color(157,4,204));
		ellipse(xbase+35, ybase+220, 12, 12);
		fill(color(227,176,242));
		ellipse(xbase+35, ybase+315, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		//@S
		text("Earthquakes",xbase+50,ybase+220);
		text("with magintudes",xbase+50,ybase+235);
		text("greater than",xbase+50,ybase+250);
		text("that of the",xbase+50,ybase+265);
		text("selected",xbase+50,ybase+280);
		text("Earthquake",xbase+50,ybase+295);
		
		text("Earthquakes",xbase+50,ybase+315);
		text("with magnitudes",xbase+50,ybase+330);
		text("less than or",xbase+50,ybase+345);
		text("equal to that",xbase+50,ybase+360);
		text("of the selected",xbase+50,ybase+375);
		text("Earthquake",xbase+50,ybase+390);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		//@S
		
		
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
