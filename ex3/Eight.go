package main

import ( "fmt"; "io/ioutil"; "bufio"; "os"; "unicode"; "strings"; "sort" )

func parse(reader *bufio.Reader, stopWords map[string]struct{}, words *[]string) {
	curWord := ""
	for {
		char, _, err := reader.ReadRune() // read character by character
		if err != nil { break }
		if unicode.IsLetter(char) || unicode.IsNumber(char) {
			curWord += string(unicode.ToLower(char))
		} else if curWord != "" { 
			if _, isStopWord := stopWords[curWord]; !isStopWord {
				*words = append(*words, curWord)
			}
			curWord = ""
			parse(reader, stopWords, words) // RECURSIVE call right here!
		}
	}
	if curWord != "" {
		if _, isStopWord := stopWords[curWord]; !isStopWord {
			*words = append(*words, curWord)
		}
	}
}


/* func parse(reader *bufio.Reader, stopWords map[string]struct{}, words *[]string, curWord *string) {
	char, _, err := reader.ReadRune() // read character by character
	if err != nil {
		if err.Error() == "EOF" {
			// fmt.Println("End of file reached.")			
			if *curWord != "" { 
				if _, isStopWord := stopWords[*curWord]; !isStopWord { *words = append(*words, strings.Clone(*curWord)) } 
			}
			return
		} else {
			fmt.Println("Error:", err)
			return
		}	 
	}

	if unicode.IsLetter(char) || unicode.IsNumber(char) {
		*curWord += string(unicode.ToLower(char))
		parse(reader, stopWords, words, curWord) // RECURSIVE call right here!
	} else if *curWord != "" { 
		if _, isStopWord := stopWords[*curWord]; !isStopWord {
			*words = append(*words, strings.Clone(*curWord))
		}
		*curWord = ""
		parse(reader, stopWords, words, curWord) // RECURSIVE call right here!
	} else {
		parse(reader, stopWords, words, curWord) // RECURSIVE call right here!
	}
}
*/


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

func sort_map(word_freqs map[string]int) []struct{Word string; Freq int} {
	var sorted_word_freqs []struct{Word string; Freq int}
	for word, freq := range word_freqs {
		sorted_word_freqs = append(sorted_word_freqs, struct {
			Word string
			Freq int
		}{
			Word: word, 
			Freq: freq,
		})
	}

	sort.Slice(sorted_word_freqs, func(i int, j int) bool {
		return sorted_word_freqs[i].Freq > sorted_word_freqs[j].Freq
	})
	
	return sorted_word_freqs
}

func print_all(sorted_word_freqs []struct{Word string; Freq int}) {
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

	stopWords := make(map[string]struct{})
	fileContent, _ := ioutil.ReadFile("./../stop_words.txt")
	for _, word := range strings.Split(string(fileContent), ",") { stopWords[word] = struct{}{} }
	for _, char := range "abcdefghijklmnopqrstuvwxyz" { stopWords[string(char)] = struct{}{} }
	
	file, _ := os.Open(os.Args[1])
	reader := bufio.NewReader(file)
	defer file.Close()

	var words []string
	parse(reader, stopWords, &words)
	// var curWord string
	// parse(reader, stopWords, &words, &curWord)
	
	print_all(sort_map(frequencies(words))[:25])
}

