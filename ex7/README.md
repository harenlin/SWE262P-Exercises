## Language
### Java
I use Java to implement the term frequency counting.

## HOW TO RUN
You can run the command:
```shell
javac Iterators.java
java Iterators ./../pride-and-prejudice.txt FINAL_ROPORT_ONLY > Iterators.txt
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

In order to give TA the easiest way to run, I have wrapped all the commands we need into these 2 files: ```runIterators.sh``` and ```runStreams.sh```, you can simply run the command ```bash run*```, to evaluate this assignment.

### P.S.
The provided code in tf-28.py generates a temporary frequency report every 5000 words. I have sought Prof. Crista's feedback on the assignment, and she emphasized that the abundance of temporary reports is not a concern. The crucial aspect is ensuring that the final report produces accurate outputs.
