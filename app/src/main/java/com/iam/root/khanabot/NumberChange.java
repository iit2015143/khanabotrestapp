package com.iam.root.khanabot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NumberChange extends AppCompatActivity {

    private EditText number;
    private Button change;
    private String setNumber;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_number_change);

            number  = (EditText)findViewById(R.id.number);
            change = (Button)findViewById(R.id.change);

            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNumber = number.getText().toString();
                }
            });
        }
}
