javac Streams.java
java Streams ./../pride-and-prejudice.txt > Streams.txt
rm *.class
diff Streams.txt ./../test_out.txt
