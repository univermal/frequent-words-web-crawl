package com.purini.fw.utils;

import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TextToWordUtilTest {

    @Test
    public void testTextToWord() {

        String text = "'Oh, you can't help that,' said the Cat: 'we're all mad 999 times 21years. I'm- mad. You're -mad at - your go-live.'";

        List<Word> words = TextToWordUtil.toWords(text);
        assertEquals(19, words.size());

        String wordsText = words.stream().map(w -> w.getWords().get(0)).collect(Collectors.joining("!"));
        assertEquals("Oh!you!can't!help!that!said!the!Cat!we're!all!mad!times!I'm!mad!You're!mad!at!your!go-live", wordsText);

        List<WordPair> wordPairs = TextToWordUtil.toWordPairs(words);
        assertEquals(18, words.size() - 1);
    }
}
