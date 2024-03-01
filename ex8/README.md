## Language
### Java
I use Java to implement the term frequency counting.

## HOW TO RUN
1. Normal Term Frequency
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
2. Generate frequency based on the starting alphabet.
```
javac ThirtytwoAlphabet.java                       
java ThirtytwoAlphabet ./../pride-and-prejudice.txt > ThirtytwoAlphabet.txt
```

In order to give TA the easiest way to run, I have wrapped all the commands we need into the file: ```run.sh```, you can simply run the command ```bash run.sh```, to evaluate this assignment.

### P.S.
Queue selection for ```Twentynine.java```. We cannot select the normal queue in Java which may cause data missing. Instead, we need to use BlockingQueue.
