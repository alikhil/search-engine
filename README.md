# search-engine
Mini project in information retrieval course

## Getting start

1. Ensure that you have Java 8 JDK (`javac -version` should result with `javac 1.8.*`)
1. Install [sbt](http://www.scala-sbt.org/download.html)
1. Clone repository - `git clone https://github.com/alikhil/search-engine`
1. `cd search-engine/search-engine`

```bash
$ sbt
> container:start
```

5. Navigate to [localhost:8080](http://localhost:9090)
6. Press `Build Index` button to index all the documents. Wait
7. Enter your query and press `Search`.
8. See the results.

P.S. Indexing may take a while. Please, don't press `Rebuild Index` without any needs.

## Screenshots

Initial screen.

![image](https://user-images.githubusercontent.com/7482065/30243791-f6827fba-95b9-11e7-9426-a4e47f5487fa.png)

After pressing `Build Index`
![image](https://user-images.githubusercontent.com/7482065/30243806-20a0b71c-95ba-11e7-971a-b699781cba97.png)

Enter query and press `Search`
![image](https://user-images.githubusercontent.com/7482065/30243590-7aa9bd58-95b5-11e7-963c-cc396d034b21.png)