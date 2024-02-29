javac Thirty.java
java Thirty ./../pride-and-prejudice.txt > Thirty.txt
diff Thirty.txt ./../test_out.txt
rm *.class
