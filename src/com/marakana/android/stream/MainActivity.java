package com.marakana.android.stream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
  private Intent postActivityIntent;
  private Intent refreshServiceIntent;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    postActivityIntent = new Intent(this, PostActivity.class);
    refreshServiceIntent = new Intent(this, RefreshService.class);
    
    // Refresh the data
    startService( refreshServiceIntent );
    
    // Setup the action bar
    getActionBar().setDisplayHomeAsUpEnabled(false); 
  }

  public void showPost(long id) {
    postActivityIntent.putExtra("com.marakana.android.stream.id", id);
    startActivity(postActivityIntent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.menu_refresh:
      startService( refreshServiceIntent );
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

}
