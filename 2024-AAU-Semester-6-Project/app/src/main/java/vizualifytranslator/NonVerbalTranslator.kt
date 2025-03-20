package vizualifytranslator

/*
This service translates words into screen readers interpretation

 */
import android.util.Log
import com.vdurmont.emoji.EmojiParser


class NonVerbalTranslator {

    private val tag: String = "NVT"
    // TODO: Take input array from AccessibilityInputReader
    //Placeholder rn for testing
    //private var inputArray: List<String> = listOf("👌", "cat", "woof", "meowrrr", "pickle", "banana")

    //The translated array of words, mutable so we can modify it
    private var outputArray: MutableList<String> = mutableListOf()

    // TODO: Need to take into account error codes (idk what this is)


    // TODO: Detects which type of translation to perform on words
    fun detectionTranslate(words: List<String>){
        //need a var to keep track of current word

        outputArray = words.toMutableList()

        for (i in outputArray.indices){

            //outputArray.add(words[i])

            //TODO: Two possible solutions
            // Send the output array and position (Think this is better)
            // Only send word, not the whole array and position, then append the word to output array

            //Something to identify the right function for each word
                // - What about if a word needs 2 translate functions?
            //Switch case that calls translation function for each word

            //*** Emoji
            emojiTranslate(i, outputArray)

            //*** Elongated
            elongationTranslate(i, outputArray)

            //*** Punctuation
            punctuationTranslate(i, outputArray)

            //*** Capital
            //We need to look for words mostly compromised of caps, "RAUGHHH", "RAUGHHhhhh"
            //For now everything is lowercase
            capsTranslate(i, outputArray)
        }

        Log.i(tag,"The translated sentence is: " + outputArray)

    }


    //Translates emojis to corresponding CLDR short name
    // TODO: Maybe make a Map for the skin tone modifiers
    fun emojiTranslate(wordPosition: Int, outputArray: MutableList<String>): MutableList<String> {

        //The emoji is translated to text
        var emojiToText = EmojiParser.parseToAliases(outputArray[wordPosition])

        //TODO: The library does not include all emojis (hand emojis), and some are translated weird "👍" = "+1"
        //Looking for emoji pattern, and remove ":" and "_"
        emojiToText = Regex(":[\\w|]+:").replace(emojiToText){it.value.replace(":","").replace("_","")}

        val replacements = mapOf(
            "|type12" to " light skin tone",
            "|type3" to " medium light skin tone",
            "|type4" to " medium skin tone",
            "|type5" to " medium dark skin tone",
            "|type6" to " dark skin tone"
        )

        val pattern = replacements.keys.joinToString(separator = "|") { Regex.escape(it) }.toRegex()


        emojiToText = pattern.replace(emojiToText) { matchResult ->
            val key = matchResult.value
            replacements[key] ?: key // Use replacement if found, otherwise keep original key
        }

        //The word is added to the output array
        outputArray[wordPosition] = emojiToText

        return outputArray
    }


    //capsTranslate() Translate caps to lowercase
    fun capsTranslate(wordPosition: Int, outputArray: MutableList<String>): MutableList<String>{

        val lowercase = outputArray[wordPosition].lowercase()

        outputArray[wordPosition] = lowercase

        return outputArray
    }


    // "nooooo" to "no"
    // What about "look" which is a word, but "lool" is not
    // A simple but not the best solution, is looking for x3 repeating characters
        // - "noo" will be a problem, but "nooo" -> "noo"
    fun elongationTranslate(wordPosition: Int, outputArray: MutableList<String>): MutableList<String>{

        val word = outputArray[wordPosition]

        //If the word is short we don't modify it
        if (word.length < 3) {
            return outputArray
        }

        val result = StringBuilder()
        result.append(word[0]) // Append the first character
        result.append(word[1]) // Append the second character

        for (i in 2 until word.length) {
            // Check if the current character is different from the previous two characters
            if (word[i] != word[i - 1] || word[i] != word[i - 2]) {
                result.append(word[i]) // Append the current character
            }
        }

        outputArray[wordPosition] = result.toString()
        return outputArray

    }

    //ASCIIEmoticonsTranslate()/Punctuation translate
    // TODO: Only if ".", "?", "!", "/"... in sentence, read aloud, else remove. Now its only implemented so all symbols are removed.
    // TODO: What about numbers?
    // TODO: When holding a button additional options/symbols popup, what about them?
    // TODO: the code can be more compact e.g. by using a map and having another file that is a "dictionary"
    // TODO: "." and "*" aren't implemented since they are special cases
    fun punctuationTranslate(wordPosition: Int, outputArray: MutableList<String>): MutableList<String>{

        //Remove punctuation from the words or translate them to text.
        var noPunctuations = outputArray[wordPosition]

        val replacements = mapOf(
            '•' to "", // non-translatable
            '¥' to "", // non-translatable
            ',' to "", // only symbols in sentence translatable
            '/' to "", // only symbols in sentence translatable
            '\\' to "", // only symbols in sentence translatable
            '[' to "", // only symbols in sentence translatable
            ']' to "", // only symbols in sentence translatable
            '(' to "", // only symbols in sentence translatable
            ')' to "", // only symbols in sentence translatable
            '-' to "", // only symbols in sentence translatable
            '‘' to "", // only symbols in sentence translatable
            '“' to "", // only symbols in sentence translatable
            '{' to "", // only symbols in sentence translatable
            '}' to "", // only symbols in sentence translatable
            '+' to " plus",
            '×' to " times",
            '÷' to " divided by",
            '=' to " equals",
            '_' to " underscore",
            '<' to " less than",
            '>' to " greater than",
            '@' to " at",
            '#' to " hash",
            '£' to " pound",
            ';' to " semicolon",
            ':' to " colon",
            '%' to " percent",
            '^' to " caret",
            '&' to " and",
            '`' to " back quote",
            '~' to " tilde",
            '°' to " degree",
            '¢' to " cent",
            '√' to " square root",
            'π' to " pi",
            '¶' to " paragraph mark",
            '∆' to " increment",
            '©' to " copyright",
            '®' to " registered",
            '™' to " trademark",
            '$' to " dollar",
            '€' to " euro"
        )

        replacements.forEach { (pattern, replacement) ->
            noPunctuations = noPunctuations.replace(pattern.toString(), replacement)
        }

        // "*.*" to "asterisk asterisk"???
        // "!!!!!!" to " "

        // Some symbols need to be translated "$" dollar sign, "*" asterisk ...

        // Some symbols need to be completely removed like "!" and "?" ...
        // Indicate that there is a pause
        // / is not read (only if alone)
        //

        //  Hello i am bob. today is a great day
        //  Hello i am bob (pause) today is a great day

        outputArray[wordPosition] = noPunctuations

        return outputArray
    }

    // We need to output the pimped/translated array

}

