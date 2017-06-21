package com.vizuri.patient.scheduler.solver;

import com.vizuri.patient.scheduler.model.Appointment;
import com.vizuri.patient.scheduler.model.AppointmentStatus;
import com.vizuri.patient.scheduler.model.ConsultingRoom;
import com.vizuri.patient.scheduler.model.Physician;
import com.vizuri.patient.scheduler.model.TimeGrain;

/**
 * This would be planning entity for another solver
 * 
 * @author kspokas
 *
 */
public class TreatmentEncounter extends Encounter {
	private static final long serialVersionUID = 893646912019541844L;
	
	private Physician technician;
	
	public TreatmentEncounter() {
		super();
	}

	public TreatmentEncounter(Long id, Appointment appointment, AppointmentStatus status) {
		super(id);
		this.appointment = appointment;
		this.status = status;
	}

	public ConsultingRoom getTreatmentRoom() {
		return room;
	}

	public void setTreatmentRoom(ConsultingRoom treatmentRoom) {
		this.room = treatmentRoom;
	}

	public Physician getTechnician() {
		return technician;
	}
	
	public void setTechnician(Physician technician) {
		this.technician = technician;
	}
	
	@Override
	public TimeGrain getStartingTime() {
		return startingTime;
	}

	@Override
	public void setStartingTime(TimeGrain startingTime) {
		this.startingTime = startingTime;
	}
	
	
	@Override
	public String toString() {
		return "TreatmentEncounter [id=" + id + ", appointment=" + appointment + ", treatmentRoom=" + room
				+ ", startingTreatmentTime=" + startingTime + ", technician=" + technician + ", status=" + status
				+ "]";
	}
	
}
