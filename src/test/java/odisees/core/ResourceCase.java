package odisees.core;

import static org.junit.Assert.*;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.Test;

public class ResourceCase {
	

	@Test
	public void viewResource() {
		JsonObject result= Resource.view("M3ZSYN-BottomPressure-Low1", null);
		assertEquals(4, result.keys().size());
		JsonArray datasets= result.get("datasets").getAsArray();
		assertEquals(1, datasets.size());
		JsonArray facts= result.get("facts").getAsArray();
		assertEquals(13, facts.size()); }}
