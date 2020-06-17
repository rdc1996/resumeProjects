///
// The source file for the attack thread. In charge of spawning the missiles at random
// intervals.
//
// author: Ryan Carnation
///

#ifndef _ATTACKER_H
#define _ATTACKER_H

// Shared int between attack and defend to show when the missiles are done
// spawning.
extern int isOver;

// The struct for the attacker.
typedef struct Attacker_S {
    char* name;
    int numMissiles;
} Attacker;

///
// Makes an attacker given a name and a number of missiles.
//
//    name - the name of the attacker
//    numMissiles - the number of missiles to be fired.
//    return - the attacker.
///
Attacker *makeAttacker(char *name, int numMissiles);


///
// Tests to see if the missiles are all done launching.
//
//    return - 0 if the attack hasnt ended and 1 if it has.
///
int attackIsOver();


///
// Destroys the attacker by freeing all of the memory that was allocated.
// 
//     attacker - the attacker that is being freed
///
void destroyAttacker(Attacker *attacker);


///
// The run method for the attacker.
// 
//     attacker - the attacker thread that is being run
//     return - voi* pointer to the attacker being run
///
void *attackerRun(void* attacker);

#endif
