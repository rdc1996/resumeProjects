///
// Program to create and traverse a maze given through the command line,
// printing the original maze and the solution.
//
// auhtor: Ryan Carnation
///

#define _DEFAULT_SOURCE

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>


// A coordinate struct for the final path array
struct coordinate {
    int row;
    int col;
};


// A queueADT to help traverse the matrix
struct AQueue {
    struct coordinate *coords;
    int capacity;
};

typedef struct AQueue *Queue; 


///
// Creates a queue and allocates memory for it given the size of the matrix
//     size - the size of the matrix that this queue will help traverse
///
Queue createQueue(int size) {
    Queue queue = (Queue)malloc(sizeof(struct AQueue));
    if (queue != NULL) {
	queue->coords = (struct coordinate*)malloc(sizeof(struct coordinate)*(size+1));
	queue->capacity = size;
	queue->coords[0].row = 0;
	queue->coords[queue->capacity].row = 1;
    }
    return queue;
}


///
// Check to see if the given queue is full
//     queue - the queue that is being checked
///
int isFull(Queue queue) {
    return queue->coords[0].row == queue->coords[queue->capacity].row;
}


///
// Check to see if the given queue is empty
//     queue - the queue that is being checked
///
int isEmpty(Queue queue) {
    return queue->coords[0].row == 0;
}


///
// Add an item to the back of the given queue
//     queue - the Queue that is getting a new value
//     coord - the coordinate to add to the queue
///
void enqueue(Queue queue, struct coordinate coord) {
    if (!isFull(queue)) {
	queue->coords[queue->coords[queue->capacity].row] = coord;
	if (isEmpty(queue)) {
	    queue->coords[0].row = 1;
	}
	queue->coords[queue->capacity].row = (queue->coords[queue->capacity].row%(queue->capacity - 1)) + 1;
    }
}


///
// Remove the top value from the given queue and return it
//     queue - the queue that is being altered
///
struct coordinate dequeue(Queue queue) {
    struct coordinate currValue;
    if (!isEmpty(queue)) {
	currValue = queue->coords[queue->coords[0].row];
	queue->coords[0].row = (queue->coords[0].row%(queue->capacity - 1)) + 1;
	if (isFull(queue)) {
	    queue->coords[0].row = 0;
	    queue->coords[queue->capacity].row = 1;
	}
    }
    return currValue;
}


///
// Using BFS find the shortest path from (0,0) to (N-1, M-1) where
// N is the rows and M is the columns
//     rows - the amount of rows in the matrix
//     cols - the amount of cols in the matrix
//     maze - the maze that is being traversed
//     path - the final path that is deemed the fastest
///
int findShortest(int rows, int cols, char** maze, struct coordinate *path) {
    struct coordinate neighbor, current = {0,0};
    if (maze[current.row][current.col] == '#') {
	return (-1);
    }
    bool visited[rows][cols];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
	    visited[i][j] = false;
	}
    }
    Queue toVisit = createQueue(rows*cols);
    visited[current.row][current.col] = true;
    enqueue(toVisit, current);
    while (!isEmpty(toVisit)) {
	current = dequeue(toVisit);
	int diffMoves[4][2] = {{0,1}, {1,0}, {0,-1}, {-1,0}};
	for (int i = 0; i < 4; i++) {
	    neighbor.row = current.row + diffMoves[i][0];
	    neighbor.col = current.col + diffMoves[i][1];
	    if (neighbor.col < 0 || neighbor.col >= cols || neighbor.row < 0 || neighbor.row >= rows) {
		continue;
	    }
	    if (neighbor.row == rows - 1 && neighbor.col == cols - 1) {
		path[neighbor.row*cols + neighbor.col] = current;
		free(toVisit->coords);
		free(toVisit);
		return (1);
	    }
	    if (visited[neighbor.row][neighbor.col] == false && maze[neighbor.row][neighbor.col] == '.') {
		path[neighbor.row*cols + neighbor.col] = current;
		visited[neighbor.row][neighbor.col] = true;
		enqueue(toVisit, neighbor);
	    }
	}
    }
    free(toVisit->coords);
    free(toVisit);
    return (-1);
}


///
// Print out the maze so that it looks nice to the user.
//     rows - the amount of rows in the matrix
//     cols - the amount of columns in the matrix
//     maze - the matrix thats being printed
//     outFile - the file being written to
///
void prettyPrintOutput(int rows, int cols, char** maze, FILE* outFile) {
    fputs(" |", outFile);
    for (int i = 0; i < (cols*2); i++) {
        fputs("-", outFile);
    }
    fputs("| ", outFile);
    fputs("\n", outFile);
    for (int i = 0; i < rows; i++) {
        if (i != 0) {
            fputs(" |", outFile);
        }
        if (i == 0) {
            fputs("  ", outFile);
        }
        for (int j = 0; j < cols; j++) {
            fprintf(outFile, "%c ", maze[i][j]);
        }
        if (i < rows - 1) {
            fputs("| ", outFile);
        }
        fputs("\n", outFile);
    }
    fputs(" |", outFile);
    for (int i = 0; i < (cols*2); i++) {
        fputs("-", outFile);
    }
    fputs("| \n", outFile);
}


///
// Print out the maze so that it looks nice to the user.
//     rows - the amount of rows in the matrix
//     cols - the amount of columns in the matrix
//     maze - the matrix thats being printed
///
void prettyPrint(int rows, int cols, char** maze) {
    printf("|");
    for (int i = 0; i < (cols*2 + 1); i++) {
        printf("-");
    }
    printf("|");
    printf("\n");
    for (int i = 0; i < rows; i++) {
        if (i != 0) {
            printf("| ");
        }
        if (i == 0) {
            printf("  ");
        }
        for (int j = 0; j < cols; j++) {
            printf("%c ", maze[i][j]);
        }
        if (i < rows - 1) {
            printf("|");
        }
        printf("\n");
    }
    printf("|");
    for (int i = 0; i < (cols*2 + 1); i++) {
        printf("-");
    }
    printf("|\n");
}


///
// Function to free up any dynamically allocated memory for the maze
//     rows - the amount of rows in the given matrix
//     maze - the matrix that will be freed
///
void deleteMaze(int rows, char** maze) {
    for (int i = 0; i < rows; i++) {
	char* currentCharPtr = maze[i];
	free(currentCharPtr);
    }
    free(maze);
}


int main(int argc, char* argv[]) {
    
    // Booleans to represent the different flags
    bool h = false;
    bool d = false;
    bool s = false;
    bool p = false;


    
    // Strings to hold the input file or output fileif it is given
    char *inputFile = NULL, *outputFile = NULL;

    // Create the 2D array for the maze and allocate memory for it.
    int rowSize = 0, colSize = 0, tempRow = 2, tempCol = 2;
    char **maze = malloc(sizeof(char*)*tempRow);
    for (int i = 0; i < tempRow; i++) {
        maze[i] = malloc(tempCol*sizeof(char));
    }
    
    // Iterate through all of the command line arguments and check
    // for all of the possible flags
    for (int i = 0; i < argc; i++) {
        if (argv[i][0] == 45) {
	    for (int j = 1; j < (int)strlen(argv[i]); j++) {
		char temp = argv[i][j];
		if (temp == 104) {
		    h = true;
		}
		if (temp == 100) {
		    d = true;
		}
		if (temp == 115) {
		    s = true;
		}
		if (temp == 112) {
		    p = true;
	    	}
		if (temp == 105) {
		    inputFile = argv[i + 1];
		}
		if (temp == 111) {
		    outputFile = argv[i + 1];
		}
	    }
	}
    }

    // If dealing with standard input.
    if (inputFile == NULL) {
	printf("Enter a maze of 0's and 1's (CTRL + D to quit):\n");
        char *line = NULL;
        size_t len = 0;		
	inputFile = "temp.txt";
	FILE *writing = fopen(inputFile, "w");
	while (getline(&line, &len, stdin) > 0) {
	    fputs(line, writing);
        }
	fclose(writing);
    }
   
    // If an input file is given, create the maze by reading the file twice.
    if (inputFile != NULL) {

	// Open the input file and check to make sure that it is not null.
        FILE *inFile = fopen(inputFile, "r");
        char *line = NULL;
        size_t len = 0;
        if (inFile == NULL) {
            perror("Error");
            return (0);
        }
        
	// Loop through each line of the file to get the size of the matrix.
	rowSize = 0;
	colSize = 0;
        while (getline(&line, &len, inFile) > 0) {
            for (int i = 0; i < (int)strlen(line); i++) {
                if (line[i] == 48 || line[i] == 49) {
                    colSize++;
                }
            }
            rowSize++;
        }
        colSize = colSize/rowSize;
        fclose(inFile);

	char **tempArray = realloc(maze, sizeof(maze)*rowSize);
	maze = tempArray;
	for (int i = tempRow; i < rowSize; i++) {
	    maze[i] = malloc(sizeof(char)*colSize);
	}

	for (int i = 0; i < rowSize; i++) {
	    char *tempValue = realloc(maze[i], sizeof(maze[i])*colSize);
	    maze[i] = tempValue;
	}
	
	// Loop through the file again to assign each of the values in the file to the matrix.
        inFile = fopen(inputFile, "r");
        int row = 0;
        while (getline(&line, &len, inFile) > 0) {
            int col = 0;
            for (int i  = 0; i < (int)strlen(line); i++) {
                char currChar = line[i];
                if (currChar == 48) {
                    maze[row][col] = 46;
                    col++;
                }
                else if (currChar == 49) {
                    maze[row][col] = 35;
                    col++;
                }
            }
            row++;
        }

	// Free the buffer if it is still full.
        if (line != NULL) {
            free(line);
        }
        fclose(inFile);
    }
    if (outputFile == NULL) {

    // If the user specifies, print out the arguments and their meaning.
        if (h) {
            printf("-h      Print this helpful message to stout and exit.\n");
            printf("-d      Pretty print (display) the maze after reading.  (Default: off)\n");
            printf("-s      Print shortest solution steps.                  (Default: off)\n");
            printf("-p      print an optimal path.                          (Default: off)\n");
            printf("-i INFILE       Read maze from INFILE.                  (Default: stdin)\n");
            printf("-o OUTFILE      Write all output to OUTFILE.            (Default: stdout)\n");
        }

	// If the user wants, print out the maze out to look nice.
        if (d) {
            prettyPrint(rowSize, colSize, maze);
        }

        // Find the shortest path of the maze and loop backwards through the solution
	// to assign '+' to any coordinates that were traversed.
        int steps = 0;
        struct coordinate path[rowSize*colSize];
        if (findShortest(rowSize, colSize, maze, path) != -1) {
	    struct coordinate final = {rowSize - 1, colSize - 1};
	    int pathIndex = final.row*colSize + final.col;
	    while (path[pathIndex].row != 0 || path[pathIndex].col != 0) {
	        pathIndex = final.row*colSize + final.col;
                final = path[pathIndex];
	        if (maze[final.row][final.col] == '.') {
	            maze[final.row][final.col] = '+';
	            steps++;
	        }
	    }
	    maze[rowSize - 1][colSize - 1] = '+';
	    steps++;
	    if (s) {
	        printf("Solution in %d steps.\n", steps);
	    }
        }
        else {
	    if (s) {
	        printf("No solution.\n");
	    }
        }

	// If the user indicates, print out the new version of the maze.
        if (p) {
	    prettyPrint(rowSize, colSize, maze);
        }
    }
    if (outputFile != NULL) {
            
        FILE *outFile = fopen(outputFile, "w");
	
        // If the user specifies, print out the arguments and their meaning.
        if (h) {
            fputs("-h      Print this helpful message to stout and exit.\n", outFile);
            fputs("-d      Pretty print (display) the maze after reading.  (Default: off)\n", outFile);
            fputs("-s      Print shortest solution steps.                  (Default: off)\n", outFile);
            fputs("-p      print an optimal path.                          (Default: off)\n", outFile);
            fputs("-i INFILE       Read maze from INFILE.                  (Default: stdin)\n", outFile);
            fputs("-o OUTFILE      Write all output to OUTFILE.            (Default: stdout)\n", outFile);
        }
             
        // If the user wants, print out the maze out to look nice.
        if (d) {
            prettyPrintOutput(rowSize, colSize, maze, outFile);
        }

        // Find the shortest path of the maze and loop backwards through the solution
        // to assign '+' to any coordinates that were traversed.
        int steps = 0;
        struct coordinate path[rowSize*colSize];
        if (findShortest(rowSize, colSize, maze, path) != -1) {
            struct coordinate final = {rowSize - 1, colSize - 1};
            int pathIndex = final.row*colSize + final.col;
            while (path[pathIndex].row != 0 || path[pathIndex].col != 0) {
                if (maze[final.row][final.col] == '.') {
                    maze[final.row][final.col] = '+';
                    steps++;
                }
                pathIndex = final.row*colSize + final.col;
                final = path[pathIndex];
            }
            if (s) {
                fprintf(outFile, "Solution in %d steps.\n", steps + 1);
            }
        }
        else {
            if (s) {
	        fputs("No solution.\n", outFile);
	    }
        }

        // If the user indicates, print out the new version of the maze.
        if (p) {
            prettyPrintOutput(rowSize, colSize, maze, outFile);
        }
	fclose(outFile);
    }

    // Free the maze and its components once everything is done.
    deleteMaze(rowSize, maze);
    return (1);
}
