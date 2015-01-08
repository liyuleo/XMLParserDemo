package com.leo.xml_parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Button mSaxParser;
	private Button mDomParser;
	private Button mPullParser;
	private Button mBaidu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mSaxParser = (Button) findViewById(R.id.sax_parser);
		mSaxParser.setOnClickListener(this);
		mDomParser = (Button) findViewById(R.id.dom_parser);
		mDomParser.setOnClickListener(this);
		mPullParser = (Button) findViewById(R.id.pull_parser);
		mPullParser.setOnClickListener(this);
		mBaidu = (Button) findViewById(R.id.baidu);
		mBaidu.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.sax_parser:
			startSaxParser();
			break;
		case R.id.dom_parser:
			startDomParser();
			break;
		case R.id.pull_parser:
			startPullParser();
		case R.id.baidu:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						gotoBaiduByClient();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}).start();
			
			break;
		default:
			break;
		}
	}

	private void gotoBaiduByClient() throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://www.bai.com/");
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if(httpResponse.getStatusLine().getStatusCode() == 200){
			Log.e("liyu", EntityUtils.toString(httpResponse.getEntity()));
		}
	}
	private void gotoBaidu() throws IOException {
		URL url = new URL("http://www.bai.com/");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(6*1000);
		conn.setRequestMethod("GET");
		if(conn.getResponseCode() == 200){
			InputStream in = conn.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = in.read(buffer)) != -1){
				bos.write(buffer, 0, len);
			}
			in.close();
			String str = new String(bos.toByteArray());
			Log.e("liyu", str);
		}
	}

	private void startPullParser() {
		XmlPullParser xmlPullParser = getResources().getXml(R.xml.persion);
		List<Person> persons = null;
		Person person = null;
		int eventType;
		try {
			eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					persons = new ArrayList<Person>();
					break;
				case XmlPullParser.START_TAG:
					if ("person".equals(xmlPullParser.getName())) {
						person = new Person();
						person.setID(Integer.parseInt(xmlPullParser
								.getAttributeValue(0)));
					}
					if (person != null) {
						if ("name".equals(xmlPullParser.getName())) {
							person.setName(xmlPullParser.nextText());
						}

						if ("age".equals(xmlPullParser.getName())) {
							person.setAge(Short.parseShort(xmlPullParser
									.nextText()));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("person".equals(xmlPullParser.getName())) {
						persons.add(person);
						person = null;
					}
					break;
				default:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Person p : persons) {
			Log.d("liyu", p.toString());
		}
	}

	private void startDomParser() {
		File file = new File("/system/media/persion.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = null;
		List<Person> persons = new ArrayList<Person>();
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			Element elementRoot = document.getDocumentElement();
			NodeList nodeList = elementRoot.getElementsByTagName("person");
			int length = nodeList.getLength();
			Person person = null;
			for (int i = 0; i < length; i++) {
				Element element = (Element) nodeList.item(i);
				person = new Person();
				person.setID(Integer.parseInt(element.getAttribute("id")));
				NodeList pNodeList = element.getChildNodes();
				int pLength = pNodeList.getLength();
				for (int j = 0; j < pLength; j++) {
					Node chileElement = pNodeList.item(j);
					if (chileElement.getNodeType() == Element.ELEMENT_NODE) {
						if ("name".equals(chileElement.getNodeName())) {
							person.setName(chileElement.getFirstChild()
									.getNodeValue());
						} else if ("age".equals(chileElement.getNodeName())) {
							person.setAge(Short.parseShort(chileElement
									.getFirstChild().getNodeValue()));
						}
					}
				}
				persons.add(person);
			}
			for (Person p : persons) {
				Log.d("liyu", p.toString());
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startSaxParser() {
		File file = new File("/system/media/persion.xml");
		SAXHandler saxHandler = new SAXHandler();
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;

		try {
			saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(file, saxHandler);
			List<Person> persons = saxHandler.getPersons();
			for (Person person : persons) {
				Log.d("liyu", person.toString());
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
