## Language
### Java
I use Java to implement the term frequency counting.

## HOW TO RUN
```shell
javac Twentynine.java
java Twentynine ./../pride-and-prejudice.txt > Twentynine.txt
diff Twentynine.txt ./../test_out.txt

javac Thirty.java
java Thirty ./../pride-and-prejudice.txt > Thirty.txt
diff Thirty.txt ./../test_out.txt

javac Thirtytwo.java
java Thirtytwo ./../pride-and-prejudice.txt > Thirtytwo.txt
diff Thirtytwo.txt ./../test_out.txt
```
In order to give TA the easiest way to run, I have wrapped all the commands we need into the file: ```run.sh```, you can simply run the command ```bash run.sh```, to evaluate this assignment.


### P.S.
Queue selection for 29.java.
