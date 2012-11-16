package com.marakana.android.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * FeedParser
 */
public abstract class FeedParser {
    /**
     * Type
     */
    public enum Type{
        /** sax parser */ SAX,
        /** dom parser */ DOM,
        /** android sax parser */ ANDROID_SAX,
        /** pull parser */ PULL;
    }

    /** XML tag: channel */
    protected static final String CHANNEL = "channel";
    /** XML tag: publication data */
    protected static final String PUB_DATE = "pubDate";
    /** XML tag: description */
    protected static final String DESCRIPTION = "description";
    /** XML tag: link */
    protected static final String LINK = "link";
    /** XML tag: title */
    protected static final String TITLE = "title";
    /** XML tag: item */
    protected static final String ITEM = "item";

    /**
     * @param feedUrl
     * @return a default parser
     */
    public static FeedParser getParser(String feedUrl) {
        return getParser(feedUrl, FeedParser.Type.ANDROID_SAX);
    }

    /**
     * @param feedUrl
     * @param type
     * @return a parser
     */
    public static FeedParser getParser(String feedUrl, FeedParser.Type type) {
        switch (type) {
            case SAX:
                return new SaxFeedParser(feedUrl);
            case DOM:
                return new DomFeedParser(feedUrl);
            case ANDROID_SAX:
                return new AndroidSaxFeedParser(feedUrl);
            case PULL:
                return new XmlPullFeedParser(feedUrl);
            default:
                throw new IllegalArgumentException("unrecognized type: " + type);
        }
    }

    private final URL feedUrl;

    /**
     * @return a list of posts
     */
    public abstract List<Post> parse();


    /**
     * @param feedUrl
     */
    protected FeedParser(String feedUrl) {
        try { this.feedUrl = new URL(feedUrl); }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return open input stream
     */
    protected InputStream getInputStream() {
        try { return feedUrl.openConnection().getInputStream(); }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
