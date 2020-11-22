package com.purini.fw.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StopWordsUtilTest {

    @Test
    public void testStopWord() {

        String text = "Cold wave prevailed In Himachal Pradesh with many places - Keylong and Kalpa - recording below zero degrees " +
                "Celsius on Friday. IT is improving in the world. It is maturing.";
        List<String> withoutStopWords = new StopWordsUtil().copyWithoutStopWords(TextToWordUtil.toWords(text)).stream()
                .map(w -> w.getWords().get(0))
                .collect(Collectors.toList());

        assertFalse(withoutStopWords.contains("In")); //mixed case is ignored correctly
        assertFalse(withoutStopWords.contains("in"));
        assertFalse(withoutStopWords.contains("with"));
        assertFalse(withoutStopWords.contains("and"));
        assertFalse(withoutStopWords.contains("on"));
        assertFalse(withoutStopWords.contains("is"));
        assertFalse(withoutStopWords.contains("the"));
        assertFalse(withoutStopWords.contains("It"));
        assertFalse(withoutStopWords.contains("it"));

        assertTrue(withoutStopWords.contains("Cold"));
        assertTrue(withoutStopWords.contains("wave"));
        assertTrue(withoutStopWords.contains("IT")); //all caps case is retained

    }
}
