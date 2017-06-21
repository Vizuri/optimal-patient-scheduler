package com.vizuri.patient.scheduler.model;

import java.io.Serializable;
import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class AbstractPersistable implements Serializable, Comparable<AbstractPersistable> {
	private static final long serialVersionUID = 1L;

	protected Long id;

	public AbstractPersistable() {
		super();
	}

	public AbstractPersistable(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int compareTo(AbstractPersistable other) {
		return new CompareToBuilder().append(getClass().getName(), other.getClass().getName()).append(id, other.id)
				.toComparison();
	}

	@Override
	public String toString() {
		return getClass().getName().replaceAll(".*\\.", "") + "-" + id;
	}
}
