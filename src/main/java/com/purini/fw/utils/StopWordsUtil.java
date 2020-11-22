package com.purini.fw.utils;

import com.purini.fw.PropertyStore;
import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;

import java.util.List;
import java.util.stream.Collectors;

public class StopWordsUtil {

    private final List<String> stopWords;

    public StopWordsUtil() {
        stopWords = PropertyStore.getInstance().getPropertyList(PropertyStore.STOP_WORDS);
    }

    /**
     * Excludes the stop words and gives out an overall copy
     * @param words - list of words
     * @return list of words without stop words
     */
    public List<Word> copyWithoutStopWords(List<Word> words) {
        return words.stream().filter(w -> {
            String s = w.getWords().get(0);
            return s.toUpperCase().equals(s) || !stopWords.contains(s.toLowerCase());
        }).collect(Collectors.toList());
    }

    /**
     * Excludes when both words in the pair are stop words and gives out an overall copy
     * @param wordPairs - list of word pairs
     * @return list of word pairs without stop words
     */
    public List<WordPair> copyWithoutStopWordPairs(List<WordPair> wordPairs) {
        return wordPairs.stream().filter(w -> {
            String s1 = w.getWords().get(0);
            String s2 = w.getWords().get(1);
            return s1.toUpperCase().equals(s1) || s2.toUpperCase().equals(s2)
                    || !stopWords.contains(s1.toLowerCase()) || !stopWords.contains(s2.toLowerCase());
        }).collect(Collectors.toList());
    }
}
