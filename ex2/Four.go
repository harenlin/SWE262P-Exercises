package main

// For reading path arguments.
import "os"

// For printing something out.
import "fmt"

// For reading stop_words and the pride-and-prejudice text files.
import "bufio"
import "io/ioutil"

// For transforming strings e.g. to lower case
import "strings"

// For checking the characters e.g. isalnum
import "unicode"


func main () {
	if len(os.Args) < 2 {
		fmt.Println("Please include the file path!")
		return
	}

	// the global list of [word, frequency] pairs
	var word_freqs []struct {
		Word string
		Freq int
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
		line := scanner.Text() + "\n"

		var start_char = -1 // I set -1 as None

		for i, char := range line {
			if start_char == -1 {
				if unicode.IsLetter(char) || unicode.IsNumber(char) {
					// We found the start of a word
					start_char = i
				}
			} else {
				if !unicode.IsLetter(char) && !unicode.IsNumber(char) {
					// We found the end of a word. Process it.
					var found = false
					word := strings.ToLower(line[start_char:i])

					// Ignore stop words

					var word_in_stop_words = false
					for _, stop_word := range stop_words {
						if word == stop_word {
							word_in_stop_words = true
							break
						}
					}

					if word_in_stop_words == false {
						var pair_index = 0
						// Let's see if it already exists
						for idx, pair := range word_freqs {
							if word == pair.Word {
								word_freqs[idx].Freq += 1
								found = true
								break
							}
							pair_index += 1
						}
						if found == false {
							word_freqs = append(word_freqs, struct {
								Word string
								Freq int
							}{
								Word: word, 
								Freq: 1,
							})
						} else if len(word_freqs) > 1 {
							// we may need to reorder
							for n := pair_index; n >= 0; n-=1 {
								if word_freqs[pair_index].Freq > word_freqs[n].Freq {
									// swap
									word_freqs[n], word_freqs[pair_index] = word_freqs[pair_index], word_freqs[n]
									pair_index = n
								}
							}
						}
					}
					// Let's reset
					start_char = -1
				}
			}
		}
	}
	
	for _, v := range word_freqs[:25] {
		fmt.Printf("%s  -  %d\n", v.Word, v.Freq)
	}
}
