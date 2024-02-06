package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"regexp"
	"sort"
	"strings"
)

type EventManager struct {
	subscriptions map[string][]func([]interface{})
} 

func (em *EventManager) init() *EventManager {
	em.subscriptions = make(map[string][]func([]interface{}))
	return em
}

func (em *EventManager) publish(event []interface{}) *EventManager {
	event_type := event[0].(string)
	if handlers, exists := em.subscriptions[event_type]; exists {
		for _, handler := range handlers {
			handler(event)
		} 
	} 
	return em;
}

func (em *EventManager) subscribe(event_type string, handler func([]interface{})) *EventManager {
	if _, exists := em.subscriptions[event_type]; exists {
		em.subscriptions[event_type] = append(em.subscriptions[event_type], handler)
	} else {
		em.subscriptions[event_type] = []func([]interface{}){handler}
	}
	return em;
} 

type DataStorage struct {
	event_manager *EventManager
	data         string
}

func (ds *DataStorage) init(eventManager *EventManager) {
	ds.event_manager = eventManager
	ds.event_manager.subscribe("load", ds.load)
	ds.event_manager.subscribe("start", ds.produceWords)
}

func (ds *DataStorage) load(event []interface{}) {
	path_to_file := event[1].(string)
	fileContent, _ := ioutil.ReadFile(path_to_file)
	ds.data = string(fileContent)
	re := regexp.MustCompile(`[\W_]+`)
	ds.data = strings.ToLower(re.ReplaceAllString(ds.data, " "))
}

func (ds *DataStorage) produceWords(event []interface{}) {
	words := strings.Fields(ds.data)
	for _, w := range words {
		ds.event_manager.publish([]interface{}{"word", w})
	}
	ds.event_manager.publish([]interface{}{"eof", nil})
}

type StopWordFilter struct {
	event_manager *EventManager
	stop_words map[string]struct{}
}

func (swf *StopWordFilter) init(eventManager *EventManager) {
	swf.stop_words = make(map[string]struct{})
	swf.event_manager = eventManager
	swf.event_manager.subscribe("load", swf.load)
	swf.event_manager.subscribe("word", swf.is_stop_word)
}

func (swf *StopWordFilter) load(event []interface{}) {
	fileContent, _ := ioutil.ReadFile("./../stop_words.txt")
	for _, word := range strings.Split(string(fileContent), ",") { 
		swf.stop_words[word] = struct{}{}
	}
	for _, char := range "abcdefghijklmnopqrstuvwxyz" { 
		swf.stop_words[string(char)] = struct{}{}
	}
}

func (swf *StopWordFilter) is_stop_word(event []interface{}) {
	word := event[1].(string)
	if _, isStopword := swf.stop_words[word]; !isStopword { 
		swf.event_manager.publish([]interface{}{"valid_word", word})
	}
}

type WordFrequencyCounter struct {
	event_manager *EventManager
	word_freqs map[string]int
	sorted_word_freqs []struct{Word string; Freq int}
} 

func (wfc *WordFrequencyCounter) init(eventManager *EventManager) {
	wfc.word_freqs = make(map[string]int)
	wfc.event_manager = eventManager
	wfc.event_manager.subscribe("valid_word", wfc.increment_count)
	wfc.event_manager.subscribe("print", wfc.print_freqs)
}

func (wfc *WordFrequencyCounter) print_freqs(event []interface{}) {
	for word, freq := range wfc.word_freqs {
		wfc.sorted_word_freqs = append(wfc.sorted_word_freqs, struct{Word string; Freq int}{Word: word, Freq: freq})
	}
	sort.Slice(wfc.sorted_word_freqs, func(i int, j int) bool {
		return wfc.sorted_word_freqs[i].Freq > wfc.sorted_word_freqs[j].Freq
	})
	for _, v := range wfc.sorted_word_freqs[:25] { 
		fmt.Printf("%s  -  %d\n", v.Word, v.Freq) 
	}
}

func (wfc *WordFrequencyCounter) increment_count(event []interface{}) {
	word := event[1].(string)
	if _, exist := wfc.word_freqs[word]; exist {
		wfc.word_freqs[word]++
	} else {
		wfc.word_freqs[word] = 1
	}
}

type WordFrequencyApplication struct {
	event_manager *EventManager
}

func (wfa *WordFrequencyApplication) init(eventManager *EventManager) {
	wfa.event_manager = eventManager
	wfa.event_manager.subscribe("run", wfa.run)
	wfa.event_manager.subscribe("eof", wfa.stop)
}

func (wfa *WordFrequencyApplication) run(event []interface{}) {
	path_to_file := event[1].(string)
	wfa.event_manager.publish([]interface{}{"load", path_to_file})
	wfa.event_manager.publish([]interface{}{"start", nil})
}

func (wfa *WordFrequencyApplication) stop(event []interface{}){
	wfa.event_manager.publish([]interface{}{"print", nil})
}

type WordWithZChecker struct {
	event_manager *EventManager
	zWords []string
}

func (wwzch *WordWithZChecker) init(eventManager *EventManager){
	wwzch.event_manager = eventManager
	wwzch.event_manager.subscribe("valid_word", wwzch.check_z)
	wwzch.event_manager.subscribe("print", wwzch.print_n_word_with_z)
}

func (wwzch *WordWithZChecker) check_z(event []interface{}){
	word := event[1].(string)
	if strings.ContainsRune(word, 'z') {
		wwzch.zWords = append(wwzch.zWords, word)
    } 
} 

func (wwzch *WordWithZChecker) print_n_word_with_z(event []interface{}){
	fmt.Printf("The number of non-stop words with the letter z is %d.\n", len(wwzch.zWords))
}

func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: go run Sixteen.go <path_to_file>")
		return
	}

	// Part1. Print top frequent 25 words
	em := &EventManager{}
	em.init()
	
	ds := &DataStorage{}
	ds.init(em)
	
	swf := &StopWordFilter{}
	swf.init(em)

	wfc := &WordFrequencyCounter{}
	wfc.init(em)
	
	wfa := &WordFrequencyApplication{}
	wfa.init(em)
	
	// Part2. Words with z. 
	// Change the given example program so that it implements an additional task: 
	// after printing out the list of 25 top words, 
	// it should print out the number of non-stop words with the letter z. 
	// Additional constraints: 
	// (i) no changes should be made to the existing classes; 
	// 	   adding new classes and more lines of code to the main function is allowed; 
	// (ii) files should be read only once for both term frequency and “words with z” tasks.
	wwzch := &WordWithZChecker{}
	wwzch.init(em)

	em.publish([]interface{}{"run", os.Args[1]})
}

