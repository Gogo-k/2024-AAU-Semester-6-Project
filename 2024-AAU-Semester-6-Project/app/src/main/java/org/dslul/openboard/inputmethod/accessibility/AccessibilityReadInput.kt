package org.dslul.openboard.inputmethod.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import androidx.annotation.RequiresApi
import org.dslul.openboard.interfaces.Subscriber
import vizualifytranslator.TextToFileService
import vizualifytranslator.STTService


/*
This service reads the content of the users input

 */

class AccessibilityReadInput:  AccessibilityService() {

    // tag used for logging
    private val TAG: String = "Input_Reader"

    private val STTService = STTService(this)
    private val textToFileService: TextToFileService = TextToFileService(this)

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.i(TAG, "$TAG found an accessibility event")



        // checks the Accessibility Events type is a text changed event
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {

            val source = event.source
            // check if the source is not null and that the sources class is EditText
            if (source != null && source.className == EditText::class.java.name) {

                var enteredText = event.text.toString() // takes input from input field

                enteredText = enteredText.substring(1, enteredText.length-1)

                //makes each word comma separated which improves the results of the STT
                enteredText = enteredText.replace(" " , ",  ")

                //only makes a TTS and STT call when space at the end of
                if(enteredText[enteredText.length-1] == ' '){
                    textToFileService.createSoundFile(enteredText)
                }
            }
        }
    }


    override fun onInterrupt() {
        // Handle interruption
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onServiceConnected() {
        super.onServiceConnected()
        // Set up your service
        Log.i(TAG, "$TAG is Connected")
        textToFileService.initTextToSpeech()
        textToFileService.addSubscriber(STTService)

        STTService.initModel()
    }
}
