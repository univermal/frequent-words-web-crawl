package com.purini.fw.utils;

import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextToWordUtil {

    /**
     * Makes pairs from the list of words is the given order
     * @param words - list of words
     * @return list of word pairs
     */
    public static List<WordPair> toWordPairs(List<Word> words) {
        List<WordPair> wordPairs = new ArrayList<>();
        String last = null;
        for (Word word : words) {
            String current = word.getWords().get(0);
            if (last != null) {
                wordPairs.add(new WordPair(last, current));
            }
            last = current;
        }
        return wordPairs;
    }


    /**
     * Breaks texts into words. First splits by white space and then strips
     * starting and ending non-alphanumeric characters, or words that start with number.
     * E.g. 'It can't be true', said the good-hearted? 22 : and that's all there is to it.
     * Would give words as - It can't be true said the good-hearted and that's all there is to it
     * @param text - text to split
     * @return list of Word objects
     */
    public static List<Word> toWords(String text) {
        return Arrays.stream(text.split("\\s+"))
                .map(s -> s.replaceAll("^[\\W]*(.*?)[\\W]*$", "$1")
                        .replaceAll("^\\d.*", ""))
                .filter(s -> !s.isEmpty())
                .map(Word::new)
                .collect(Collectors.toList());
    }

}
