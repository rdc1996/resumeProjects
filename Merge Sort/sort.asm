#
# FILE:         $File$
# AUTHOR:       Phil White, RIT 2016
# CONTRIBUTORS:
#		W. Carithers
#		R. Carnation
#
# DESCRIPTION:
#	This program is an implementation of merge sort in MIPS
#	assembly 
#
# ARGUMENTS:
#	None
#
# INPUT:
# 	The numbers to be sorted.  The user will enter a 9999 to
#	represent the end of the data
#
# OUTPUT:
#	A "before" line with the numbers in the order they were
#	entered, and an "after" line with the same numbers sorted.
#
# REVISION HISTORY:
#	Aug  08		- P. White, original version
#

#-------------------------------

#
# Numeric Constants
#

PRINT_STRING = 4
PRINT_INT = 1

#-------------------------------

#

# ********** BEGIN STUDENT CODE BLOCK 1 **********

#
# Make sure to add any .globl that you need here
#

.globl merge
.globl sort

# Name:         sort
# Description:  sorts an array of integers using the merge sort
#		algorithm
# 		Arguments Note: a1 and a2 specify the range inclusively
#
# Arguments:    a0:     a parameter block containing 3 values
#                       - the address of the array to sort
#                       - the address of the scrap array needed by merge
#                       - the address of the compare function to use
#                         when ordering data
#               a1:     the index of the first element in the range to sort
#               a2:     the index of the last element in the range to sort
# Returns:      none
#

sort:
        addi    $sp, $sp, -16                 # make space for ra/variables
        sw      $ra, 12($sp)                  # push ra on the stack
#        sw      $a0, 12($sp)                  # push parameter block on stack
        sw      $a1, 8($sp)                   # push first index on stack
        sw      $a2, 4($sp)                   # push second index on stack

        sub     $t0, $a2, $a1                 # sub first index from second
        addi    $t1, $zero, 1                 # load 2 into temp reg (t1)
        slt     $t2, $t0, $t1                 # check if t0 < t1, t2 = 1|0 
        bne     $t2, $zero, sort_done         # if t2 = 1|0, return 

        add     $t3, $a1, $a2                 # add left and right indexes
        div     $t3, $t3, 2                   # divide by 2, midpoint -> t3
        sw      $t3, 0($sp)                   # store midpoint on stack
        move    $a2, $t3                      # move mid to high for first half
        jal     sort                          # call sort on first half

        lw      $a1, 0($sp)                   # load midpoint
        addi    $a1, $a1, 1                   # increment mid by 1 -> low
        lw      $a2, 4($sp)                   # load second index
        jal     sort                          # call sort on second half

        lw      $a3, 0($sp)                   # load midpoint
        lw      $a2, 4($sp)                   # load second index
        lw      $a1, 8($sp)                   # load first index
#        lw      $a0, 8($sp)                   # load parameter block
        jal     merge                         # call the merge routine

sort_done:
        lw      $ra, 12($sp)                  # load the return address
        addi    $sp, $sp, 16                  # move stack pointer back

        jr      $ra                           # return to caller

# ********** END STUDENT CODE BLOCK 1 **********

#
# End of Merge sort routine.
#
