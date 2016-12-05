package guimodule;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class EarthQCityMap extends PApplet{
	private UnfoldingMap map;
	
	public void setup(){
		size(950,600,OPENGL);
		map = new UnfoldingMap(this,200,50,700,500,new Microsoft.RoadProvider() );
		// Microsoft.HybridProvider()
		// Google.GoogleMapProvider()
		map.zoomLevel(0);
		MapUtils.createDefaultEventDispatcher(this, map);
		Location valLoc = new Location(-38,-73);
		// Feature (class) - PointFeature (class)
		Feature eq = new PointFeature(valLoc);
		eq.addProperty("title", "validia, chile");
		eq.addProperty("magnitude", 9.5);
		// Marker (interface) - SimplePointMarker (class)
		Marker valM = new SimplePointMarker(valLoc,eq.getProperties());
		map.addMarker(valM);
		// List (interface) - ArrayList (class)
		List<PointFeature> feq = new ArrayList<PointFeature>();
		//List<Feature> feq = new ArrayList<>();
		feq.add(new PointFeature(new Location(-24,56)));
		feq.add(new PointFeature(new Location(45,-34)));
		feq.add(new PointFeature(new Location(23,-76)));
		
		feq.get(0).addProperty("magnitude", 8);
		feq.get(1).addProperty("magnitude", 7);
		feq.get(2).addProperty("magnitude", 5);
		
		List<Marker> listMk = new ArrayList<Marker>();
		for(PointFeature feat:feq){
			listMk.add(new SimplePointMarker(feat.getLocation(),feat.getProperties()));
		}
		map.addMarkers(listMk);		//add markerS : a list of marker
		int yellow = color(250,127,0);
		int gray = color(150,150,150);
		int blue = color(0,0,200);
		for(Marker mk:listMk){
			if((int) mk.getProperty("magnitude")>6)
				mk.setColor(yellow);
			else
				mk.setColor(gray);
		}
		
	}
	
	public void draw(){
		background(210);
		map.draw();
//		addKey();
	}
}
