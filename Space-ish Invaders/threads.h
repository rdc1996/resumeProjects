///
// Header file so that every source file can have access to the shared lock.
//
// author: Ryan Carnation
///

#ifndef _THREADS_H
#define _THREADS_H

#include <pthread.h>

///
// The lock to be shared between all of the source files.
///
extern pthread_mutex_t sharedLock;

#endif
