package me.interview.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class OptionGroup implements IDAware<Long> {

	private static final long serialVersionUID = -7448814545577623152L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@JsonManagedReference
	@OneToMany(mappedBy="owner", cascade={CascadeType.PERSIST}, fetch=FetchType.EAGER)
	@BatchSize(size=10)
	private Set<OptionValue> options;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<OptionValue> getOptions() {
		return options;
	}

	public void setOptions(Set<OptionValue> options) {
		this.options = options;
	}
}
