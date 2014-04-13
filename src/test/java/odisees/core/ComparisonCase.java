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
				"M3ZSYN-BottomPressure-LowerMid1"};
		JsonObject result= Comparison.list(vars, null);
		assertEquals(15, result.get("relations").getAsArray().size());
		JsonArray variables= result.get("variables").getAsArray();
		assertEquals(3, variables.size());
		JsonObject first= variables.get(0).getAsObject();
		assertEquals(15, first.get("quickFacts").getAsArray().size()); }}
