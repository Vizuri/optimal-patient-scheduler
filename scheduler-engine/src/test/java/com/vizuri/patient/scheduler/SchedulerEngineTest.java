package com.vizuri.patient.scheduler;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.scheduler.model.Appointment;
import com.vizuri.patient.scheduler.model.AppointmentType;
import com.vizuri.patient.scheduler.model.Patient;
import com.vizuri.patient.scheduler.model.TimeGrain;
import com.vizuri.patient.scheduler.solver.AppointmentSolution;
import com.vizuri.patient.scheduler.solver.ConsultationEncounter;
import com.vizuri.patient.scheduler.solver.Encounter;
import com.vizuri.patient.scheduler.solver.SchedulerEngine;
import com.vizuri.patient.scheduler.solver.SchedulerEngineImpl;
import com.vizuri.patient.scheduler.solver.TreatmentEncounter;
import com.vizuri.patient.scheduler.util.DataFactory;
import com.vizuri.patient.scheduler.util.PlannerConfig;

public class SchedulerEngineTest {
	private final static transient Logger logger = LoggerFactory.getLogger(SchedulerEngineTest.class);
	
	private SchedulerEngine engine;
	
	@Before
	public void reset() {
		DataFactory.reset();
		engine = new SchedulerEngineImpl();
	}
	
	@Test
	public void testEngineTop10() {
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildTestSolutionA(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		
		Patient patient = solution.getPatients().get(0);
		
		logger.info("Patient A: " + patient);
		engine.setUnsolvedSolution(solution);
		List<Encounter> encounters = engine.generateConsultationOptions(patient, 10);
		
		AppointmentSolution bestSolution = engine.getLastBestSolution();
		logger.info("\nSolved Physician Scheduling Problem :\nBest score: " + bestSolution.getScore());
		
		int consultCount = 0;
		
		for (TreatmentEncounter treatment : bestSolution.getTreatments()) {
			if (treatment.getAppointment().getPatient().equals(patient)) {
				logger.info("Treatment schedule:: " + treatment);
			}
		}
		
		for (ConsultationEncounter encounter : bestSolution.getEncounters()) {
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT)) {
				consultCount++;
				logger.info("Consult details: " + encounter);
			} 
		}
		
		for (Encounter encounter : encounters) {
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT)) {
				consultCount++;
				logger.info("Consult details: " + encounter);
			} 
		}
		
		assertEquals("Expecting single consultation", 20, consultCount);
	}
	
	@Test
	public void testEngineScheduleRandom() {
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildTestSolutionA(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		
		Patient patient = solution.getPatients().get(0);
		
		logger.info("Patient A: " + patient);
		engine.setUnsolvedSolution(solution);
		engine.generateScheduleByCount(15);
		
		do {
			// sleep 1 second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException i) {
				//gulp
			}
		} while (engine.isSolving());
		
		logger.info("Last best score: " + engine.getLastBestSolution().getScore());
		
		for (ConsultationEncounter encounter : engine.getLastBestSolution().getEncounters()) {
			logger.info(" DRAFT ? >>> " + encounter);
		}
		
		engine.acceptAllDraftEncounters();
		
		for (ConsultationEncounter encounter : engine.getUnsolvedSolution().getEncounters()) {
			logger.info(" ACCEPTED ? >>> " + encounter);
		}
		
	}
	@Test
	public void testOverlap() {
		PlannerConfig config = getUIConfig();
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.seedPlannerSolution(solution, config, LocalDate.of(2017, Month.MARCH, 13));
		
		TimeGrain tenThirty = null;
		TimeGrain twelveNoon = null;
		for (TimeGrain grain : solution.getAppointmentTimes()) {
			if (grain.getDay().getDayOfYear() == 73) {
				if (grain.getStartingMinuteOfDay() == 630) {
					tenThirty = grain;
				} else if (grain.getStartingMinuteOfDay() == 720) {
					twelveNoon = grain;
				}
			}
		}
		
		Encounter encTen = new ConsultationEncounter();
		encTen.setStartingTime(tenThirty);
		Appointment tenApp = new Appointment();
		tenApp.setDurationInGrains(3);
		encTen.setAppointment(tenApp);
		Encounter encTwelve = new ConsultationEncounter();
		encTwelve.setStartingTime(twelveNoon);
		Appointment twelveApp = new Appointment();
		twelveApp.setDurationInGrains(16);
		encTwelve.setAppointment(twelveApp);
		logger.info("Ten thirty: " + tenThirty + ", " + encTen.calculateGapGrains(encTwelve));
		logger.info("Twelve noon: " + twelveNoon +", " + encTwelve.calculateGapGrains(encTen));
	}
	
	@Test
	public void testEngineTop10WithConfig() {
		PlannerConfig config = getUIConfig();
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.seedPlannerSolution(solution, config, LocalDate.of(2017, Month.MARCH, 13));
		Patient patient = solution.getPatients().get(0);
		
		logger.info("Patient A: " + patient);
		engine.setUnsolvedSolution(solution);
		List<Encounter> encounters = engine.generateConsultationOptions(patient, 10);
		
		AppointmentSolution bestSolution = engine.getLastBestSolution();
		logger.info("\nSolved Physician Scheduling Problem :\nBest score: " + bestSolution.getScore());
		
		int consultCount = 0;
		
		/*for (ConsultationEncounter encounter : bestSolution.getEncounters()) {
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT)) {
				consultCount++;
				logger.info("Consult details: " + encounter);
			} 
		}*/
		
		for (Encounter encounter : encounters) {
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT)) {
				consultCount++;
				logger.info("Consult details: " + encounter);
			} 
		}
		
		assertEquals("Expecting single consultation", 10, consultCount);
	}
	
	@Test
	public void testSchedule30OneClinic() {
		PlannerConfig config = getUIConfig();
		
		config.setMaxClinics(1);
		config.setMaxPatientAppointments(10);
		config.setMaxPatientsPerClinic(30);
		config.setMaxRooms(3);
		config.setScheduleWindow(3);
		config.setMaxPhysicians(5);
		
		runEngine(config, LocalDate.now());
	}
	
	@Test
	public void testSchedule20OneClinic() {
		testEngineSchedule20Config(1);
	}
	
	@Test
	public void testSchedule20TwoClinics() {
		testEngineSchedule20Config(2);
	}
	
	//@Test
	public void testSchedule20ThreeClinics() {
		testEngineSchedule20Config(3);
	}
	
	//@Test
	public void testSchedule20FourClinics() {
		testEngineSchedule20Config(4);
	}
	
	public void testEngineSchedule20Config(int clinicCount) {
		PlannerConfig config = getUIConfig();
		config.setMaxClinics(clinicCount);
		config.setMaxPhysicians(14 * clinicCount);
		config.setMaxPatientAppointments(20 * clinicCount);
		runEngine(config, LocalDate.of(2017, Month.MARCH, 13));
	}
	
	private void runEngine(PlannerConfig config, LocalDate scheduleDate) {
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.seedPlannerSolution(solution, config, scheduleDate);
		engine.setUnsolvedSolution(solution);
		engine.generateScheduleByCount(config.getMaxPatientAppointments());
		do {
			// sleep 1 second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException i) {
				//gulp
			}
		} while (engine.isSolving());
		
		logger.info("Last best score: " + engine.getLastBestSolution().getScore());
		logger.info("SchedulingDay: " + engine.getLastBestSolution().getSchedulingDay());
		int unassignedPhysicianCount = 0;
		for (ConsultationEncounter encounter : engine.getLastBestSolution().getEncounters()) {
			logger.info(" DRAFT ? >>> " + encounter);
			if (encounter.getPhysician() == null) {
				unassignedPhysicianCount++;
			}
		}
		logger.info("Unassigned physician count: " + unassignedPhysicianCount);
	}

	private PlannerConfig getUIConfig() {
		PlannerConfig config = new PlannerConfig();
		config.setMaxClinics(1);
		config.setMaxPhysicians(10);
		config.setMaxRooms(3);
		config.setMaxPatientsPerClinic(30);
		config.setScheduleWindow(2);
		return config;
	}
}
