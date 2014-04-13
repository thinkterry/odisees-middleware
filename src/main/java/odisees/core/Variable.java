package odisees.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.lib.MultiMap;
import org.apache.jena.atlas.lib.MultiMapToSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import odisees.utils.App;

public class Variable {
	private static String query(String parameterTerm) { return App.prefix+
			"select ?vnTerm ?vnName "+ 
			"       ?varTerm ?description "+ 
			"       ?projectTerm ?projectName "+ 
			"       ?datasetTerm ?datasetName ?uri { "+
			"?varUri :category :"+parameterTerm+" ; "+
			"        :variableName ?vnUri ; "+
			"        :project ?projectUri; "+ 
			"        :dataSet ?datasetUri . "+
			"?vnUri rdfs:label ?vnName ; "+
			"       rdfs:label ?description . "+ 
			"?projectUri rdfs:label ?projectName . "+
			"?datasetUri rdfs:label ?datasetName ; "+
			"            :productionStatus :ASDCCurrentDataProduct . "+
			"OPTIONAL { ?datasetUri :url ?uri } "+
			"bind (strafter(str(?vnUri), '#') as ?vnTerm) "+
			"bind (strafter(str(?varUri), '#') as ?varTerm) "+
			"bind (strafter(str(?projectUri), '#') as ?projectTerm) "+
			"bind (strafter(str(?datasetUri), '#') as ?datasetTerm) "+
			"} order by desc(?vnName) "; }

	public static JsonObject list(String parameterTerm, String keyword, 
			Map<String, String[]> filters, String remoteService) {
		JsonObject result= new JsonObject();
		String query= query(parameterTerm);
		String filteredQuery= App.filter(query, filters, "?varUri", keyword);
		ResultSet rs= App.query(filteredQuery, remoteService);
		result.put("variableNames", format(rs));
		return result; }

	private static JsonArray format(ResultSet rs) {
		Set<String> variableNames= new HashSet<String>();
		Map<String, String> names= new HashMap<String, String>();
		Map<String, String> varDatasets= new HashMap<String, String>();
		Map<String, String> varUris= new HashMap<String, String>();
		Map<String, String> descriptions= new HashMap<String, String>();
		MultiMap<String, String> vnProjects= new MultiMapToSet<String, String>();
		MultiMap<String, String> vnVariables= new MultiMapToSet<String, String>();
		while (rs.hasNext()) {
			QuerySolution qs= rs.nextSolution();
			String vnTerm= App.str("vnTerm", qs);
			String vnName= App.str("vnName", qs);
			String varTerm= App.str("varTerm", qs);
			String descrip= App.str("description", qs);
			String projectTerm= App.str("projectTerm", qs);
			String projectName= App.str("projectName", qs);
			String datasetTerm= App.str("datasetTerm", qs);
			String datasetName= App.str("datasetName", qs);
			if (qs.contains("uri")) { varUris.put(varTerm, qs.getLiteral("uri").getString()); }
			variableNames.add(vnTerm);
			names.put(vnTerm, vnName);
			names.put(projectTerm, projectName);
			names.put(varTerm, datasetName);
			varDatasets.put(varTerm, datasetTerm);
			descriptions.put(vnTerm, descrip);
			vnProjects.put(vnTerm, projectTerm);
			vnVariables.put(vnTerm, varTerm); }
		JsonArray results= new JsonArray();
		for (String vn : variableNames) {
			JsonObject vnObj= new JsonObject();
			vnObj.put("variableName", vn);
			vnObj.put("label", names.get(vn));
			vnObj.put("description", descriptions.get(vn));
			JsonArray projArray= new JsonArray();
			for (String proj : vnProjects.get(vn)) {
				JsonObject projObj= new JsonObject();
				projObj.put("project", proj);
				projObj.put("name", names.get(proj)); 
				projArray.add(projObj); }
			vnObj.put("projects", projArray);
			JsonArray varArray= new JsonArray();
			for (String var : vnVariables.get(vn)) {
				JsonObject varObj= new JsonObject();
				varObj.put("variable", var);
				varObj.put("dataset", varDatasets.get(var));
				varObj.put("name", names.get(var));
				if (varUris.containsKey(var)) { 
					varObj.put("uri", varUris.get(var)); }
				varArray.add(varObj); }
			vnObj.put("variables", varArray);
			results.add(vnObj); }
		return results; }}
