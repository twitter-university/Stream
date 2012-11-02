package com.marakana.android.stream;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.parser.FeedParser;
import com.marakana.android.parser.FeedParserFactory;
import com.marakana.android.parser.ParserType;
import com.marakana.android.parser.Post;

public class RefreshService extends IntentService {
	private static final String TAG = "Stream-RefreshService";
	private static final String FEED_URL = "http://marakana.com/s/feed.rss";
	private static final ParserType PARSER_TYPE = ParserType.ANDROID_SAX;
	private FeedParser parser;

	public RefreshService() {
		super(TAG);
		parser = FeedParserFactory.getParser(FEED_URL, PARSER_TYPE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		List<Post> posts = parser.parse();
		
		if(posts==null) {
			Log.d(TAG, "No posts from feed: "+FEED_URL);
		}
		
		ContentValues values = new ContentValues();
		// Iterate over all the posts
		for(Post post: posts) {
			values.put( StreamContract.Columns._ID,  post.hashCode() );
			values.put( StreamContract.Columns.TITLE, post.getTitle() );
			values.put( StreamContract.Columns.DESCRIPTION, post.getDescription() );
			values.put( StreamContract.Columns.LINK, post.getLink().toString() );
			values.put( StreamContract.Columns.PUB_DATE,  System.currentTimeMillis() );
			
			// Insert into the content provider
			getContentResolver().insert( StreamContract.CONTENT_URI, values);
		}
		
		Log.d(TAG, "Inserted records: "+posts.size());
	}

}
