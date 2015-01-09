package com.example.face;

import java.io.Serializable;

public class Face implements Serializable {

	private static final long serialVersionUID = 2171620018494844019L;

	private String name;
	private int path;

	public Face(String name, int path) {
		super();
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPath() {
		return path;
	}

	public void setPath(int path) {
		this.path = path;
	}

}
