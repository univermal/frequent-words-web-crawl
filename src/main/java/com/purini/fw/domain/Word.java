package com.purini.fw.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * represent a single word
 */
public class Word implements WordConstruct<Word> {

    private String word;

    public Word(String word) {
        this.word = word;
    }

    @Override
    public List<String> getWords() {
        return Collections.singletonList(word);
    }

    @Override
    public String getValue() {
        return word;
    }

    @Override
    public Word toLowerCase() {
        return new Word(word.toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word1 = (Word) o;
        return word.equals(word1.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public int compareTo(Word o) {
        return getValue().compareTo(o.getValue());
    }
}
