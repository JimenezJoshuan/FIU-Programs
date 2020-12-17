# FIU-Programs

Negation handle requires negation cues and scope

For cues - need a Lexicon of negation terms. Exists, but might be better to make our own

Explicit cues and implicit cues. Care more about explicit.

If explicit cues found, see if scope.

Scope found by doing POS tagging or window method. POS might be best four novels

If scope found based on POS rules, then we negate or flat out ignore the emotion.

I am not at all happy with the service

Not is cue word.
clause is happy.
Therefore, if there is emotion attributed to clause, then either ignore or flip it based on emotion.
This case happy is emotion span and means happy. However, once pos tagging applied, see that not is a cue word and applies to clause, so update info.

Get sentence -> check if emotion detected based on phrase or etc. -> Declare as emotion span -> If found, then check for negation cue-> If found, check scope of negation. > If scope is part of emotion span, update emotion span and negate labels.

Lexicon, Solution for affixes, why we didnt do some,

Negation Solution

Send Algorithm
No Solution in NLP Stanford?
