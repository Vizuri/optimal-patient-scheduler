package com.vizuri.patient.scheduler.util;

public class PlannerConfig {

	private static final int factLimit = 1000;
	
	private int maxPhysicians;
	private int maxPatientsPerClinic;
	private int maxRooms;
	private int maxClinics;
	private int maxPatientAppointments;  // %
	private int scheduleWindow;	// in weeks
	
	public int getMaxPhysicians() {
		return maxPhysicians;
	}
	
	public void setMaxPhysicians(int maxPhysicians) {
		this.maxPhysicians = maxPhysicians;
	}
	
	public int getMaxPatientsPerClinic() {
		return maxPatientsPerClinic;
	}
	
	public void setMaxPatientsPerClinic(int maxPatientsPerClinic) {
		this.maxPatientsPerClinic = maxPatientsPerClinic;
	}
	
	public int getMaxRooms() {
		return maxRooms;
	}
	
	public void setMaxRooms(int maxRooms) {
		this.maxRooms = maxRooms;
	}
	
	public int getMaxClinics() {
		return maxClinics;
	}
	
	public void setMaxClinics(int maxClinics) {
		this.maxClinics = maxClinics;
	}
	
	public int getMaxPatientAppointments() {
		return maxPatientAppointments;
	}
	
	public void setMaxPatientAppointments(int maxPatientAppointments) {
		this.maxPatientAppointments = maxPatientAppointments;
	}
	
	public int getScheduleWindow() {
		return scheduleWindow;
	}

	public void setScheduleWindow(int scheduleWindow) {
		this.scheduleWindow = scheduleWindow;
	}

	public static int getFactlimit() {
		return factLimit;
	}

	@Override
	public String toString() {
		return "PlannerConfig [maxPhysicians=" + maxPhysicians + ", maxPatientsPerClinic=" + maxPatientsPerClinic
				+ ", maxRooms=" + maxRooms + ", maxClinics=" + maxClinics + ", maxPatientAppointments="
				+ maxPatientAppointments + ", scheduleWindow=" + scheduleWindow + "]";
	}
	
}
