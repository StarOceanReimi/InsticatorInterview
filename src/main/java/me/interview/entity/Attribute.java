package me.interview.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Attribute implements IDAware<Long>, UniqueNameAware {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true, nullable=false)
	private String name;
	
	@JsonManagedReference
	@OneToMany(mappedBy="owner", fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	private Set<AttributeValue> values;

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

	public Set<AttributeValue> getValues() {
		return values;
	}

	public void setValues(Set<AttributeValue> values) {
		this.values = values;
	}
	
}
