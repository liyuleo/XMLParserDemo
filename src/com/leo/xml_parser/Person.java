package com.leo.xml_parser;

public class Person {
	private int mID;
	private String mName;
	private short mAge;

	public Person() {

	}

	public int getID() {
		return mID;
	}

	public void setID(int id) {
		this.mID = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public short getmAge() {
		return mAge;
	}

	public void setAge(short age) {
		this.mAge = age;
	}

	@Override
	public String toString() {
		return "ID=" + mID + ":" + mName + ":" + mAge;
	}

}
