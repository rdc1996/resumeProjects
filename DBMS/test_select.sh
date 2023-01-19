#!/bin/bash

PIPE='/tmp/db_input'
OUT_PIPE='/tmp/db_out'

### UTILITY FUNCTIONS ###

flush() {
    PIPE_TO_FLUSH=$1
    WAIT=$2
    [ $WAIT ] && dd if=$PIPE_TO_FLUSH count=1 status=none
    while true
    do
        # sleeps are dumb, but too bad!
        sleep 1 ; dd bs=1G if=$PIPE_TO_FLUSH iflag=nonblock status=none count=1 2> /dev/null || break
    done
}

wait_user() {
    printf "press enter to continue...\n\n"
    read
}

destroy_pipe() {
    [ -e "$PIPE" ] && {
        echo "destroying input pipe..."
        rm "$PIPE" || echo "input pipe not destroyed"
    }

    [ -e "$OUT_PIPE" ] && {
        echo "destroying output pipe..."
        rm "$OUT_PIPE" || echo "output pipe not destroyed"
    }
}

die() {
    echo $@
    destroy_pipe
    exit 1
}

[ -e Database.java ] || die "please run this tester from where the database file is"

### DEFAULTS ###

CWD="$(pwd)"
DB_DIR="$1"

[ -z "$DB_DIR" ] && DB_DIR="database"
PAGE_SIZE=4096
BUFFER_SIZE=5
DB_PATH="${CWD}/${DB_DIR}"

### SQL STATEMENTS ###
create_table() {
    [ -z "$1" ] && die "name not given for table"
    CREATE_TABLE_STATEMENT="create table $1( attr1 integer primarykey, attr2 double, attr3 boolean, attr4 char(5), attr5 varchar(10) );"

    echo $CREATE_TABLE_STATEMENT > $PIPE
}

insert_data() {
    TABLE=$1
    RECORDS=$2

    INSERT_STATEMENT="insert into $TABLE values "

    for RECORD in $RECORDS
    do
        INSERT_STATEMENT="$INSERT_STATEMENT($RECORD),"
    done

    # kinda a dumb way to remove the last comma don't @ me
    INSERT_STATEMENT=$(echo $INSERT_STATEMENT | sed 's/,$//')
    INSERT_STATEMENT="$INSERT_STATEMENT;"

    echo $INSERT_STATEMENT > $PIPE
}

execute() {
    STATEMENT=$1
    echo "execute ${STATEMENT} (y/n)?"
    read yn
    yn="$(echo $yn | grep -Exi 'n(o)?')"
    [ -z "$yn" ] || {
        printf "skipping...\n\n"
        return
    }
    echo "$STATEMENT" > $PIPE
    echo "=== BEGIN DATABASE OUTPUT ==="
    flush $OUT_PIPE true
    printf "\n=== END DATABASE OUTPUT ===\n"
    wait_user
}

### MAIN ROUTINE ###

echo "expecting the database folder to be clean before running test."
printf "remove everything in the folder: \"${DB_PATH}\"(y/n)? "

read yn
yn="$(echo $yn | grep -Exi 'y(es)?')"
[ -z "$yn" ] && die "did not receive an affirmative answer"

echo "removing database folder..."
rm -rf "$DB_PATH" || die "remove failed unexpectedly"

echo "compiling code..."
find . -path '*.java' -exec javac {} + || die "failed to compile code"

echo "initializing pipes..."
destroy_pipe &> /dev/null
mkfifo $PIPE
mkfifo $OUT_PIPE

echo "opening pipe for writing..."
exec 3<> $PIPE

echo "initializing database..."

(java Database "$DB_PATH" $PAGE_SIZE $BUFFER_SIZE < $PIPE &> $OUT_PIPE || die "a fatal error occurred running the database...") &

# create two tables
create_table table1
create_table table2
create_table table3
create_table table4

# yeah the cat is a bit dumb, but either way I'd have to pipe to strip the filename
LINES=$(cat test_data.csv | wc -l)
MID=$(expr $LINES / 2)

# insert test data. split "evenly"
insert_data table1 "$(cat test_data.csv | awk "{if (NR <= $MID) print}")"
insert_data table2 "$(cat test_data.csv | awk "{if (NR > $MID) print}")"
insert_data table3 "$(cat test_data.csv | awk "{if (NR <= $MID) print}")"
insert_data table4 "$(cat test_data.csv | awk "{if (NR > $MID) print}")"

# dumb, but it works
head -8 $OUT_PIPE #&> /dev/null

# pipe needs to be opened for reading, but it must occur after head...
# I don't really know why but it works!
exec 4< $OUT_PIPE

# flush the rest
flush $OUT_PIPE &> /dev/null

printf '\nfinished initializing database\n'
wait_user

echo 'selecting only one attr from table1'
execute 'select attr1 from table1;'

echo 'selecting two attrs from table1'
execute 'select attr1, attr2 from table1;'

echo 'selecting same attr from table1 twice'
execute 'select attr1, attr1 from table1;'

echo 'selecting three attrs from table1'
execute 'select attr1, attr2, attr3 from table1;'

echo 'selecting all attrs from table1'
execute 'select * from table1;'

echo 'selecting all attrs from table1 no star'
execute 'select attr1, attr2, attr3, attr4, attr5 from table1;'

echo 'selecting record 0 from table1'
execute 'select * from table1 where attr1 = 0;'

echo 'selecting no records from table1'
execute 'select * from table1 where attr1 = -1;'

echo 'selecting a non-existant attribute'
execute 'select foo from table1;'

echo 'selecting a non-existant attribute, no dot'
execute 'select foo from table1;'

echo 'selecting from a non-existant table'
execute 'select * from ungabunga;'

echo 'bad select keyword'
execute 'selct foo from table1;'

echo 'bad from keyword'
execute 'select foo fro table1;'

echo 'attempting cartesian product between one table1 record and all of table2'
execute 'select * from table1, table2 where table1.attr1 = 1;'

echo 'attempting cartesian product between no records of table1 and all of table2'
execute 'select * from table1, table2 where table1.attr1 = -1;'

echo 'ensuring we get duplicate records if we only select one attribute'
execute 'select table1.attr1 from table1, table2 where table1.attr1 = 1;'

echo 'ensuring we get duplicate records if we only select multiple attributes'
execute 'select table1.attr1, table1.attr2 from table1, table2 where table1.attr1 = 1;'
execute 'select table1.attr1, table2.attr2 from table1, table2 where table1.attr1 = 1;'
execute 'select table1.attr1, table2.attr2, table1.attr3 from table1, table2 where table1.attr1 = 1;'

echo "attempting cartesian product between two of table1's records and all of table2"
execute 'select * from table1, table2 where table1.attr1 < 2;'

echo "attempting cartesian product between two of table1's records and all of table2, and the ordering"
execute 'select * from table1, table2 where table1.attr1 < 2 orderby table1.attr1;'

echo 'attempting cartesian product between all of table1 and table2'
execute 'select * from table1, table2;'

echo 'attempting cartesian product between all of table1 and table2 and all of table3'
execute 'select * from table1, table2, table3;'

echo 'attempting cartesian product between all of table1 and table2 and all of table3 and all of table4'
execute 'select * from table1, table2, table3, table4;'

echo 'selecting a non-existant attribute of a cartesian product'
execute 'select table1.foo from table1, table2;'

echo 'selecting a non-existant attribute of a cartesian product, no dot'
execute 'select foo from table1, table2;'

echo 'selecting a non-renamed attribute of a cartesian product'
execute 'select attr1 from table1, table2;'

echo 'attempting cartesian product between existing and non-existing table'
execute 'select * from table1, ungabunga;'

echo 'attempting cartesian product between a table and itself'
execute 'select * from table1, table1;'

echo "attempting cartesian product but the where clause is bad"
execute 'select * from table1, table2 where table1.attr1 < 2 and orderby table1.attr1;'

echo 'attempting orderby on integer (primary key)'
execute 'select * from table1 orderby attr1;'

echo 'attempting orderby on double'
execute 'select * from table1 orderby attr2;'

echo 'attempting orderby on boolean'
execute 'select * from table1 orderby attr3;'

echo 'attempting orderby on char'
execute 'select * from table1 orderby attr4;'

echo 'attempting orderby on varchar'
execute 'select * from table1 orderby attr5;'

echo 'attempting orderby with multiple attributes'
execute 'select * from table1 orderby attr3 attr1;'

echo 'attempting orderby on cartesian product'
execute 'select * from table1, table2 orderby table1.attr2;'

echo 'attempting orderby on non-existant attribute'
execute 'select * from table1 orderby table1.foo;'

echo 'attempting orderby on non-existant attribute, no dot'
execute 'select * from table1 orderby foo;'

echo "attempting orderby on existant attribute that we do select"
execute 'select attr1, attr2 from table1 orderby attr1;'

echo "attempting orderby on existant attribute that we don't select"
execute 'select attr1, attr3 from table1 orderby attr2;'

echo 'attempting orderby on non-renamed cartesian product attribute'
execute 'select * from table1, table2 orderby attr1;'

echo "quit" > $PIPE

echo "closing pipe..."
echo EOF > $PIPE
exec 3>&- # close file descriptor 3
destroy_pipe
echo "done"
exit 0
