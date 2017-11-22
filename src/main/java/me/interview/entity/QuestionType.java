package me.interview.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuestionType {
	@JsonProperty("trivia")
	TRIVIA,
	@JsonProperty("poll")
	POLL,
	@JsonProperty("checkbox")
	CHECKBOX,
	@JsonProperty("matrix")
	MATRIX
}
