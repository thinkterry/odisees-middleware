package odisees.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.lib.MultiMap;
import org.apache.jena.atlas.lib.MultiMapToSet;
import org.apache.jena.atlas.lib.StrUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import odisees.utils.App;

public class Comparison {
	private static String query(String[] vars) { 
		List<String> listVars= Arrays.asList(vars);
		List<String> rdfVars= new ArrayList<String>();
		for (String v : listVars) { rdfVars.add(":"+v); }
		String variables= StrUtils.strjoin(",", rdfVars);
		return App.prefix+
				"select ?variable ?variableName ?rel ?relName ?value ?valueName { "+
				"?variableUri ?relUri ?value . "+
				"filter(?variableUri in ("+variables+")) . "+
				"?relUri rdf:type :QuickFact . "+
				"optional { ?variableUri rdfs:label ?variableName } . "+
				"optional { ?relUri rdfs:label ?relName } . "+
				"optional { ?value rdfs:label ?valueName } . "+
				"bind (strafter(str(?variableUri), '#') as ?variable) "+
				"bind (strafter(str(?relUri), '#') as ?rel) } "; }

	public static JsonObject list(String[] vars, String remoteService) {
		ResultSet rs= App.query(query(vars), remoteService);
		return format(rs); }

	private static JsonObject format(ResultSet rs) {
		Set<String> variables= new HashSet<String>();
		Set<String> relationsUnsorted= new HashSet<String>();
		Map<String, String> names= new HashMap<String, String>();
		MultiMap<String, String> values= new MultiMapToSet<String, String>();
		while (rs.hasNext()) {
			QuerySolution qs= rs.nextSolution();
			String var= App.str("variable", qs);
			String varName= App.str("variableName", qs);
			String rel= App.str("rel", qs);
			String relName= App.str("relName", qs);
			String val= qs.getResource("value").toString();
			String valName= App.str("valueName", qs);
			variables.add(var);
			relationsUnsorted.add(rel);
			names.put(var, varName);
			names.put(rel, relName);
			names.put(val, valName);
			values.put(var+"#"+rel, val); }
		List<String> relations= new ArrayList<String>();
		relations.addAll(relationsUnsorted);
		if (relations.contains("variableName")) { relations.remove("variableName"); }
		JsonObject result= new JsonObject();
		JsonArray relArr= new JsonArray();
		relArr.add("Variable Name");
		for (String rel : relations) { relArr.add(names.get(rel)); }
		result.put("relations", relArr);
		JsonArray varArr= new JsonArray();
		for (String var : variables) {
			JsonObject varObj= new JsonObject();
			varObj.put("variable", var);
			if (names.containsKey(var)) { varObj.put("name", names.get(var)); }
			else { varObj.put("name", var); }
			JsonArray quickFacts= new JsonArray();
			JsonObject nameObj= new JsonObject();
			nameObj.put("relation", "Variable Name");
			nameObj.put("value", names.get(var));
			quickFacts.add(nameObj);
			for (String rel : relations) {
				JsonObject relObj= new JsonObject();
				if (names.containsKey(rel)) { relObj.put("relation", names.get(rel)); }
				else { relObj.put("relation", rel); }
				String varrel= var+"#"+rel; 
				String val= values.getOne(varrel);
				if (names.containsKey(val)) { relObj.put("value", names.get(val)); }
				else { relObj.put("value", val.split("#")[1]); }
				quickFacts.add(relObj); }
			varObj.put("quickFacts", quickFacts);
			varArr.add(varObj); }
		result.put("variables", varArr);
		return result; }}
