package odisees.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.lib.MultiMap;
import org.apache.jena.atlas.lib.MultiMapToList;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class Parameter { 
	private static String generalQuery= ""+
			"select distinct ?pcTerm ?pcName { "+ 
			"?pcUri a :ODISEESCategory ; "+
			"rdfs:label ?pcName . "+
			"?var :category ?pcUri ; "+
			"     :dataSet/:productionStatus :ASDCCurrentDataProduct . "+
			"bind (strafter(str(?pcUri), '#') as ?pcTerm) "+
			"} order by desc(?pcName)";

	private static String detailedQuery(String parameterTerm) { return  ""+
			"select ?pcTerm ?filterTerm ?valueTerm ?filterName ?valueName (count(?varUri) as ?num) { "+
			"bind('"+parameterTerm+"' as ?pcTerm) . "+
			"?filterUri :searchFilterFor :"+parameterTerm+" . "+
			"?varUri :category :"+parameterTerm+" ; "+
			"        ?filterUri ?valueUri ; "+
			"        :variableName ?vnUri ; "+
			"        :project ?projectUri ; "+
			"        :dataSet ?datasetUri . "+
			"?vnUri rdfs:label ?vnName ; "+
			"rdfs:label ?description . "+
			"?projectUri rdfs:label ?projectName . "+
			"?datasetUri rdfs:label ?datasetName ; "+
			":productionStatus :ASDCCurrentDataProduct . "+
			"?filterUri rdfs:label ?filterName . "+
			"?valueUri rdfs:label ?valueName . "+
			"bind (strafter(str(?filterUri), '#') as ?filterTerm) "+
			"bind (strafter(str(?valueUri), '#') as ?valueTerm) "+
			"bind (strafter(str(?vnUri), '#') as ?vnTerm) "+
			"} group by ?pcTerm ?filterTerm ?valueTerm ?filterName ?valueName "+
			"having (count(?varUri) > 0) "; }

	public static JsonObject list(String parameterTerm, Model localService) {		
		JsonObject result= new JsonObject();
		if (parameterTerm == null) {
			ResultSet genRs= query(generalQuery, localService);
			result.put("parameters", format(genRs)); }
		else { 
			ResultSet genRs= query(generalQuery, localService);
			ResultSet detRs= query(detailedQuery(parameterTerm), localService);
			result.put("parameters", format(genRs, detRs)); }
		return result; }

	public static JsonObject list(String parameterTerm, String remoteService) {
		JsonObject result= new JsonObject();
		if (parameterTerm == null) {
			ResultSet genRs= query(generalQuery, remoteService);
			result.put("parameters", format(genRs)); }
		else { 
			ResultSet genRs= query(generalQuery, remoteService);
			ResultSet detRs= query(detailedQuery(parameterTerm), remoteService);
			result.put("parameters", format(genRs, detRs)); }
		return result; }

	private static JsonArray format(ResultSet rs) {
		List<String> params= new ArrayList<String>();
		Map<String, String> names= new HashMap<String, String>();
		while (rs.hasNext()) { 
			QuerySolution qs= rs.nextSolution();
			params.add(str("pcTerm", qs));
			names.put(str("pcTerm", qs), str("pcName", qs)); }
		JsonArray results= new JsonArray();
		for (String p : params) {
			JsonObject param= new JsonObject();
			param.put("parameter", p);
			param.put("name", names.get(p));
			param.put("filters", new JsonObject());
			results.add(param); }
		return results; }
	
	private static JsonArray format(ResultSet general, ResultSet detailed) {
		List<String> params= new ArrayList<String>();
		Map<String, String> names= new HashMap<String, String>();
		MultiMap<String, String> filters= new MultiMapToList<String, String>();
		MultiMap<String, String> filterValues= new MultiMapToList<String, String>();
		Map<String, Integer> filterCounts= new HashMap<String, Integer>();
		while (general.hasNext()) { 
			QuerySolution qs= general.nextSolution();
			params.add(str("pcTerm", qs));
			names.put(str("pcTerm", qs), str("pcName", qs)); }
		while (detailed.hasNext()) {
			QuerySolution qs= detailed.nextSolution();
			String pcTerm= str("pcTerm", qs);
			String filterTerm= str("filterTerm", qs);
			String valueTerm= str("valueTerm", qs);
			String filterName= str("filterName", qs);
			String valueName= str("valueName", qs);
			Integer num= qs.getLiteral("num").getInt();
			names.put(filterTerm, filterName);
			names.put(valueTerm, valueName);
			filters.put(pcTerm, filterTerm); 
			filterValues.put(filterTerm, valueTerm);
			filterCounts.put(valueTerm, num);
		}
		JsonArray results= new JsonArray();
		for (String p : params) {
			JsonObject param= new JsonObject();
			param.put("parameter", p);
			param.put("name", names.get(p));
			//param.put("filters", new JsonObject());
			results.add(param); }
		return results; }
	
	private static String str(String variable, QuerySolution qs) { 
		return qs.getLiteral(variable).getString(); }

	private static ResultSet query(String query, String remoteService) {
		return QueryExecutionFactory.sparqlService(remoteService, query).execSelect(); }

	private static ResultSet query(String query, Model localService) {
		return QueryExecutionFactory.create(query, localService).execSelect(); }
}
