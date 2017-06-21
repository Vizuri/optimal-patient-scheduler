package com.vizuri.patient.scheduler.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class Day extends AbstractPersistable {
	private static final long serialVersionUID = 2195198856571026230L;
	
	private int year;
	private int dayOfYear;
	private int weekOfYear;
	private DayOfWeek dayOfWeek;
	
    public Day() {
		super();
	}

    public Day(Long id, int year, int dayOfYear, int weekOfYear, DayOfWeek dayOfWeek) {
		super(id);
		this.year = year;
		this.dayOfYear = dayOfYear;
		this.weekOfYear = weekOfYear;
		this.dayOfWeek = dayOfWeek;
	}
    
	public Day(Long id, int dayOfYear, int weekOfYear, DayOfWeek dayOfWeek) {
		super(id);
		
		// default year
		this.year = LocalDate.now().getYear();
		this.dayOfYear = dayOfYear;
		this.weekOfYear = weekOfYear;
		this.dayOfWeek = dayOfWeek;
	}

	public Day(Long id, LocalDate date) {
		super(id);
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
		weekOfYear = date.get(weekFields.weekOfWeekBasedYear());
		dayOfYear = date.getDayOfYear();
		dayOfWeek = date.getDayOfWeek();
		year = date.getYear();
	}

	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public String getDateString() {
        return "Day:" + dayOfYear;
    }

    public int getWeekOfYear() {
		return weekOfYear;
	}

	public void setWeekOfYear(int weekOfYear) {
		this.weekOfYear = weekOfYear;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	public boolean after(Day otherDay) {
		return ((otherDay.year > this.year) || (otherDay.year == this.year && otherDay.dayOfYear > this.dayOfYear ));
	}
	
	@Override
	public String toString() {
		return "Day [dayOfYear=" + dayOfYear + ", weekOfYear=" + weekOfYear + ", dayOfWeek=" + dayOfWeek + ", id=" + id
				+ "]";
	}
	
}
