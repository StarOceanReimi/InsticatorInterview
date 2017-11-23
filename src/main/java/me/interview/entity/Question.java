package me.interview.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Question implements IDAware<Long> {

	private static final long serialVersionUID = 6693600677353178383L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String title;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(nullable=false)
	private QuestionType type;

	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumn(name="index_id", nullable=false)
	private OptionGroup index;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumn(name="column_id")
	private OptionGroup column;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinTable(
		name="questiontags",
		joinColumns=@JoinColumn(name="question_id", nullable=false, referencedColumnName="id"),
		inverseJoinColumns=@JoinColumn(name="tag_id", nullable=false, referencedColumnName="id")
	)
	private Set<Tag> tags;
	
	@Column
	private LocalDateTime createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public OptionGroup getIndex() {
		return index;
	}

	public void setIndex(OptionGroup index) {
		this.index = index;
	}

	public OptionGroup getColumn() {
		return column;
	}

	public void setColumn(OptionGroup column) {
		this.column = column;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
}
