package org.dslul.openboard.inputmethod.keyboard.orangeBox;

import android.content.Context;
import android.speech.tts.*;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.Locale;

import org.dslul.openboard.inputmethod.latin.KeyboardWrapperView;
import org.dslul.openboard.inputmethod.latin.R;

public final class OrangeScript extends RelativeLayout implements OnClickListener,
        OnLongClickListener {

    public final String TAG = "Orangebox";

    private final ImageButton mResizeKey;
    private final ImageButton mToSpeechKey;

    private TextToSpeech textToSpeech;

    private boolean isExpanded=false;

    private static TextVisualizer textVisualizer;

    private KeyboardWrapperView parent;

    private TextView mText;


    public OrangeScript(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.orangeBoy);
    }

    public void setParent(KeyboardWrapperView parent){
        this.parent = parent;
    }

    public OrangeScript(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.orange_preview_box, this);

        mResizeKey = findViewById(R.id.imageButton2);
        mToSpeechKey = findViewById(R.id.imageButton);
        mText = findViewById(R.id.orangebox);
        mText.setMovementMethod(new ScrollingMovementMethod());

        mResizeKey.setOnClickListener(this);
        mToSpeechKey.setOnClickListener(this);


        // create an object textToSpeech and adding features into it
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });


        textVisualizer = new TextVisualizer(mText);

    }


    @Override
    public void onClick (View view){
        if (view == mResizeKey) {
           if (isExpanded){
               minmizeOrangeBox();
           }else {
               expandOrangeBox(100);
           }
            isExpanded = !isExpanded;
            return;
        }
        if (view == mToSpeechKey) {
            Log.i(TAG,textVisualizer.getOutputString());
            
            textToSpeech.speak(textVisualizer.getOutputString(),TextToSpeech.QUEUE_FLUSH,null);
            return;
        }
    }

    public void expandOrangeBox(int sizeChange){
        ViewGroup.LayoutParams params = parent.getLayoutParams();
        params.height = dpToPx(pxToDp(params.height)+sizeChange); //1200 is not expanded
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        parent.setLayoutParams(params);
        mResizeKey.setRotationX(0);


        parent.getChildAt(1).setTranslationY(dpToPx(pxToDp((int)parent.getChildAt(1).getTranslationY())+sizeChange));

        parent.getChildAt(2).getLayoutParams().height =  dpToPx(pxToDp(parent.getChildAt(2).getLayoutParams().height) + sizeChange);
        mText.getLayoutParams().height = dpToPx(pxToDp(mText.getLayoutParams().height) + sizeChange);
    }

    public void minmizeOrangeBox(){
        ViewGroup.LayoutParams params = parent.getLayoutParams();
        params.height = dpToPx(410); //1200 is not expanded
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        parent.setLayoutParams(params);

        parent.getChildAt(1).setTranslationY(dpToPx(110));

        parent.getChildAt(2).getLayoutParams().height =  dpToPx(100);
        mText.getLayoutParams().height = dpToPx(70);
        mResizeKey.setRotationX(180);

    }

    public TextVisualizer getTextVisualizer() {
        return textVisualizer;
    }


    @Override
    public boolean onLongClick (View view){

        return false;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
