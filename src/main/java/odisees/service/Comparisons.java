package odisees.service;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import odisees.core.Comparison;

@Path("/comparison")
@Produces(MediaType.APPLICATION_JSON)
public class Comparisons {
	@Context HttpServletRequest req;
	@Context ServletConfig sc;
	
	@SuppressWarnings("unchecked")
	@GET
	public String list() {
	String remoteService= sc.getInitParameter("sparqlEndpoint");
	Map<String, String[]> params= req.getParameterMap();
	System.out.println(params);
	String[] vars= req.getParameterValues("vars[]");
	return Comparison.list(vars, remoteService).toString(); }}
