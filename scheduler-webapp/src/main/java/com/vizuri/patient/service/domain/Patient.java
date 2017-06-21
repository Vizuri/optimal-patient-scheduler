package com.vizuri.patient.service.domain;

import java.util.ArrayList;
import java.util.List;

import com.vizuri.patient.scheduler.model.TreatmentDays;

public class Patient {

	private String id;
	private String name;
	private List<Appointment> appointments;
    private Location clinic;
    private String clinicId;
    private String treatmentDays;

    public Patient(){}
    
	public Patient(String name) {
		
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}
	
	public void addAppointment(Appointment appointment){
		
		if (appointments == null){
			appointments = new ArrayList<Appointment>();
		}
		
		appointments.add(appointment);
	}
	
	

	public String getTreatmentDays() {
		return treatmentDays;
	}

	public void setTreatmentDays(String treatmentDays) {
		this.treatmentDays = treatmentDays;
	}

	public Location getClinic() {
		return clinic;
	}

	public void setClinic(Location clinic) {
		this.clinic = clinic;
	}

	public String getClinicId() {
		return clinicId;
	}

	public void setClinicId(String clinicId) {
		this.clinicId = clinicId;
	}

	@Override
	public String toString() {
		return "Patient [id=" + id + ", name=" + name + ", appointments=" + appointments + ", clinic=" + clinic
				+ ", clinicId=" + clinicId + ", treatmentDays=" + treatmentDays + "]";
	}
	
}
