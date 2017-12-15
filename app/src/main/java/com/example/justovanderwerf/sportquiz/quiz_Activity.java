package com.example.justovanderwerf.sportquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class quiz_Activity extends AppCompatActivity {
    String TAG = "QUIZ";
    ArrayList<Question> qList = new ArrayList<>();
    int currentq = 0;
    int[] indexList = {0,1,2,3};
    ArrayList<RadioButton> buttons = new ArrayList<>();
    TextView questione;
    RadioGroup radioButtonGroup;
    RequestQueue queue;
    int oldScore;
    private FirebaseAuth mAuth;
    int score = 0;
    FirebaseDatabase database;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
//        users = database.getReference("users");




        questione = findViewById(R.id.questionView);
        radioButtonGroup = findViewById(R.id.radioGroup);
        buttons.add((RadioButton) findViewById(R.id.radioButton1));
        buttons.add((RadioButton) findViewById(R.id.radioButton2));
        buttons.add((RadioButton) findViewById(R.id.radioButton3));
        buttons.add((RadioButton) findViewById(R.id.radioButton4));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("quiz").child("results");


        /**
         * Get value of the question and answers. Then add them to the Question class.
         */
        queue = Volley.newRequestQueue(this);

        String newUrl = "https://opentdb.com/api.php?amount=10&category=18&difficulty=easy&type=multiple";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, newUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("results");


                            for(int i = 0; i < arr.length(); i++){
                                JSONObject q = arr.getJSONObject(i);
                                String questionString = q.getString("question");
                                String correct = q.getString("correct_answer");
                                JSONArray incorrect = q.getJSONArray("incorrect_answers");
                                String incorrect1 = incorrect.getString(0);
                                String incorrect2 = incorrect.getString(1);
                                String incorrect3 = incorrect.getString(2);
                                Question question = new Question(correct, incorrect1, incorrect2, incorrect3, questionString);
                                qList.add(question);
                                Log.d(TAG, correct + incorrect1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        updateUI();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SHIT", "onErrorResponse: wrong");
            }
        });
        queue.add(stringRequest);








    }

    /**
     * Update the screen with the new question.
     */
    private void updateUI() {
        Question q = qList.get(currentq);
        ArrayList<String> a = q.getAnswers();

        questione.setText(Html.fromHtml(q.getQuest(), Html.FROM_HTML_MODE_COMPACT));

        indexList = RandomizeArray(indexList);
        int j = 0;
        for(int i = 0; i < indexList.length; i++){
            RadioButton butt = buttons.get(i);
            butt.setText(a.get(indexList[i]));

        }



    }

    /**
     * Generate random order of the answers.
     */

    public static int[] RandomizeArray(int[] array){
        Random rgen = new Random();  // Random number generator

        for (int i=0; i<array.length; i++) {
            int randomPosition = rgen.nextInt(array.length);
            int temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }

        return array;
    }

    /**
     * Check whether an answer is valid. Then update the score and question.
     */

    public void submit(View view) {
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        View radioButton = radioButtonGroup.findViewById(radioButtonID);
        int idx = radioButtonGroup.indexOfChild(radioButton);

        if(indexList[idx] == 0){
            score += 1;
        }

        currentq += 1;

        if(currentq < 10)
            updateUI();
        else{
            users = database.getReference("scores").child(mAuth.getCurrentUser().getUid());
            // Read from the database
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    oldScore = dataSnapshot.getValue(int.class);
                    Log.d("score", "Value is: " + oldScore);

                    Toast.makeText(quiz_Activity.this, "Your score is: " + score, Toast.LENGTH_SHORT).show();
                    String uid = mAuth.getCurrentUser().getUid();

                    DatabaseReference ref = database.getReference("scores");

                    // Get the highscore.
                    if(oldScore > score){
                        ref.child(uid).setValue(oldScore);
                    }
                    else{
                        ref.child(uid).setValue(score);
                    }


                    Intent intent = new Intent(quiz_Activity.this, score_Activity.class);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("failed", "Failed to read value.", error.toException());
                }
            });



        }

    }
}
