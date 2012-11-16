package com.marakana.android.parser;

import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;


/**
 * AndroidSaxFeedParser
 */
public class AndroidSaxFeedParser extends FeedParser {
    private static final String RSS = "rss";

    /**
     * @param feedUrl
     */
    public AndroidSaxFeedParser(String feedUrl) {
        super(feedUrl);
    }

    /**
     * @see com.marakana.android.parser.FeedParser#parse()
     */
    @Override
    public List<Post> parse() {
        final List<Post> messages = new ArrayList<Post>();
        final Post currentMessage = new Post();
        RootElement root = new RootElement(RSS);
        Element channel = root.getChild(CHANNEL);
        Element item = channel.getChild(ITEM);

        item.setEndElementListener(new EndElementListener() {
            @Override public void end() { messages.add(new Post(currentMessage)); }
        });

        item.getChild(TITLE).setEndTextElementListener(
            new EndTextElementListener() {
                @Override public void end(String body) { currentMessage.setTitle(body); }
            });

        item.getChild(LINK).setEndTextElementListener(
            new EndTextElementListener() {
                @Override public void end(String body) { currentMessage.setLink(body); }
            });

        item.getChild(DESCRIPTION).setEndTextElementListener(
            new EndTextElementListener() {
                @Override public void end(String body) { currentMessage.setDescription(body); }
            });

        item.getChild(PUB_DATE).setEndTextElementListener(
            new EndTextElementListener() {
                @Override public void end(String body) { currentMessage.setDate(body); }
            });

        try {
            Xml.parse(getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
            return messages;
        }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
