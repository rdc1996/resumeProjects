///
// The header file for the missiles.
//
// author: Ryan Carnation
///

#ifndef _MISSILE_H
#define _MISSILE_H

// Struct for the missiles
typedef struct Missile_S {
    char *graphic;
    char *destroyedGraphic;
    int row;
    int col;
} Missile;

///
// Makes the missiles.
//
//     return - a pointer to the missile that was made.
///
Missile *makeMissile();


///
// Destroys the missile by freeing the memory that was allocated.
//
//     missile - the missile that is being destroyed.
///
void destroyMissile(Missile *missile);


///
// The run method for the missile.
// 
//     missile - the missile being run.
//     return - void* pointer of the missile being run.
///
void *missileRun(void* missile);

#endif
