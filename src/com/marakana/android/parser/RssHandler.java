package com.marakana.android.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * RssHandler
 */
public class RssHandler extends DefaultHandler {
    private List<Post> messages;

    private Post currentMessage;

    private StringBuilder builder;

    /**
     * @return messages
     */
    public List<Post> getMessages() { return this.messages; }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);
        if (this.currentMessage != null) {
            if (localName.equalsIgnoreCase(FeedParser.TITLE)) {
                currentMessage.setTitle(builder.toString());
            }
            else if (localName.equalsIgnoreCase(FeedParser.LINK)) {
                currentMessage.setLink(builder.toString());
            }
            else if (localName.equalsIgnoreCase(FeedParser.DESCRIPTION)) {
                currentMessage.setDescription(builder.toString());
            }
            else if (localName.equalsIgnoreCase(FeedParser.PUB_DATE)) {
                currentMessage.setDate(builder.toString());
            }
            else if (localName.equalsIgnoreCase(FeedParser.ITEM)) {
                messages.add(currentMessage);
            }
            builder.setLength(0);
        }
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        messages = new ArrayList<Post>();
        builder = new StringBuilder();
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
        throws SAXException
    {
        super.startElement(uri, localName, name, attributes);
        if (localName.equalsIgnoreCase(FeedParser.ITEM)) {
            this.currentMessage = new Post();
        }
    }
}
