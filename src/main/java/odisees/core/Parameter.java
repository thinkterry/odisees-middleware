package odisees.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import odisees.utils.App;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.lib.MultiMap;
import org.apache.jena.atlas.lib.MultiMapToSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class Parameter { 
	private static String generalQuery= App.prefix+
			"select distinct ?pcTerm ?pcName { "+ 
			"?pcUri a :ODISEESCategory ; "+
			"rdfs:label ?pcName . "+
			"?var :category ?pcUri ; "+
			"     :dataSet/:productionStatus :ASDCCurrentDataProduct . "+
			"bind (strafter(str(?pcUri), '#') as ?pcTerm) "+
			"} order by desc(?pcName)";

	private static String detailedQuery(String parameterTerm) { return  App.prefix+
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

	public static JsonObject list(String parameterTerm, String keyword, 
			Map<String, String[]> filters, String remoteService) {
		JsonObject result= new JsonObject();
		if (parameterTerm == null) {
			ResultSet genRs= App.query(generalQuery, remoteService);
			result.put("parameters", format(genRs)); }
		else { 
			String query= detailedQuery(parameterTerm);
			String filteredQuery= App.filter(query, filters, "?varUri", keyword);
			ResultSet genRs= App.query(generalQuery, remoteService);
			ResultSet detRs= App.query(filteredQuery, remoteService);
			result.put("parameters", format(genRs, detRs)); }
		return result; }

	private static JsonArray format(ResultSet rs) {
		Set<String> unsortedParams= new HashSet<String>();
		Map<String, String> names= new HashMap<String, String>();
		while (rs.hasNext()) { 
			QuerySolution qs= rs.nextSolution();
			unsortedParams.add(App.str("pcTerm", qs));
			names.put(App.str("pcTerm", qs), App.str("pcName", qs)); }
		List<String> params= new ArrayList<String>(unsortedParams);
		Collections.sort(params);
		JsonArray results= new JsonArray();
		for (String p : params) {
			JsonObject param= new JsonObject();
			param.put("parameter", p);
			param.put("name", names.get(p));
			param.put("filters", new JsonObject());
			results.add(param); }
		return results; }

	private static JsonArray format(ResultSet general, ResultSet detailed) {
		Set<String> unsortedParams= new HashSet<String>();
		Map<String, String> names= new HashMap<String, String>();
		MultiMap<String, String> paramFilters= new MultiMapToSet<String, String>();
		MultiMap<String, String> filterValues= new MultiMapToSet<String, String>();
		Map<String, Integer> valueCounts= new HashMap<String, Integer>();
		while (general.hasNext()) { 
			QuerySolution qs= general.nextSolution();
			unsortedParams.add(App.str("pcTerm", qs));
			names.put(App.str("pcTerm", qs), App.str("pcName", qs)); }
		while (detailed.hasNext()) {
			QuerySolution qs= detailed.nextSolution();
			String pcTerm= App.str("pcTerm", qs);
			String filterTerm= App.str("filterTerm", qs);
			String valueTerm= App.str("valueTerm", qs);
			String filterName= App.str("filterName", qs);
			String valueName= App.str("valueName", qs);
			Integer num= qs.getLiteral("num").getInt();
			names.put(filterTerm, filterName);
			names.put(valueTerm, valueName);
			paramFilters.put(pcTerm, filterTerm); 
			filterValues.put(filterTerm, valueTerm);
			valueCounts.put(valueTerm, num);
		}
		List<String> params= new ArrayList<String>(unsortedParams);
		Collections.sort(params);
		JsonArray results= new JsonArray();
		for (String p : params) {
			JsonObject param= new JsonObject();
			param.put("parameter", p);
			param.put("name", names.get(p));
			JsonObject filtersObj= new JsonObject();
			if (paramFilters.containsKey(p)) {
				for (String filter : paramFilters.get(p)) {
					JsonObject filterObj= new JsonObject();
					filterObj.put("filter", filter);
					filterObj.put("name", names.get(filter));
					JsonArray valuesArr= new JsonArray();
					for (String value : filterValues.get(filter)) {
						JsonObject valueObj= new JsonObject();
						valueObj.put("value", value);
						valueObj.put("name", names.get(value));
						valueObj.put("count", valueCounts.get(value));
						valuesArr.add(valueObj); }
					filterObj.put("values", valuesArr);
					filtersObj.put(filter, filterObj); }}
			param.put("filters", filtersObj);
			results.add(param); }
		return results; }}
