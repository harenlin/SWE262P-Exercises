## Language
### Java
I use Java to implement the term frequency counting.

## HOW TO RUN
You can run the command:
```shell
javac Iterators.java
java Iterators ./../pride-and-prejudice.txt > Iterators.txt
```
, and
```shell
javac Streams.java
java Streams ./../pride-and-prejudice.txt > Streams.txt
```
to generate the output text file.

And run 
```shell
diff Iterators.txt ./../test_out.txt
diff Streams.txt ./../test_out.txt
```
to see if the result is matched or not.
