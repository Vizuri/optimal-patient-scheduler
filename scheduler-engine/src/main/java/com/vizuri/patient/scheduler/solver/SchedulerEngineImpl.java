package com.vizuri.patient.scheduler.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.termination.TerminationCompositionStyle;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.scheduler.model.AppointmentStatus;
import com.vizuri.patient.scheduler.model.AppointmentType;
import com.vizuri.patient.scheduler.model.Patient;
import com.vizuri.patient.scheduler.util.DataFactory;

public class SchedulerEngineImpl implements SchedulerEngine {
	private static final transient Logger logger = LoggerFactory.getLogger(SchedulerEngineImpl.class);

	private static final String SOLVER_CONFIG = "com/vizuri/patient/scheduler/consultationSchedulingSolverConfig.xml";
	private static ExecutorService solvingExecutor = Executors.newFixedThreadPool(4);
	private static Solver<AppointmentSolution> solver;
	private static SolverFactory<AppointmentSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
	
	private AppointmentSolution unsolvedSolution;
	private AppointmentSolution lastBestSolution;
	private int attempts;
	
	public void init() {
		solver = solverFactory.buildSolver();
		unsolvedSolution.setScore(null);
		lastBestSolution = null;
		attempts = 0;
		solver.addEventListener(new SolverEventListener<AppointmentSolution>() {
			public void bestSolutionChanged(BestSolutionChangedEvent<AppointmentSolution> event) {
				lastBestSolution = (AppointmentSolution) event.getNewBestSolution();
				attempts++;
				if (logger.isDebugEnabled()) {
					logger.debug("Current best score: " + lastBestSolution.getScore());
				}
			}
		});
	}
	
	public List<Encounter> generateConsultationOptions(Long patientId, int optionCount) {
		for (Patient patient : unsolvedSolution.getPatients()) {
			if (patient.getId().equals(patientId)) {
				logger.info("generate appointments for patient[" + patientId + "]");
				return generateConsultationOptions(patient, optionCount);
			}
		}
		throw new RuntimeException("Patient not found for scheduling...");
	}
	
	public List<Encounter> generateConsultationOptions(Patient patient, int optionCount) {
		
		if (!unsolvedSolution.getPatients().contains(patient)) {
			throw new RuntimeException("Patient not found for scheduling...");
		}
		
		if (isSolving()) {
			throw new RuntimeException("generateConsultationOptions requested while solver is solving");
		}
		
		removeDraftEncounters();
		
		for (int i = 0; i < optionCount; i++) {
			DataFactory.generateSingleConsult(unsolvedSolution, patient);
		}
		
		logger.info("Starting to solve top " + optionCount + " options for patient: " + patient);
		updateTerminationConstraints(String.format("0hard/0medium/-%dsoft", (patient.getTreatmentShift() == 2 ? 150 : 200)), 1, 3);
		init();
		solver.solve(unsolvedSolution);
		lastBestSolution = solver.getBestSolution();
		logger.info("\nSolved Physician Scheduling Problem :\nBest score: " + lastBestSolution.getScore());
		
		List<Encounter> encounterOptions = new ArrayList<Encounter>();
		for (ConsultationEncounter encounter : lastBestSolution.getEncounters()) {
			
			if (encounter.getAppointment().getType().equals(AppointmentType.CONSULT) &&
				encounter.getStatus().equals(AppointmentStatus.DRAFT) &&
				encounter.getAppointment().getPatient().getId().equals(patient.getId())) {
				logger.info("add draft appointment to patient: " + patient);
				encounterOptions.add(encounter);
			} 
		}
		
		if (encounterOptions.size() > 1) {
			Collections.sort(encounterOptions, Encounter.EncounterComparator);
		}
		
		logger.info("Found: " + encounterOptions.size() + " options");
		return encounterOptions;
	}
	
	public void removeDraftEncounters() {
		List<ConsultationEncounter> keepers = new ArrayList<ConsultationEncounter>();
		for (ConsultationEncounter encounter : unsolvedSolution.getEncounters()) {
			if (!encounter.getStatus().equals(AppointmentStatus.DRAFT)) {
				keepers.add(encounter);
			}
		}
		unsolvedSolution.setEncounters(keepers);
	}
	
	public boolean acceptAllDraftEncounters() {
		try {
			removeDraftEncounters();
			for (ConsultationEncounter encounter : lastBestSolution.getEncounters()) {
				if (encounter.getStatus().equals(AppointmentStatus.DRAFT)) {
					encounter.setStatus(AppointmentStatus.ACCEPTED);
					unsolvedSolution.getEncounters().add(encounter);
				}
			}
			lastBestSolution = null;
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	public boolean acceptConsultationOption(Long encounterId) {
		boolean foundAppointment = false;
		
		for (ConsultationEncounter encounter : lastBestSolution.getEncounters()) {
			
			if (encounter.getId().equals(encounterId)) {
				
				logger.info("accept encounter[" + encounter.getId() + "]");
				encounter.setStatus(AppointmentStatus.ACCEPTED);
				removeDraftEncounters();
				unsolvedSolution.getEncounters().add(encounter);
				lastBestSolution = null;
				foundAppointment = true;
			}
		}
		
		return foundAppointment;
	}

	public boolean generateScheduleByPercent(int appointmentPercent) {
		float actualPercent = (appointmentPercent)/100f;
		int actualCount = unsolvedSolution.getPatients().size();
		Float computedCount = actualPercent * actualCount;
		logger.info(String.format("Generating schedule by percent %d, (%f) actual count: %d, appt count: %f", appointmentPercent, actualPercent, actualCount, computedCount));
		return generateScheduleByCount(computedCount.intValue());
	}
	
	public boolean generateScheduleByCount(int maxAppointments) {
		logger.info("Enter generateScheduleByCount for maxAppointments: " + maxAppointments);
		if (isSolving()) {
			throw new RuntimeException("generateConsultationOptions requested while solver is solving");
		}
		
		removeDraftEncounters();
		Random rand = new Random();
		int patientCount = unsolvedSolution.getPatients().size();
		Set<Patient> assignedPatients = new HashSet<Patient>();
		do {
			Patient nextPatient = unsolvedSolution.getPatients().get(rand.nextInt(patientCount));
			if (!assignedPatients.contains(nextPatient)) {
				DataFactory.generateSingleConsult(unsolvedSolution, nextPatient);
				assignedPatients.add(nextPatient);
			}
		} while (assignedPatients.size() < maxAppointments);
		
		updateTerminationConstraints(String.format("0hard/0medium/-%dsoft", (100 * unsolvedSolution.getClinics().size())), 5, 20);
		init();
		
		solvingExecutor.submit(new Runnable() {
            public void run() {
                solver.solve(unsolvedSolution);
            }
        });
		
		return true;
	}

	// Getters
	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	
	public AppointmentSolution getUnsolvedSolution() {
		return unsolvedSolution;
	}

	public void setUnsolvedSolution(AppointmentSolution unsolvedSolution) {
		if (solver != null && solver.isSolving()) {
			throw new RuntimeException("Attempting to update unsolved solution while solver is solving");
		}
		this.unsolvedSolution = unsolvedSolution;
	}

	public AppointmentSolution getLastBestSolution() {
		return lastBestSolution;
	}

	public void setLastBestSolution(AppointmentSolution lastBestSolution) {
		this.lastBestSolution = lastBestSolution;
	}
	
	
	// Helpers
	public boolean isSolving() {
		return solver != null && solver.isSolving();
	}
	
	public boolean terminateEarly() {
		if (solver != null && solver.isSolving()) {
			return solver.terminateEarly();
		}
		return false;
	}
	
	public void updateTerminationConstraints(String bestScoreLimit, long minutesSpentLimit, long unimprovedSecondsSpentLimit) {
		TerminationConfig terminationConfig = new TerminationConfig();
		terminationConfig.setTerminationCompositionStyle(TerminationCompositionStyle.OR);
		terminationConfig.setBestScoreLimit(bestScoreLimit);
		terminationConfig.setMinutesSpentLimit(minutesSpentLimit);
		terminationConfig.setUnimprovedSecondsSpentLimit(unimprovedSecondsSpentLimit);
		solverFactory.getSolverConfig().setTerminationConfig(terminationConfig);
	}
	
	public void resetSolverFactory() {
		solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
	}
	
}
