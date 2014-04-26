package odisees.service;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import odisees.utils.App;

import com.sun.jersey.api.model.AbstractResourceModelContext;
import com.sun.jersey.api.model.AbstractResourceModelListener;

@Provider
public class Listener implements AbstractResourceModelListener {
	
	@Context ServletConfig sc;

    @Override
    public void onLoaded(AbstractResourceModelContext modelContext) {
        String remoteService= sc.getInitParameter("sparqlEndpoint");
        String username= sc.getInitParameter("username");
        String passwd= sc.getInitParameter("password");
        System.out.println("##### endpoint: "+remoteService);
        System.out.println("##### username: "+username);
        System.out.println("##### password: "+passwd);
        try { App.setAuth(remoteService, username, passwd); }
        catch (Exception e) { e.printStackTrace(); }}}
