package com.purini.fw.domain;

import java.util.List;

/**
 * Abstraction over word or word pair
 * @param <T> word or word pair
 */
public interface WordConstruct<T> extends Comparable<T>{

    List<String> getWords();
    String getValue();
    T toLowerCase();

}
