package me.interview.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class OptionGroup implements IDAware<Long> {

	private static final long serialVersionUID = -7448814545577623152L;

	@Min(1)
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@NotNull
	@Size(min=1)
	@JsonManagedReference
	@OneToMany(mappedBy="owner", cascade={ CascadeType.ALL }, fetch=FetchType.EAGER)
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

	public Set<@Valid OptionValue> getOptions() {
		return options;
	}

	public void setOptions(Set<OptionValue> options) {
		this.options = options;
	}
}
