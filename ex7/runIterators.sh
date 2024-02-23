javac Iterators.java
java Iterators ./../pride-and-prejudice.txt FINAL_ROPORT_ONLY > Iterators_FINAL_REPORT_ONLY.txt
java Iterators ./../pride-and-prejudice.txt INCLUDE_TEMPORARY_REPORT > Iterators_INCLUDE_TEMPORARY_REPORT.txt
rm *.class
diff Iterators_FINAL_REPORT_ONLY.txt ./../test_out.txt

# "FINAL_ROPORT_ONLY", "INCLUDE_TEMPORARY_REPORT"
