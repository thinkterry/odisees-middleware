package odisees.core;

import static org.junit.Assert.*;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.junit.Test;

public class VariableCase {
		
	@Test
	public void listVariables() {
		JsonObject result= Variable.list("Pressure", null);
		JsonArray variables= result.get("variableNames").getAsArray();
		for (JsonValue v : variables) {
			JsonObject var= v.getAsObject();
			String varName= var.get("variableName").getAsString().toString();
			if (varName.contentEquals("Top_Pressure_UpperMid")) {
				assertEquals("Top_Pressure_UpperMid", var.get("description").getAsString().toString());
				assertEquals("Top_Pressure_UpperMid", var.get("label").getAsString().toString());
				assertEquals(1, var.get("projects").getAsArray().size());
				assertEquals(3, var.get("variables").getAsArray().size()); }}
		assertEquals(15, variables.size()); }}
