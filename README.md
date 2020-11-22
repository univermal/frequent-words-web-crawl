# Frequent Words Analysis for Websites

Given a web url and max depth, crawl its content to find frequent words and word pairs.

Basic approach is to use BFS for traversing the links, parse them using a DOM parser (Jsoup is used in this case) and finally pass it to Document Processor which does text processing to come up with top frequent words and word pairs.

## Main functionalitites

### Url Traverser: 

BFS Vs DFS: I have chosen BFS for the following reasons:

- Although DFS is more memory efficient in this case because as as soon as a document is parsed it can be passed over asynchronously, but memory can still be managed in other ways. Since fetching content is IO bound, that is much more important get parallelized, and which is pretty hard in DFS.
- BFS allows for immediate levels child documents to be fetched in parallel and that provides better performance time wise.
- Memory is partly controlled using a fixed thread pool for receiving the documents for text analysis in a LinkedBlockingQueue so that back pressure is maintained. However memory can still blow up if there too much breadth an given level. **An implementation of DFS (UrlTraverserDFS) is put under tests for the same reason if you fall into that scenario and not able to devote more memory, then feel free to use that.**
- Another direct algorithmic benefit of BFS in situation when there can be more than one path to a resource. In that case we would like to give more priority to a shallower child than to a deeper one, especially in frequent word analysis as the shallower ones should be considered more relevant. In DFS this is again hard to achieve.

### Word Store

The basic goal here is store the words and track their frequency. To optimize on what data structures to use for storing, following points were considered:

- Hashed index on the word is needed so that it's frequency can be updated
- The results need to be in sorted order of decreasing frequencies. Since the collection is unbounded, it would be costly to sort it at the end. Instead if it can be kept sorted always, that would be more efficient.
- For these reasons a combination of HashMap (word -> frequency) and TreeMultimap (frequency -> word) have been used. The actual value type of the maps is bit more complex as apart from lower case word (used for all string comparisons), original word is also preserved, for use in the final output.

### Document Processor

This is a simple completion service based implementation to track completion from outside.

### Text Analysis

- Stop words have been used from https://www.textfixer.com/tutorials/common-english-words.txt so that the result is more relevant.
- Stop words that appear in full capitals have still been retained. E.g. IT
- Punctuation has been stripped off using regular expression with care that hyphenated words are preserved and also apostrophe in between the characters is preserved. E.g. go-live, McDonald's 


## USAGE


         new Application(<maxDepth>, <url>, <internalLinksOnly>).getTopNWordsAndWordPairs(<topNCount>);

Here are the results from https://en.wikipedia.org/wiki/Main_Page for depth=4, internalLinksOnly=true and topNCount=10:

>[doc-processor-4] DEBUG c.p.fw.process.DocumentProcessor[38] - docs processed - 199
>[main] INFO  com.purini.fw.Application[99] - top 10 words - [(Retrieved,5357), (November,4574), (Wikipedia,3354), (Edit,2917), (b,2670), (new,2442), (Talk,2369), (COVID-19,2259), (article,2096), (March,2091)]
>[main] INFO  com.purini.fw.Application[100] - top 10 word pairs - [(the original,1857), (original on,1079), (November UTC,1076), (the first,833), (articles with,807), (United States,779), (COVID-19 pandemic,698), (Archived from,673), (New York,672), (on November,590)]
>[main] DEBUG com.purini.fw.Application[101] - seconds elapsed - 28

Also, some of the config related to thread pool can be tweaked from application.properties

Have fun!



