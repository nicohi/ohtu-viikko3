package ohtu;

import java.util.Arrays;

public class Submission {
    private int week;
    private int hours;
    private int[] exercises;
    private String course;

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int[] getExercises() {
		return exercises;
	}

	public void setExercises(int[] exercises) {
		this.exercises = exercises;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

    public void setWeek(int week) {
        this.week = week;
    }

    public int getWeek() {
        return week;
    }

    @Override
    public String toString() {
        return course+", viikko "+week+", tehtyjä tehtäviä yhteensä "+exercises.length+", aikaa kului "+hours+", tehdyt tehtävät: "+Arrays.toString(exercises);
    }
    
}