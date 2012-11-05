package com.marakana.android.stream;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.marakana.android.parser.Post;

public class PostActivity extends Activity {
  private static final String TAG = "Stream-PostActivity";
  private PostFragment postFragment;
  private Post post;
  private long id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post);

    // Setup the action bar
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // Setup the post fragment
    postFragment = (PostFragment) getFragmentManager().findFragmentById(
        R.id.fragment_post);

    this.id = getIntent().getLongExtra("com.marakana.android.stream.id", -1);
    
    post = PostHelper.getPost(this, this.id);
    this.setTitle(post.getTitle());
    postFragment.update(post);
    
    Log.d(TAG, "Updated post for id: " + id);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_post, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      // This is called when the Home (Up) button is pressed
      // in the Action Bar.
      Intent parentActivityIntent = new Intent(this, MainActivity.class);
      parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
          | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(parentActivityIntent);
      finish();
      return true;
    case R.id.menu_link:
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( post.getLink().toString() ));
      startActivity(intent);
      return true;
    case R.id.menu_share:
      Intent share = new Intent(Intent.ACTION_SEND);
      share.setType("text/plain");
      share.putExtra(Intent.EXTRA_SUBJECT, post.getTitle() );
      share.putExtra(Intent.EXTRA_TEXT, post.getTitle() + ": " +post.getLink());
      startActivity(Intent.createChooser(share, "Share this post"));
    }
    return super.onOptionsItemSelected(item);
  }

}
