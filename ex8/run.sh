javac Twentynine.java
java Twentynine ./../pride-and-prejudice.txt > Twentynine.txt
diff Twentynine.txt ./../test_out.txt

javac Thirty.java
java Thirty ./../pride-and-prejudice.txt > Thirty.txt
diff Thirty.txt ./../test_out.txt

javac Thirtytwo.java
java Thirtytwo ./../pride-and-prejudice.txt > Thirtytwo.txt
diff Thirtytwo.txt ./../test_out.txt

javac ThirtytwoAlphabet.java                       
java ThirtytwoAlphabet ./../pride-and-prejudice.txt > ThirtytwoAlphabet.txt

rm *.class
