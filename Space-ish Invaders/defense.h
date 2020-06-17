///
// Header file for the defense thread.
//
// author: Ryan Carnation
///

#ifndef _DEFENSE_H
#define _DEFENSE_H


///
// Struct for the shield
///
typedef struct Defense_Shield {
    int row;
    int col;
    char *graphic;
    char *name;
} Shield;


///
// Makes the shield with the given name, row and col.
//
//     name - the name of the defender.
//     row - the row that the shield with be on.
//     col - the column that the shield will start at.
//     return - a pointer to the shield.
///
Shield *makeShield(char *name, int row, int col);


///
// Frees all of the memory that was allocated for the shield.
//
//     shield - the shield being freed
///
void destroyShield(Shield *shield);


///
// The run method for the defense thread.
// 
//     shield - the shield that is going to be shown on the screen.
//     return - a void* of the shield that was passed in.
///
void *defenseRun(void *shield);

#endif
