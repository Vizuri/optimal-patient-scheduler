package com.vizuri.patient.service.domain;

public class Room {

	private String id;
	private String name;
	private String label;
	private String type;

	public Room(String name) {
		
		this.name = name;
	}
	
	public Room(String id, String name, String label, String type) {
		
		
		this.id = id;
		this.name = name;
		this.label = label;
		this.type = type;
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
