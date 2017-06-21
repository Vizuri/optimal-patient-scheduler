package com.vizuri.patient.service.domain;

import java.util.ArrayList;
import java.util.List;

import com.vizuri.patient.service.domain.Patient;

// Clinic
public class Location {

	private String id;
	private String name;
	private List<Room> rooms;
	private List<Patient> patients = new ArrayList<Patient>();
	private List<Appointment> appointments = new ArrayList<Appointment>();

	public Location() {};
	
	public Location(String name) {
		
		this.name = name;
	}

	public Location(String name, List<Room> rooms) {
		
		this.name = name;
		this.rooms = rooms;
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

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	public void addRoom(Room room){
		
		if (rooms == null){
			rooms = new ArrayList<Room>();
		}
		
		rooms.add(room);
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

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}
	
	public void addPatient(Patient patient){
		
		if (patients == null){
			patients = new ArrayList<Patient>();
		}
		
		patients.add(patient);
	}
	
	
	
}
