package odisees.core;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.junit.Test;

public class ParameterCase {
	
	@Test
	public void searchParameters() {
		JsonObject result= Parameter.list("Pressure", "Bottom", null, null);
		JsonArray parameters= result.get("parameters").getAsArray();
		assertEquals(9, parameters.size());

		Map<String, String[]> params= new HashMap<String, String[]>();
		params.put("outputFrequency", new String[] {"ThreeHours"});
		params.put("spatialResolutionType", new String[] {"ZonalRegion-Latitudinal"});
		result= Parameter.list("Pressure", "Bottom", params, null);
		parameters= result.get("parameters").getAsArray();
		assertEquals(9, parameters.size()); }
	
	@Test
	public void generalParameters() {
		JsonObject result= Parameter.list(null, null, null, null);
		JsonArray params= result.get("parameters").getAsArray();
		assertEquals(9, params.size()); }		
	
	@Test
	public void detailedParameters() {
		JsonObject result= Parameter.list("Pressure", null, null, null);
		JsonArray params= result.get("parameters").getAsArray();
		for (JsonValue val : params) {
			JsonObject param= val.getAsObject();
			JsonObject filters= param.get("filters").getAsObject(); 
			if (!filters.isEmpty()) {		
				assertEquals("\"Pressure\"", param.get("name").getAsString().toString());
				assertEquals(8, filters.keys().size()); }}
		assertEquals(9, params.size()); }}
