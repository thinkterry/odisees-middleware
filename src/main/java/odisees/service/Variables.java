package odisees.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import odisees.core.Variable;

@Path("/variables")
@Produces(MediaType.APPLICATION_JSON)
public class Variables {
	@Context HttpServletRequest req;
	@Context ServletConfig sc;
	
	@SuppressWarnings("unchecked")
	@GET
	public String list() {
		String remoteService= sc.getInitParameter("sparqlEndpoint");
		Map<String, String[]> params= new HashMap<String, String[]>();
		params.putAll(req.getParameterMap());
		String id = null;
		String keyword = null;
		if (params.containsKey("id")) { 
			id= params.get("id")[0]; params.remove("id"); }
		if (params.containsKey("keyword")) { 
			keyword= params.get("keyword")[0]; params.remove("keyword"); }
		return Variable.list(id, keyword, params, remoteService).toString(); }}
