"""
Ryan Carnation
Lab #9
Test File
"""


from airit_simulation import *


def test_make_gate(maximum):
    """
    Tests the make_empty_gate function.
    :param maximum:
    :return:
    """
    return make_empty_gate(maximum)


def test_make_plane(maximum):
    """
    Tests the make_empty_plane function
    :param maximum:
    :return:
    """
    return make_empty_plane(maximum)


def test_read_passenger(file_name):
    """
    Tests the read_passenger function.
    :param file_name:
    :return:
    """
    file = open(file_name)
    for _ in file:
        passenger = read_passenger(file)
        print(passenger)
    file.close()


def test_line_up(file, maximum):
    """
    Tests the line_up function.
    :param file:
    :param maximum:
    :return:
    """
    file = open(file)
    gate = make_empty_gate(maximum)
    line_up(gate, file)
    file.close()
    return gate


def test_boarding(gate, maximum):
    """
    Tests the boarding function.
    :param gate:
    :param maximum:
    :return:
    """
    plane = make_empty_plane(maximum)
    boarding(gate, plane)
    return plane


def test_empty_plane(plane):
    """
    Tests the empty_plane function.
    :param plane:
    :return:
    """
    empty_plane(plane)


def test_all():
    """
    Calls all of the test functions and uses prints statements to make everything more organized.
    :return:
    """
    print("---Creating Gate 1---")
    gate1 = test_line_up("passengers_very_small.txt", 5)
    print("---Creating Gate 2---")
    gate2 = test_line_up("passengers_small.txt", 50)
    print("---Creating Gate 3---")
    gate3 = test_line_up("passengers_large.txt", 500)
    print("---Boarding Plane 1---")
    plane1 = test_boarding(gate1, 5)
    print("---Boarding Plane 2---")
    plane2 = test_boarding(gate2, 50)
    print("---Boarding Plane 3---")
    plane3 = test_boarding(gate3, 500)
    print("---Emptying Plane 1---")
    test_empty_plane(plane1)
    print("---Emptying Plane 2---")
    test_empty_plane(plane2)
    print("---Emptying Plane 3---")
    test_empty_plane(plane3)
    print("All planes are empty!")


def main():
    test_all()


if __name__ == '__main__':
    main()

