package main
import ( "fmt"; "io/ioutil"; "os"; "regexp"; "strings"; "sort" )

func main() {
	stopWords := make(map[string]struct{})
	fileContent, _ := ioutil.ReadFile("./../stop_words.txt")
	for _, word := range strings.Split(string(fileContent), ",") { stopWords[word] = struct{}{} }
	for _, char := range "abcdefghijklmnopqrstuvwxyz" { stopWords[string(char)] = struct{}{} }
	fileContent, _ = ioutil.ReadFile(os.Args[1])
	words := regexp.MustCompile("[a-z]{2,}").FindAllString(strings.ToLower(string(fileContent)), -1)
	counts := make(map[string]int)
	for _, w := range words { if _, isStopword := stopWords[w]; !isStopword { counts[w]++ } }
	var sortedWordFreqs []struct{ Word string; Freq int }
	for word, freq := range counts { sortedWordFreqs = append(sortedWordFreqs, struct{ Word string; Freq int }{Word: word, Freq: freq}) }
	sort.Slice(sortedWordFreqs, func(i, j int) bool { return sortedWordFreqs[i].Freq > sortedWordFreqs[j].Freq })
	for _, v := range sortedWordFreqs[:25] { fmt.Printf("%s  -  %d\n", v.Word, v.Freq) }
}
