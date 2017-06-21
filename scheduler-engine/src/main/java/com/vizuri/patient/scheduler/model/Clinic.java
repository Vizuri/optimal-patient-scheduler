package com.vizuri.patient.scheduler.model;

import java.util.ArrayList;
import java.util.List;

import com.vizuri.patient.scheduler.model.Patient;

public class Clinic extends AbstractPersistable {
	private static final long serialVersionUID = 1808306210789549724L;

	private String name;
	private List<ConsultingRoom> rooms;
	private List<Patient> patients = new ArrayList<Patient>();

	public Clinic() {
		super();
	}

	public Clinic(Long id, String name, List<ConsultingRoom> rooms) {
		super(id);
		this.name = name;
		this.rooms = rooms;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ConsultingRoom> getRooms() {
		return rooms;
	}

	public void setRooms(List<ConsultingRoom> rooms) {
		this.rooms = rooms;
	}
	
	public void addPatient(Patient patient){
		
		if (patients == null){
			patients = new ArrayList<Patient>();
		}
		
		patients.add(patient);
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	@Override
	public String toString() {
		return "Clinic [id=" + id + ", name=" + name + ", rooms=" + rooms + "]";
	}

}
