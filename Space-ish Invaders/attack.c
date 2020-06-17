///
// Source file for the attacker thread.
//
// author: Ryan Carnation
///

#include "threads.h"
#include "missile.h"
#include "attack.h"
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <ncurses.h>
#include <string.h>

// Integer to represent the current state of the missiles
int isOver = 0;


// Makes an attacker and returns a pointer to it.
Attacker *makeAttacker(char *name, int numMissiles) {
    Attacker *newAttacker = (Attacker*)malloc(sizeof(Attacker));
    char *newName = (char*)malloc(sizeof(char)*(int)strlen(name) + 1);
    strncpy(newName, name, (int)strlen(name));
    if (newAttacker) {
	newAttacker->name = newName;
	newAttacker->numMissiles = numMissiles;
    }
    return newAttacker;
}


// Tests to see if the missiles are done.
int attackIsOver() {
    if (isOver) {
	return (1);
    }
    return (0);
}


// Frees all of the allocated memory for the attacker.
void destroyAttacker(Attacker *attacker) {
    if (attacker) {
	free(attacker->name);
	free(attacker);
    }
}


// The run method for the attacker.
void *attackerRun(void *attacker) {
    Attacker *tempAttacker = (Attacker*)attacker;
    int missilesSent = 0;
    int maxWidth = getmaxx(stdscr);
    while (true) {
	int numMissiles = rand() % 20 + 1;
	if ((numMissiles + missilesSent > tempAttacker->numMissiles) && tempAttacker->numMissiles != 0) {
	    numMissiles = tempAttacker->numMissiles - missilesSent;
	}
        Missile *missileArray[numMissiles];
	for (int i = 0; i < numMissiles; i++) {
	    missileArray[i] = makeMissile();
	    missilesSent++;
	}
        pthread_t threads[numMissiles];
	for (int i = 0; i < numMissiles; i++) {
	    int ms = pthread_create(&threads[i], NULL, missileRun, (void*)missileArray[i]);

	    if (ms) {
		printf("Error: pthread_create() returned %d\n", ms); 
		exit(EXIT_FAILURE);
	    }
	}
        for (int i = 0; i < numMissiles; i++) {
	    pthread_join(threads[i], NULL);
	}
	for (int i = 0; i < numMissiles; i++) {
	    destroyMissile(missileArray[i]);
	}
        if (tempAttacker->numMissiles == 0) {
	    continue;
	}
        if (missilesSent >= tempAttacker->numMissiles) {
	    break;
	}
    }
    pthread_mutex_lock(&sharedLock);
    move(2, maxWidth/4);
    addstr("The ");
    addstr(tempAttacker->name);
    move(2, maxWidth/4 + 4 + (int)strlen(tempAttacker->name));
    addstr("attack has ended.");
    refresh();
    pthread_mutex_unlock(&sharedLock);

    isOver = 1;
    
    void *finalAttacker = (void*)tempAttacker;
    return finalAttacker;
}
