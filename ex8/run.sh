javac Thirty.java
java Thirty ./../pride-and-prejudice.txt > Thirty.txt
diff Thirty.txt ./../test_out.txt

javac Thirtytwo.java
java Thirtytwo ./../pride-and-prejudice.txt > Thirtytwo.txt
diff Thirtytwo.txt ./../test_out.txt

rm *.class
