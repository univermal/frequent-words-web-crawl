package com.purini.fw.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a word pair, separator is by default a space
 */
public class WordPair implements WordConstruct<WordPair> {

    private String word1;
    private String word2;
    private String separator;

    public WordPair(String word1, String word2) {
        this(word1, word2, " ");
    }

    public WordPair(String word1, String word2, String separator) {
        this.word1 = word1;
        this.word2 = word2;
        this.separator = separator;
    }

    @Override
    public List<String> getWords() {
        return Arrays.asList(word1, word2);
    }

    @Override
    public String getValue() {
        return word1 + separator + word2;
    }

    @Override
    public WordPair toLowerCase() {
        return new WordPair(word1.toLowerCase(), word2.toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordPair wordPair = (WordPair) o;
        return word1.equals(wordPair.word1) &&
                word2.equals(wordPair.word2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word1, word2);
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public int compareTo(WordPair o) {
        return getValue().compareTo(o.getValue());
    }
}
