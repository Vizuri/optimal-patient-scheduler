package com.vizuri.patient.service.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.jaxrs.config.BeanConfig;

@ApplicationPath("/rest")
public class JaxRsActivator extends Application{
	private static final Logger log =LoggerFactory.getLogger(JaxRsActivator.class);

	public JaxRsActivator() {
	        
	log.info("Starting the rest interface for Patient scheduler!");
	BeanConfig beanConfig = new BeanConfig();
	        beanConfig.setVersion("1.0");
	        beanConfig.setSchemes(new String[]{"http"});
	        beanConfig.setHost("localhost:8080");
	        beanConfig.setBasePath("/scheduler-web-api/rest");	
	        beanConfig.setResourcePackage("com.vizuri.patient.service.rest");
	        beanConfig.setScan(true);
	        
	       
	    }

	public Set<Class<?>> getClasses() {
	    
	        Set<Class<?>> resources = new HashSet<>();
	        
	        resources.add(PlannerService.class);
	        
	        resources.add(ServiceJacksonJsonProvider.class);
	        
	        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
	        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
	        
	        return resources;

	}

}
