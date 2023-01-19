#!/bin/python3
import random as rand
import string
import sys

def random_string(max_length):
    length = rand.randrange(max_length)

    astr = ''.join(rand.choice(string.ascii_uppercase + string.ascii_lowercase + string.digits) for _ in range(length))
    # string must be surrounded in quotes
    astr = '"' + astr + '"'

    return astr

def gen_record(pkeys):
    record = (rand.choice(pkeys), rand.random(), rand.choice(["true", "false"]), random_string(5), random_string(10))
    pkeys.remove(record[0])

    stringify_record = ""
    for col in record:
        stringify_record += str(col)
        stringify_record += ','

    stringify_record = stringify_record[:-1]

    return stringify_record

if __name__ == "__main__":
    num_records = 1000
    if len(sys.argv) > 1:
        num_records = int(sys.argv[1])

    keys = [i for i in range(num_records)]

    with open('test_data.csv', 'w') as file:
        for _ in range(num_records):
            file.write(gen_record(keys))
            file.write('\n')

    print(num_records, 'records created')
