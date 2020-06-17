///
// The source file for the defense thread
//
// author: Ryan Carnation
///

#include "threads.h"
#include "defense.h"
#include "attack.h"
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <ncurses.h>
#include <string.h>


// Makes the shield
Shield *makeShield(char *name, int row, int col) {
    Shield *newShield = (Shield*)malloc(sizeof(Shield));
    char *display = (char*)malloc(sizeof(char)*5 + 1);
    char *newName = (char*)malloc(sizeof(char)*(int)strlen(name) + 1);
    strncpy(newName, name, (int)strlen(name));
    for (int i = 0; i < 5; i++) {
	display[i] = '#';
    }
    if (newShield) {
        newShield->graphic = display;
        newShield->row = row;
	newShield->col = col;
	newShield->name = newName;
    }
    return newShield;    
}

// Destroys the shield.
void destroyShield(Shield *shield) {
    if (shield) {
	free(shield->graphic);
	free(shield->name);
	free(shield);
    }
}

// Run method for the defense thread.
void *defenseRun(void *shield) {
    Shield *tempShield = (Shield*)shield;
    int y = tempShield->row;
    int maxx = getmaxx(stdscr);
    pthread_mutex_lock(&sharedLock);
    move(y, 0);
    clrtoeol();
    move(y, tempShield->col);
    addstr(tempShield->graphic);
    refresh();
    pthread_mutex_unlock(&sharedLock);
    while (true) {
	int currChar = wgetch(stdscr);
	if (currChar == '\033') {
	    wgetch(stdscr);
	    switch (wgetch(stdscr)) {
		case 'C':
		    // Right arrow movement.
		    tempShield->col++;
		    pthread_mutex_lock(&sharedLock);
		    move(y, 0);
		    clrtoeol();
		    move(y, tempShield->col);
                    printw(tempShield->graphic);
		    refresh();
                    pthread_mutex_unlock(&sharedLock);
		    break;
		case 'D':
		    // Left arrow movement.
		    tempShield->col--;
                    pthread_mutex_lock(&sharedLock);
                    move(y, 0);
                    clrtoeol();
                    move(y, tempShield->col);
                    printw(tempShield->graphic);
                    refresh();
                    pthread_mutex_unlock(&sharedLock);
		    break;
	    }
	}
        if (attackIsOver() == 1 || currChar == ' ') {
	    pthread_mutex_lock(&sharedLock);
            move(4, maxx/4);
            addstr("The ");
            addstr(tempShield->name);
            move(4, maxx/4 + (int)strlen(tempShield->name) + 4);
            addstr("defense has ended.");
            move(5, maxx/4);
            addstr("hit 'q' to close screen...");
            refresh();
            pthread_mutex_unlock(&sharedLock);
	    break;
	}
    }
    void *finalShield = (void*)tempShield;
    return finalShield;
}
