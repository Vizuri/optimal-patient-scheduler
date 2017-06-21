package com.vizuri.patient.scheduler.model;

public class ConsultingRoom extends AbstractPersistable {
	private static final long serialVersionUID = 573498712614704756L;

	private String name;
	private String label;

	public ConsultingRoom() {
		super();
	}

	public ConsultingRoom(Long id, String name, String label) {
		super(id);
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "ConsultingRoom [id=" + id + ", name=" + name + ", label=" + label+ "]";
	}

}
