package com.vizuri.patient.service.domain;

import java.time.LocalDate;
import java.time.LocalTime;

/*
 * 	LocalDateTime timePoint = LocalDateTime.now( );     // The current date and time
 * 	LocalDate.now()
	LocalDate.of(2012, Month.DECEMBER, 12); // from values
	LocalDate.ofEpochDay(150);  // middle of 1970
	LocalTime.of(17, 18); // the train I took home today
	LocalTime.parse("10:15:30"); // From a String
 */
public class Appointment {

	private String id;
	private String type;	// "T": treatment, "A": appointment
	
	private Location clinic;
	private String clinicName;
	private LocalDate appointmentDate;
	private LocalTime startTime;
	private LocalTime endTime;
	
	private String roomId;
	private String status;
	
	private Physician physician;
	private String physicianName;
    private Patient patient;
    private String patientName;
	
	public Appointment(){}

	public Appointment(String type, LocalDate appointmentDate, 
					   LocalTime startTime, 
					   LocalTime endTime, 
					   String roomId) {
		
		this.type = type;
		this.appointmentDate = appointmentDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.roomId = roomId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDate getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(LocalDate appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	
	public String getStartHour() {
		
		if (startTime != null){
			return String.format("%02d", startTime.getHour());
		}
		return "-1";
	}
	
	public String getStartMinute() {
		
		if (startTime != null){
			return String.format("%02d", startTime.getMinute());
		}
		return "-1";
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}
	
	public String getEndHour() {
		
		if (endTime != null){
			return String.format("%02d", endTime.getHour());
		}
		return "-1";
	}
	
	public String getEndMinute() {
		
		if (endTime != null){
			return String.format("%02d", endTime.getMinute());
		}
		return "-1";
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Physician getPhysician() {
		return physician;
	}

	public void setPhysician(Physician physician) {
		this.physician = physician;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPhysicianName() {
		return physicianName;
	}

	public void setPhysicianName(String physicianName) {
		this.physicianName = physicianName;
	}

	public Location getClinic() {
		return clinic;
	}

	public void setClinic(Location clinic) {
		this.clinic = clinic;
	}

	public String getClinicName() {
		return clinicName;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", type=" + type + ", clinic=" + clinic + ", clinicName=" + clinicName
				+ ", appointmentDate=" + appointmentDate + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", roomId=" + roomId + ", status=" + status + ", physician=" + physician + ", physicianName="
				+ physicianName + ", patient=" + patient + ", patientName=" + patientName + "]";
	}
	
}
