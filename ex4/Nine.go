package main

import (
	"fmt"
	"io/ioutil"
	"regexp"
	"strings"
	"sort"
	"os"
)


/*
-> read_file(path_to_file, func = filter_chars) 
-> 		Call: filter_chars(str_data, func = normalize)
-> 			Call: normalize(..., scan)
-> 				Call: scan(..., remove_stop_words)
-> 					Call: remove_stop_words(word_list, frequencies)
-> 						Call: frequencies(word_list, sort)
->                      	Call: sort(, print_text)
-> 								Call: print_text(, no_op)
*/


func read_file(path_to_file string, 
			   fn func(string, func(string, func(string, func([]string, func([]string, func(map[string]int, func([]struct{Word string; Freq int}, func(func()))))))))){
	fileContent, _ := ioutil.ReadFile(path_to_file)
	fn(string(fileContent), normalize)
}


func filter_chars(str_data string, 
				  fn func(string, func(string, func([]string, func([]string, func(map[string]int, func([]struct{Word string; Freq int}, func(func())))))))){
	re := regexp.MustCompile(`[\W_]+`)
	str_data = re.ReplaceAllString(str_data, " ")
	fn(str_data, scan)
}


func normalize(str_data string, 
               fn func(string, func([]string, func([]string, func(map[string]int, func([]struct{Word string; Freq int}, func(func()))))))){
	fn(strings.ToLower(str_data), remove_stop_words)
}


func scan(str_data string, 
          fn func([]string, func([]string, func(map[string]int, func([]struct{Word string; Freq int}, func(func())))))){
	fn(strings.Fields(str_data), frequencies)
}

 
func remove_stop_words(word_list []string, 
                       fn func([]string, func(map[string]int, func([]struct{Word string; Freq int}, func(func()))))){	
	stopWords := make(map[string]struct{})
	fileContent, _ := ioutil.ReadFile("./../stop_words.txt")
	for _, word := range strings.Split(string(fileContent), ",") { 
		stopWords[word] = struct{}{}
	}
	for _, char := range "abcdefghijklmnopqrstuvwxyz" { 
		stopWords[string(char)] = struct{}{}
	}

	var stopwords_removed_word_list []string
	for _, w := range word_list { 
		if _, isStopword := stopWords[w]; !isStopword { 
			stopwords_removed_word_list = append(stopwords_removed_word_list, w)
		} 
	}

	fn(stopwords_removed_word_list, sort_map)
}


func frequencies(word_list []string, 
                 fn func(map[string]int, func([]struct{Word string; Freq int}, func(func())))){
	wf := make(map[string]int)
	for _, w := range word_list { 
		if _, exist := wf[w]; exist { 
			wf[w]++ 
		} else {
			wf[w] = 1
		}
	}

	fn(wf, print_text)
} 


func sort_map(word_freqs map[string]int, fn func([]struct{Word string; Freq int}, func(func()))){
	var sorted_word_freqs []struct {Word string; Freq int}

	for word, freq := range word_freqs {
		sorted_word_freqs = append(sorted_word_freqs, struct {Word string; Freq int}{Word: word, Freq: freq})
	}

	sort.Slice(sorted_word_freqs, func(i int, j int) bool {
		return sorted_word_freqs[i].Freq > sorted_word_freqs[j].Freq
	})

	fn(sorted_word_freqs, no_op);
}


func print_text(sorted_word_freqs []struct{Word string; Freq int}, fn func(fn func())){
	for _, v := range sorted_word_freqs[:25] { 
		fmt.Printf("%s  -  %d\n", v.Word, v.Freq) 
	}
}


func no_op(fn func()) {
    return 
}


func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: go run Nine.go <path_to_file>")
		os.Exit(1)
	}

	read_file(os.Args[1], filter_chars)
}
