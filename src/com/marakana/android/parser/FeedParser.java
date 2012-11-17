package com.marakana.android.parser;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * XmlPullFeedParser
 */
public class FeedParser {
    private static final String TAG = "PARSER";

    /** XML tag: rss */ public static final String TAG_RSS = "rss";
    /** XML tag: channel */ public static final String TAG_CHANNEL = "channel";
    /** XML tag: item */ public static final String TAG_ITEM = "item";
    /** XML tag: title */ public static final String TAG_TITLE = "title";
    /** XML tag: publication date */ public static final String TAG_PUB_DATE = "pubDate";
    /** XML tag: description */ public static final String TAG_DESCRIPTION = "description";
    /** XML tag: link */ public static final String TAG_LINK = "link";

    /** Post handler */
    public interface PostHandler {
        /** post complete */
        void finish();
        /** @param title */
        void setTitle(String title);
        /** @param author */
        void setAuthor(String author);
        /** @param pubDate */
        void setPubDate(String pubDate);
        /** @param desc  */
        void setDescription(String  desc);
        /** @param link */
        void setLink(String link);
    }

    private static abstract class Element {
        protected final Map<String, Element> parseMap;
        public Element(Map<String, Element> parseMap) { this.parseMap = parseMap; }
        public abstract void start(FeedParser root, XmlPullParser parser, PostHandler hdlr)
            throws XmlPullParserException, IOException;
        public abstract void text(FeedParser root, String text, PostHandler hdlr)
            throws XmlPullParserException, IOException;
        public abstract void end(FeedParser root, PostHandler hdlr)
            throws XmlPullParserException, IOException;
    }

    // the parsing tables
    private static final Map<String, Element> itemParseTable;
    static {
        Map<String, Element> m = new HashMap<String, Element>();
        m.put(
            TAG_TITLE,
            new Element(null) {
                @Override public void start(FeedParser root, XmlPullParser parser, PostHandler hdlr) { }
                @Override public void text(FeedParser root, String text, PostHandler hdlr) { hdlr.setTitle(text); }
                @Override public void end(FeedParser root, PostHandler hdlr) { }
            });
        m.put(
            TAG_PUB_DATE,
            new Element(null) {
                @Override public void start(FeedParser root, XmlPullParser parser, PostHandler hdlr) { }
                @Override public void text(FeedParser root, String text, PostHandler hdlr) { hdlr.setPubDate(text); }
                @Override public void end(FeedParser root, PostHandler hdlr) { }
            });
        m.put(
            TAG_DESCRIPTION,
            new Element(null) {
                @Override public void start(FeedParser root, XmlPullParser parser, PostHandler hdlr) { }
                @Override public void text(FeedParser root, String text, PostHandler hdlr) { hdlr.setDescription(text); }
                @Override public void end(FeedParser root, PostHandler hdlr) { }
            });
        m.put(
            TAG_LINK,
            new Element(null) {
                @Override public void start(FeedParser root, XmlPullParser parser, PostHandler hdlr) { }
                @Override public void text(FeedParser root, String text, PostHandler hdlr) { hdlr.setLink(text); }
                @Override public void end(FeedParser root, PostHandler hdlr) { }
            });
        itemParseTable = Collections.unmodifiableMap(m);
    }

    private static final Map<String, Element> channelParseTable;
    static {
        Map<String, Element> m = new HashMap<String, Element>();
        m.put(
            TAG_ITEM,
            new Element(itemParseTable) {
                @Override public void start(FeedParser root, XmlPullParser parser, PostHandler hdlr)
                    throws XmlPullParserException, IOException
                {
                    root.parseElement(parser, hdlr, parseMap);
                }
                @Override public void text(FeedParser root, String text, PostHandler hdlr) { }
                @Override public void end(FeedParser root, PostHandler hdlr) { hdlr.finish(); }
            });
        channelParseTable = Collections.unmodifiableMap(m);
    }

    private static final Map<String, Element> rssParseTable;
    static {
        Map<String, Element> m = new HashMap<String, Element>();
        m.put(
            TAG_CHANNEL,
            new Element(channelParseTable) {
                @Override public void start(FeedParser root, XmlPullParser parser, PostHandler hdlr)
                    throws XmlPullParserException, IOException
                {
                    root.parseElement(parser, hdlr, parseMap);
                }
                @Override public void text(FeedParser root, String text, PostHandler hdlr) { }
                @Override public void end(FeedParser root, PostHandler hdlr) { hdlr.finish(); }
            });

        rssParseTable = Collections.unmodifiableMap(m);
    }

    private static final Map<String, Element> docParseTable;
    static {
        Map<String, Element> m = new HashMap<String, Element>();
        m.put(
            TAG_RSS,
            new Element(rssParseTable) {
                @Override public void start(FeedParser root, XmlPullParser parser, PostHandler hdlr)
                    throws XmlPullParserException, IOException
                {
                    root.parseElement(parser, hdlr, parseMap);
                }
                @Override public void text(FeedParser root, String text, PostHandler hdlr) { }
                @Override public void end(FeedParser root, PostHandler hdlr) { hdlr.finish(); }
            });
        docParseTable = Collections.unmodifiableMap(m);
    }

    /**
     * @param in
     * @param hdlr
     * @throws XmlPullParserException
     * @throws IOException
     */
    public void parse(InputStream in, PostHandler hdlr) throws XmlPullParserException, IOException {
        Log.d(TAG, "Starting parse");
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        while (true) {
            switch (parser.getEventType()) {
                case XmlPullParser.START_DOCUMENT:
                    parser.next();
                    parseElement(parser, hdlr, docParseTable);
                    break;

                case XmlPullParser.END_DOCUMENT:
                    parser.next();
                    return;

                default:
                    parser.next();
            }
        }
    }

    void parseElement(XmlPullParser parser, PostHandler hdlr, Map<String, Element> parseMap)
        throws XmlPullParserException, IOException
    {
        Element elem = null;
        String name = null;

        while (true) {
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    parser.next();

                    elem = parseMap.get(name.toLowerCase(Locale.US));
                    if (null != elem) {
                        elem.start(this, parser, hdlr);
                        break;
                    }

                    break;

                case XmlPullParser.TEXT:
                    if (null != elem) { elem.text(this, parser.getText(), hdlr); }
                    parser.next();
                    break;

                case XmlPullParser.END_TAG:
                    if (null == name) { return; }

                    if (name.equals(parser.getName())) {
                        if (null != elem) {
                            elem.end(this, hdlr);
                            elem = null;
                        }
                        name = null;
                        parser.next();
                        break;
                    }

                    throw new XmlPullParserException(
                        "mismatched end tag: " + name + "!=" + parser.getName());

                default:
                    parser.next();
            }
        }
    }
}