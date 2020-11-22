package com.purini.fw.process;

import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;
import com.purini.fw.utils.StopWordsUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Contains the stores for the found words and word pairs.
 * Thus provides a single place to access the output.
 */
public class WordCountStoreService {

    private final WordCountStore<Word> wordCountStore;
    private final WordCountStore<WordPair> wordPairCountStore;
    private final StopWordsUtil stopWordsUtil;

    public WordCountStoreService() {
        this.wordCountStore = new WordCountStore<>();
        this.wordPairCountStore = new WordCountStore<>();
        this.stopWordsUtil = new StopWordsUtil();
    }

    public void addWords(List<Word> words) {
        wordCountStore.addWords(stopWordsUtil.copyWithoutStopWords(words));
    }

    public void addWordPairs(List<WordPair> wordPairs) {
        wordPairCountStore.addWords(stopWordsUtil.copyWithoutStopWordPairs(wordPairs));
    }

    public List<Pair<Word, Integer>> getTopNWords(int n) {
        return wordCountStore.getTopNWords(n);
    }

    public List<Pair<WordPair, Integer>> getTopNWordPairs(int n) {
        return wordPairCountStore.getTopNWords(n);
    }


}
