## Language
### Java
I use Java to implement the term frequency counting.

## HOW TO RUN
1. [Iterators] You can run the commands:
```shell
javac Iterators.java
java Iterators ./../pride-and-prejudice.txt FINAL_ROPORT_ONLY > Iterators_Final.txt
java Iterators ./../pride-and-prejudice.txt INCLUDE_TEMPORARY_REPORT > Iterators_Full.txt
diff Iterators_Final.txt ./../test_out.txt
```
to generate the temporary and final report of TF counting. 
And then, we can check the final report generated from the iterators is correct or not (with the given correct report, i.e., ```test_out.txt```).

2. [Stremas] You can run the commands:
```shell
javac Streams.java
java Streams ./../pride-and-prejudice.txt > Streams.txt
diff Streams.txt Iterators_Full.txt 
```
to generate the output text file contains full reports.
And then, we can check the correctness by comparing the ```Iterators_Full.txt``` and ```Streams.txt```.

3. In order to give TA the easiest way to run, I have wrapped all the commands we need into these 2 files: ```runIterators.sh``` and ```runStreams.sh```, you can simply run the command ```bash run*```, to evaluate this assignment.

### P.S.
The provided code in tf-28.py generates a temporary frequency report every 5000 words. I have sought Prof. Crista's feedback on the assignment, and she emphasized that the abundance of temporary reports is not a concern. The crucial aspect is ensuring that the final report produces accurate outputs.
