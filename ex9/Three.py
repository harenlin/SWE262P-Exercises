import sys, string
import numpy as np
import argparse

# print(sys.argv)

# Example input: "Hello  World!" 

# 1) Read the raw text in.
characters = np.array([' ']+list(open(sys.argv[1]).read())+[' '])
# Result: array([' ', 'H', 'e', 'l', 'l', 'o', ' ', ' ', 'W', 'o', 'r', 'l', 'd', '!', ' '])

# 2) Normalize the text - a. replace non-alphabet words.
characters[~np.char.isalpha(characters)] = ' '
# Result: array([' ', 'H', 'e', 'l', 'l', 'o', ' ', ' ', 'W', 'o', 'r', 'l', 'd', ' ', ' '])

# 3) Normalize the text - b. to uppercase.
characters = np.char.upper(characters)
# Result: array([' ', 'H', 'E', 'L', 'L', 'O', ' ', ' ', 'W', 'O', 'R', 'L', 'D', ' ', ' '])

# 4) Replace the vowels with their Leet counterparts.
"""
A: ∆,4, /-\, /_\, @, /\, Д, а
B: 8, |3, 13, |}, |:, |8, 18, 6, |B, |8, lo, |o, j3, ß, в, ь
C: <, {, [, (, ©, ¢, с
D: |), |}, |], |>
E: 3, £, ₤, €, е
F: 7, |=, ph, |#, |", ƒ
G: 9,[, -, [+, 6, C-
H: #, 4, |-|, [-], {-}, }-{, }{, |=|, [=], {=}, /-/, (-), )-(, :-:, I+I, н
I: 1, |, !, 9
J: √,_|, _/, _7, 9,[1] _), _], _}
K: |<, 1<, l<, |{, l{
L: |_, |, 1, ][
M: 44, |\/|, ^^, /\/\, /X\, []\/][, []V[], ][\\//][, (V), //., .\\, N\, м
N: |\|, /\/, /V, ][\\][, И, и, п
O: 0, (), [], {}, <>, Ø, oh, Θ, о, ө
P: |o, |O, |>, |*, |°, |D, /o, []D, |7, р
Q: O_, 9, (,), 0, kw
R: |2, 12, .-, |^, l2, Я, ®
S: 5, $, §
T: 7, +, 7`, '|', `|`, ~|~, -|-, '][', т
U: |_|, \_\, /_/, \_/, (_), [_], {_}
V: \/
W: \/\/, (/\), \^/, |/\|, \X/, \\', '//, VV, \_|_/, \\//\\//, Ш, 2u, \V/
X: ×,%, *, ><, }{, )(, Ж
Y: `/, ¥, \|/, Ч, ү, у
Z: 5, 7_, >_, (/)
"""

leet_mapping = {
    'A': '4', ###
    'B': 'B',
    'C': 'C',
    'D': 'D',
    'E': '3', ###
    'F': 'F',
    'G': 'G',
    'H': 'H',
    'I': '1', ###
    'J': 'J',
    'K': 'K',
    'L': 'L',
    'M': 'M',
    'N': 'N',
    'O': '0', ###
    'P': 'P',
    'Q': 'Q',
    'R': 'R',
    'S': 'S',
    'T': 'T',
    'U': 'U', ######
    'V': 'V',
    'W': 'W',
    'X': 'X',
    'Y': 'Y',
    'Z': 'Z',
    ' ': ' '
}

# np.vectorize(function)(numpy_object)
characters = np.vectorize(lambda key: leet_mapping[key])(characters)
# Result: [' ', 'H', '3', 'L', 'L', '0', ' ', ' ', 'W', '0', 'R', 'L', 'D', ' ', ' ']

# 5) Ignores words smaller than 2 characters
space_indices = np.where(characters == ' ')
# Result: [0, 6, 7, 13, 14]

# A little trick: Double each index, and then take pairs
repeated_indices = np.repeat(space_indices, 2)
# Result: [0, 0, 6, 6, 7, 7, 13, 13, 14, 14]

# Get the pairs as a 2D matrix, skip the first and the last
word_ranges = np.reshape(repeated_indices[1:-1], (-1, 2))
# Result: [[ 0, 6], [ 6, 7], [ 7, 13], [13, 14]]

# Remove the indexing to the spaces themselves
word_ranges = word_ranges[np.where(word_ranges[:, 1] - word_ranges[:, 0] > 2)]
# Result: [[ 0, 6], [ 7, 13]]

# 6) Get 1-gram words first.
words = list(map(lambda r: characters[r[0]:r[1]], word_ranges))
# Result: [array([' ', 'H', '3', 'L', 'L', '0'], dtype='<U1'), array([' ', 'W', '0', 'R', 'L', 'D'], dtype='<U1')]

one_grams = np.array(list(map(lambda w: ''.join(w).strip(), words)))
# Result: ['H3LL0', 'W0RLD']

############################ No need to care about stopwords here!
filter_stop_words = sys.argv[2]
if filter_stop_words == 'True':
    stop_words_characters = np.array([' ']+list(open('./../stop_words.txt').read())+[' '])
    stop_words_characters[~np.char.isalpha(stop_words_characters)] = ' '
    stop_words_characters = np.char.upper(stop_words_characters)
    stop_words_characters = np.vectorize(lambda key: leet_mapping[key])(stop_words_characters)
    sw_space_indices = np.where(stop_words_characters == ' ')
    sw_repeated_indices = np.repeat(sw_space_indices, 2)
    sw_word_ranges = np.reshape(sw_repeated_indices[1:-1], (-1, 2))
    sw_word_ranges = sw_word_ranges[np.where(sw_word_ranges[:, 1] - sw_word_ranges[:, 0] > 2)]
    stop_words = list(map(lambda r: stop_words_characters[r[0]:r[1]], sw_word_ranges))
    stop_words = np.array(list(map(lambda w: ''.join(w).strip(), stop_words)))
    one_grams = one_grams[~np.isin(one_grams, stop_words)]

# 7) Similar way to get 2-gram words.
repeated_one_grams = np.repeat(one_grams, 2)
# Result: ['H3LL0', 'H3LL0', 'W0RLD', 'W0RLD']

two_gram_word_pairs = np.reshape(repeated_one_grams[1:-1], (-1, 2))
# Result: [['H3LL0', 'W0RLD']]

two_grams = np.array(list(map(lambda words: ' '.join(words), two_gram_word_pairs)))
# Result: ['H3LL0 W0RLD']

# 8) Count the unique two-gram and the corresponding frequencies
unique_two_grams, counts = np.unique(two_grams, axis=0, return_counts=True)

# 9) Sort DESC
sorted_list = sorted(zip(unique_two_grams, counts), key=lambda p: p[1], reverse=True)

# 10) Print the top 5 frequent ones
for two_gram, count in sorted_list[:5]:
    print(two_gram, '-', count)
