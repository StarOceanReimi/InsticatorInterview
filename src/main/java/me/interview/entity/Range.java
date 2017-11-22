package me.interview.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("RANGE")
public class Range extends AttributeValue {

	private Double start;
	
	private Double end;

	public Range(String name, Double start, Double end) {
		super(name);
		this.start = start;
		this.end = end;
	}

	public Double getStart() {
		return start;
	}

	public void setStart(Double start) {
		this.start = start;
	}

	public Double getEnd() {
		return end;
	}

	public void setEnd(Double end) {
		this.end = end;
	}
	
}
