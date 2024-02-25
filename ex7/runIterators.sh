javac Iterators.java
java Iterators ./../pride-and-prejudice.txt FINAL_ROPORT_ONLY > Iterators_Final.txt
java Iterators ./../pride-and-prejudice.txt INCLUDE_TEMPORARY_REPORT > Iterators_Full.txt
rm *.class
diff Iterators_Final.txt ./../test_out.txt
