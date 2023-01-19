#!/bin/sh

FILES=$(find -path '*.java')

printf 'compiling...\n'
javac $FILES
printf '\ndone\n'
