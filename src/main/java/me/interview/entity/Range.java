package me.interview.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="RANGE")
public class Range extends OptionValue {

	private static final long serialVersionUID = -7556582826480903068L;

	private Double start;
	
	private Double end;

	public Range() {
	}
	
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
