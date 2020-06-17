///
// The main thread to Project3 - Posix Attacks!
//
// In charge of spawning the attack and defend threads and building the
// landscape.
//
// author: Ryan Carnation
///

#define _DEFAULT_SOURCE

#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ncurses.h>
#include <unistd.h>
#include "threads.h"
#include "defense.h"
#include "attack.h"
#include "missile.h"

// The lock that is shared between all of the threads.
pthread_mutex_t sharedLock;


// The main function to the program, creates the attack and defend threads.
int main(int argc, char* argv[]) {
    srand(time(NULL));
    if (argc != 2) {
	fprintf(stderr, "Usage: %s config-file\n", argv[0]);
	return (1);
    }

    FILE *fp = fopen(argv[1], "r");
    char *buffer = NULL, attackName[81] = "notAName", defenseName[81] = "notAName";
    size_t len = 0;
    size_t val;
    int maxWidth = 200, currWidth = 0, currHeight, fileCounter = 0, nextHeight = 0;
    int missiles = 100000, previousHeight, maxHeight, errorCounter = 0, maxValue = 0;

    if (fp == NULL) {
	fprintf(stderr, "File not found");
	return (1);
    }

    if (fp != NULL) {
	while ((val = getline(&buffer, &len, fp) > 0) && currWidth < maxWidth) {
            //////////
	    //exit(1);///
	    //////////
	    if (val == (size_t)-1) {
		fprintf(stderr, "Error: input reached EOF.");
		exit(1);
	    }
	    if (strcmp(buffer, "\n") == 0) {
		fprintf(stderr, "Error: file is empty.\n");
		exit(1);
	    }
	    if (buffer[0] == '#') {
		continue;
	    }
	    if (fileCounter == 0) {
                if ((int)strlen(buffer) > 80) {
		    fprintf(stderr, "Defense force name too long!\n");
		    exit(1);
		}
		strcpy(defenseName, buffer);
	    }
	    if (fileCounter == 1) {
		if ((int)strlen(buffer) > 80) {
		    fprintf(stderr, "Attack force name too long!\n");
		    exit(1);
		}
		strcpy(attackName, buffer);
	    }
	    if (fileCounter == 2) {
		int temp = atoi(buffer);
		missiles = temp;
	    }
	    if (fileCounter > 2) {
		if (!isdigit(buffer[0])) {
		    fprintf(stderr, "Error: missing building configuration.\n");
		    exit(1);
		}
		if (errorCounter == 0) {
		    initscr(); // initializes stdscr, a curses global variable.
                    cbreak();
                    noecho();
                    getmaxyx(stdscr, maxHeight, maxWidth);
		    maxValue = maxHeight;
                    move(0, maxWidth/4);
                    addstr("Hit Space to end defense, or control-C to quit.");
		    errorCounter++;
		}
		for (int i = 0; i < (int)strlen(buffer); i++) {
		    currHeight = maxHeight - (int)(buffer[i] - '0');
                    int nextInt = i + 2;
		    char temp[2];
		    if (nextInt < (int)strlen(buffer)) {
			if (buffer[i] != ' ' && buffer[i+1] != ' ') {
                            temp[0] = buffer[i];
                            temp[1] = buffer[i+1];
			    currHeight = maxHeight - atoi(temp);
                        }
                        nextHeight = maxHeight - (int)(buffer[nextInt] - '0');
		    }
		    if (buffer[i] == ' ') {
	                continue;
		    }
		    if (currHeight < maxValue) {
			maxValue = currHeight;
		    }
		    if (currHeight == (maxHeight - 2)) {
			pthread_mutex_lock(&sharedLock);
			mvaddch(currHeight, currWidth, '_');
			refresh();
			pthread_mutex_unlock(&sharedLock);
			previousHeight = currHeight;
			currWidth++;
		    }
		    if (currHeight < (maxHeight - 2)) {
			pthread_mutex_lock(&sharedLock);
                        char beforeUnderscore = mvinch(previousHeight, currWidth - 1);
                        char beforeBar = mvinch(previousHeight + 1, currWidth - 1);
                        pthread_mutex_unlock(&sharedLock);
                        if (currHeight < previousHeight) {
			    pthread_mutex_lock(&sharedLock);
			    for (int i = (maxHeight - 2); i > currHeight; i--) {
				mvaddch(i, currWidth, '|');
				refresh();
			    }
			    pthread_mutex_unlock(&sharedLock);
			    previousHeight = currHeight;
			    currWidth++;
			}
			else if (previousHeight == currHeight) {
			    if (beforeUnderscore == '_' && nextHeight != currHeight) {
				if (nextHeight > currHeight) {
			            pthread_mutex_lock(&sharedLock);
				    for (int i = previousHeight + 1; i <= (maxHeight - 2); i++) {
				        mvaddch(i, currWidth, '|');
                                        refresh();
                                    }
				    pthread_mutex_unlock(&sharedLock);
				    previousHeight = currHeight;
				    currWidth++;
				    continue;
				}
				if (nextHeight < currHeight) {
                                    continue;
				}
		            }
			    if ((beforeUnderscore == '_' && nextHeight == currHeight) || beforeBar == '|') {
			        pthread_mutex_lock(&sharedLock);
			        mvaddch(currHeight, currWidth, '_');
                                refresh();
                                pthread_mutex_unlock(&sharedLock);
			        currWidth++;
			    }
			}
			else if (currHeight > previousHeight) {
			    if (beforeBar == '|') {
			        pthread_mutex_lock(&sharedLock);
				mvaddch(currHeight, currWidth, '_');
				refresh();
			        pthread_mutex_unlock(&sharedLock);
			        previousHeight = currHeight;
			        currWidth++;
				continue;
			    }
			    pthread_mutex_lock(&sharedLock);
			    for (int i = previousHeight + 1; i <= (maxHeight - 2); i++) {
                                    mvaddch(i, currWidth, '|');
                                    refresh();
                            }
			    pthread_mutex_unlock(&sharedLock);
			    previousHeight = currHeight;
			    currWidth++;
			}
		    }
		}
	    }
	    fileCounter++;
	}
	if (currWidth < maxWidth) {
	    pthread_mutex_lock(&sharedLock);
	    for (int i = currWidth; i <= maxWidth; i++) {
		mvaddch((maxHeight - 2), i, '_');
		refresh();
	    }
	    pthread_mutex_unlock(&sharedLock);
	}
    }

    if (strcmp(defenseName, "notAName") == 0) {
        fprintf(stderr, "Error: missing defender name.\n");
        exit(1);
    }

    if (strcmp(attackName,"notAName") == 0) {
        fprintf(stderr, "Error: missing attacker name.\n");
        exit(1);
    }

    if (missiles == 100000) {
        fprintf(stderr, "Error: missing missile count.\n");
        exit(1);
    }
    
    Shield *defense = makeShield(defenseName, maxValue - 2, maxWidth/2);
    Attacker *offense = makeAttacker(attackName, missiles);
    
    int numThreads = 2;
    pthread_t threads[numThreads];
    for (int i = 0; i < numThreads; i++) {
	
	if (i == 0) {
            int def = pthread_create(&threads[i], NULL, defenseRun, (void*)defense);

	    if (def) {
            printf ("Error: pthread_create() returned %d/n", def);
            return (1);
            }
	}
	
	if (i == 1) {
	    int def = pthread_create(&threads[i], NULL, attackerRun, (void*)offense);

	    if (def) {
            printf ("Error: pthread_create() returned %d/n", def);
            return (1);
            }
	}
    }

    for (int i = 0; i < numThreads; i++) {
	pthread_join(threads[i], NULL);
    }

    while (getchar() != 'q') {
        continue;
    }
    endwin(); 

    destroyAttacker(offense);
    destroyShield(defense);
    
    return (0);
}
