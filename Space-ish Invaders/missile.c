///
// The source file for the missiles.
//
// author: Ryan Carnation
///

#define _DEFAULT_SOURCE

#include "threads.h"
#include "missile.h"
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <ncurses.h>
#include <string.h>

// Makes the missile.
Missile *makeMissile() {
    Missile *newMissile = (Missile*)malloc(sizeof(Missile));
    char *display = (char*)malloc(sizeof(char));
    char *destroyedDisplay = (char*)malloc(sizeof(char)*2);
    display[0] = '|';
    destroyedDisplay[0] = '?';
    destroyedDisplay[1] = '*';
    int maxCol = getmaxx(stdscr);
    if (newMissile) {
	newMissile->graphic = display;
	newMissile->destroyedGraphic = destroyedDisplay;
	newMissile->col = rand() % maxCol + 1;
	newMissile->row = 6;
    }
    return newMissile;
}


// Destroys the missile by freeing space that was allocated.
void destroyMissile(Missile *missile) {
    if (missile) {
	free(missile->graphic);
	free(missile->destroyedGraphic);
	free(missile);
    }
}

// The run method for the missile.
void *missileRun(void* missile) {
    Missile *tempMissile = (Missile*)missile;
    int maxx;
    while (true) {
	pthread_mutex_lock(&sharedLock);
	char charOnScreen = (char)mvinch(tempMissile->row + 1, tempMissile->col);
        maxx = getmaxx(stdscr);
	pthread_mutex_unlock(&sharedLock);
	usleep(rand() % 125000 + 1000);
	if (tempMissile->row >= (maxx - 2)) {
	    charOnScreen = '_';
	}	    
        if (charOnScreen == '_' || charOnScreen == '*') {
	    pthread_mutex_lock(&sharedLock);
            mvaddch(tempMissile->row, tempMissile->col, ' ');	    
	    tempMissile->row++;
	    mvaddch(tempMissile->row, tempMissile->col, tempMissile->destroyedGraphic[0]);
	    tempMissile->row++;
	    mvaddch(tempMissile->row, tempMissile->col, tempMissile->destroyedGraphic[1]);
	    refresh();
	    pthread_mutex_unlock(&sharedLock);
	    break;
	}
	else if(charOnScreen == '|' || charOnScreen == '#') {
	    pthread_mutex_lock(&sharedLock);
	    mvaddch(tempMissile->row, tempMissile->col, ' ');
	    tempMissile->row++;
            mvaddch(tempMissile->row, tempMissile->col, tempMissile->destroyedGraphic[1]);
            tempMissile->row--;
            mvaddch(tempMissile->row, tempMissile->col, tempMissile->destroyedGraphic[0]);
	    refresh();
	    pthread_mutex_unlock(&sharedLock);
	    break;
	}
	else {
	    pthread_mutex_lock(&sharedLock);
	    mvaddch((tempMissile->row), tempMissile->col, ' ');
	    tempMissile->row++;
	    mvaddch(tempMissile->row, tempMissile->col, '|');
            refresh();
	    pthread_mutex_unlock(&sharedLock);
	}
    }
    void *finalMissile = (void*)tempMissile;
    return finalMissile;
}


