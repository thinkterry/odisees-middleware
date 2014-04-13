package odisees.service;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import odisees.core.Resource;

@Path("/info/{uuid}")
@Produces(MediaType.APPLICATION_JSON)
public class Resources {
	@Context ServletConfig sc;

	@GET
	public String info(@PathParam("uuid") String item) {
		String remoteService= sc.getInitParameter("sparqlEndpoint");
		return Resource.view(item, remoteService).toString(); }}
