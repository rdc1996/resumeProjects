"""
Ryan Carnation
Lab #9
"""


from rit_lib import *
from myQueue import *
from myStack import *


Passenger = struct_type("Passenger",
                        (str, "full_name"),
                        (int, "ticket"),
                        (bool, "carry_on"))


Gate = struct_type("Gate",
                   (Queue, "Zone1"),
                   (Queue, "Zone2"),
                   (Queue, "Zone3"),
                   (Queue, "Zone4"),
                   (int, "maximum"),
                   (int, "current"))


def make_empty_gate(maximum):
    """
    This function just creates a gate with four empty Queues and takes the maximum as a parameter with a current of zero.
    :param maximum:
    :return:
    """
    return Gate(mkEmptyQueue(), mkEmptyQueue(), mkEmptyQueue(), mkEmptyQueue(), maximum, 0)


AiRIT = struct_type("AiRIT",
                    (Stack, "passengers"),
                    (Stack, "carry_on"),
                    (int, "current"),
                    (int, "maximum"))


def make_empty_plane(maximum):
    """
    This function just creates a plane with two empty Queues and takes the maximum as a parameter with a current of zero.
    :param maximum:
    :return:
    """
    return AiRIT(mkEmptyStack(), mkEmptyStack(), 0, maximum)


def read_passenger(file_name):
    """
    This function will just check each passenger in the file and make sure that is not an empty string.
    :param file_name:
    :return:
    """
    passenger = file_name.readline()
    if passenger == "":
        return None
    else:
        return passenger


def line_up(gate, file):
    """
    This function will just take all of the passengers in a file and put them in their Zones based on
    their ticket number.
    :param gate:
    :param file:
    :return:
    """
    print("Passengers are lining up at the gate...")
    while gate.maximum > gate.current:
        carry_on = True
        passenger = read_passenger(file)
        if passenger is None:
            break
        passenger = passenger.strip()
        passenger = passenger.split(",")
        if passenger[2] == "True":
            carry_on = True
        if passenger[2] == "False":
            carry_on = False
        passenger = Passenger(passenger[0], int(passenger[1]), carry_on)
        if gate.maximum > gate.current:
            if passenger.ticket > 40000:
                enqueue(gate.Zone4, passenger)
            elif passenger.ticket > 30000:
                enqueue(gate.Zone3, passenger)
            elif passenger.ticket > 20000:
                enqueue(gate.Zone2, passenger)
            elif passenger.ticket > 10000:
                enqueue(gate.Zone1, passenger)
            gate.current += 1
            print(" ", passenger)
    if gate.maximum == gate.current:
        print("The gate is full; remaining passengers must wait.")
        return False
    else:
        return True


def boarding(gate, plane):
    """
    This function will take the passengers from the boarding zones and place them on the plane with the highest
    boarding zone going first.
    :param gate:
    :param plane:
    :return:
    """
    print("Passengers are boarding the aircraft...")
    while plane.maximum > plane.current and gate.current > 0:
        if gate.Zone4.front is not None:
            print(" ", gate.Zone4.front.value)
            if gate.Zone4.front.value.carry_on == True:
                push(plane.carry_on, gate.Zone4.front.value)
                dequeue(gate.Zone4)
            elif gate.Zone4.front.value.carry_on == False:
                push(plane.passengers, gate.Zone4.front.value)
                dequeue(gate.Zone4)
        elif gate.Zone3.front is not None:
            print(" ", gate.Zone3.front.value)
            if gate.Zone3.front.value.carry_on == True:
                push(plane.carry_on, gate.Zone3.front.value)
                dequeue(gate.Zone3)
            elif gate.Zone3.front.value.carry_on == False:
                push(plane.passengers, gate.Zone3.front.value)
                dequeue(gate.Zone3)
        elif gate.Zone2.front is not None:
            print(" ", gate.Zone2.front.value)
            if gate.Zone2.front.value.carry_on == True:
                push(plane.carry_on, gate.Zone2.front.value)
                dequeue(gate.Zone2)
            elif gate.Zone2.front.value.carry_on == False:
                push(plane.passengers, gate.Zone2.front.value)
                dequeue(gate.Zone2)
        elif gate.Zone1.front is not None:
            print(" ", gate.Zone1.front.value)
            if gate.Zone1.front.value.carry_on == True:
                push(plane.carry_on, gate.Zone1.front.value)
                dequeue(gate.Zone1)
            elif gate.Zone1.front.value.carry_on == False:
                push(plane.passengers, gate.Zone1.front.value)
                dequeue(gate.Zone1)
        gate.current -= 1
        plane.current += 1


def empty_plane(plane):
    """
    This will just empty the plane once it has landed, getting the people without carry-on items first, then the people
    with carry-ons get off.
    :param plane:
    :return:
    """
    print("Passengers are disembarking...")
    while plane.current > 0:
        if plane.passengers.size > 0:
            print(" ", plane.passengers.nodes.value)
            pop(plane.passengers)
        elif plane.carry_on.size > 0:
            print(" ", plane.carry_on.nodes.value)
            pop(plane.carry_on)
        plane.current -= 1


def main():

    file = open(input("Passenger data file: "))
    gate = make_empty_gate(int(input("Gate capacity: ")))
    aircraft = make_empty_plane(int(input("Aircraft capacity: ")))
    print("Beginning simulation...")

    left = line_up(gate, file)

    while gate.current > 0:
        boarding(gate, aircraft)
        empty_plane(aircraft)

    while left is False:
        left = line_up(gate, file)
        if left is True:
            print("The last passenger is in line!")
        while gate.current > 0:
            boarding(gate, aircraft)
            empty_plane(aircraft)

    file.close()

    print("Simulation complete; all passengers are at their destination")


if __name__ == '__main__':
    main()




