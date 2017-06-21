package com.vizuri.patient.service.rest;

import java.util.TimeZone;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.vizuri.patient.service.domain.Appointment;
import com.vizuri.patient.service.domain.Patient;

@Provider
@Produces(MediaType.APPLICATION_JSON)	
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceJacksonJsonProvider extends JacksonJaxbJsonProvider{

	private static final Logger log = LoggerFactory.getLogger(ServiceJacksonJsonProvider.class);
	
	public ServiceJacksonJsonProvider() {
		log.info("constructing ServiceJacksonJsonProvider");
		ObjectMapper objectMapper = new ObjectMapper();
		//objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);	// CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES
		
		//json to java
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		// do not send fields back with null values
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        
        // getters
        objectMapper.addMixIn(Patient.class, ExcludeFieldsMixIn.class);
        objectMapper.addMixIn(Appointment.class, ExcludeFieldsMixIn.class);
        
		// To change default date format
		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		// objectMapper.setDateFormat(df);
		// OR anotate field like this
		// @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
		// private Date date;
        
        //03.11.2015
        //objectMapper.setDateFormat(new SimpleDateFormat("dd.MM.yyyy"));
        // 6/6/2017
        //objectMapper.setDateFormat(new SimpleDateFormat("MM/dd/yyyy"));
        
        // Serialize - getter
        //objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        
		
		// java to json : disable converting a date to an integer timestamp
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// set the default time zone
		objectMapper.setTimeZone(TimeZone.getTimeZone("EST"));
		
		super.setMapper(objectMapper);
	}
	

	
}


