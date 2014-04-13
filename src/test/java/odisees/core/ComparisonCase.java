package odisees.core;

import static org.junit.Assert.*;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.Test;

public class ComparisonCase {
	
	@Test
	public void compareVariables() {
		String[] vars= new String[] {
				"M3ZSYN-BottomPressure-Low1", 
				"M3ZSYN-BottomPressure-Low2", 
				"M3ZSYN-BottomPressure-LowerMid1",
				"SFCWN_Surface1",
				"SFCWN_Surface2"};
		JsonObject result= Comparison.list(vars, null);
		assertEquals(16, result.get("relations").getAsArray().size());
		JsonArray variables= result.get("variables").getAsArray();
		assertEquals(5, variables.size());
		JsonObject first= variables.get(0).getAsObject();
		assertEquals(16, first.get("quickFacts").getAsArray().size()); }}
