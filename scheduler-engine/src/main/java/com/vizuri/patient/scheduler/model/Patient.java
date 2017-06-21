package com.vizuri.patient.scheduler.model;

public class Patient extends AbstractPersistable {
	private static final long serialVersionUID = 4089314021107000500L;
	
	private String name;
	private Clinic clinic;
	private TreatmentDays treatmentDays;	// enum MWF, TTS
	private int treatmentShift; // 1,2,3
	
	public Patient() {
		super();
	}

	public Patient(Long id, String name, Clinic clinic) {
		super(id);
		this.name = name;
		this.clinic = clinic;
	}

	public Patient(String name, Clinic clinic, TreatmentDays treatmentDays, int treatmentShift) {
		super();
		this.name = name;
		this.clinic = clinic;
		this.treatmentDays = treatmentDays;
		this.treatmentShift = treatmentShift;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}
	
	public TreatmentDays getTreatmentDays() {
		return treatmentDays;
	}

	public void setTreatmentDays(TreatmentDays treatmentDays) {
		this.treatmentDays = treatmentDays;
	}

	public int getTreatmentShift() {
		return treatmentShift;
	}

	public void setTreatmentShift(int treatmentShift) {
		this.treatmentShift = treatmentShift;
	}

	@Override
	public String toString() {
		return "Patient [id=" + id + ", name=" + name + ", clinic=" + clinic.getName() + ", treatmentDays=" + treatmentDays
				+ ", treatmentShift=" + treatmentShift + "]";
	}
	
}
