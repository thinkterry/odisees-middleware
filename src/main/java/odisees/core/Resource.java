package odisees.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.lib.MultiMap;
import org.apache.jena.atlas.lib.MultiMapToSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import odisees.utils.App;

public class Resource {
	private static String query(String item) { return App.prefix+
		"select distinct ?item ?rel ?value ?itemName ?relName ?valueName ?isResource ?uri { "+
			"bind (:"+item+" as ?itemUri) . "+
			"?itemUri ?relUri ?value . "+
			"?relUri rdf:type :QuickFact . "+
			"OPTIONAL { ?itemUri rdfs:label ?itemName } "+ 
			"OPTIONAL { ?relUri rdfs:label ?relName } "+ 
			"OPTIONAL { ?value rdfs:label ?valueName } "+
			"OPTIONAL { ?value :url ?uri } "+
			"bind (strafter(str(?itemUri), '#') as ?item) "+
			"bind (strafter(str(?relUri), '#') as ?rel) "+
			"bind (exists { " +
			" ?value ?valRel ?valVal . ?valRel a :QuickFact } " +
			"as ?isResource) }"; }
	
	public static JsonObject view(String item, String remoteService) {
		ResultSet rs= App.query(query(item), remoteService);
		return format(item, rs); }
	
	private static JsonObject format(String item, ResultSet rs) {
		Map<String, String> names= new HashMap<String, String>();
		Map<String, String> uris= new HashMap<String, String>();
		Map<String, Boolean> isResource= new HashMap<String, Boolean>();
		MultiMap<String, String> relations= new MultiMapToSet<String, String>();
		MultiMap<String, String> values= new MultiMapToSet<String, String>();		
		while (rs.hasNext()) {
			QuerySolution qs= rs.nextSolution();
			String rel= App.str("rel", qs);
			String value= App.str("value", qs);
			if (qs.contains("itemName")) { names.put(item, App.str("itemName", qs)); }
			if (qs.contains("relName")) { names.put(rel, App.str("relName", qs)); }
			if (qs.contains("valueName")) { names.put(value, App.str("valueName", qs)); }
			if (qs.contains("uri")) { uris.put(value, App.str("uri", qs)); }
			Boolean isResrc= qs.getLiteral("isResource").getBoolean();
			relations.put(item, rel);
			values.put(rel, value);
			isResource.put(value, isResrc); }
		JsonObject result= new JsonObject();
		result.put("uuid", item);
		if (names.containsKey(item)) { result.put("name", names.get(item)); }
		else { result.put("name", item); }
		JsonArray factsArr= new JsonArray();
		JsonArray datasetArr= new JsonArray();
		for (String rel : relations.get(item)) {
			if (rel.contentEquals("dataSet")) { 
				datasetArr.add(getRelInfo(item, rel, values, names, uris, isResource)); }
			else { 
				factsArr.add(getRelInfo(item, rel, values, names, uris, isResource)); }}
		result.put("facts", factsArr);
		result.put("datasets", datasetArr);
		return result; }
	
	private static JsonObject getRelInfo(String item, String rel, 
			MultiMap<String, String> values, Map<String, String> names,
			Map<String, String> uris, Map<String, Boolean> isResource) {
		JsonObject result= new JsonObject();
		if (names.containsKey(rel)) { result.put("relation", names.get(rel)); }
		else { result.put("relation", rel); }
		JsonArray valArr= new JsonArray();
		for (String val : values.get(rel)) {
			String value;
			if (val.contains("#")) { value= val.split("#")[1]; }
			else { value= val; }
			JsonObject valObj= new JsonObject();
			valObj.put("uuid", value);
			if (names.containsKey(val)) { valObj.put("name", names.get(val)); }
			else { valObj.put("name", value); }
			valObj.put("isResource", isResource.get(val));
			if (uris.containsKey(val)) { valObj.put("uri", uris.get(val)); }
			valArr.add(valObj); }
		result.put("values", valArr);
		return result;
	}
}
