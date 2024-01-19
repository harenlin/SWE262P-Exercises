## Language
### Golang
I use golang to implement the term frequency counting.

## HOW TO RUN
You can run the command:
```shell
go run Four.go ./../pride-and-prejudice.txt  >  Four.txt
go run Five.go ./../pride-and-prejudice.txt  >  Five.txt
go run Six.go ./../pride-and-prejudice.txt  >  Six.txt
```
to generate the output text file.

And run 
```shell
 diff Four.txt ./../test_out.txt 
 diff Five.txt ./../test_out.txt
 diff Six.txt ./../test_out.txt 
```
to see if the result is matched or not.
