package com.marakana.android.parser;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class RssHandler extends DefaultHandler{
	private List<Post> messages;
	private Post currentMessage;
	private StringBuilder builder;
	
	public List<Post> getMessages(){
		return this.messages;
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);
		if (this.currentMessage != null){
			if (localName.equalsIgnoreCase(BaseFeedParser.TITLE)){
				currentMessage.setTitle(builder.toString());
			} else if (localName.equalsIgnoreCase(BaseFeedParser.LINK)){
				currentMessage.setLink(builder.toString());
			} else if (localName.equalsIgnoreCase(BaseFeedParser.DESCRIPTION)){
				currentMessage.setDescription(builder.toString());
			} else if (localName.equalsIgnoreCase(BaseFeedParser.PUB_DATE)){
				currentMessage.setDate(builder.toString());
			} else if (localName.equalsIgnoreCase(BaseFeedParser.ITEM)){
				messages.add(currentMessage);
			}
			builder.setLength(0);	
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		messages = new ArrayList<Post>();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equalsIgnoreCase(BaseFeedParser.ITEM)){
			this.currentMessage = new Post();
		}
	}
}