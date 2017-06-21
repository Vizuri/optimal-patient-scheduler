package com.vizuri.patient.service.api.util;

public class SolutionResponse {

	private String status = "STOPPED";	// STOPPED  BUSY COMPLETE FAIL
	private String score;
	private int attempts = 0;
	private String message;
	private int treatmentCount;
	private int draftConsultCount;
	private int acceptedConsultCount;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public int getAttempts() {
		return attempts;
	}
	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getTreatmentCount() {
		return treatmentCount;
	}
	public void setTreatmentCount(int treatmentCount) {
		this.treatmentCount = treatmentCount;
	}
	public int getDraftConsultCount() {
		return draftConsultCount;
	}
	public void setDraftConsultCount(int draftConsultCount) {
		this.draftConsultCount = draftConsultCount;
	}
	public int getAcceptedConsultCount() {
		return acceptedConsultCount;
	}
	public void setAcceptedConsultCount(int acceptedConsultCount) {
		this.acceptedConsultCount = acceptedConsultCount;
	}
	
	
}
