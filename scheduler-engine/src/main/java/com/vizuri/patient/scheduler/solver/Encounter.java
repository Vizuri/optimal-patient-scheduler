package com.vizuri.patient.scheduler.solver;

import java.util.Comparator;

import com.vizuri.patient.scheduler.model.AbstractPersistable;
import com.vizuri.patient.scheduler.model.Appointment;
import com.vizuri.patient.scheduler.model.AppointmentStatus;
import com.vizuri.patient.scheduler.model.ConsultingRoom;
import com.vizuri.patient.scheduler.model.TimeGrain;

public abstract class Encounter extends AbstractPersistable {
	private static final long serialVersionUID = -8231219825226652458L;
	
	private final static int MAX_GAP = 40; // using this as largest grain gap for a day

	protected Appointment appointment;

	protected ConsultingRoom room;
	protected TimeGrain startingTime;
	protected AppointmentStatus status;
	
	public Encounter() {
		super();
	}
	
	public Encounter(Long id) {
		super(id);
	}
	
	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public ConsultingRoom getRoom() {
		return room;
	}

	public void setRoom(ConsultingRoom room) {
		this.room = room;
	}

	public abstract TimeGrain getStartingTime();

	public abstract void setStartingTime(TimeGrain startingTime);
	
	public AppointmentStatus getStatus() {
		return status == null ? AppointmentStatus.DRAFT : status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}
	
	// ************************************************************************
	// Complex methods
	// ************************************************************************
	public int calculateOverlap(Encounter other) {
		if (startingTime == null || other.getStartingTime() == null) {
			return 0;
		}
		int start = startingTime.getGrainIndex();
		int end = start + appointment.getDurationInGrains();
		int otherStart = other.startingTime.getGrainIndex();
		int otherEnd = otherStart + other.appointment.getDurationInGrains();

		if (end < otherStart) {
			return 0;
		} else if (otherEnd < start) {
			return 0;
		}
		return Math.min(end, otherEnd) - Math.max(start, otherStart);
	}
	
	public int calculateGapGrains(Encounter other) {
		if (startingTime == null || other.startingTime == null) {
			return MAX_GAP;
		}
		
		if (startingTime.getDay().equals(other.startingTime.getDay())) {
			int start = startingTime.getGrainIndex();
			int end = start + appointment.getDurationInGrains();
			int otherStart = other.startingTime.getGrainIndex();
			int otherEnd = otherStart + other.getAppointment().getDurationInGrains();
			
			if (end <= otherStart || otherEnd <= start) {
				return Math.max(start, otherStart) - Math.min(end, otherEnd);
			}
		}
		return MAX_GAP;
	}

	public Integer getLastTimeIndex() {
		if (startingTime == null) {
			return null;
		}
		return startingTime.getGrainIndex() + appointment.getDurationInGrains() - 1;
	}
	
	public static Comparator<Encounter> EncounterComparator = new Comparator<Encounter> () {

		public int compare(Encounter encounter1, Encounter encounter2) {
			if (encounter1.getStartingTime() != null && encounter2.getStartingTime() != null) {
					return (new Integer(encounter1.getStartingTime().getGrainIndex()).compareTo(encounter2.getStartingTime().getGrainIndex()));
			}
			return 0;
		}
		
	};
	
}
