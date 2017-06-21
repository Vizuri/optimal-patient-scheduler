package com.vizuri.patient.scheduler.model;

public class Appointment extends AbstractPersistable {
	private static final long serialVersionUID = -1791246378784728246L;

	private int durationInGrains; // Offset from starting time
	private Patient patient;
	private Clinic clinic;
	private String title;
	private AppointmentType type;

	public Appointment() {
		super();
	}

	public Appointment(Long id, int durationInGrains, Patient patient, Clinic clinic, String title,
			AppointmentType type) {
		super(id);
		this.durationInGrains = durationInGrains;
		this.patient = patient;
		this.clinic = clinic;
		this.title = title;
		this.type = type;
	}

	public int getDurationInGrains() {
		return durationInGrains;
	}

	public void setDurationInGrains(int durationInGrains) {
		this.durationInGrains = durationInGrains;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", durationInGrains=" + durationInGrains + ", patient=" + patient + ", clinic="
				+ clinic + ", title=" + title + ", type=" + type + "]";
	}

}
