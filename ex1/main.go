package main

import "os"
import "fmt"
import "io/ioutil"
import "regexp"
import "strings"
import "slices"
import "sort"


func read_file(path_to_file string) (string, error){
	/*
    Takes a path to a file and returns the entire
    contents of the file as a string.
    */
	data, err := ioutil.ReadFile(path_to_file)
	if err != nil {
		return "", err
	}

	return string(data[:]), nil
}


func filter_chars_and_normalize(str_data string) string {
	/*
	Takes a string and returns a copy with all nonalphanumeric 
    chars replaced by white space.
	*/
	pattern := regexp.MustCompile(`[\W_]+`)
	return strings.ToLower(pattern.ReplaceAllString(str_data, " "))
}


func scan(str_data string) []string {
	/*
	Takes a string and scans for words, returning
    a list of words.
	*/
	return strings.Split(str_data, " ")
}


func remove_stop_words(tokens []string) ([]string, error) {
	/*
	Takes a list of words and returns a copy with all stop 
    words removed.
	*/
	stop_words_text, err := ioutil.ReadFile("./../stop_words.txt")
	if err != nil {
        return nil, err
    }
	stop_words := strings.Split(string(stop_words_text[:]), ",")

	// add single-letter words
	for _, char := range "abcdefghijklmnopqrstuvwxyz" {
		stop_words = append(stop_words, string(char))
	}

	filtered_tokens := make([]string, 0)
	for _, token := range tokens {
		if slices.Contains(stop_words, token) == false {
			filtered_tokens = append(filtered_tokens, token)
		}
	}

	return filtered_tokens, nil
}


func frequencies(word_list []string) map[string]int {
	/*
	Takes a list of words and returns a dictionary associating
    words with frequencies of occurrence.
	*/
	word_freqs := make(map[string]int)

	for _, word := range word_list {
		if count, ok := word_freqs[word]; ok {
			word_freqs[word] = count + 1
		} else {
			word_freqs[word] = 1
		}
	}

	return word_freqs
}


func sort_map(word_freqs map[string]int) []struct {
	Word string
	Freq int
} {
	/*
	Takes a dictionary of words and their frequencies
    and returns a list of pairs where the entries are
    sorted by frequency.
	*/
	var sorted_word_freqs []struct {
		Word string
		Freq int
	}

	for word, freq := range word_freqs {
		sorted_word_freqs = append(sorted_word_freqs, struct {
			Word string
			Freq int
		}{
			Word: word, 
			Freq: freq,
		})
	}

	// It looks really like C++ std::sort
	sort.Slice(sorted_word_freqs, func(i int, j int) bool {
		return sorted_word_freqs[i].Freq > sorted_word_freqs[j].Freq
	})
	
	return sorted_word_freqs
}


func print_all(sorted_word_freqs []struct {
	Word string
	Freq int
}) {
	/*
	Takes a list of pairs where the entries are sorted 
	by frequency and print them recursively.
	*/ 
	if len(sorted_word_freqs) > 0 {
		fmt.Printf("%s  -  %d\n", sorted_word_freqs[0].Word, sorted_word_freqs[0].Freq)
		print_all(sorted_word_freqs[1:])
	}
} 


func main() {
	if len(os.Args) < 2 {
		fmt.Println("Please include the file path!")
		return
	}

	/* Part1. Read in text file. */
	data, err := read_file(os.Args[1])
	if err != nil {
		fmt.Println("Error:", err)
		return
	}

	/* Part2. Filter characters and Normalize. */
	data = filter_chars_and_normalize(data)

	/* Part3. Scaning (Turn to list of strings). */
	tokens := scan(data)
	
	/* Part4. Remove stop words. */
	tokens, err = remove_stop_words(tokens)
	if err != nil {
		fmt.Println("Error:", err)
		return
	}

	/* Part5. Counting. */
	word_freqs := frequencies(tokens)
	
	/* Part6. Sorting. */
	sorted_word_freqs := sort_map(word_freqs)

	/* Part7. Print results. */
	print_all(sorted_word_freqs[:25])
}
