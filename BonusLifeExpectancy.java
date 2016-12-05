/* three different uses of "map"
 * 1 - UnfoldingMap (unfolding map library)
 * 2 - ADT MAP: Keys -> Values
 * 3 - map method (from processing library)
 * 
 * Visualizing data by different shades of colors for countries
 */

import processing.core.PApplet;
//import processing.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
//import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class BonusLifeExpectancy extends PApplet{
	private UnfoldingMap map;
	private Map<String,Float> lifeExpByCountry;
	private List<Marker> countryMarkers;
	private List<Feature> countries;
	
	public void setup(){
		size(800,600,OPENGL);
		map = new UnfoldingMap(this,50,50,700,500,new Microsoft.RoadProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		// 
		String filename = "/Users/Sahba/Downloads/UCSDUnfoldingMaps/data/LifeExpectancyWorldBank.csv";
		lifeExpByCountry = loadLifeExpFromCSV(filename);
		// methods from the unfolding map library
		countries = GeoJSONReader.loadData(this, "/Users/Sahba/Downloads/UCSDUnfoldingMaps/data/countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		map.addMarkers(countryMarkers);
		shadeCountries();
		
	}
	
	private void shadeCountries(){
		for(Marker marker:countryMarkers){
			String countryID = marker.getId();
			if(lifeExpByCountry.containsKey(countryID)){
				float lifeExp = lifeExpByCountry.get(countryID);
				int colorLevel = (int) map(lifeExp,40,90,10,255);
				marker.setColor(color(255-colorLevel,100,colorLevel));
			}
			else {
				marker.setColor(color(150,150,150));
			}
		}
	}
	
	private Map<String,Float> loadLifeExpFromCSV(String filename){
		Map<String,Float> lifeExp = new HashMap<String,Float>();
		String[] rows = loadStrings(filename); 			// function to load the file line by line!!!
		int i =1;
		for(String row:rows){
			String[] columns = row.split(",");
			// @S: exceptions: countries with "The" in their names which end up being two slots after applying delimiter (,) +
			// non-number slots
			if(!columns[5].equals("..")){
				if(!(columns[5] instanceof String)){
					if(Float.parseFloat(columns[5])>0){
						float value = Float.parseFloat(columns[5]);
						lifeExp.put(columns[4], value);
						//System.out.print(i++); System.out.print(" ");
					}
				}
				else{
					if(!columns[6].equals("..")){
						if(Float.parseFloat(columns[6])>0){
							float value = Float.parseFloat(columns[6]);
							lifeExp.put(columns[4], value);
							//System.out.print(i++); System.out.print(" ");
						} //
					} //
				} // else
			} // if line 72
		} // for
		
		return lifeExp;
	}
	

	
	public void draw(){
		map.draw();
	}

}
