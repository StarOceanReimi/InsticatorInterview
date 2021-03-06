package me.interview.entity;

import static javax.persistence.CascadeType.ALL;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class OptionGroup implements IDAware<Long> {

	private static final long serialVersionUID = -7448814545577623152L;

	@Min(1)
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Field
	private String name;
	
	@IndexedEmbedded
	@Size(min=1)
	@JsonManagedReference
	@OneToMany(mappedBy="owner", cascade={ ALL }, fetch=FetchType.EAGER)
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
