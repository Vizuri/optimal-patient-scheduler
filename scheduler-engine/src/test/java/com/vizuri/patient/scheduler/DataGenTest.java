package com.vizuri.patient.scheduler;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.scheduler.model.TimeGrain;
import com.vizuri.patient.scheduler.solver.AppointmentSolution;
import com.vizuri.patient.scheduler.util.DataFactory;

public class DataGenTest {
	public static final transient Logger logger = LoggerFactory.getLogger(DataGenTest.class);
	
	@Test
	public void testMod3() {
		for (int i = 0; i < 20; i++) {
			logger.info(String.format("%d mod3 : %d", i, i%3));
		}
	}
	
	@Test
	public void testCaseA() {
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildTestSolutionA(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		logger.info("Summary of solution: " + solution);
	}
	
	@Test
	public void testGapCalculation() {
		AppointmentSolution solution = new AppointmentSolution();
		DataFactory.buildTestSolutionA(solution, LocalDate.of(2017, Month.MARCH, 1), 3);
		
		TimeGrain midpoint = solution.getAppointmentTimes().get(20);
		logger.info("midpoint: " + midpoint);
		for (int i = 0; i < 40; i++) {
			TimeGrain testpoint = solution.getAppointmentTimes().get(i);
			logger.info("testpoint: " + testpoint);
			logger.info(String.format("gap %d : %d",  calcGap(testpoint, midpoint), calcGap(midpoint, testpoint)));
		}
	}
	
	private int calcGap(TimeGrain a, TimeGrain b) {
		int durationInGrains = 3;
		if (a.getDay().equals(b.getDay())) {
			int start = a.getGrainIndex();
			int end = start + durationInGrains;
			int otherStart = b.getGrainIndex();
			int otherEnd = otherStart + durationInGrains;
			
			if (end <= otherStart || otherEnd <= start) {
				return Math.max(start, otherStart) - Math.min(end, otherEnd);
			}
			return Integer.MAX_VALUE;
		}
		return Integer.MAX_VALUE;
	}
}
