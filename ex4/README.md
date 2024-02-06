## Language
### Golang
I use golang to implement the term frequency counting.

### Explanation
For the styles 15 and 16, I choose 16 to do the practice.

## HOW TO RUN
You can run the command:
```shell
go run Nine.go ./../pride-and-prejudice.txt > Nine.txt
go run Ten.go ./../pride-and-prejudice.txt > Ten.txt
go run Sixteen.go ./../pride-and-prejudice.txt > Sixteen.txt
```
to generate the output text file.

And run 
```shell
diff Nine.txt ./../test_out.txt
diff Ten.txt ./../test_out.txt
diff Sixteen.txt ./../test_out.txt
```
to see if the result is matched or not.

For the ```Sixteen.txt```, you will see the only difference as below:
```shell
26d25
< The number of non-stop words with the letter z is 837.
```

