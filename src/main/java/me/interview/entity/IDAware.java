package me.interview.entity;

import java.io.Serializable;

public interface IDAware<ID> extends Serializable {

	ID getId();
	
	void setId(ID id);
}
