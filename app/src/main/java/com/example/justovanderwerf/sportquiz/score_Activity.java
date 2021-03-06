package com.example.justovanderwerf.sportquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class score_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference users;
    int highScore;
    TextView scoreView;
    EditText searchUserText;
    Button searchButton;
    String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_);
        scoreView = findViewById(R.id.scoreView);
        searchUserText = findViewById(R.id.searchUserText);
        searchButton = findViewById(R.id.searchButton);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("scores").child(mAuth.getCurrentUser().getUid());

        String uid = mAuth.getCurrentUser().getUid();

        mail = mAuth.getCurrentUser().getEmail();

        showScore(uid);
    }

    /**
     *  Show the score of the user.
     */

    private void showScore(String uid) {
        // Read from the database
        users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    highScore = dataSnapshot.getValue(int.class);
                    updateTextView(highScore);


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("failed", "Failed to read value.", error.toException());
                }
    });
}

    private void updateTextView(int highScore) {
        scoreView.setText(mail + "'s highscore is: " + highScore);
        Log.d("score", "Value is: " + highScore);
    }

    public void logOut(View view) {
        mAuth.signOut();
        Intent intent = new Intent(score_Activity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Get the user id of an user, then return the score of that user.
     */
    public void searchClick(View view) {
        String email = searchUserText.getText().toString().replace(".",",");
        mail = email;

        DatabaseReference ref = database.getReference("emailToUid");

        // Read from the database
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.getValue(String.class);
                showScore(uid);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("failed", "Failed to read value.", error.toException());
            }
        });
    }
}
