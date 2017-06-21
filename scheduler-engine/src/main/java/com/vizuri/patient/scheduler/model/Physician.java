package com.vizuri.patient.scheduler.model;

public class Physician extends AbstractPersistable {
	private static final long serialVersionUID = -3750694306828371862L;

	private String name;

	public Physician() {
		super();
	}

	public Physician(Long id, String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Physician [name=" + name + ", id=" + id + "]";
	}

}
