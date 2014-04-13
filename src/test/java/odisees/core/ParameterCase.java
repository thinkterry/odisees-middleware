package odisees.core;

import static org.junit.Assert.*;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.junit.BeforeClass;
import org.junit.Test;

public class ParameterCase {
	static String remote= "http://localhost:8080/openrdf-sesame/repositories/nasa";
	
	@BeforeClass
	public static void setup() {
	
	}
	
	@Test
	public void generalParameters() {
		JsonObject result= Parameter.list(null, remote);
		JsonArray params= result.get("parameters").getAsArray();
		assertEquals(9, params.size()); }		
	
	@Test
	public void detailedParameters() {
		JsonObject result= Parameter.list("Pressure", remote);
		JsonArray params= result.get("parameters").getAsArray();
		for (JsonValue val : params) {
			JsonObject param= val.getAsObject();
			JsonObject filters= param.get("filters").getAsObject(); 
			if (!filters.isEmpty()) {		
				assertEquals("\"Pressure\"", param.get("name").getAsString().toString());
				assertEquals(8, filters.keys().size()); }}
		assertEquals(9, params.size()); }}
