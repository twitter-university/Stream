package com.marakana.android.parser;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * XmlPullFeedParser
 *
 */
public class AtomFeedParser {
    /** XML tag: feed */ public static final String TAG_FEED = "feed";
    /** XML tag: entry */ public static final String TAG_ENTRY = "entry";
    /** XML tag: id */ public static final String TAG_ID = "id";
    /** XML tag: title */ public static final String TAG_TITLE = "title";
    /** XML tag: author */ public static final String TAG_AUTHOR = "author";
    /** XML tag: name */ public static final String TAG_NAME = "name";
    /** XML tag: uri */ public static final String TAG_URI = "uri";
    /** XML tag: publication date */ public static final String TAG_PUB_DATE = "published";
    /** XML tag: summary */ public static final String TAG_SUMMARY = "summary";
    /** XML tag: category */ public static final String TAG_CATEGORY = "category";
    /** XML attribute: label */ public static final String ATTR_LABEL = "label";
    /** XML attribute: term */ public static final String ATTR_TERM = "term";
    /** XML tag: link */ public static final String TAG_LINK = "link";
    /** XML attribute: rel */ public static final String ATTR_REL = "rel";
    /** XML attribute: href */ public static final String ATTR_HREF = "href";
    /** XML attribute value: self */ public static final String LINK_TYPE_SELF = "self";
    /** XML attribute value: thumbnail */ public static final String LINK_TYPE_THUMB = "thumbnail";

    /** Post handler */
    public interface PostHandler {
        /** @param id */
        void setId(String id);
        /** @param title */
        void setTitle(String title);
        /** @param name */
        void setAuthorName(String name);
        /** @param uri */
        void setAuthorUri(String uri);
        /** @param date */
        void setPubDate(String date);
        /** @param summary */
        void setSummary(String summary);
        /** @param link */
        void setThumb(String link);
        /** @param link */
        void setContent(String link);
        /**
         * @param label
         * @param term
         */
        void addCategory(String label, String term);
        /** post complete */
        void finish();
    }

    private enum LinkType { NONE, CONTENT, THUMB; }

    // the parsing tables
    private static final Map<String, Element> authorParseTable;
    static {
        Map<String, Element> m = new HashMap<String, Element>();
        m.put(
            TAG_NAME,
            new Element(null) {
                @Override public void text(AtomFeedParser root, String text, PostHandler hdlr) {
                    hdlr.setAuthorName(text);
                }
            });
        m.put(
            TAG_URI,
            new Element(null) {
                @Override public void text(AtomFeedParser root, String text, PostHandler hdlr) {
                    hdlr.setAuthorUri(text);
                }
            });
        authorParseTable = Collections.unmodifiableMap(m);
    }

    private static final Map<String, Element> entryParseTable;
    static {
        Map<String, Element> m = new HashMap<String, Element>();
        m.put(
            TAG_ID,
            new Element(null) {
                @Override public void text(AtomFeedParser root, String text, PostHandler hdlr) {
                    hdlr.setId(text);
                }
            });
        m.put(
            TAG_TITLE,
            new Element(null) {
                @Override public void text(AtomFeedParser root, String text, PostHandler hdlr) {
                    hdlr.setTitle(text);
                }
            });
        m.put(
            TAG_AUTHOR,
            new Element(authorParseTable) {
                @Override public void start(AtomFeedParser root, XmlPullParser parser, PostHandler hdlr)
                    throws XmlPullParserException, IOException
                {
                    super.start(root, parser, hdlr);
                    root.parseElement(parser, hdlr, parseMap);
                }
            });
        m.put(
            TAG_PUB_DATE,
            new Element(null) {
                @Override public void text(AtomFeedParser root, String text, PostHandler hdlr) {
                    hdlr.setPubDate(text);
                }
            });
        m.put(
            TAG_SUMMARY,
            new Element(null) {
                @Override public void text(AtomFeedParser root, String text, PostHandler hdlr) {
                    hdlr.setSummary(text);
                }
            });
        m.put(
            TAG_LINK,
            new Element(null) {
                @Override public void start(AtomFeedParser root, XmlPullParser parser, PostHandler hdlr)
                    throws XmlPullParserException, IOException
                {
                    root.parseLink(parser, hdlr);
                }
            });
        m.put(
            TAG_CATEGORY,
            new Element(null) {
                @Override public void start(AtomFeedParser root, XmlPullParser parser, PostHandler hdlr)
                    throws XmlPullParserException, IOException
                {
                    root.parseCategory(parser, hdlr);
                }
            });
        entryParseTable = Collections.unmodifiableMap(m);
    }

    private static final Map<String, Element> feedParseTable;
    static {
        Map<String, Element> m = new HashMap<String, Element>();
        m.put(
            TAG_ENTRY,
            new Element(entryParseTable) {
                @Override public void start(AtomFeedParser root, XmlPullParser parser, PostHandler hdlr)
                    throws XmlPullParserException, IOException
                {
                    super.start(root, parser, hdlr);
                    root.parseElement(parser, hdlr, parseMap);
                }
                @Override public void end(AtomFeedParser root, PostHandler hdlr) { hdlr.finish(); }
            });
        feedParseTable = Collections.unmodifiableMap(m);
    }

    private static final Map<String, Element> docParseTable;
    static {
        Map<String, Element> m = new HashMap<String, Element>();
        m.put(
            TAG_FEED,
            new Element(feedParseTable) {
                @Override public void start(AtomFeedParser root, XmlPullParser parser, PostHandler hdlr)
                    throws XmlPullParserException, IOException
                {
                    super.start(root, parser, hdlr);
                    root.parseElement(parser, hdlr, parseMap);
                }
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
        XmlPullParser parser = ParserFactory.getParser();
        parser.setInput(in, null);
        while (true) {
            switch (parser.getEventType()) {
                case XmlPullParser.START_DOCUMENT:
                    parser.next();
                    parseElement(parser, hdlr, docParseTable);
                    break;

                case XmlPullParser.END_DOCUMENT:
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

                    elem = parseMap.get(name);
                    if (null == elem) { parser.next(); }
                    else { elem.start(this, parser, hdlr); }

                    break;

                case XmlPullParser.TEXT:
                    if (null != elem) { elem.text(this, parser.getText(), hdlr); }
                    parser.next();
                    break;

                case XmlPullParser.END_DOCUMENT:
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

    void parseLink(XmlPullParser parser, PostHandler hdlr)
        throws XmlPullParserException, IOException
    {
        String link = null;
        LinkType type = LinkType.NONE;

        int nAttrs = parser.getAttributeCount();
        ATTR: for (int i = 0; i < nAttrs; i++) {
            String name = parser.getAttributeName(i);
            String val = parser.getAttributeValue(i);
            if (ATTR_HREF.equals(name)) { link = val; }
            else if (ATTR_REL.equals(name)) {
                if (LINK_TYPE_SELF.equals(val)) { type = LinkType.CONTENT; }
                else if (LINK_TYPE_THUMB.equals(val)) { type = LinkType.THUMB; }
                else { break ATTR; }
            }
        }

        switch (type) {
            case CONTENT:
                hdlr.setContent(link);
                break;
            case THUMB:
                hdlr.setThumb(link);
                break;
            case NONE:
                break;
        }

        parser.next();
    }

    void parseCategory(XmlPullParser parser, PostHandler hdlr)
        throws XmlPullParserException, IOException
    {
        String label = null;
        String term = null;

        int nAttrs = parser.getAttributeCount();
        for (int i = 0; i < nAttrs; i++) {
            String name = parser.getAttributeName(i);
            String val = parser.getAttributeValue(i);
            if (ATTR_LABEL.equals(name)) { label = val; }
            else if (ATTR_TERM.equals(name)) { term = val; }
        }

        hdlr.addCategory(label, term);

        parser.next();
    }
}

