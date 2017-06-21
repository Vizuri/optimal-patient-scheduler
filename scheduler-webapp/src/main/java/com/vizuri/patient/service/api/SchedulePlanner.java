package com.vizuri.patient.service.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.scheduler.model.AppointmentStatus;
import com.vizuri.patient.scheduler.solver.AppointmentSolution;
import com.vizuri.patient.scheduler.solver.ConsultationEncounter;
import com.vizuri.patient.scheduler.solver.Encounter;
import com.vizuri.patient.scheduler.solver.SchedulerEngine;
import com.vizuri.patient.scheduler.solver.SchedulerEngineImpl;
import com.vizuri.patient.scheduler.util.DataFactory;
import com.vizuri.patient.scheduler.util.PlannerConfig;
import com.vizuri.patient.service.api.util.DomainModelMapper;
import com.vizuri.patient.service.api.util.SolutionResponse;
import com.vizuri.patient.service.domain.Appointment;
import com.vizuri.patient.service.domain.Location;
import com.vizuri.patient.service.domain.Patient;
import com.vizuri.patient.service.domain.Physician;

@Singleton
@Named("schedulePlanner")
public class SchedulePlanner {

	private static final Logger log = LoggerFactory.getLogger(SchedulePlanner.class);
	private static ConcurrentHashMap<String, Location> clinicCache = new ConcurrentHashMap<String, Location>();
	private static ConcurrentHashMap<String, Patient> patientCache = new ConcurrentHashMap<String, Patient>();
	private static ConcurrentHashMap<String, Physician> physicianCache = new ConcurrentHashMap<String, Physician>();
	private static ConcurrentHashMap<String, Appointment> appointmentCache = new ConcurrentHashMap<String, Appointment>();

	private SchedulerEngine schedulerEngine = new SchedulerEngineImpl();
	private AppointmentSolution lastBestSolution = null;

	public SchedulePlanner() {
	}

	@PostConstruct
	public void init() {

	}

	public synchronized void seedFacts(PlannerConfig config) throws Exception {
		log.info("Inside generateFacts, config: " + config);

		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.seedPlannerSolution(solution, config, LocalDate.now());	//LocalDate.of(2017, Month.MARCH, 1)
		
		schedulerEngine.setUnsolvedSolution(solution);
		lastBestSolution = null;
		updateCache(new DomainModelMapper(solution)); 
	}

	private void updateCache(DomainModelMapper mapper) throws Exception {
		try {
			clinicCache.clear();
			patientCache.clear();
			physicianCache.clear();
			appointmentCache.clear();
			
			for (Location location : mapper.getClinics()) {
				clinicCache.put(location.getName(), location);
			}
			for (Patient patient : mapper.getPatients()) {
				if (log.isDebugEnabled()) { log.debug("Adding patient: " + patient); }
				patientCache.put(patient.getName(), patient);
			}
			for (Physician physician : mapper.getPhysicians()) {
				physicianCache.put(physician.getName(), physician);
			}
			for (Appointment appointment : mapper.getAppointments()) {
				appointmentCache.put(appointment.getId(), appointment);
			}
			
			for (Appointment appointment : appointmentCache.values()) {
				if (log.isDebugEnabled()) { log.debug("Adding appointment: " + appointment); }
				clinicCache.get(appointment.getClinicName()).getAppointments().add(appointment);
				patientCache.get(appointment.getPatientName()).getAppointments().add(appointment);
				if (appointment.getPhysician() != null) {
					physicianCache.get(appointment.getPhysician().getName()).getAppointments().add(appointment);
				}
			}
			
		} catch (Exception e) {
			log.error("Exception in updateCache", e);
			throw e;
		}
	}

	public synchronized List<Location> getAllClinics() {

		log.info("Inside getAllClinics");

		List<Location> list = new ArrayList<Location>();

		if (clinicCache != null) {

			list = new ArrayList<Location>(clinicCache.values());

		}

		return list;
	}
	
	public Location getClinic(String name){
		
		log.info("Inside getClinic, name: " + name);
		
		if (clinicCache != null) {

			return clinicCache.get(name);

		}
		
		return null;
	}

	public synchronized List<Patient> getAllPatients() {

		log.info("Inside getAllPatients");

		List<Patient> list = new ArrayList<Patient>();

		if (patientCache != null) {

			list = new ArrayList<Patient>(patientCache.values());

		}

		return list;
	}

	public List<Patient> getPatientsForClinic(String name) {

		log.info("Inside getPatientsForClinic, name: " + name);

		if (clinicCache != null && name != null) {

			Location clinic = clinicCache.get(name);

			if (clinic != null) {
				Patient patient;
				
				for (Patient p : clinic.getPatients()) {
					
					patient = patientCache.get(p.getName());
					
					p.setAppointments(patient.getAppointments());
				}
				return clinic.getPatients();
			}
			else{
				log.info("no patients for Clinic[" + name + "]");
			}
		}

		return null;

	}

	public List<Appointment> getPatientAvailableAppointments(String name) throws Exception{
		log.info("Inside getPatientAvailableAppointments, name: " + name);

		List<Appointment> availableAppointments = new ArrayList<Appointment>();

		Patient patient = patientCache.get(name);
		
		if (patient != null){
			
			List<Encounter> encounters = schedulerEngine.generateConsultationOptions(Long.parseLong(patient.getId()), 10);
			for (Encounter encounter : encounters) {
				availableAppointments.add(DomainModelMapper.mapAppointment(encounter));
			}
		}
		else{
			log.error("Invalid patient: " + name);
			throw new Exception("Invalid patient: " + name);
		}
		
		return availableAppointments;

	}

	public boolean acceptPatientAppointment(String id) throws Exception {
		log.info("Inside acceptPatientAppointment, id: " + id);
		
		boolean success = schedulerEngine.acceptConsultationOption(Long.parseLong(id));
		
		try {
			if (success) {
				updateCache(new DomainModelMapper(schedulerEngine.getUnsolvedSolution()));
			}
		} catch (Exception e) {
			log.error("Error accepting patient appointment", e);
			throw e;
		}
		return success;
	}

	public List<Appointment> getPhysicianAppointments(String name) {
		log.info("Inside getPhysicianAppointments, name: " + name);
	
		List<Appointment> appointments = new ArrayList<Appointment>();
		AppointmentSolution solution = schedulerEngine.getLastBestSolution();
		
		if (solution == null) {
				
			log.info("Use unsolved solution");
			solution = schedulerEngine.getUnsolvedSolution();
		
		} 
		
		List<ConsultationEncounter> encounters = solution.getEncounters();
		
		for (ConsultationEncounter encounter : encounters) {
			
			if (encounter.getPhysician() != null &&
				encounter.getPhysician().getName().equals(name)) {
				
				log.info("add appointment to physician: " + name);
				appointments.add(DomainModelMapper.mapAppointment(encounter));
			}
		}
		
		return appointments;
	}

	public synchronized List<Physician> getAllPhysicians() {

		log.info("Inside getAllPhysicians");

		List<Physician> list = new ArrayList<Physician>();

		if (physicianCache != null) {

			list = new ArrayList<Physician>(physicianCache.values());

		}

		return list;
	}
	
	
	public synchronized List<Physician> getPhysiciansWithAppointments() {

		log.info("Inside getPhysiciansWithAppointments");

		List<Physician> list = new ArrayList<Physician>();

		if (physicianCache != null) {
	
			for (Physician p : physicianCache.values()) {
				
				if ((p.getAppointments() != null) && (p.getAppointments().size() > 0)) {
				
					list.add(p);
				}
			}

		}

		return list;
	}

	public synchronized Patient getPatient(String name) {
		log.info("Inside getPatient");

		if (patientCache != null) {

			return patientCache.get(name);

		}

		return null;
	}

	public synchronized Physician getPhysician(String name) {
		log.info("Inside getPhysician");

		if (physicianCache != null) {
			return physicianCache.get(name);
		}
		
		return null;
	}
	
	public SolutionResponse getNextBestSolution(){
		log.info("Inside getNextBestSolution");
		SolutionResponse response = new SolutionResponse();
		
		response.setStatus(schedulerEngine.isSolving() ? "SOLVING" : "STOPPED");
		
		AppointmentSolution solution = schedulerEngine.getLastBestSolution();
		if (solution == null) { solution = schedulerEngine.getUnsolvedSolution(); }
		
		if (solution != null) {
			response.setAttempts(schedulerEngine.getAttempts());
			response.setScore(String.valueOf(solution.getScore() != null ? solution.getScore() : ""));
		
			if (solution.getScore() == null || !solution.getScore().equals(lastBestSolution.getScore())) {
				log.info("Updating cache!");
				lastBestSolution = solution;
				try {
					updateCache(new DomainModelMapper(lastBestSolution));
				} catch (Exception ex) {
					response.setStatus("ERROR");
					response.setMessage(ex.getMessage());
				}
				response.setTreatmentCount(solution.getTreatments().size());
				int draftCount = 0;
				int acceptCount = 0;
				for (ConsultationEncounter encounter : lastBestSolution.getEncounters()) {
					if (encounter.getStatus().equals(AppointmentStatus.DRAFT)) {
						draftCount++;
					} else if (encounter.getStatus().equals(AppointmentStatus.ACCEPTED)) {
						acceptCount++;
					}
				}
				response.setAcceptedConsultCount(acceptCount);
				response.setDraftConsultCount(draftCount);
			}
		}
		return response;
	}
		
	public void generateSchedule(String maxAppointments) {
		schedulerEngine.generateScheduleByPercent(Integer.parseInt(maxAppointments));
	}
	
	public boolean terminateEarly() {
		return schedulerEngine.terminateEarly();
	}

	public boolean acceptAllDrafts() {
		boolean response = schedulerEngine.acceptAllDraftEncounters();
		try {
			updateCache(new DomainModelMapper(schedulerEngine.getUnsolvedSolution()));
		} catch (Exception ex) {
			log.error("Could not accept all appointments", ex);
			return false;
		}
		return response;
	}

}
