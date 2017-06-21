package com.vizuri.patient.service.domain;

import java.util.ArrayList;
import java.util.List;

public class Physician {
	
	private String name;
	private List<Appointment> appointments = new ArrayList<Appointment>();

	public Physician(){}
    
	public Physician(String name) {
		
		this.name = name;
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

}
