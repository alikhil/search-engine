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

Index is stored in RAM as scala `Map` data structure, where key is a term and value is a list of document in which that term exists.

After building index it saved to disk as `index.bin` file and can be read on next program start.

### Boolean Queries

Current implementation supports OR(|), AND(&), NOT(!) operators and grouping them with parenthesis. Also operator AND can be ommited.

For example `(Innopolis University) | (MIT & USA)`  request with such query with respond with documents containg either **Innopolis University** or **MIT USA** somewhere in document.

**Operators priority depends on their relative position. Please use parenthesis to define order of operations.**

## Screenshots

<details>
    <summary>Expand</summary>

Initial screen.

![image](https://user-images.githubusercontent.com/7482065/30243791-f6827fba-95b9-11e7-9426-a4e47f5487fa.png)

After pressing `Build Index`
![image](https://user-images.githubusercontent.com/7482065/30243806-20a0b71c-95ba-11e7-971a-b699781cba97.png)

Enter query and press `Search`
![image](https://user-images.githubusercontent.com/7482065/30243590-7aa9bd58-95b5-11e7-963c-cc396d034b21.png)
</details>
