#
# Makefile for CompOrg rasm/rlink
#
##################################
#
# You'll need to change the name "FILENAME" to the basename of your
# asm program (so if my program was "life.asm" I would change FILENAME
# to life everywhere)
#
##################################
#

#
# Location of the processing programs
#
RASM  = /home/fac/wrc/bin/rasm
RLINK = /home/fac/wrc/bin/rlink

#
# Suffixes to be used or created
#
.SUFFIXES:	.asm .obj .lst .out

#
# Transformation rule: .asm into .obj
#
.asm.obj:
	$(RASM) -l $*.asm > $*.lst

#
# Transformation rule: .obj into .out
#
.obj.out:
	$(RLINK) -o $*.out $*.obj

#
# Main target
#
forestfire.out:	forestfire.obj
