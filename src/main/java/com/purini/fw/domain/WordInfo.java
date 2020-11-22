package com.purini.fw.domain;

/**
 * Contains
 * - word which could be in lower case to ease comparisons
 * - original word, used for output
 * @param <T> Word or WordPair
 */
public class WordInfo<T extends WordConstruct<T>> implements Comparable<WordInfo<T>> {

    private T word;
    private T originalWord;

    public WordInfo(T word, T originalWord) {
        this.word = word;
        this.originalWord = originalWord;
    }

    @Override
    public int compareTo(WordInfo<T> o) {
        return word.getValue().compareTo(o.getWord().getValue());
    }

    public T getWord() {
        return word;
    }

    public T getOriginalWord() {
        return originalWord;
    }

}
