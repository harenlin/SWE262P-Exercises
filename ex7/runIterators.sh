javac Iterators.java
java Iterators ./../pride-and-prejudice.txt FINAL_ROPORT_ONLY > Iterators.txt
rm *.class
diff Iterators.txt ./../test_out.txt
