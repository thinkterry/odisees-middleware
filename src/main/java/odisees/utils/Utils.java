package odisees.utils;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class Utils {
	public static Model localService;

	public static String prefix= 
			"prefix : <http://eosweb.larc.nasa.gov/2014/asdc#> " +
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";

	public static String str(String variable, QuerySolution qs) { 
		return qs.getLiteral(variable).getString(); }

	public static ResultSet query(String query, String remoteService) {
		if (remoteService == null) {
			return QueryExecutionFactory.create(query, localService).execSelect(); }
		else {
			return QueryExecutionFactory.sparqlService(remoteService, query).execSelect(); }}}
