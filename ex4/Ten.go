package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"regexp"
	"sort"
	"strings"
	"strconv"
)

type TFTheOne struct {
	value interface{}
}

func (t *TFTheOne) bind(f func(interface{}) interface{}) *TFTheOne {
	t.value = f(t.value)
	return t
}

func (t *TFTheOne) printMe() {
	fmt.Println(t.value)
}

var read_file = func(object interface{}) interface{} {
	fileContent, _ := ioutil.ReadFile(object.(string))
	return string(fileContent)
}

var filter_chars = func(object interface{}) interface{} {
	str_data := object.(string)
	re := regexp.MustCompile(`[\W_]+`)
	return re.ReplaceAllString(str_data, " ")
}

var normalize = func(object interface{}) interface{} {
	return strings.ToLower(object.(string))
}

var scan = func(object interface{}) interface{} {
	return strings.Fields(object.(string))
}

var remove_stop_words = func(object interface{}) interface{} {
	stop_words := make(map[string]struct{})
	fileContent, _ := ioutil.ReadFile("./../stop_words.txt")
	for _, word := range strings.Split(string(fileContent), ",") { 
		stop_words[word] = struct{}{}
	}
	for _, char := range "abcdefghijklmnopqrstuvwxyz" { 
		stop_words[string(char)] = struct{}{}
	}
	word_list := object.([]string)
	var stopwords_removed_word_list []string
	for _, w := range word_list { 
		if _, isStopword := stop_words[w]; !isStopword { 
			stopwords_removed_word_list = append(stopwords_removed_word_list, w)
		} 
	}
	return stopwords_removed_word_list
}


var frequencies = func(object interface{}) interface{} {
	word_list := object.([]string)
	wf := make(map[string]int)
	for _, w := range word_list { 
		if _, exist := wf[w]; exist { 
			wf[w]++ 
		} else {
			wf[w] = 1
		}
	}
	return wf
}

var sort_map = func(object interface{}) interface{} {
	word_freqs := object.(map[string]int)
	var sorted_word_freqs []struct {Word string; Freq int}
	for word, freq := range word_freqs {
		sorted_word_freqs = append(sorted_word_freqs, struct {Word string; Freq int}{Word: word, Freq: freq})
	}
	sort.Slice(sorted_word_freqs, func(i int, j int) bool {
		return sorted_word_freqs[i].Freq > sorted_word_freqs[j].Freq
	})
	return sorted_word_freqs
}

var top25_freqs = func(object interface{}) interface{} {
	sorted_word_freqs := object.([]struct {Word string; Freq int})
	var top25 strings.Builder
	for i := 0; i < 25 && i < len(sorted_word_freqs); i++ {
		top25.WriteString(sorted_word_freqs[i].Word)
		top25.WriteString("  -  ")
		top25.WriteString(strconv.Itoa(sorted_word_freqs[i].Freq))
		top25.WriteString("\n")
	}
	res := top25.String()
	return res[:len(res) - 1]
}

func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: go run Ten.go <path_to_file>")
		return
	}

	t := &TFTheOne{value: os.Args[1]}
	t.bind(read_file).
	  bind(filter_chars).
      bind(normalize).
	  bind(scan).
	  bind(remove_stop_words).
	  bind(frequencies).
	  bind(sort_map).
	  bind(top25_freqs).
	  printMe()
}
