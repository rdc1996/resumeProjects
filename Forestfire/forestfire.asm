# File:         $Id$
# Author:       R. Carnation
# Description:  This program runs the Forest-Fire model with some modifications
#               that eliminate some of the randomness. This is a simple 
#               two-dimensional cellular automaton where each cell exists in 
#               one of three states: empty (grass), occupied (tree), or 
#               burning. The game progresses one generation at a time where 
#               the state of the grid from the previous generation is queried,
#               and based on the state of the cells there, the grid for the 
#               new generation will be constructed.
#
# Purpose:      To implement the Forest-Fire model with some modifications
#
# CONSTANTS
#
# syscall codes
PRINT_INT =     1
PRINT_STRING =  4
READ_INT =      5
READ_STRING =   8
PRINT_CHARACTER = 11

# various frame sizes used by different routines

FRAMESIZE_8 =   8
FRAMESIZE_24 =  24
FRAMESIZE_40 =  40
FRAMESIZE_48 =  48

        .data
        .align 2

        #
        # Constants to be used by the code 
        #
wind_north:
        .ascii "N"
wind_east:
        .ascii "E"
wind_south:
        .ascii "S"
wind_west:
        .ascii "W"
burning_tree:
        .ascii "B"
tree:
        .ascii "t"
grass:
        .ascii "."
fig_char:
        .asciiz "0123456789"
program_banner_1:
        .asciiz "+-------------+"
program_banner_2:
        .asciiz "\| FOREST FIRE \|"
generation_num_str1:
        .asciiz "==== \#"
generation_num_str2:
        .asciiz " ===="
generation_header_plus:
        .asciiz "+"
generation_header_dash:
        .asciiz "-"
generation_sides:
        .asciiz "\|"
newline:
        .asciiz "\n"
grid_error:
        .asciiz "ERROR: invalid grid size"
gen_error:
        .asciiz "ERROR: invalid number of generations"
wind_error:
        .asciiz "ERROR: invalid wind direction"
input_error:
        .asciiz "ERROR: invalid character in grid"


#
# Name:         MAIN PROGRAM
#
# Description:  Main logic for the program.
#
#               The program reads four values from standard input:
#               1) An integer representing the grid size
#               2) An integer representing the number of generations
#               3) A character representing the direction of the wind
#               4) A set of strings that represent the state of the grid
#
#               The program will then run for the number of generations
#               specified. Trees will spread their seeds only in the direction
#               of the wind as long as the adjacenmt space is grass. Burning
#               trees will infect any trees touching them. Grass will remain.
#               
#

old_generation:
        .space 10000        # allocate more than enough space for og gen
new_generation:
        .space 10000        # allocate same space for next generation

        .text                   # this is program code
        .align 2                # instructions must be on word boundaries

main:

        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)

        #
        # Read the grid size into s0
        #
        li      $v0, READ_INT
        syscall
        move    $s0, $v0
        move    $s2, $s0        # copy grid size to s2
        mul     $s4, $s0, 5     # number of chars to read

        #
        # Read the num of generations into s1
        #
        li      $v0, READ_INT
        syscall
        move    $s1, $v0

        #
        # Read the character representing the wind direction
        #
        li      $v0, READ_STRING    # read a string
        la      $a0, fig_char       # place to store the char read in
        addi    $a1, $zero, 9       # the number of characters to read
        syscall

        #
        # Read the grid
        #
        la      $s3, old_generation      # load address of buffer -> a0

read_grid:
        beq     $s2, $zero, done_reading      # if done reading print header

        move    $a0, $s3                      # where string is stored
        li      $v0, READ_STRING              # read a string
        add     $a1, $zero, $s4               # number of chars to read
        syscall

        add     $s3, $s3, $s0                 # increment array by grid size
        addi    $s2, $s2, -1                  # decrement grid counter
        j       read_grid                     # read more input lines

done_reading:
        jal     print_header           # jump to method to print header
       
        move    $a0, $s0               # a0 = grid size
        move    $a1, $s1               # a1 = num of generations
        lbu     $a2, fig_char          # a2 = wind direction
        la      $a3, old_generation    # a3 = loaction of old generation

        jal     error_checking         # jump to error checking

        li      $s0, 1                 # load 1 -> s0
        beq     $s0, $v0, kill_program # kill program if error       

        jal     updating_generations   # print out the generation

#
# All done -- exit the program!
#
kill_program:
        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra

#
# Update the current generation
#
updating_generations:

        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)
  
        move    $s0, $a0                   # s0 = grid size
        move    $s1, $a1                   # s1 = gen number
        move    $s2, $a2                   # s2 = wind direction
        la      $s3, old_generation        # s3 = old grid array
        add     $a3, $zero, $zero          # a2 = current gen number

        jal     print_gen_number
        addi    $a3, $a3, 1                # increment gen number

        move    $a1, $s3                   # generation to print -> a1
        move    $a2, $s0                   # move grid size -> a2

        jal     print_current_generation

generations_loop:
        beq     $s1, $zero, done_updating_gen  # loop until gen num = 0
        jal     print_gen_number               # print out current number

        move    $a0, $s0                       # grid size -> a0
        move    $a1, $s2                       # wind direction -> a1
        jal     change_generation_trees        # update all actual trees
        jal     change_generation_burning      # update burning trees

        jal     swap_generations               # put the new gen in the old spot

        la      $a1, old_generation            # generation to print -> a1
        move    $a2, $s0                       # move grid size -> a2
        jal     print_current_generation       # print out the current gen
        addi    $s1, $s1, -1                   # decrement gens left
        addi    $a3, $a3, 1                    # increment gen number
        j       generations_loop

done_updating_gen:

        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra
#
# Takes the old generation and makes the changes then moves it back to the
# old generation spot in memory
#
# grid size -> a0
# wind direction -> a1
#
change_generation_trees:
        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)

        la      $s0, old_generation            # load the old gen ->s0
        la      $s1, old_generation            # load the old gen ->s1
        la      $s2, new_generation            # load address for new gen ->s2
        mul     $s3, $a0, $a0                  # grid counter
        lbu     $t0, burning_tree              # t0 = (B)
        lbu     $t1, tree                      # t1 = (t)
        lbu     $t2, grass                     # t2 = (.)

        lbu     $s4, wind_north                # check if wind is north
        beq     $a1, $s4, wind_is_north

        lbu     $s4, wind_east                 # check if wind is east
        beq     $a1, $s4, wind_is_east

        lbu     $s4, wind_south                # check if wind is south
        beq     $a1, $s4, wind_is_south

        lbu     $s4, wind_west                 # check if wind is west
        beq     $a1, $s4, wind_is_west

wind_is_north:
        sub     $s3, $s3, $a0                  # remove last row of trees
        add     $s1, $s1, $a0                  # s1 = pointer 1 row ahead
        move    $s6, $a0                       # grid size -> s6
loop_north:
        beq     $s3, $zero, done_north         # if grid counter = 0, done
        lbu     $s4, 0($s0)                    # old gen first -> s4
        lbu     $s5, 0($s1)                    # old gen second -> s5
        bne     $s4, $t2, skip_location_north  # if not grass, skip
        bne     $s5, $t1, skip_location_north  # if not also tree, skip
        sb      $t1, 0($s2)                    # store tree in new gen
        j       already_stored_north
skip_location_north:
        sb      $s4, 0($s2)                    # store old symbol in new gen
already_stored_north:
        addi    $s0, $s0, 1                    # increment old gen counter 1
        addi    $s1, $s1, 1                    # increment old gen counter 2
        addi    $s2, $s2, 1                    # increment new gen counter
        addi    $s3, $s3, -1                   # decrement grid counter
        j       loop_north
done_north:
        beq     $s6, $zero, done_trees         # if grid size = 0, done trees
        lbu     $s4, 0($s0)                    # load byte from old gen
        sb      $s4, 0($s2)                    # store byte in new gen
        addi    $s0, $s0, 1                    # increment old gen pointer
        addi    $s2, $s2, 1                    # increment new gen pointer
        addi    $s6, $s6, -1                   # decrement grid counter
        j       done_north

wind_is_east:
        la      $s7, new_generation            # another pointer -> s7
        addi    $s1, $s1, 1                    # s1 = next old gen
        addi    $s7, $s7, 1                    # s7 = next new gen
loop_east:
        beq     $s3, $zero, done_trees         # if grid counter = 0, done
        rem     $s4, $s3, $a0                  # rem counter/grid size = t0
        li      $s5, 1                         # load 1 -> t1
        beq     $s4, $s5, skip_location_east   # if last col, skip
        bne     $s4, $zero, not_first_col      # if rem != 0, not first col
        lbu     $s4, 0($s0)                    # load from old gen curr
        sb      $s4, 0($s2)                    # store in new gen
not_first_col:
        lbu     $s4, 0($s0)                    # old gen curr = s4
        lbu     $s5, 0($s1)                    # old gen next = s5
        bne     $s4, $t1, skip_location_east   # if not tree, skip
        bne     $s5, $t2, skip_location_east   # if also not grass, skip
        sb      $t1, 0($s7)                    # store in next new gen spot
        j       already_stored_east
skip_location_east:
        sb      $s5, 0($s7)                    # store in next new gen spot
already_stored_east:
        addi    $s0, $s0, 1                    # increment old gen counter 1
        addi    $s1, $s1, 1                    # increment old gen counter 2
        addi    $s2, $s2, 1                    # increment new gen counter 1
        addi    $s7, $s7, 1                    # increment new gen counter 2
        addi    $s3, $s3, -1                   # decrement grid counter
        j       loop_east

wind_is_south:
        sub     $s3, $s3, $a0                  # remove last row of trees
        move    $s6, $a0                       # grid size -> s6
before_south:
        beq     $s6, $zero, loop_south         # if grid size = 0, done trees
        lbu     $s4, 0($s1)                    # load byte from old gen
        sb      $s4, 0($s2)                    # store byte in new gen
        addi    $s1, $s1, 1                    # increment second old gen ptr
        addi    $s2, $s2, 1                    # increment new gen pointer
        addi    $s6, $s6, -1                   # decrement grid counter
        j       before_south
loop_south:
        beq     $s3, $zero, done_trees         # if grid counter = 0, done
        lbu     $s4, 0($s0)                    # old gen first -> s4
        lbu     $s5, 0($s1)                    # old gen second -> s5
        bne     $s4, $t1, skip_location_south  # if not tree, skip
        bne     $s5, $t2, skip_location_south  # if not also grass, skip
        sb      $t1, 0($s2)                    # store tree in new gen
        j       already_stored_south
skip_location_south:
        sb      $s5, 0($s2)                    # store old symbol in new gen
already_stored_south:
        addi    $s0, $s0, 1                    # increment old gen counter 1
        addi    $s1, $s1, 1                    # increment old gen counter 2
        addi    $s2, $s2, 1                    # increment new gen counter
        addi    $s3, $s3, -1                   # decrement grid counter
        j       loop_south

wind_is_west:
        la      $s7, new_generation            # another pointer -> s7
        addi    $s1, $s1, 1                    # s1 = next old gen
loop_west:
        beq     $s3, $zero, done_trees         # if grid counter = 0, done
        rem     $s4, $s3, $a0                  # rem counter/grid size = t0
        li      $s5, 1                         # load 1 -> t1
        bne     $s4, $s5, not_last_col         # if last col, skip
        lbu     $s4, 0($s0)                    # load from old gen curr
        sb      $s4, 0($s2)                    # store in new gen
        j       already_stored_west
not_last_col:
        lbu     $s4, 0($s0)                    # old gen curr = s4
        lbu     $s5, 0($s1)                    # old gen next = s5
        bne     $s4, $t2, skip_location_west   # if not grass, skip
        bne     $s5, $t1, skip_location_west   # if also not tree, skip
        sb      $t1, 0($s2)                    # store in next new gen spot
        j       already_stored_west
skip_location_west:
        sb      $s4, 0($s2)                    # store in next new gen spot
already_stored_west:
        addi    $s0, $s0, 1                    # increment old gen counter 1
        addi    $s1, $s1, 1                    # increment old gen counter 2
        addi    $s2, $s2, 1                    # increment new gen counter 1
        addi    $s3, $s3, -1                   # decrement grid counter
        j       loop_west

done_trees:
        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra

#
# In charge of putting the new generation in the old spot
#
swap_generations:
        la      $t0, old_generation            # old generation = t0
        la      $t1, new_generation            # new generation = t1
        mul     $t2, $a0, $a0                  # grid size = a0^2
swap_loop:
        beq     $t2, $zero, done_swap
        lbu     $t3, 0($t1)                    # load byte from new gen -> t3
        sb      $t3, 0($t0)                    # store byte frm new gen -> old
        addi    $t0, $t0, 1                    # increment old gen pointer
        addi    $t1, $t1, 1                    # increment new gen pointer
        addi    $t2, $t2, -1                   # decrement grid counter
        j       swap_loop
done_swap:
        jr      $ra

#
# Update generation with burning trees
#
change_generation_burning:
        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)

        move    $s2, $a0               # copy grid size -> s2

        la      $s0, old_generation    # pointer to old gen -> s0
        la      $t0, old_generation    # pointer to old gen next-> t0
        addi    $t0, $t0, 1
        la      $t1, old_generation    # pointer to old gen last -> t1
        addi    $t1, $t1, -1
        la      $t2, old_generation    # pointer to old gen + grid -> t2
        add     $t2, $t2, $s2
        la      $t3, old_generation    # pointer to old gen - grid -> t3
        sub     $t3, $t3, $s2

        la      $s1, new_generation    # pointer to new gen -> s1
        la      $t4, new_generation    # pointer to new gen next -> t4
        addi    $t4, $t4, 1
        la      $t5, new_generation    # pointer to new gen last -> t5
        addi    $t5, $t5, -1
        la      $t6, new_generation    # pointer to new gen + grid -> t6
        add     $t6, $t6, $s2
        la      $t7, new_generation    # pointer to new gen - grid -> t7
        sub     $t7, $t7, $s2

        mul     $s3, $s2, $s2          # square grid size for counter
        lbu     $t8, burning_tree      # t8 = (B)
        lbu     $t9, grass             # t9 = (.)
        lbu     $v0, tree              # v0 = (t)
burning_loop:
        beq     $s3, $zero, done_burning
        lbu     $s4, 0($s0)                # load current byte from old gen
        beq     $t8, $s4, found_one        # if s4 = t8, found (B)
        j       increment_hell
found_one:
        sb      $t9, 0($s1)                # update (B) -> (.)
        lbu     $s4, 0($t0)                # load byte next space
        beq     $s4, $v0, next_burning     # if (B) found burning
        j       test_before
next_burning:
        sb      $t8, 0($t4)                # store new (B) in new gen
test_before:
        lbu     $s4, 0($t1)                # load byte before space
        beq     $s4, $v0, before_burning   # if (B) found burning
        j       test_plus_grid
before_burning:
        sb      $t8, 0($t5)                # store new (B) in new gen
test_plus_grid:
        lbu     $s4, 0($t2)                # load byte next row
        beq     $s4, $v0, plus_burning     # if (B) found burning
        j       test_minus_grid
plus_burning:
        sb      $t8, 0($t6)                # store new (B) in new gen
test_minus_grid:
        lbu     $s4, 0($t3)                # load byte last row
        beq     $s4, $v0, minus_burning    # if (B) found burning
        j       increment_hell
minus_burning:
        sb      $t8, 0($t7)                # store new (B) in new gen
increment_hell:
        addi    $s0, $s0, 1                # increment my manyyyyyy pointers
        addi    $t0, $t0, 1
        addi    $t1, $t1, 1
        addi    $t2, $t2, 1
        addi    $t3, $t3, 1
        addi    $t4, $t4, 1
        addi    $t5, $t5, 1
        addi    $t6, $t6, 1
        addi    $t7, $t7, 1
        addi    $s1, $s1, 1
        addi    $s3, $s3,-1
        j       burning_loop
done_burning:
        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra

#
# Print the current generation out
#
print_current_generation:
        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)

        jal     print_gen_box                # print top of box

        jal     print_newline                # print newline

        move    $s0, $a1                     # move generation to print -> s0 
        mul     $s1, $a2, $a2                # t1 = (a2)^2
        add     $s2, $zero, $zero            # t2 = line counter
        
print_generation_loop:
        beq     $s1, $zero, print_generation_end  
        bne     $s2, $zero, continue_row
        jal     print_bar_char               # print bar (|) char

continue_row:
        lb      $a0, 0($s0)                  # load and print current byte
        li      $v0, PRINT_CHARACTER            
        syscall

        addi    $s2, $s2, 1                       # increment curr char cnter
        addi    $s1, $s1, -1                      # decrement total counter 
        addi    $s0, $s0, 1                       # increment char pointer
        bne     $s2, $a2, print_generation_loop   # if not new line, loop

        add     $s2, $zero, $zero                 # reset curr char counter
        jal     print_bar_char                    # print bar (|) char
        jal     print_newline                     # print newline
        j       print_generation_loop             # loop again

print_generation_end:
        jal     print_gen_box                # print bottom of box

        jal     print_newline                # print newlines for aesthetics
        jal     print_newline

        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra

#
# Print the top and bottom of generation box
# 
print_gen_box:
        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)

        #
        # Print out corner
        #
        move    $s0, $a2                     # move grid size -> s0
        li      $a0, 43
        li      $v0, PRINT_CHARACTER
        syscall

        #
        # Print out dashes
        #
print_dashes:
        beq     $s0, $zero, done_dashes
        li      $a0, 45
        li      $v0, PRINT_CHARACTER
        syscall
        addi    $s0, $s0, -1
        j       print_dashes        

        #
        # Print out corner
        #
done_dashes:
        li      $a0, 43
        li      $v0, PRINT_CHARACTER
        syscall


        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra

#
# Print the bar (|) character
#
print_bar_char:
        li      $a0, 124
        li      $v0, PRINT_CHARACTER
        syscall
        
        jr      $ra
#
# Prints the current generation number (a2)
#
print_gen_number:
        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)

        la      $a0, generation_num_str1     # print half of gen header
        li      $v0, PRINT_STRING
        syscall

        move    $a0, $a3                     # print number of gen
        li      $v0, PRINT_INT
        syscall

        la      $a0, generation_num_str2     # print other half of gen header
        li      $v0, PRINT_STRING
        syscall

        jal     print_newline

        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra

#
# Check parameters to make sure they are in the constraints given
#
error_checking:

        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)

        li      $s5, 4
        slt     $s0, $a0, $s5                  # is grid size < 4?
        bne     $s0, $zero, print_grid_error

        li      $s5, 31
        slt     $s1, $a0, $s5                  # is grid size < 31?
        beq     $s1, $zero, print_grid_error

        slt     $s2, $a1, $zero                # is gen num < 0?
        bne     $s2, $zero, print_gen_error
 
        li      $s5, 21
        slt     $s3, $a1, $s5                  # is gen num < 21?
        beq     $s3, $zero, print_gen_error

        lbu     $s4, wind_north                # check if wind is north
        beq     $a2, $s4, check_grid

        lbu     $s4, wind_east                 # check if wind is east
        beq     $a2, $s4, check_grid

        lbu     $s4, wind_south                # check if wind is south
        beq     $a2, $s4, check_grid

        lbu     $s4, wind_west                 # check if wind is west
        beq     $a2, $s4, check_grid
 
        la      $a0, wind_error                # no match, print error
        li      $v0, PRINT_STRING
        syscall

        jal     print_newline

        li      $v0, 1                   # load 1 into v0 to show error
        j       done_error_checking

check_grid:
        mul     $s0, $a0, $a0            # number of chars to read = a0^2
        lbu     $s1, burning_tree        # load burning tree (B) -> s1
        lbu     $s2, tree                # load reg tree (t) -> s2
        lbu     $s3, grass               # load grass (.) -> s3
        move    $s5, $a3                 # move grid array -> s5

check_grid_loop:
        beq     $s0, $zero, done_error_checking   # if s0 == 0, jump to done
        lbu     $s4, 0($s5)                       # load char from grid array
        beq     $s4, $s1, check_grid_loop_match   # check if char = (B)
        beq     $s4, $s2, check_grid_loop_match   # check if char = (t)
        beq     $s4, $s3, check_grid_loop_match   # check if char = (.)
        j       print_input_error

check_grid_loop_match:
        addi    $s5, $s5, 1       # increment grid array
        addi    $s0, $s0, -1      # decrement char counter
        j       check_grid_loop   # jump to top of loop

print_input_error:
        la      $a0, input_error
        li      $v0, PRINT_STRING
        syscall

        jal     print_newline

        li      $v0, 1                   # load 1 into v0 to show error
        j       done_error_checking

print_gen_error:
        la      $a0, gen_error
        li      $v0, PRINT_STRING
        syscall

        jal     print_newline        

        li      $v0, 1                   # load 1 into v0 to show error     
        j       done_error_checking

print_grid_error:
        la      $a0, grid_error
        li      $v0, PRINT_STRING
        syscall

        jal     print_newline
    
        li      $v0, 1                   # load 1 into v0 to show error
        j       done_error_checking

done_error_checking:
        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra

#
# Print out the header for Forest Fire
#
print_header:

        addi    $sp, $sp,-FRAMESIZE_48
        sw      $ra, -4+FRAMESIZE_48($sp)
        sw      $s7, -8+FRAMESIZE_48($sp)
        sw      $s6, -12+FRAMESIZE_48($sp)
        sw      $s5, -16+FRAMESIZE_48($sp)
        sw      $s4, -20+FRAMESIZE_48($sp)
        sw      $s3, -24+FRAMESIZE_48($sp)
        sw      $s2, -28+FRAMESIZE_48($sp)
        sw      $s1, -32+FRAMESIZE_48($sp)
        sw      $s0, -36+FRAMESIZE_48($sp)
        
        #
        # Print out the top of the banner
        #
        la      $a0, program_banner_1
        li      $v0, PRINT_STRING
        syscall

        jal     print_newline

        #
        # Print out the text for the banner
        #
        la      $a0, program_banner_2
        li      $v0, PRINT_STRING
        syscall

        jal     print_newline
   
        #
        # Print out the bottom of the banner
        #
        la      $a0, program_banner_1       
        li      $v0, PRINT_STRING
        syscall  

        jal     print_newline
        jal     print_newline

        lw      $ra, -4+FRAMESIZE_48($sp)
        lw      $s7, -8+FRAMESIZE_48($sp)
        lw      $s6, -12+FRAMESIZE_48($sp)
        lw      $s5, -16+FRAMESIZE_48($sp)
        lw      $s4, -20+FRAMESIZE_48($sp)
        lw      $s3, -24+FRAMESIZE_48($sp)
        lw      $s2, -28+FRAMESIZE_48($sp)
        lw      $s1, -32+FRAMESIZE_48($sp)
        lw      $s0, -36+FRAMESIZE_48($sp)
        addi    $sp, $sp, FRAMESIZE_48

        jr      $ra

#
# Print. out a newline at the end
#
print_newline:
        la      $a0, newline
        li      $v0, PRINT_STRING
        syscall

        jr      $ra
