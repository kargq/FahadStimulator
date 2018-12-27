package me.kindeep.fahadstimulator;

import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.Locale;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {
    //i changes
    TextToSpeech t1;
    TextView ed1;
    Button b1;
    Button hear;
    boolean recognized;
    SpeechRecognitionResult result;
    // Replace below with your own subscription key
    private static String speechSubscriptionKey = "818b0c8310c942f5b2a4c20e769a3e74";
    // Replace below with your own service region (e.g., "westus").
    private static String serviceRegion = "eastus2";
    volatile private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Note: we need to request the permissions
        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);

        hear = findViewById(R.id.button);
        b1 = (Button) findViewById(R.id.speak);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        running = true;
        while (running) {
            hear();
            if(checkExit()) break;
            speak();
        }
    }

    private boolean checkExit() {
        boolean flag = false;
        String text = result.getText();
        if(text.equals("exit")||text.equals("Exit")) flag = true;
        return flag;
    }

    public void onSpeechButtonClicked(View view) {
        hear();
    }


    public void hear() {


        hear.setText("Listening");

        // TextView txt = (TextView) this.findViewById(R.id.hello); // 'hello' is the ID of your text view

        try {
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            assert (config != null);

            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert (reco != null);

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert (task != null);

            // Note: this will block the UI thread, so eventually, you want to
            //        register for the event (see full samples)
            result = task.get();
            assert (result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                recognized = true;
            } else {
                recognized = false;
            }

            reco.close();
        } catch (Exception ex) {

            assert (false);
        }
        // hear.setText("Ready to play");

    }

    public void onSpeakButtonClicked(View view) {
        speak();
    }

    public void speak() {
        try {
            if (recognized) {

                String toSpeak = Fahad.fahadifySentence(result.getText());
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            } else {
                String toSpeak = "Cant hear bleep";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        } catch (Exception e) {
            String toSpeak = "Something went wrong, please try again";
            Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
        hear.setText("Hear");
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
