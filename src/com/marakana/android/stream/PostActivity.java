package com.marakana.android.stream;

import android.os.Bundle;


/**
 * PostActivity
 */
public class PostActivity extends BaseActivity {

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ((PostFragment) getFragmentManager().findFragmentById(R.id.fragment_post))
            .loadPost(getIntent().getExtras());
    }
}
