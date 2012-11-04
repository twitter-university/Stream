package com.marakana.android.parser;
public abstract class FeedParserFactory {
	
	public static FeedParser getParser(String feedUrl){
		return getParser(feedUrl, ParserType.ANDROID_SAX);
	}
	
	public static FeedParser getParser(String feedUrl, ParserType type){
		switch (type){
			case SAX:
				return new SaxFeedParser(feedUrl);
			case DOM:
				return new DomFeedParser(feedUrl);
			case ANDROID_SAX:
				return new AndroidSaxFeedParser(feedUrl);
			case XML_PULL:
				return new XmlPullFeedParser(feedUrl);
			default: return null;
		}
	}
}
