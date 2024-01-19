package main

import "os"
import "fmt"

import "bufio"

import "io/ioutil"
// import "regexp"
import "strings"
// import "slices"
// import "sort"

import "unicode"


func main () {
	if len(os.Args) < 2 {
		fmt.Println("Please include the file path!")
		return
	}


	// the list of stop words
	stop_words_text, err := ioutil.ReadFile("./../stop_words.txt")
	if err != nil {
		fmt.Println("Error opening stop words textfile:", err)
        return 
    }
	stop_words := strings.Split(string(stop_words_text[:]), ",")
	for _, char := range []rune("abcdefghijklmnopqrstuvwxyz") {
		stop_words = append(stop_words, string(char))
	}
		
	// iterate through the file one line at a time 
	file, err := os.Open(os.Args[1])
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}
	defer file.Close()
	scanner := bufio.NewScanner(file)

	for scanner.Scan() {
		line := scanner.Text()
		fmt.Println(line)

		var start_char = -1 // I set -1 as None
		var i = 0

		for _, char := range line {
			fmt.Println(char)
			if start_char == -1 {
				if unicode.IsLetter(char) || unicode.IsNumber(char) {
					// We found the start of a word
					start_char = i
				}
			} else {
				// We found the end of a word. Process it
				// var found = false
				word := strings.ToLower(line[start_char:i])
				// Ignore stop words
				fmt.Println(word)
			}
			i += 1
		}
		break
	}

}


/* func read_file(path_to_file string) (string, error){
	data, err := ioutil.ReadFile(path_to_file)
	if err != nil {
		return "", err
	}

	return string(data[:]), nil
}


func filter_chars_and_normalize(str_data string) string {
	pattern := regexp.MustCompile(`[\W_]+`)
	return strings.ToLower(pattern.ReplaceAllString(str_data, " "))
}


func scan(str_data string) []string {
	return strings.Split(str_data, " ")
}


func remove_stop_words(tokens []string) ([]string, error) {
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

	data, err := read_file(os.Args[1])
	if err != nil {
		fmt.Println("Error:", err)
		return
	}

	data = filter_chars_and_normalize(data)

	tokens := scan(data)
	
	tokens, err = remove_stop_words(tokens)
	if err != nil {
		fmt.Println("Error:", err)
		return
	}

	word_freqs := frequencies(tokens)
	
	sorted_word_freqs := sort_map(word_freqs)

	print_all(sorted_word_freqs[:25])
}
*/
