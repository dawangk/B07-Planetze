package com.example.b07projectfall2024.Questionnaire;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.example.b07projectfall2024.R;

/**
 * QuestionnaireActivity
 * This activity is the first that a user is redirected to upon registration. It informs the user of
 * the questionnaire they are to complete, and gives them the option to begin the questionnaire.
 */
public class QuestionnaireActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire);

        //Start Questionnaire button
        Button start = findViewById(R.id.start_questionnaire_button);

        //When start is clicked, navigates to QuestionnaireActivity2
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnaireActivity.this, QuestionnaireActivity2.class);
                startActivity(intent);
                finish();
            }
        });


    }
}