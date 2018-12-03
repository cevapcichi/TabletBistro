package de.munich.iteratec.tabletbistro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.ahmadrosid.svgloader.SvgLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Question> questions = new ArrayList<>();
    private DatabaseReference database;
    private ViewFlipper viewFlipper;
    private TextView questionView, answer1Text, answer2Text, answer3Text, answer4Text, noQuestions;
    private ImageView answer1Image, answer2Image, answer3Image, answer4Image;
    private SmileRating rating;
    private TimerTask getQuestions, setQuestions, removeIndex0;

    int countAnswer1Text, countAnswer2Text, countAnswer3Text, countAnswer4Text,
            countAnswer1Image, countAnswer2Image, countAnswer3Image, countAnswer4Image,
            countRating1, countRating2, countRating3, countRating4, countRating5;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRandromBackground();

        //set counts to 0
        countAnswer1Text = countAnswer2Text = countAnswer3Text = countAnswer4Text =
                countAnswer1Image = countAnswer2Image = countAnswer3Image = countAnswer4Image =
                        countRating1 = countRating2 = countRating3 = countRating4 = countRating5 = 0;


        //question being displayed
        questionView = (TextView) findViewById(R.id.question);

        //Standard output when no questions left
        noQuestions = (TextView) findViewById(R.id.noQuestions);


        //answers of Text layout
        answer1Text = (TextView) findViewById(R.id.answer1Text);
        answer2Text = (TextView) findViewById(R.id.answer2Text);
        answer3Text = (TextView) findViewById(R.id.answer3Text);
        answer4Text = (TextView) findViewById(R.id.answer4Text);


        //answers of Icon layout
        answer1Image = (ImageView) findViewById(R.id.answer1Image);
        answer2Image = (ImageView) findViewById(R.id.answer2Image);
        answer3Image = (ImageView) findViewById(R.id.answer3Image);
        answer4Image = (ImageView) findViewById(R.id.answer4Image);


        //SmileyRating of Rating layout
        rating = (SmileRating) findViewById(R.id.smileyRating);


        //database with stored questions
        database = FirebaseDatabase.getInstance().getReference();


        //viewflipper to flip between layouts that are displayed
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);


        //ArrayList to save questions retrieved from Firebase and read to set question text and answer text

        getQuestions = new TimerTask() {
            @Override
            public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                database.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                        getData(dataSnapshot);
                                        String questionLayout = dataSnapshot.child("Fragentyp").getValue(String.class);

                                        //differentiating between different question layouts and using different Question constructors
                                        // to save the questions into an ArrayList
                                        if (questionLayout != null && ("Antworten als Text").equals(questionLayout)) {

                                            questions.add(new Question(dataSnapshot.getKey(),
                                                    dataSnapshot.child("Antwort 1").getValue(String.class),
                                                    dataSnapshot.child("Antwort 2").getValue(String.class),
                                                    dataSnapshot.child("Antwort 3").getValue(String.class),
                                                    dataSnapshot.child("Antwort 4").getValue(String.class),
                                                    dataSnapshot.child("Fragentyp").getValue(String.class)));

                                            Log.i("Liste", questions.get(0).getQuestion());

                                        } else if (questionLayout != null && ("Antworten als Icons").equals(questionLayout)) {

                                            questions.add(new Question(dataSnapshot.getKey(),
                                                    dataSnapshot.child("Antwort 1").getValue(String.class),
                                                    dataSnapshot.child("Antwort 2").getValue(String.class),
                                                    dataSnapshot.child("Antwort 3").getValue(String.class),
                                                    dataSnapshot.child("Antwort 4").getValue(String.class),
                                                    dataSnapshot.child("Fragentyp").getValue(String.class)));

                                        } else if (questionLayout != null && ("Bewertung").equals(questionLayout)) {

                                            questions.add(new Question(dataSnapshot.getKey(),
                                                    dataSnapshot.child("Rating 1").getValue(String.class),
                                                    dataSnapshot.child("Rating 2").getValue(String.class),
                                                    dataSnapshot.child("Rating 3").getValue(String.class),
                                                    dataSnapshot.child("Rating 4").getValue(String.class),
                                                    dataSnapshot.child("Rating 5").getValue(String.class),
                                                    dataSnapshot.child("Fragentyp").getValue(String.class)));

                                        }

                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    //remove Question out of ArrayList when Question is removed from database
                                    @Override
                                    public void onChildRemoved(@NonNull final DataSnapshot dataSnapshot) {
                                        Iterator<Question> iterator = questions.iterator();
                                        while (iterator.hasNext()) {
                                            Question question = iterator.next();
                                            if (question.getQuestion().equals(dataSnapshot.getKey().toString())) {
                                                iterator.remove();
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });

                                setQuestions = new TimerTask() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (questions.size() > 0) {

                                                    questionView.setText(questions.get(0).getQuestion());

                                                    switch (questions.get(0).getQuestionType()) {
                                                        case "Antworten als Text":

                                                            Log.i("Antwort 1 Text", answer1Text.getText().toString());

                                                            viewFlipper.setDisplayedChild(0);
                                                            answer1Text.setText(questions.get(0).getAnswer1());
                                                            answer2Text.setText(questions.get(0).getAnswer2());
                                                            answer3Text.setText(questions.get(0).getAnswer3());
                                                            answer4Text.setText(questions.get(0).getAnswer4());

                                                            answer1Text.setOnClickListener(MainActivity.this);
                                                            answer2Text.setOnClickListener(MainActivity.this);
                                                            answer3Text.setOnClickListener(MainActivity.this);
                                                            answer4Text.setOnClickListener(MainActivity.this);

                                                            break;

                                                        case "Antworten als Icons":

                                                            Log.i("Fragentyp", questions.get(0).questionType);

                                                            viewFlipper.setDisplayedChild(1);
                                                            SvgLoader.pluck().with(MainActivity.this).load(questions.get(0).getAnswer1(), answer1Image);
                                                            SvgLoader.pluck().with(MainActivity.this).load(questions.get(0).getAnswer2(), answer2Image);
                                                            SvgLoader.pluck().with(MainActivity.this).load(questions.get(0).getAnswer3(), answer3Image);
                                                            SvgLoader.pluck().with(MainActivity.this).load(questions.get(0).getAnswer4(), answer4Image);

                                                            answer1Image.setOnClickListener(MainActivity.this);
                                                            answer2Image.setOnClickListener(MainActivity.this);
                                                            answer3Image.setOnClickListener(MainActivity.this);
                                                            answer4Image.setOnClickListener(MainActivity.this);

                                                            break;

                                                        case "Bewertung":

                                                            viewFlipper.setDisplayedChild(2);

                                                            rating.setNameForSmile(BaseRating.TERRIBLE, questions.get(0).getRating1());
                                                            rating.setNameForSmile(BaseRating.BAD, questions.get(0).getRating2());
                                                            rating.setNameForSmile(BaseRating.OKAY, questions.get(0).getRating3());
                                                            rating.setNameForSmile(BaseRating.GOOD, questions.get(0).getRating4());
                                                            rating.setNameForSmile(BaseRating.GREAT, questions.get(0).getRating5());
                                                            rating.setSelectedSmile(BaseRating.OKAY);

                                                            rating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
                                                                @Override
                                                                public void onRatingSelected(int level, boolean reselected) {
                                                                    switch (level) {
                                                                        case 1:
                                                                            countRating1++;
                                                                            database.child(questions.get(0).getQuestion()).child("Rating 1 Clicks").setValue(countRating1);
                                                                            showThankYouMessage();
                                                                            break;
                                                                        case 2:
                                                                            countRating2++;
                                                                            database.child(questions.get(0).getQuestion()).child("Rating 2 Clicks").setValue(countRating2);
                                                                            showThankYouMessage();
                                                                            break;
                                                                        case 3:
                                                                            countRating3++;
                                                                            database.child(questions.get(0).getQuestion()).child("Rating 3 Clicks").setValue(countRating3);
                                                                            showThankYouMessage();
                                                                            break;
                                                                        case 4:
                                                                            countRating4++;
                                                                            database.child(questions.get(0).getQuestion()).child("Rating 4 Clicks").setValue(countRating4);
                                                                            showThankYouMessage();
                                                                            break;
                                                                        case 5:
                                                                            countRating5++;
                                                                            database.child(questions.get(0).getQuestion()).child("Rating 5 Clicks").setValue(countRating5);
                                                                            showThankYouMessage();
                                                                            break;
                                                                    }
                                                                }
                                                            });

                                                            break;
                                                    }

                                                    countAnswer1Text = countAnswer2Text = countAnswer3Text = countAnswer4Text =
                                                            countAnswer1Image = countAnswer2Image = countAnswer3Image = countAnswer4Image =
                                                                    countRating1 = countRating2 = countRating3 = countRating4 = countRating5 = 0;

                                                    //if no questions left, display the following
                                                }else if (questions.size() == 0) {
                                                    viewFlipper.setDisplayedChild(3);
                                                    noQuestions.setText("Leider gibt es momentan keine Fragen, komm später wieder!");
                                                    questionView.setText("");
                                                }

                                            }
                                        });
                                    }
                                };
                                Timer setQuestion = new Timer();
                                setQuestion.schedule(setQuestions, 60000);

                                removeIndex0 = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (questions.size() > 0) {
                                            questions.remove(0);
                                        }
                                    }
                                };
                                Timer removeIndex = new Timer();
                                removeIndex.schedule(removeIndex0, 280000);
                            }
                        });
            }
        };
        Timer getQuestion = new Timer();
        getQuestion.scheduleAtFixedRate(getQuestions, 0, 300000);

        TimerTask resetDatabase= new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        database.removeValue();

                    }
                });
            }
        };
        final TimerTask restartApp = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        restartApp();
                    }
                });
            }
        };
        Timer deleteDatabase = new Timer();
        Timer restartApplication = new Timer();
        deleteDatabase.scheduleAtFixedRate(resetDatabase, 1080000, 1200000);
        restartApplication.scheduleAtFixedRate(restartApp, 1200000, 1200000);

    }

    public void restartApp(){
        Intent intent = this.getBaseContext().getPackageManager().getLaunchIntentForPackage(this.getBaseContext().getPackageName() );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }


    @Override
    public void onClick(View v) {
        addCounts(v);
        showThankYouMessage();
    }

    public void addCounts(View v) {
        switch (v.getId()) {
            case R.id.answer1Text:
                countAnswer1Text++;
                database.child(questions.get(0).getQuestion()).child("Antwort 1 Clicks").setValue(countAnswer1Text);
                break;
            case R.id.answer2Text:
                countAnswer2Text++;
                database.child(questions.get(0).getQuestion()).child("Antwort 2 Clicks").setValue(countAnswer2Text);
                break;
            case R.id.answer3Text:
                countAnswer3Text++;
                database.child(questions.get(0).getQuestion()).child("Antwort 3 Clicks").setValue(countAnswer3Text);
                break;
            case R.id.answer4Text:
                countAnswer4Text++;
                database.child(questions.get(0).getQuestion()).child("Antwort 4 Clicks").setValue(countAnswer4Text);
                break;
            case R.id.answer1Image:
                countAnswer1Image++;
                database.child(questions.get(0).getQuestion()).child("Antwort 1 Clicks").setValue(countAnswer1Image);
                break;
            case R.id.answer2Image:
                countAnswer2Image++;
                database.child(questions.get(0).getQuestion()).child("Antwort 2 Clicks").setValue(countAnswer2Image);
                break;
            case R.id.answer3Image:
                countAnswer3Image++;
                database.child(questions.get(0).getQuestion()).child("Antwort 3 Clicks").setValue(countAnswer3Image);
                break;
            case R.id.answer4Image:
                countAnswer4Image++;
                database.child(questions.get(0).getQuestion()).child("Antwort 4 Clicks").setValue(countAnswer4Image);
                break;
        }
    }

    public void showThankYouMessage(){

        //build AlertDialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setCancelable(true).setTitle("Danke für deine Teilnahme!").setMessage("Komm nächste Woche wieder für neue Fragen \uD83C\uDF89");


        //set AlertDialog and make it not focusable when created to prevent action bar and navigation bar showing
        final AlertDialog alert = dialog.create();
        alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        alert.show();
        //clear flags 'not focusable'
        alert.getWindow().getDecorView().setSystemUiVisibility(this.getWindow().getDecorView().getSystemUiVisibility());
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


        //close AlertDialog
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        //delay closing AlertDialog with delay
        handler.postDelayed(runnable, 2500);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void getData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Log.d("Frage", "Frage: "+ dataSnapshot.getKey());
            Log.d("Fragentyp", "Fragentyp: "+ dataSnapshot.child("Fragentyp").getValue(String.class));
            break;
        }
    }

    public  void setRandromBackground(){
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        mainLayout.setBackgroundColor(randomAndroidColor);
    }
}


