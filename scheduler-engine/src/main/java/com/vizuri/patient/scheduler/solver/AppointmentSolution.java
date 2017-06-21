package com.vizuri.patient.scheduler.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import com.vizuri.patient.scheduler.model.AbstractPersistable;
import com.vizuri.patient.scheduler.model.Appointment;
import com.vizuri.patient.scheduler.model.Clinic;
import com.vizuri.patient.scheduler.model.Day;
import com.vizuri.patient.scheduler.model.Patient;
import com.vizuri.patient.scheduler.model.Physician;
import com.vizuri.patient.scheduler.model.TimeGrain;

@PlanningSolution
public class AppointmentSolution extends AbstractPersistable implements Solution<HardMediumSoftScore> {
	private static final long serialVersionUID = 7677615078150720191L;
	
	public AppointmentSolution() {
		super();
	}
	
	public AppointmentSolution(Long id) {
		super(id);
	}
	
	// Planning facts
	private Day schedulingDay;
	private List<Appointment> appointments = new ArrayList<Appointment>();
	private List<TimeGrain> appointmentTimes = new ArrayList<TimeGrain>();
	private List<Physician> physicians = new ArrayList<Physician>();
	private List<TreatmentEncounter> treatments = new ArrayList<TreatmentEncounter>();
	
	// Don't really need these, just convenience
	private List<Clinic> clinics = new ArrayList<Clinic>();
	private List<Patient> patients = new ArrayList<Patient>();
	private List<Day> days = new ArrayList<Day>();
	
	// Planning Entity/score
	private List<ConsultationEncounter> encounters = new ArrayList<ConsultationEncounter>();
	private HardMediumSoftScore score;
	
	public Day getSchedulingDay() {
		return schedulingDay;
	}

	public void setSchedulingDay(Day schedulingDay) {
		this.schedulingDay = schedulingDay;
	}

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}

	@ValueRangeProvider(id = "appointmentTimeRange")
	public List<TimeGrain> getAppointmentTimes() {
		return appointmentTimes;
	}

	public void setAppointmentTimes(List<TimeGrain> appointmentTimes) {
		this.appointmentTimes = appointmentTimes;
	}

	@ValueRangeProvider(id = "physicianRange")
	public List<Physician> getPhysicians() {
		return physicians;
	}

	public void setPhysicians(List<Physician> physicians) {
		this.physicians = physicians;
	}

	@PlanningEntityCollectionProperty
	public List<ConsultationEncounter> getEncounters() {
		return encounters;
	}

	public void setEncounters(List<ConsultationEncounter> encounters) {
		this.encounters = encounters;
	}

	public List<TreatmentEncounter> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<TreatmentEncounter> treatments) {
		this.treatments = treatments;
	}

	// Convenience
	public List<Clinic> getClinics() {
		return clinics;
	}

	public void setClinics(List<Clinic> clinics) {
		this.clinics = clinics;
	}
	
	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}
	
	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
	}

	// ************************************************************************
	// Solver Methods
	// ************************************************************************
	public Collection<? extends Object> getProblemFacts() {
		List<Object> facts = new ArrayList<Object>();
		facts.add(schedulingDay);
		facts.addAll(treatments);
		facts.addAll(appointmentTimes);
		//encounters automatically added
		return facts;
	}

	public HardMediumSoftScore getScore() {
		return score;
	}

	public void setScore(HardMediumSoftScore score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "AppointmentSolution counts ["
				+ ", schedulingDay=" + schedulingDay
				+ ", appointments=" + appointments.size() 
				+ ", appointmentTimes="+ appointmentTimes.size() 
				+ ", physicians=" + physicians.size() 
				+ ", treatments=" + treatments.size() 
				+ ", clinics=" + clinics.size() 
				+ ", patients=" + patients.size() 
				+ ", encounters=" + encounters.size() 
				+ ", patients=" + patients.size() 
				+ ", days=" + days.size() 
				+ ", score=" + score + "]";
	}
	
	

}
