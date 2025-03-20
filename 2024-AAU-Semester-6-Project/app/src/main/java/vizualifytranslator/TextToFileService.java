package vizualifytranslator;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import org.dslul.openboard.interfaces.Publisher;
public class TextToFileService extends Publisher{


    private String envPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Download";


    private static final String TAG = "TextToFile";


    private TextToSpeech textToSpeech;

    private Context context;

    public TextToFileService(Context context) {
        this.context = context;
    }

    private FileObserver fileObserver;


    public void initTextToSpeech(){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if (i != TextToSpeech.ERROR) {
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.US);
                    //textToSpeech.setSpeechRate(0.35f);
                } else {
                    Log.e(TAG, "tts no work");
                }

            }
        });
    }



    public void createSoundFile(String inputText){
        HashMap<String, String> myHashRender = new HashMap<String, String>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TextToSpeechAudio");

        String destFileName = envPath;
        File fileTTS = new File(destFileName,"tts_file.wav");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.synthesizeToFile(inputText, null, fileTTS,null);
        }else {
            textToSpeech.synthesizeToFile(inputText, null, fileTTS.getAbsolutePath());
        }

        if (!fileTTS.exists()) {
            Log.e(TAG, "failed while creating fileTTS");
        }




        startFileObserver(fileTTS, fileTTS.getPath());

    }


    public void startFileObserver(File fileTTS, String filePath) {

        Log.i(TAG, "File Observer was made");
        fileObserver = new FileObserver(filePath) {
            @Override
            public void onEvent(int event, String path) {
                Log.i(TAG, "File event "+String.valueOf(event)+" "+path);

                if (event == FileObserver.CLOSE_WRITE) {
                    Log.i(TAG, "File event for me");

                    if (fileTTS.length() > 0) {
                        Log.i(TAG, "File has content, calling onDone");
                        notifier();
                        stopWatching(); // Stop watching the file after it's written
                        stopFileObserver();

                    }
                }
            }
        };
        fileObserver.startWatching();
    }

    // Call this method when the file is no longer needed to be observed
    public void stopFileObserver() {
        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
    }

}
