# search-engine

Mini search engine based on [Inverted Index](https://en.wikipedia.org/wiki/Inverted_index).

## Dataset

[Dataset](https://github.com/alikhil/search-engine/tree/master/dataset) contains about 6000 article abstracts from LISA collection.

## Getting started

1. Ensure that you have Java 8 JDK (`javac -version` should result with `javac 1.8.*`)
1. Install [sbt](http://www.scala-sbt.org/download.html)
1. Clone repository - `git clone https://github.com/alikhil/search-engine`
1. `cd search-engine/search-engine`

```bash
$ sbt
> container:start
```

5. Navigate to [localhost:9090](http://localhost:9090)
6. Press `Build Index` button to index all the documents. Wait
7. Enter your query and press `Search`.
8. See the results.

P.S. Indexing may take a while. Please, don't press `Rebuild Index` without no needs.

## Architecture Design

### User Interface

I have choosen Web as UI, because it easier and faster for me to develop.

Used tools:

* [Bootstrap 3](http://getbootstrap.com/) template
* Hiltor.js - for highlighting query in results
* WatingDialag.js
* Scalatra - for backend

### Parsing Documents

To build index all documents in files from `dataset` directory which satisfy regexp `^LISA\d\.\d{3}` are used.

All tokens containing numbers and charecters like `.,:?@$%^*` are removed.

Then documents are lemmanized using [Standford NLP](https://stanfordnlp.github.io/CoreNLP/index.html) library.

### Index

Index is stored in RAM as scala `Map` data structure, where key is a term and value is a sorted map where key is document id in which that term exists and value is *term frequency*.

After building index it saved to disk as `index.bin` file and can be read on next program start.

### Ranked Search

Now, engine returns first 20 results sorted by descending of their scores. For ranking **ltc.lnc** is used.

## Evaluation

For evaluation query *Machine Learning* was used. The engine marked 100 documents as relevant for this query.
I labeled them by hand and find out that there is only 8 relevant documents. Result is [here](https://github.com/alikhil/search-engine/blob/master/dataset/EVAL).

Only 4 of 20 documents returned in response are relevant:
|               | Relevant | Nonrelevant |
|---------------|----------|-------------|
| Retrived      |4         |16           |
| Not Retrieved |4         |76           |

Recall = 4 / (4 + 4) = 0.5

Precision = 4 / (4 + 16) = 0.2

F1 = 2 * (Precision * Recall) / (Precion + Recall) = 0.29

## Changes from previous version

* Inverted index was updated to store not only document ids, but also term frequencies.

* Ranking method with cosince similarity was implemented.

## Screenshots

<details>
    <summary>Expand</summary>

Initial screen.

![image](https://user-images.githubusercontent.com/7482065/32135143-c88cb7b4-bc02-11e7-92ef-7d42cc4e28ad.png)

After pressing `Build Index`
![image](https://user-images.githubusercontent.com/7482065/30243806-20a0b71c-95ba-11e7-971a-b699781cba97.png)

Enter query and press `Search`
![image](https://user-images.githubusercontent.com/7482065/32135131-7b107610-bc02-11e7-863a-620abadd92b8.png)
</details>
