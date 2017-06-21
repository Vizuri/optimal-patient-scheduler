package com.vizuri.patient.scheduler.solver;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.vizuri.patient.scheduler.model.AppointmentStatus;

public class MoveableEncounterSelectionFilter implements SelectionFilter<ConsultationEncounter>{

	public boolean accept(ScoreDirector scoreDirector, ConsultationEncounter encounter) {
		return encounter.getStatus().equals(AppointmentStatus.DRAFT);
	}

}
