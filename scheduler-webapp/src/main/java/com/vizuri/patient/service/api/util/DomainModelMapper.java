package com.vizuri.patient.service.api.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.scheduler.model.AppointmentType;
import com.vizuri.patient.scheduler.model.Clinic;
import com.vizuri.patient.scheduler.model.ConsultingRoom;
import com.vizuri.patient.scheduler.model.Day;
import com.vizuri.patient.scheduler.model.Physician;
import com.vizuri.patient.scheduler.model.TimeGrain;
import com.vizuri.patient.scheduler.solver.AppointmentSolution;
import com.vizuri.patient.scheduler.solver.ConsultationEncounter;
import com.vizuri.patient.scheduler.solver.Encounter;
import com.vizuri.patient.service.domain.Appointment;
import com.vizuri.patient.service.domain.Location;
import com.vizuri.patient.service.domain.Room;

public class DomainModelMapper {

	private static final Logger log = LoggerFactory.getLogger(DomainModelMapper.class);
	
	private AppointmentSolution solution;
	
	public DomainModelMapper() {
		
	}
	
	public DomainModelMapper(AppointmentSolution solution) {
		this.solution = solution;
	}
	
	public List<Location> getClinics() {
		List<Location> locations = new ArrayList<Location>();
		
		if (solution == null ||
			solution.getClinics() == null ||
			solution.getClinics().size() == 0){
			
			log.error("No Clinics to map");
			return locations;
		}
		
		for (Clinic clinic : solution.getClinics()) {
			locations.add(mapClinic(clinic));
		}
		return locations;
	}
	
	public Location mapClinic(Clinic clinic){
		Location location = new Location(clinic.getName());
		location.setId(clinic.getId().toString());
		
		for (ConsultingRoom r : clinic.getRooms()){
			location.addRoom(mapRoom(r));
		}
		
		// we need a list of patients for each clinic in the ui
		// we can potentially just use patient names
		for (com.vizuri.patient.scheduler.model.Patient p : clinic.getPatients()){
			location.addPatient(mapPatient(p));
		}
		location.setAppointments(new ArrayList<Appointment>());
		return location;
	}

	private com.vizuri.patient.service.domain.Room mapRoom(ConsultingRoom r){
		return new Room(r.getId().toString(), r.getName(), r.getLabel(), "A");
	}
	
	public List<com.vizuri.patient.service.domain.Patient> getPatients() {
		List<com.vizuri.patient.service.domain.Patient> patients = new ArrayList<com.vizuri.patient.service.domain.Patient>();
		
		for (com.vizuri.patient.scheduler.model.Patient p : solution.getPatients()) {
			patients.add(mapPatient(p));
		}
		
		return patients;
	}
	
	private com.vizuri.patient.service.domain.Patient mapPatient(com.vizuri.patient.scheduler.model.Patient p){
		com.vizuri.patient.service.domain.Patient patient = new com.vizuri.patient.service.domain.Patient(p.getName());
		patient.setId(p.getId().toString());
		patient.setClinicId(p.getClinic().getId().toString());
		//patient.setClinic(mapClinic(p.getClinic()));
		patient.setAppointments(new ArrayList<Appointment>());
		patient.setTreatmentDays(p.getTreatmentDays().name());
		return patient;
	}
	
	public List<Appointment> getAppointments() {
		List<Appointment> appointments = new ArrayList<Appointment>();
		
		for (Encounter consult : solution.getEncounters()) {
			appointments.add(mapAppointment(consult));
		}
		for (Encounter treatment : solution.getTreatments()) {
			appointments.add(mapAppointment(treatment));
		}
		
		return appointments;
	}
	
	public static Appointment mapAppointment(Encounter encounter){
		if (log.isDebugEnabled()) { log.debug("Inside mapAppointment, for encounter: " + encounter); }
		
		Appointment appointment = new Appointment();
		String id = encounter.getId().toString();
		
		appointment.setId(id);
		
		AppointmentType type = encounter.getAppointment().getType();
			
		if (type == AppointmentType.TREATMENT){
			
			appointment.setType("T");
		}
		else{
			appointment.setType("A");		
		}
		
		appointment.setStatus(encounter.getStatus().name());
		appointment.setRoomId(encounter.getRoom().getLabel());
		
		LocalDate appointmentDate;
		LocalTime startTime;
		LocalTime endTime;
				
		LocalTime midnight = LocalTime.MIDNIGHT;
		
		startTime = midnight.plusMinutes(encounter.getStartingTime().getStartingMinuteOfDay());
		int appointmentDuration = encounter.getAppointment().getDurationInGrains() * TimeGrain.GRAIN_LENGTH_IN_MINUTES;
		endTime = startTime.plusMinutes(appointmentDuration);

		appointment.setStartTime(startTime);
		appointment.setEndTime(endTime);
		
		Day day = encounter.getStartingTime().getDay();
		
		appointmentDate = LocalDate.ofYearDay(day.getYear(), day.getDayOfYear());
			
		appointment.setAppointmentDate(appointmentDate);

		appointment.setPatientName(encounter.getAppointment().getPatient().getName());
		appointment.setClinicName(encounter.getAppointment().getClinic().getName());
		
		
		if (encounter instanceof ConsultationEncounter) {
			Physician physician = ((ConsultationEncounter)encounter).getPhysician();
			if (physician != null) {
				if (log.isDebugEnabled()) { log.debug("Add physician["+physician.getName()+"], for appointment: " + encounter.getId()); }
				appointment.setPhysician(new com.vizuri.patient.service.domain.Physician(physician.getName()));
				appointment.setPhysicianName(physician.getName());
			}
		}
		
		return appointment;
	}
	
	public List<com.vizuri.patient.service.domain.Physician> getPhysicians(){
		List<com.vizuri.patient.service.domain.Physician> physicians = new ArrayList<com.vizuri.patient.service.domain.Physician>();
		
		for (com.vizuri.patient.scheduler.model.Physician p : solution.getPhysicians()) {
			com.vizuri.patient.service.domain.Physician physician = new com.vizuri.patient.service.domain.Physician(p.getName());
			physician.setAppointments(new ArrayList<Appointment>());
			physicians.add(physician);
		}
		
		return physicians;
	}

}
