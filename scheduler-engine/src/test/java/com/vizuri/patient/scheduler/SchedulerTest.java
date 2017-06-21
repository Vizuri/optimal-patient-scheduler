package com.vizuri.patient.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.termination.TerminationCompositionStyle;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.scheduler.model.AppointmentType;
import com.vizuri.patient.scheduler.model.Day;
import com.vizuri.patient.scheduler.model.Patient;
import com.vizuri.patient.scheduler.model.Physician;
import com.vizuri.patient.scheduler.model.TreatmentDays;
import com.vizuri.patient.scheduler.solver.AppointmentSolution;
import com.vizuri.patient.scheduler.solver.ConsultationEncounter;
import com.vizuri.patient.scheduler.solver.Encounter;
import com.vizuri.patient.scheduler.solver.TreatmentEncounter;
import com.vizuri.patient.scheduler.util.DataFactory;

public class SchedulerTest {
	private static final transient Logger logger = LoggerFactory.getLogger(SchedulerTest.class);

	public static final String SOLVER_CONFIG = "com/vizuri/patient/scheduler/consultationSchedulingSolverConfig.xml";
	private static SolverFactory<AppointmentSolution> solverFactory;
	
	@Before
	public void reset() {
		DataFactory.reset();
		solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
	}
	
	@Test
	public void testScheduler() {
		logger.info("Test!");
	}

	@Test
	public void testSolverNoMoves() {
		Solver<AppointmentSolution> solver = createSolver();
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildTestSolutionA(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		solver.solve(solution);
		AppointmentSolution bestSolution = solver.getBestSolution();
		logger.info("\nSolved Physician Scheduling Problem :\nBest score: " + bestSolution.getScore());
		assertEquals("Expected no hard score", 0, bestSolution.getScore().getHardScore());
		assertEquals("Expected no soft score", 0, bestSolution.getScore().getSoftScore());
	}

	@Test
	public void testSolverSingleEncounter() {
		Solver<AppointmentSolution> solver = createSolver();
		
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildTestSolutionA(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		
		Patient patient = solution.getPatients().get(0);
		DataFactory.generateSingleConsult(solution, patient);
		solver.solve(solution);
		AppointmentSolution bestSolution = solver.getBestSolution();
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
		
		assertEquals("Expecting single consultation", 1, consultCount);
		
	}
	
	@Test
	public void testSolverSingleEncounterTop10() {
		Solver<AppointmentSolution> solver = createSolver();
		
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildTestSolutionA(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		
		Patient patient = solution.getPatients().get(0);
		for (int i = 0; i < 10; i++) {
			DataFactory.generateSingleConsult(solution, patient);
		}
		
		Patient patientb = solution.getPatients().get(10);
		for (int i = 0; i < 10; i++) {
			DataFactory.generateSingleConsult(solution, patientb);
		}
		
		logger.info("Patient A: " + patient);
		logger.info("Patient B: " + patientb);
		
		solver.solve(solution);
		AppointmentSolution bestSolution = solver.getBestSolution();
		logger.info("\nSolved Physician Scheduling Problem :\nBest score: " + bestSolution.getScore());
		
		int consultCount = 0;
		
		for (TreatmentEncounter treatment : bestSolution.getTreatments()) {
			if (treatment.getAppointment().getPatient().equals(patient)) {
				logger.info("Treatment schedule:: " + treatment);
			}
		}
		
		List<ConsultationEncounter> encounters = new ArrayList<ConsultationEncounter>();
		encounters.addAll(bestSolution.getEncounters());
		Collections.sort(encounters, Encounter.EncounterComparator);
		for (ConsultationEncounter encounter : encounters) {
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT) && 
					encounter.getAppointment().getPatient().equals(patient)) {
				consultCount++;
				logger.info("(A) Consult details: " + encounter);
			} 
		}
		
		for (ConsultationEncounter encounter : encounters) {
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT) && 
					encounter.getAppointment().getPatient().equals(patientb)) {
				consultCount++;
				logger.info("(B) Consult details: " + encounter);
			} 
		}
		
		assertEquals("Expecting single consultation", 20, consultCount);
		
	}
	
	@Test
	public void testSolverSinglePhysician() {
		Solver<AppointmentSolution> solver = createSolver();
		
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildSinglePhysicianSolution(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		
		Patient patient = solution.getPatients().get(0); // 2 appointments, same patient
		DataFactory.generateSingleConsult(solution, patient);
		DataFactory.generateSingleConsult(solution, patient);
		solver.solve(solution);
		AppointmentSolution bestSolution = solver.getBestSolution();
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
		
		assertEquals("Expecting 2 consultation", 2, consultCount);
		
	}
	
	@Test
	public void testSolver1Physician2Clinics() {
		Solver<AppointmentSolution> solver = createSolver();
		
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.build1Physician2ClinicsSolution(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		
		Patient patientA = solution.getPatients().get(0); // 2 appointments, same patient
		DataFactory.generateSingleConsult(solution, patientA);
		
		Patient patientB = null;
		for (Patient patient : solution.getPatients()) {
			if (!patient.getClinic().equals(patientA.getClinic()) && patient.getTreatmentDays().equals(patientA.getTreatmentDays())) {
				patientB = patient;
				DataFactory.generateSingleConsult(solution, patient);
				break;
			}
		}
		solver.solve(solution);
		AppointmentSolution bestSolution = solver.getBestSolution();
		logger.info("\nSolved Physician Scheduling Problem :\nBest score: " + bestSolution.getScore());
		
		int consultCount = 0;
		
		for (TreatmentEncounter treatment : bestSolution.getTreatments()) {
			if (treatment.getAppointment().getPatient().equals(patientA) || treatment.getAppointment().getPatient().equals(patientB)) {
				logger.info("Treatment schedule:: " + treatment);
			}
		}
		
		for (ConsultationEncounter encounter : bestSolution.getEncounters()) {
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT)) {
				consultCount++;
				logger.info("Consult details: " + encounter);
			} 
		}
		
		assertEquals("Expecting 2 consultation", 2, consultCount);
		
		solution.setEncounters(new ArrayList<ConsultationEncounter> ());
		DataFactory.generateSingleConsult(solution, patientA);
		patientB = null;
		for (Patient patient : solution.getPatients()) {
			if (patient.getClinic().equals(patientA.getClinic()) && patient.getTreatmentDays().equals(patientA.getTreatmentDays()) && patient.getTreatmentShift() != patientA.getTreatmentShift()) { 
				patientB = patient;
				DataFactory.generateSingleConsult(solution, patient);
				break;
			}
		}
		solver.solve(solution);
		bestSolution = solver.getBestSolution();
		logger.info("\nSolved Physician Scheduling Problem :\nBest score: " + bestSolution.getScore());
		
		consultCount = 0;
		
		for (TreatmentEncounter treatment : bestSolution.getTreatments()) {
			if (treatment.getAppointment().getPatient().equals(patientA) || treatment.getAppointment().getPatient().equals(patientB)) {
				logger.info("Treatment schedule:: " + treatment);
			}
		}
		
		for (ConsultationEncounter encounter : bestSolution.getEncounters()) {
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT)) {
				consultCount++;
				logger.info("Consult details: " + encounter);
			} 
		}
		
		assertEquals("Expecting 2 consultation", 2, consultCount);
		
	}
	
	@Test
	public void testSolverManyPhysician1Clinic() {
		/*TerminationConfig terminationConfig = new TerminationConfig();
		terminationConfig.setTerminationCompositionStyle(TerminationCompositionStyle.OR);
		terminationConfig.setBestScoreLimit("0hard/0medium/-50soft");
		terminationConfig.setMinutesSpentLimit(1L);
		terminationConfig.setUnimprovedSecondsSpentLimit(30L);
		solverFactory.getSolverConfig().setTerminationConfig(terminationConfig);*/
		Solver<AppointmentSolution> solver = createSolver();
		
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildManyPhysician1ClinicSolution(solution, LocalDate.of(2017, Month.MARCH, 1), 4);
		
		int maxAppointments = 5;
		int appointmentCount = 0;
		for (Patient patient : solution.getPatients()) {
			if (patient.getTreatmentDays().equals(TreatmentDays.MWF) && appointmentCount < maxAppointments) {
				DataFactory.generateSingleConsult(solution, patient);
				appointmentCount++;
			}
		}
		solver.solve(solution);
		AppointmentSolution bestSolution = solver.getBestSolution();
		logger.info("\nSolved Physician Scheduling Problem :\nBest score: " + bestSolution.getScore());
		
		// Expecting no days with more than one physician
		Map<Day, Physician> physicianTable = new HashMap<Day,Physician>();
		Map<String, Set<Day>> shiftTable = new HashMap<String, Set<Day>>();
		for (ConsultationEncounter encounter : bestSolution.getEncounters()) {
			logger.info("Consultation: " + encounter);
			
			if (encounter.getStartingTime() != null) {
				Day currentDay = encounter.getStartingTime().getDay();
				Physician currentPhysician = encounter.getPhysician();
				String shiftKey = currentPhysician.getId() + "-" +currentDay.getWeekOfYear();
				
				if (physicianTable.get(currentDay) == null) {
					physicianTable.put(currentDay, currentPhysician);
				} else {
					assertEquals("Expecting same physician assigned each day", currentPhysician, physicianTable.get(currentDay));
				}
				
				if (shiftTable.get(shiftKey) == null) {
					Set<Day> days = new HashSet<Day>();
					days.add(currentDay);
					shiftTable.put(shiftKey, days);
				} else {
					shiftTable.get(shiftKey).add(currentDay);
					assertTrue("Expecting no more than 2 shifts per week per physician", 2 >= shiftTable.get(shiftKey).size());
				}
			} else {
				logger.error("Unscheduled consultation: " + encounter);
			}
		}
		
		
	}
	
	protected Solver<AppointmentSolution> createSolver() {
		
		Solver<AppointmentSolution> solver = solverFactory.buildSolver();
		solver.addEventListener(new SolverEventListener<AppointmentSolution>() {
			public void bestSolutionChanged(BestSolutionChangedEvent<AppointmentSolution> event) {
				//if (event.isNewBestSolutionInitialized()) { //&& event.getNewBestSolution().getScore().isFeasible()) {
					AppointmentSolution bestSolution = (AppointmentSolution) event.getNewBestSolution();
	
					if (logger.isDebugEnabled()) {
						logger.debug("Current best score: " + bestSolution.getScore());
					}
				//}
			}
		});
		return solver;
	}
}
