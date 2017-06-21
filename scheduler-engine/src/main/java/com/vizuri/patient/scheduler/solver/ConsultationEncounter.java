package com.vizuri.patient.scheduler.solver;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.vizuri.patient.scheduler.model.Appointment;
import com.vizuri.patient.scheduler.model.AppointmentStatus;
import com.vizuri.patient.scheduler.model.ConsultingRoom;
import com.vizuri.patient.scheduler.model.Physician;
import com.vizuri.patient.scheduler.model.TimeGrain;

@PlanningEntity(movableEntitySelectionFilter = MoveableEncounterSelectionFilter.class)
public class ConsultationEncounter extends Encounter {
	private static final long serialVersionUID = -1707706265743551744L;
	
	private Physician physician;

	public ConsultationEncounter() {
		super();
	}

	public ConsultationEncounter(Long id, Appointment appointment, AppointmentStatus status) {
		super(id);
		this.appointment = appointment;
		this.status = status;
	}

	// ************************************************************************
	// Planning Variables
	// ************************************************************************
	@PlanningVariable(valueRangeProviderRefs = { "consultingRoomRange" })
	public ConsultingRoom getConsultingRoom() {
		return room;
	}

	public void setConsultingRoom(ConsultingRoom consultingRoom) {
		this.room = consultingRoom;
	}

	@PlanningVariable(valueRangeProviderRefs = { "appointmentTimeRange" })
	public TimeGrain getStartingTime() {
		return startingTime;
	}

	public void setStartingTime(TimeGrain startingTime) {
		this.startingTime = startingTime;
	}
	
	@PlanningVariable(valueRangeProviderRefs = { "physicianRange" }, nullable = true)
	public Physician getPhysician() {
		return physician;
	}

	public void setPhysician(Physician physician) {
		this.physician = physician;
	}

	// ************************************************************************
	// Planning Helpers
	// ************************************************************************
	@ValueRangeProvider(id = "consultingRoomRange")
	public List<ConsultingRoom> getRoomsForAppointment() {
		return this.appointment.getClinic().getRooms();
	}

	@Override
	public String toString() {
		return "ConsultationEncounter [id=" + id + ", appointment=" + appointment + ", consultingRoom=" + room
				+ ", startingAppointmentTime=" + startingTime + ", physician=" + physician + ", status="
				+ status + "]";
	}
	
}
