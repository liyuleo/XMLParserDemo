package com.leo.xml_parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SAXHandler extends DefaultHandler {
	public static final String TAG = "SAXHandler";
	public static final String TAG_PERSON = "person";
	public static final String TAG_NAME = "name";
	public static final String TAG_AGE = "age";
	private List<Person> mPersons;
	private Person mCurrPerson;
	private String mPerTag;

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String str = new String(ch, start, length).trim();
		Log.i(TAG, "characters:" + str);
		if (TAG_NAME.equals(mPerTag)) {
			if(mCurrPerson != null){
				mCurrPerson.setName(str);
			}
		} else if (TAG_AGE.equals(mPerTag)) {
			if(mCurrPerson != null){
				mCurrPerson.setAge(Short.parseShort(str));
			}
		}
	}

	@Override
	public void endDocument() throws SAXException {
		Log.i(TAG, "endDocument");
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		Log.i(TAG, "endElement" + uri + ":" + localName + ":" + qName);
		mPerTag = null;
		if (TAG_PERSON.equals(localName)) {
			mPersons.add(mCurrPerson);
			mCurrPerson = null;
		}
	}

	@Override
	public void startDocument() throws SAXException {
		Log.i(TAG, "startDocument");
		mPersons = new ArrayList<Person>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		Log.i(TAG, "startElement:" + localName + ":" + qName);
		if (TAG_PERSON.equals(localName)) {
			mCurrPerson = new Person();
			mCurrPerson.setID(Integer.parseInt(attributes.getValue(0)));
		} else if (TAG_NAME.equals(localName)) {
			mPerTag = TAG_NAME;
		} else if (TAG_AGE.equals(localName)) {
			mPerTag = TAG_AGE;
		}
	}

	public List<Person> getPersons(){
		return mPersons;
	}
}
