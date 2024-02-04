go run Nine.go ./../pride-and-prejudice.txt > Nine.txt
diff Nine.txt ./../test_out.txt

go run Ten.go ./../pride-and-prejudice.txt > Ten.txt
diff Ten.txt ./../test_out.txt

# javac Ten.java 
# java Ten ./../pride-and-prejudice.txt > Ten.txt
# diff Ten.txt ./../test_out.txt
