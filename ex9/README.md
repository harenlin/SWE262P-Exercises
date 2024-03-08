## Language
#### Python3

## Exercise Spec
Using Python + numpy, or any other array programming language, 
implement a program that takes as input an array of characters 
(the characters from ```pride-and-prejudice```, for example), 
normalizes to UPPERCASE, 
ignores words smaller than 2 characters, 
and replaces the vowels with their [Leet](https://simple.wikipedia.org/wiki/Leet) counterparts when there is a one-to-one mapping. 
Then it prints out the 5 most frequently occurring 2-grams. 
(2-grams are all 2 consecutive words in a sequence) 
Note that you should stick to the array programming style as much as possible. 
This means: avoid explicit iteration over the elements of the array. 
If you find yourself writing an iteration, 
think of how you could do it with one or more array operations. 
(Sometimes, it's not possible; but most often it is)

## HOW TO RUN
0. Please make sure the numpy is installed: ```pip install numpy```.
1. Simply run the command on the terminal: ```python3 Three.py ./../pride-and-prejudice.txt```.


## Description
For the Leet, I create the mapping of 26 uppercase alphabets to the corresponding one. 
Since the spec only mentions the transformation of VOWELs, i.e., `A`, `E`, `I`, `O`, `U`,
 I just change the value of these 5 characters whenever I encounter them.
However, `U` cannot be transformed to another character (the listed leet are all more than 1 char), 
so the transformed leet for `U` is still `U`.

## Code Author
Hao-Lun Lin (laolunl@uci.edu)
