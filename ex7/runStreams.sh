javac Streams.java
java Streams ./../pride-and-prejudice.txt > Streams.txt
rm *.class
diff Streams.txt Iterators_Full.txt 
