import sys, string
import numpy as np

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
# np.vectorize(function)(numpy_object)
#  
# Result: 

# 5) Ignores words smaller than 2 characters
# space_indices = np.where(np.logical_or(characters == ' ', ~np.char.isalnum(characters)))
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
# Result: [array([' ', 'H', 'E', 'L', 'L', 'O'], dtype='<U1'), array([' ', 'W', 'O', 'R', 'L', 'D'], dtype='<U1')]

one_grams = np.array(list(map(lambda w: ''.join(w).strip(), words)))
# Result: ['HELLO', 'WORLD']

# No need to care about stopwords here!

# 7) Similar way to get 2-gram words.
repeated_one_grams = np.repeat(one_grams, 2)
# Result: ['HELLO', 'HELLO', 'WORLD', 'WORLD']

two_gram_word_pairs = np.reshape(repeated_one_grams[1:-1], (-1, 2))
# Result: [['HELLO', 'WORLD']]

two_grams = np.array(list(map(lambda words: ' '.join(words), two_gram_word_pairs)))
# Result: ['HELLO WORLD']

# 8) Count the unique two-gram and the corresponding frequencies
unique_two_grams, counts = np.unique(two_grams, axis=0, return_counts=True)

# 9) Sort DESC
sorted_list = sorted(zip(unique_two_grams, counts), key=lambda p: p[1], reverse=True)

# 10) Print the top 5 frequent ones
for two_gram, count in sorted_list[:5]:
    print(two_gram, '-', count)
