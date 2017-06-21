package com.vizuri.patient.scheduler.model;

public class Shift extends AbstractPersistable {
	private static final long serialVersionUID = 1L;

	private Physician physician;
	private Clinic clinic;
	private Day day;
	private boolean locked;
	
	public Shift() {
		super();
	}

	public Shift(Physician physician) {
		super();
		this.physician = physician;
	}

	public Shift(Long id, Physician physician, Clinic clinic, Day day) {
		super(id);
		this.physician = physician;
		this.clinic = clinic;
		this.day = day;
	}

	public Physician getPhysician() {
		return physician;
	}

	public void setPhysician(Physician physician) {
		this.physician = physician;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public String toString() {
		return "Shift [physician=" + physician + ", clinic=" + clinic + ", day=" + day + ", locked=" + locked + ", id=" + id + "]";
	}
	
}
