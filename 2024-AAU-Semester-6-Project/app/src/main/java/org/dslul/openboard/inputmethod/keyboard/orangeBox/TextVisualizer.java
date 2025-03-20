package org.dslul.openboard.inputmethod.keyboard.orangeBox;

import android.util.Log;
import android.widget.TextView;

import org.dslul.openboard.interfaces.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class TextVisualizer {


    private static TextView mText;


    private static String outputString;

    public TextVisualizer(TextView mText) {
        this(mText,"");
    }

    public TextVisualizer(TextView mText, String shownText) {
        this.mText = mText;
        this.outputString = shownText;
    }

    public String getOutputString() {
        return outputString;
    }

    public static void setOutputString(String input) {
        outputString = input;
    }

    public static void showOutputString(){
        mText.setText(outputString);
    }

    public static void setAndShowOutputString(String input){
        setOutputString(input);
        showOutputString();
    }

}
