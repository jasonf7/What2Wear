package com.jasonf7.what2wear.view.clothing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jasonf7.what2wear.R;

/**
 * Created by jasonf7 on 31/05/15.
 */
public class DummyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);

        Intent intent = new Intent();
        intent.putExtra("newClothing", getIntent().getParcelableExtra("clothing"));
        setResult(RESULT_OK, intent);
        finish();
    }
}
