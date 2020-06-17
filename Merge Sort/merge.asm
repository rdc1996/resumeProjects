#
# FILE:         $File$
# AUTHOR:       Phil White, RIT 2016
#               
# CONTRIBUTORS:
#		R. Carnation
#
# DESCRIPTION:
#	This file contains the merge function of mergesort
#

#-------------------------------

#
# Numeric Constants
#

PRINT_STRING = 4
PRINT_INT = 1


#***** BEGIN STUDENT CODE BLOCK 2 ***************************


#
# Make sure to add any .globl that you need here
#
.globl merge
.globl sort

#
# Name:         merge
# Description:  takes two individually sorted areas of an array and
#		merges them into a single sorted area
#
#		The two areas are defined between (inclusive)
#		area1:	low   - mid
#		area2:	mid+1 - high
#
#		Note that the area will be contiguous in the array
#
# Arguments:    a0:     a parameter block containing 3 values
#			- the address of the array to sort
#			- the address of the scrap array needed by merge
#			- the address of the compare function to use
#			  when ordering data
#               a1:     the index of the first element of the area
#               a2:     the index of the last element of the area
#               a3:     the index of the middle element of the area
# Returns:      none
# Destroys:     t0,t1
#

merge:
        addi    $sp, $sp, -40              # make space for saved registers
        sw      $ra, 36($sp)               # store ra on the stack
        sw      $a0, 32($sp)               # store a0 on the stack
        sw      $a1, 28($sp)               # store a1 on the stack
        sw      $a2, 24($sp)               # store a2 on the stack
        sw      $a3, 20($sp)               # store a3 on the stack
        sw      $s4, 16($sp)               # store s4 on the stack
        sw      $s3, 12($sp)               # store s3 on the stack
        sw      $s2, 8($sp)                # store s2 on the stack
        sw      $s1, 4($sp)                # store s1 on the stack
        sw      $s0, 0($sp)                # store s0 on the stack

        lw      $s2, 0($a0)                # load the addr of the array -> s2
        move    $s0, $s2                   # copy address of array for later
        lw      $t6, 4($a0)                # load scrap array -> t6
        move    $s1, $t6                   # copy address of scrap array
        lw      $t7, 8($a0)                # load compare function -> t7
        move    $s3, $s2                   # copy array -> s3
        move    $t2, $a3                   # copy midpoint -> t2
        addi    $t2, $t2, 1                # increment midpoint for area2
        move    $t3, $a2                   # copy end index -> t3
        addi    $t3, $t3, 1                # increment end index so inclusive
        move    $t4, $a1                   # copy start index -> t4
        move    $t5, $t2                   # new copy of midpoint for reference
        move    $t8, $t2                   # yet another midpoint copy
        addi    $t0, $zero, 0              # counter for first pointer
        sub     $s4, $a2, $a1              # difference in start and end
        addi    $s4, $s4, 1                # increment the difference
set_first_pointer:
        beq     $t0, $a1, set_second_pointer    # if t0 == start, pointer set
        addi    $s2, $s2, 4                     # increment array
        addi    $t0, $t0, 1                     # increment midpoint counter
        addi    $s0, $s0, 4                     # increment main array pointer
        j       set_first_pointer               # jump to top of loop
set_second_pointer:
        beq     $t2, $zero, merge_loop     # if t2 == 0, pointer set
        addi    $s3, $s3, 4                # increment array
        addi    $t2, $t2, -1               # decrement midpoint counter
        j       set_second_pointer         # jump to top of loop
merge_loop:
        beq     $t4, $t5, area1_empty      # if start = mid, area1 empty
        beq     $t8, $t3, area2_empty      # if mid = end, area2 empty
        lw      $a0, 0($s2)                # load area1 element -> a0
        lw      $a1, 0($s3)                # load area2 element -> a1
        jalr    $t7                        # jal to compare function
        beq     $v0, $zero, area2_smaller  # if v0 = 0, area2 element smaller
        sw      $a0, 0($t6)                # move area1 element -> scrap array
        addi    $s2, $s2, 4                # increment area1
        addi    $t4, $t4, 1                # increment area1 counter
        addi    $t6, $t6, 4                # increment scrap array
        j       merge_loop                 # jump back to loop
area2_smaller:
        sw      $a1, 0($t6)                # move area2 element -> scrap array
        addi    $s3, $s3, 4                # increment area2
        addi    $t8, $t8, 1                # increment area2 counter
        addi    $t6, $t6, 4                # increment scrap array
        j       merge_loop                 # jump back to loop
area1_empty:
        beq     $t8, $t3, copy_scrap       # if start area2 = end, done
        lw      $a0, 0($s3)                # load current area2 element
        sw      $a0, 0($t6)                # move element to scrap array
        addi    $s3, $s3, 4                # increment area2
        addi    $t8, $t8, 1                # increment area2 counter
        addi    $t6, $t6, 4                # increment scrap array
        j       area1_empty                # jump back to loop
area2_empty:
        beq     $t4, $t5, copy_scrap       # if start area1 = mid, done
        lw      $a0, 0($s2)                # load current area1 element
        sw      $a0, 0($t6)                # move element to scrap array
        addi    $s2, $s2, 4                # increment area1
        addi    $t4, $t4, 1                # increment area1 counter
        addi    $t6, $t6, 4                # increment scrap array
        j       area2_empty                # jump back to loop
copy_scrap:
        beq     $s4, $zero, merge_done     # if end index = 0, done copy
        lw      $a0, 0($s1)                # load element from scrap
        sw      $a0, 0($s0)                # store element in OG array
        addi    $s1, $s1, 4                # increment scrap array pointer
        addi    $s0, $s0, 4                # increment OG array pointer
        addi    $s4, $s4, -1               # decrement end index
        j       copy_scrap                 # jump to top of loop
merge_done:
        lw      $ra, 36($sp)               # restore ra from stack
        lw      $a0, 32($sp)               # store a0 on the stack
        lw      $a1, 28($sp)               # store a1 on the stack
        lw      $a2, 24($sp)               # store a2 on the stack
        lw      $a3, 20($sp)               # store a3 on the stack
        lw      $s4, 16($sp)               # restore s4 from stack
        lw      $s3, 12($sp)               # restore s3 from stack
        lw      $s2, 8($sp)                # restore s2 from stack
        lw      $s1, 4($sp)                # restore s1 from stack
        lw      $s0, 0($sp)                # restore s0 from stck
        addi    $sp, $sp, 40               # move sp back

        jr      $ra                        # return to caller
        

# ********** END STUDENT CODE BLOCK 2 **********

#
# End of Merge routine.
#
