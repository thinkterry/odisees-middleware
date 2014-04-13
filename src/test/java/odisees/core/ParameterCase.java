package odisees.core;

import org.apache.jena.atlas.json.JsonObject;
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
		System.out.println(result); }		
	
	@Test
	public void detailedParameters() {
		JsonObject result= Parameter.list("Pressure", remote);
		System.out.println(result);
	}
}
