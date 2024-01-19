package main

import "os"
import "fmt"
import "io/ioutil"
import "strings"
import "slices"
import "sort"
import "unicode"


// The shared mutable data
var data[]rune
var words []string
var word_freqs []struct {
	Word string
	Freq int
}


func read_file(path_to_file string) {
	tmpData, err := ioutil.ReadFile(path_to_file)
	if err != nil {
		panic(err)
	}
	data = append(data, []rune(string(tmpData))...)
}


func filter_chars_and_normalize() {
	for i := 0; i < len(data); i++ {
		if !(unicode.IsLetter(data[i]) || unicode.IsNumber(data[i])) {
			data[i] = ' '
		} else {
			data[i] = []rune(strings.ToLower(string(data[i])))[0]
		}
	}
}


func scan() {
	data_str := string(data)
	words = append(words, strings.Fields(data_str)...)
}


func remove_stop_words() {
	stop_words_text, err := ioutil.ReadFile("./../stop_words.txt")
	if err != nil {
        panic(err)
    }
	stop_words := strings.Split(string(stop_words_text[:]), ",")
	// add single-letter words
	for _, char := range "abcdefghijklmnopqrstuvwxyz" {
		stop_words = append(stop_words, string(char))
	}

	var indexes []int

	for i := 0; i < len(words); i++ {
		if slices.Contains(stop_words, words[i]) {
			indexes = append(indexes, i)
		}
	}

	for i := len(indexes) - 1; i >= 0; i-- {
		words = append(words[:indexes[i]], words[indexes[i] + 1:]...)
	}
}


func frequencies() {
    // Creates a list of pairs associating words with frequencies
	for _, word := range words {
		keys := make([]string, 0, len(words))
		for _, v := range word_freqs {
			keys = append(keys, v.Word)
		}
		if slices.Contains(keys, word) {
			for idx, v := range word_freqs {
				if v.Word == word {
					word_freqs[idx].Freq += 1
					break
				}
			}
		} else {
			word_freqs = append(word_freqs, struct {
				Word string
				Freq int
			}{
				Word: word, 
				Freq: 1,
			})
		}
	}
}


func sort_() {
    // Sorts word_freqs by frequency
	sort.Slice(word_freqs, func(i int, j int) bool {
		return word_freqs[i].Freq > word_freqs[j].Freq
	})
}


func main() {
	if len(os.Args) < 2 {
		fmt.Println("Please include the file path!")
		return
	}

	read_file(os.Args[1])	
	filter_chars_and_normalize()
	scan()
	remove_stop_words()
	frequencies()
	sort_()

	for _, v := range word_freqs[:25] {
		fmt.Printf("%s  -  %d\n", v.Word, v.Freq)
	}
}
