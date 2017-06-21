package com.vizuri.patient.scheduler.solver;

import java.util.List;

import com.vizuri.patient.scheduler.model.Patient;

public interface SchedulerEngine {
	List<Encounter> generateConsultationOptions(Patient patient, int optionCount);
	List<Encounter> generateConsultationOptions(Long patientId, int optionCount);
	boolean acceptConsultationOption(Long encounterId);
	boolean generateScheduleByCount(int maxAppointments);
	boolean generateScheduleByPercent(int appointmentPercent);
	
	AppointmentSolution getLastBestSolution();
	boolean acceptAllDraftEncounters();
	
	void setUnsolvedSolution(AppointmentSolution solution);
	AppointmentSolution getUnsolvedSolution();
	
	boolean isSolving();
	int getAttempts();
	
	void updateTerminationConstraints(String bestScoreLimit, long minutesSpentLimit, long unimprovedSecondsLimit);
	void resetSolverFactory();
	boolean terminateEarly();
}
