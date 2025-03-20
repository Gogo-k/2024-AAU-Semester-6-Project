package vizualifytranslator;

/*
This service translates words into screen readers interpretation

 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.dslul.openboard.inputmethod.keyboard.orangeBox.TextVisualizer;
import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.StorageService;

import org.dslul.openboard.interfaces.Subscriber;
import org.dslul.openboard.interfaces.Publisher;


public class STTService implements Subscriber {

    private static final float SAMPLE_RATE = 16000.f;
    private static final String TAG = "VoskSST";
    private static final String INTERNAL_STORAGE_FOLDER = "Download";
    private Model model;
    private Recognizer recognizer;
    public Context accessibilityContext;

    private String oldOutput= null;

    public STTService(Context accessibilityContext) {
        super();
        this.accessibilityContext = accessibilityContext;

    }

    public void initModel() {
        Log.d(TAG,"Initializing model");
        StorageService.unpack(accessibilityContext, "model-en-us", "model",
                (model) -> {

                    this.model = model;
                    Log.d(TAG,"model is" + model);
                    this.recognizer = new Recognizer(model, SAMPLE_RATE);

                },
                (exception) -> Log.e(TAG, "Failed to unpack the model: " + exception.getMessage()));

    }


    private String getFilePath() {
        File internalStorageDir = new File(Environment.getExternalStorageDirectory(), INTERNAL_STORAGE_FOLDER);
        File audioFile = new File(internalStorageDir, "tts_file.wav");
        return audioFile.getAbsolutePath();
    }




    public void recognizeFromFile() throws InterruptedException {
        String filePath = getFilePath();
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                this.recognizer.acceptWaveForm(buffer, bytesRead);
            }




        } catch (IOException e) {
            Log.e(TAG, "Error reading audio file", e);
        }
        // Extract the value associated with the key "text"
        JSONObject jsonObject = null;
        //
        // Thread.sleep(500); // TODO: find a better number
        while (true) {
            String finalResult = recognizer.getFinalResult();
            if (finalResult != oldOutput) {
                try {
                    jsonObject = new JSONObject(finalResult);
                    TextVisualizer.setAndShowOutputString(jsonObject.getString("text"));
                    oldOutput = finalResult;

                    break; // Exit the loop once the result is obtained
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Optionally, you can add a delay to avoid continuously polling
                Thread.sleep(1000); // Add appropriate delay in milliseconds
            }
        }


    }


    @Override
    public void update(){
        try {
            recognizeFromFile();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

