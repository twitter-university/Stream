package com.marakana.android.parser;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * SaxFeedParser
 */
public class SaxFeedParser extends FeedParser {

    /**
     * @param feedUrl
     */
    protected SaxFeedParser(String feedUrl) {
        super(feedUrl);
    }

    /**
     * @see com.marakana.android.parser.FeedParser#parse()
     */
    @Override
    public List<Post> parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            RssHandler handler = new RssHandler();
            parser.parse(this.getInputStream(), handler);
            return handler.getMessages();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
