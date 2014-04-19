package odisees.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class App {
	public static Model localService;

	public static String prefix= 
			"prefix : <http://eosweb.larc.nasa.gov/2014/asdc#> " +
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";

	public static String str(String variable, QuerySolution qs) {
		RDFNode valNode= qs.get(variable);
		if (valNode.isLiteral()) { return valNode.asLiteral().getString(); }
		else { return valNode.asResource().getURI(); }}

	public static ResultSet query(String query, String remoteService) {
		if (remoteService == null) {
			return QueryExecutionFactory.create(query, localService).execSelect(); }
		else {
			return QueryExecutionFactory.sparqlService(remoteService, query).execSelect(); }}

	public static String filter(String query, Map<String, String[]> filters, String filterVar, String keyword) {
		String beginning= query.substring(0, query.lastIndexOf('}'));
		String ending= query.substring(query.lastIndexOf('}'), query.length());
		String keywordedQuery= addKeyword(beginning, keyword);
		String filteredQuery= addFilters(keywordedQuery, filterVar, filters);
		return filteredQuery+ending;
	}

	private static String addKeyword(String query, String keyword) {
		if (keyword == null || keyword.isEmpty()) { return query; }
		else {
			return query+" filter("+
					"contains(lcase(?vnName), lcase('"+keyword+"')) || "+
					"contains(lcase(?description), lcase('"+keyword+"')) || "+
					"contains(lcase(?projectName), lcase('"+keyword+"')) || "+
					"contains(lcase(?datasetName), lcase('"+keyword+"'))) "; }}
	
	private static String addFilters(String query, String filterVar, Map<String, String[]> filters) {
		if (filters == null || filters.isEmpty()) { return query; }
		else {
			for (Entry<String, String[]> filter : filters.entrySet()) {
				List<String> filterValues= Arrays.asList(filter.getValue());
				for (String fv : filterValues) {
					query+= filterVar+" :"+filter.getKey()+" :"+fv+" . "; }}
			return query; }}

}
