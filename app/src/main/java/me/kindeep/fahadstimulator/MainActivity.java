package me.kindeep.fahadstimulator;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.CancellationDetails;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {
    TextToSpeech t1;
    TextView ed1;
    boolean recognized;
    SpeechRecognitionResult result;
    //subscription key
    private static String SpeechSubscriptionKey = "818b0c8310c942f5b2a4c20e769a3e74";
    //service region
    private static String SpeechRegion = "eastus2";
    private boolean paused = true;
    private Button playPause;
    private ImageView fahadView;
    private String toBeSpoken;
    private MicrophoneStream microphoneStream;
    private ConstraintLayout root;
    private String resultText;

    private MicrophoneStream createMicrophoneStream() {
        if (microphoneStream != null) {
            microphoneStream.close();
            microphoneStream = null;
        }

        microphoneStream = new MicrophoneStream();
        return microphoneStream;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);
        fahadView = findViewById(R.id.fahad_display);
        //fahadView.loadDataWithBaseURL(null, "<html><body><center><img style='align:center;max-width:100%;max-height:100% ; border-radius:50%' src='file:///android_asset/giphy.gif'/></center></body></html>", "text/html", "UTF-8", "");
        //fahadView.setBackgroundColor(Color.TRANSPARENT);

        setGifForImage(R.raw.just_fahad, fahadView);


        // Initialize SpeechSDK and request required permissions.
        try {
            // a unique number within the application to allow
            // correlating permission request responses with the request.
            int permissionRequestId = 5;

            // Request permissions needed for speech recognition
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, permissionRequestId);
        } catch (Exception ex) {
            Log.e("SpeechSDK", "could not init sdk, " + ex.toString());
        }

        // create config
        final SpeechConfig speechConfig;
        try {
            speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }

        ///////////////////////////////////////////////////
        // recognize
        ///////////////////////////////////////////////////
        root.setOnClickListener(view -> {
            final String logTag = "reco 1";

            startedListening();

            try {
                // final AudioConfig audioInput = AudioConfig.fromDefaultMicrophoneInput();
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
                    stoppedListening();
                });

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.e("TTS", "tts started");
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            new Thread() {
                                public void run() {
                                    MainActivity.this.runOnUiThread(() -> {
                                        Log.e("TTS", "done");
                                        setGifForImage(R.raw.just_fahad, fahadView);
                                    });
                                }
                            }.start();

                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.e("TTS", "tts error");
                        }
                    });
                }
            }

        });



    }

    private void setGifForImage(int gifResId, ImageView imageView) {
        /*from raw folder*/
        Glide.with(this)
                .load(gifResId)
                .into(imageView);

    }

    public void speak() {
        try {
            if (recognized) {

                String toSpeak = Fahad.fahadifySentence(resultText);
                //Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            } else {
                String toSpeak = "Cant hear bleep";
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

    private void startedListening() {
        MainActivity.this.runOnUiThread(() -> {
            //change image
            setGifForImage(R.raw.hearing_fahad, fahadView);
        });
    }

    private void stoppedListening() {
        MainActivity.this.runOnUiThread(() -> {
            //change image
            setGifForImage(R.raw.talking_fahad, fahadView);
            speak();
        });
    }

    private <T> void setOnTaskCompletedListener(Future<T> task, OnTaskCompletedListener<T> listener) {
        s_executorService.submit(() -> {
            T result = task.get();
            listener.onCompleted(result);
            return null;
        });
    }

    private interface OnTaskCompletedListener<T> {
        void onCompleted(T taskResult);
    }

    private static ExecutorService s_executorService;

    static {
        s_executorService = Executors.newCachedThreadPool();
    }

    static void loge(String[] what) {
        StringBuilder lol = new StringBuilder();
        for (int i = 0; i < what.length; i++) {
            lol.append(what[i]);
            // result +=(what[i]);
        }
        Log.e("Array", lol.toString());
    }
}