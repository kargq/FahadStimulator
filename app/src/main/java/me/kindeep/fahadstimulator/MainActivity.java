/**
 *
 */
package me.kindeep.fahadstimulator;

import java.util.Random;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.CancellationDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {


    //subscription key
    private static String SpeechSubscriptionKey = "818b0c8310c942f5b2a4c20e769a3e74";

    //service region
    private static String SpeechRegion = "eastus2";


    TextToSpeech t1;
    TextView ed1;
    boolean recognized;
    SpeechRecognitionResult result;

    private boolean paused = true;
    private Button playPause;
    private ImageView fahadView;
    private String toBeSpoken;
    private MicrophoneStream microphoneStream;
    private ConstraintLayout root;
    private String resultText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);
        fahadView = findViewById(R.id.fahad_display);


        //Setting the welcome gif "SALAM DARI"
        setGifForImage(R.raw.just_fahad, fahadView);


        // Initialize SpeechSDK and request required permissions
        try {
            // a unique number within the application to allow
            // correlating permission request responses with the request.
            int permissionRequestId = 5;

            // Request permissions needed for speech recognition
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, permissionRequestId);
        } catch (Exception ex) {
            Log.e("SpeechSDK", "could not init sdk, " + ex.toString());
        }



        // create configuration of the speech to text service
        final SpeechConfig speechConfig;
        try {
            speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }



       /*
       * creating essential tool for audio input, showing appropriate gifs
       *
       * */
        root.setOnClickListener(view -> {
            final String logTag = "reco 1";

            startedListening();   // displaying gif with hearing fahad

            try {
                final AudioConfig audioInput = AudioConfig.fromStreamInput(createMicrophoneStream());
                final SpeechRecognizer reco = new SpeechRecognizer(speechConfig, audioInput);

                final Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();


                setOnTaskCompletedListener(task, result -> {
                    String s = result.getText();
                    if (result.getReason() != ResultReason.RecognizedSpeech) {
                        String errorDetails = (result.getReason() == ResultReason.Canceled) ? CancellationDetails.fromResult(result).getErrorDetails() : "";
                        s = "Recognition failed with " + result.getReason() + ". Did you enter your subscription?" + System.lineSeparator() + errorDetails;
                    } else recognized = true;

                    resultText = s;

                    reco.close();
                    Log.i(logTag, "Recognizer returned: " + s);


                    speak();

                    do{
                        stoppedListening();
                        Thread.sleep(300);
                    }while (t1.isSpeaking());

                    Thread.sleep(500);

                    waitForNextOne();
                });

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        });




        /*
        * Text to speech tweaks(basically changing voices and stuff)
        *
        * */
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {


                    //Random accent and voice evertime
                    List<Voice> voices = new ArrayList<>();


                    for (Voice tmpVoice : t1.getVoices()) {
                        voices.add(tmpVoice);
                        if (tmpVoice.getName().contains("#male") && tmpVoice.getName().contains("en-us")) {
                            Log.e("FAHADVOICE", "werid sht DID ON happend" + tmpVoice.getName());
                            break;
                        } else {
                            Log.e("FAHADVOICE", "werid sht happend" + tmpVoice.getName());
                        }
                    }

                    int rindex = randInt(0, voices.size() - 1);

                    t1.setVoice(voices.get(rindex));

                    t1.setSpeechRate((float)1.0);
                    //Actions
                    t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.e("TTS", "tts started");
                        }

                        @Override
                        public void onDone(String utteranceId) {

                            Log.e("FAHADVOICE", "done");

                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.e("TTS", "tts error");
                        }
                    });
                }


            }



        }, "com.google.android.tts");



        t1.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String utteranceId) {
                Log.e("FAHADVOICE", utteranceId + "COMPLETED");
            }
        });


    }

    private void doToast(String ajajajaj) {

        Toast.makeText(this, ajajajaj, Toast.LENGTH_LONG).show();
    }


    //Does the job speaking after convertion of the text into fahadian
    public void speak() {
        try {
            if (recognized) {
                String toSpeak = Fahad.fahadifySentence(resultText);
                //Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            } else {
                String toSpeak = "blah blah blah";
                //Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
            recognized = false;


        } catch (Exception e) {
            String toSpeak = "Something's not right, maybe go at it again?";
            Log.e("Speaking Failed", e.getMessage().toString());
            Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }


    }


    //controlling microphone stream
    private MicrophoneStream createMicrophoneStream() {
        if (microphoneStream != null) {
            microphoneStream.close();
            microphoneStream = null;
        }

        microphoneStream = new MicrophoneStream();
        return microphoneStream;
    }


    //Random number generator
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }


    //Setting gif for image
    private void setGifForImage(int gifResId, ImageView imageView) {
        /*from raw folder*/
        Glide.with(this)
                .load(gifResId)
                .into(imageView);

    }


    //To start listening while having a gif playing
    private void startedListening() {
        MainActivity.this.runOnUiThread(() -> {
            //change image
            setGifForImage(R.raw.hearing_fahad, fahadView);
        });
    }



    private void waitForNextOne() {
        MainActivity.this.runOnUiThread(() -> {
            //change image
            setGifForImage(R.raw.giphy_downsized, fahadView);
            Toast.makeText(this, "Touch to do it again!",Toast.LENGTH_SHORT).show();

        });
    }


    //To stop listening while having a gif playing
    private void stoppedListening() {
        MainActivity.this.runOnUiThread(() -> {
            setGifForImage(R.raw.talking_fahad, fahadView);
        });
    }


    //For back key exit function
    private long backPressed;

    @Override
    public void onBackPressed() {

        if(backPressed+1500>System.currentTimeMillis()){
        super.onBackPressed();
        return;
        }else {
            Toast.makeText(getBaseContext(),"Press again to exit", Toast.LENGTH_LONG).show();
        }
        backPressed=System.currentTimeMillis();
    }


    /*
     * For having multiple threads
     *
     * */
    private static ExecutorService s_executorService;

    static {
        s_executorService = Executors.newCachedThreadPool();
    }


    /*
     * ????????????????
     * */
    private <T> void setOnTaskCompletedListener(Future<T> task, OnTaskCompletedListener<T> listener) {
        s_executorService.submit(() -> {
            T result = task.get();
            listener.onCompleted(result);
            return null;
        });
    }

    private interface OnTaskCompletedListener<T> {
        void onCompleted(T taskResult) throws InterruptedException;
    }


    //logging the states while running

    static void loge(String[] what) {
        StringBuilder lol = new StringBuilder();
        for (int i = 0; i < what.length; i++) {
            lol.append(what[i]);
            // result +=(what[i]);
        }
        Log.e("Array", lol.toString());
    }
}