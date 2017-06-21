package com.vizuri.patient.service.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vizuri.patient.service.domain.Location;
import com.vizuri.patient.service.domain.Patient;

abstract class ExcludeFieldsMixIn {
 
  //@JsonIgnore abstract List<PolicyType> getPolicyTypes();
	
	// fields to be ignored
	// Patient/Appointment
	@JsonIgnore abstract void setClinic(Location clinic);
	@JsonIgnore abstract Location getClinic();
	
	// Appointment
	@JsonIgnore abstract void setPatient(Patient patient);
	@JsonIgnore abstract Patient getPatient();
	
	

//	@JsonIgnore abstract ValidationType getValidationTypes();
//	@JsonIgnore abstract void setValidationTypes(ValidationType validationTypes);
	

	//sample of how to rename incoming json attributes
//	@JsonProperty("NOI") public String noi;
//	@JsonProperty("YearBuilt_nb") public String yearBuilt_nb;
//	@JsonProperty("Status_dt") public String status_dt;
//	@JsonProperty("Status_tx") public String status_tx;

	
	
	
	
	
}
