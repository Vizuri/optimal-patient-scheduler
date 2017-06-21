package com.vizuri.patient.scheduler.util;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.scheduler.model.Appointment;
import com.vizuri.patient.scheduler.model.AppointmentStatus;
import com.vizuri.patient.scheduler.model.AppointmentType;
import com.vizuri.patient.scheduler.model.Clinic;
import com.vizuri.patient.scheduler.model.ConsultingRoom;
import com.vizuri.patient.scheduler.model.Day;
import com.vizuri.patient.scheduler.model.Patient;
import com.vizuri.patient.scheduler.model.Physician;
import com.vizuri.patient.scheduler.model.TimeGrain;
import com.vizuri.patient.scheduler.model.TreatmentDays;
import com.vizuri.patient.scheduler.solver.AppointmentSolution;
import com.vizuri.patient.scheduler.solver.ConsultationEncounter;
import com.vizuri.patient.scheduler.solver.TreatmentEncounter;

public class DataFactory {
	private static final transient Logger logger = LoggerFactory.getLogger(DataFactory.class);
	
	private static long identifier = 1;
	public static int clinicCount = 0;
	private static int timeGrainSequence = 0;
	
	private static final int OPEN_MINUTE_OF_DAY = 480;
	private static final int CLOSING_MINUTE_OF_DAY = 1080;
	private static final List<DayOfWeek> OPERATING_DAYS = Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY);
	private static final List<DayOfWeek> MWF_DAYS = Arrays.asList(MONDAY, WEDNESDAY, FRIDAY);
	private static final List<DayOfWeek> TRS_DAYS = Arrays.asList(TUESDAY, THURSDAY, SATURDAY);
	private static final int TREATMENT_DURATION_GRAINS = 16;
	private static final int CONSULT_DURATION_GRAINS = 3;
	
	private static final ConsultingRoom treatmentRoomStandin = new ConsultingRoom(-1l, "Treatment Room", "tr");
	private static final Physician treatmentTechnicianStandin = new Physician(-2l, "Treatment Technician");
	
	public static void generateClinic(AppointmentSolution solution, int roomCount, int patientCount) {
		
		logger.debug(String.format("Generating clinic (%d rooms, %d patients)", roomCount, patientCount));
		
		char roomShortCode = 'A';
		
		String clinicName = String.format("Clinic %d", ++clinicCount);
		String label = null;

		
		List<ConsultingRoom> rooms = new ArrayList<ConsultingRoom>(roomCount);
		for (int i = 0; i < roomCount; i++) {
			
			label = String.valueOf(roomShortCode++);
			// need to have generic names for rooms, like a,b,c
			rooms.add(new ConsultingRoom(nextId(), clinicName + " - Room " + label , label.toLowerCase()));
		}
		
		Clinic clinic = new Clinic(nextId(), clinicName, rooms);
		//solution.getRooms().addAll(rooms);
		solution.getClinics().add(clinic);
		
		boolean dayFlip = false;
		for (int i = 0; i < patientCount; i++) {
			Patient patient = new Patient(nextId(), String.format("Patient %d : %d", clinic.getId(), i + 1), clinic);
			
			clinic.addPatient(patient);// NEED THIS FOR THE UI
			
			patient.setTreatmentDays(dayFlip ? TreatmentDays.MWF : TreatmentDays.TRS);
			
			// creates 3 treatments per day 8-12, 12-4, 4-8
			patient.setTreatmentShift( ( i % 3) + 1) ;
			
			if (patient.getTreatmentShift() == 3) {
				dayFlip = !dayFlip;
			}

			solution.getPatients().add(patient);
		}
		
	}
	
	public static void generatePhysicians(AppointmentSolution solution, int physicianCount) {
		for (int i = 0; i < physicianCount; i++) {
			long id = nextId();
			solution.getPhysicians().add(new Physician(id, "Physician " + id));
		}
	}
	
	public static void generateTreatments(AppointmentSolution solution, final LocalDate startDate, int scheduleWindowWeeks) {
		List<Appointment> appointments = new ArrayList<Appointment>();
		
		for (Patient patient : solution.getPatients()) {
			appointments.add(new Appointment(nextId(), TREATMENT_DURATION_GRAINS, patient, patient.getClinic(), "Regular treatment", AppointmentType.TREATMENT));
		}
		
		int weekCount = 0;
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
		int lastWeek = startDate.get(weekFields.weekOfWeekBasedYear());
		LocalDate currentDate = startDate;
		while (weekCount < scheduleWindowWeeks) {
			Day day = new Day(nextId(), currentDate);
			
			if (solution.getSchedulingDay() == null) {
				solution.setSchedulingDay(day);
			}
			
			if (OPERATING_DAYS.contains(day.getDayOfWeek())) {
				solution.getDays().add(day);
				Map<Integer,TimeGrain> shiftStarts = new HashMap<Integer,TimeGrain>();
				//Seed TimeGrains
				for (int i = OPEN_MINUTE_OF_DAY; i < CLOSING_MINUTE_OF_DAY; i += TimeGrain.GRAIN_LENGTH_IN_MINUTES) {
					TimeGrain timeGrain = new TimeGrain(nextId(), ++timeGrainSequence, day, i);
					solution.getAppointmentTimes().add(timeGrain);
					
					switch(timeGrain.getStartingMinuteOfDay()) {
					case 480:	// 8am
					case 720:	// 12pm
					case 960:	// 4pm
						shiftStarts.put(timeGrain.getStartingMinuteOfDay(), timeGrain);
					}
				}
				
				for (Appointment appointment : appointments) {
					TreatmentDays td = appointment.getPatient().getTreatmentDays();
					if ( (td.equals(TreatmentDays.MWF) && MWF_DAYS.contains(day.getDayOfWeek()) ) || 
						 (td.equals(TreatmentDays.TRS) && TRS_DAYS.contains(day.getDayOfWeek()) )) {
						
						int startingTime = 480 + ( 240 * ( appointment.getPatient().getTreatmentShift() - 1));
						//logger.info("Patient has starting time of: " + startingTime + "; detail: " + appointment.getPatient());
						TreatmentEncounter treatment = new TreatmentEncounter(nextId(), appointment, AppointmentStatus.ACCEPTED);
						treatment.setTreatmentRoom(treatmentRoomStandin);
						treatment.setTechnician(treatmentTechnicianStandin);
						treatment.setStartingTime(shiftStarts.get(startingTime));
						solution.getTreatments().add(treatment);
					}
				}
			}
			currentDate = currentDate.plusDays(1);
			int currentWeek = currentDate.get(weekFields.weekOfWeekBasedYear());
			if (currentWeek != lastWeek) {
				weekCount++;
				lastWeek = currentWeek;
			}
		}
		
		solution.getAppointments().addAll(appointments);
	}
	
	public static ConsultationEncounter generateSingleConsult(AppointmentSolution solution, Patient patient) {
		Appointment appointment = new Appointment(nextId(), CONSULT_DURATION_GRAINS, patient, patient.getClinic(), "Consultation", AppointmentType.CONSULT );
		solution.getAppointments().add(appointment);
		ConsultationEncounter encounter = new ConsultationEncounter(nextId(), appointment, AppointmentStatus.DRAFT);
		
		// pre-fill
		encounter.setConsultingRoom(appointment.getPatient().getClinic().getRooms().get(0));
		//encounter.setPhysician(solution.getPhysicians().get(0));
		//encounter.setPhysician(solution.getPhysicians().get(rand.nextInt(solution.getPhysicians().size() - 1)));
		
		encounter.setStartingTime(solution.getAppointmentTimes().get(0));
		//encounter.setStartingTime(solution.getAppointmentTimes().get(rand.nextInt(solution.getAppointmentTimes().size() - 1)));
		
		solution.getEncounters().add(encounter);
		
		return encounter;
	}
	
	public static synchronized long nextId() {
		return identifier++;
	}
	
	public static void buildTestSolutionA(AppointmentSolution solution, LocalDate startDate, int scheduleWindowWeeks) {
		generateClinic(solution, 3, 100);
		generatePhysicians(solution, 20);
		generateTreatments(solution, startDate, scheduleWindowWeeks);
	}
	
	public static void buildSinglePhysicianSolution(AppointmentSolution solution, LocalDate startDate, int scheduleWindowWeeks) {
		generateClinic(solution, 1, 30);
		generatePhysicians(solution, 1);
		generateTreatments(solution, startDate, scheduleWindowWeeks);
	}
	
	public static void build1Physician2ClinicsSolution(AppointmentSolution solution, LocalDate startDate, int scheduleWindowWeeks) {
		generateClinic(solution, 1, 30);
		generateClinic(solution, 1, 30);
		generatePhysicians(solution, 1);
		generateTreatments(solution, startDate, scheduleWindowWeeks);
	}
	
	public static void buildManyPhysician1ClinicSolution(AppointmentSolution solution, LocalDate startDate, int scheduleWindowWeeks) {
		generateClinic(solution, 1, 100);
		generatePhysicians(solution, 100);
		generateTreatments(solution, startDate, scheduleWindowWeeks);
	}
	
	public static void seedPlannerSolution(AppointmentSolution solution, PlannerConfig config,  LocalDate startDate) {
		logger.info("Seeding planner: " + config);
		reset();
		for (int i = 0; i < config.getMaxClinics(); i++) {
			generateClinic(solution, config.getMaxRooms(), config.getMaxPatientsPerClinic());
		}
		generatePhysicians(solution, config.getMaxPhysicians());
		generateTreatments(solution, startDate, config.getScheduleWindow());
	}
	
	public static void reset() {
		identifier = 1;
		clinicCount = 0;
	}


}
