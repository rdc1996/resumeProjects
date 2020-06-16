"""
Ryan Carnation

factors.py
"""


from utils import *
import turtle


def init_turtle():
    """
    This function just draws the basic graph with nothing on it using turtle.
    :return:
    """
    turtle.setup(1000, 775, 0, 0)
    num = 10
    turtle.up()
    turtle.goto(-350, -300)
    turtle.left(180)
    turtle.forward(25)
    turtle.write(0, font=("Arial", 12, "bold"))
    turtle.back(25)
    turtle.left(90)
    turtle.forward(25)
    turtle.write(1960, font=("Arial", 12, "bold"))
    turtle.back(25)
    turtle.left(90)
    turtle.down()
    turtle.forward(350)
    turtle.right(90)
    turtle.up()
    turtle.forward(50)
    turtle.write("Year", font=("Arial", 12, "bold"))
    turtle.back(50)
    turtle.left(90)
    turtle.down()
    turtle.forward(350)
    turtle.right(90)
    turtle.up()
    turtle.forward(25)
    turtle.write(2015, font=("Arial", 12, "bold"))
    turtle.back(25)
    turtle.right(90)
    turtle.forward(700)
    turtle.right(90)
    while num < 100:
        turtle.down()
        turtle.forward(60)
        turtle.left(90)
        turtle.up()
        turtle.forward(25)
        turtle.write(num, font=("Arial", 12, "bold"))
        turtle.back(25)
        turtle.right(90)
        num += 10
    turtle.up()
    turtle.back(270)
    turtle.left(90)
    turtle.forward(100)
    turtle.write("Life", font=("Arial", 12, "bold"))
    turtle.left(90)
    turtle.forward(25)
    turtle.write("Exp.", font=("Arial", 12, "bold"))
    turtle.goto(-350, -300)
    turtle.left(180)


def legend_region():
    """
    This function creates a legend for the regions using turtle.
    :return:
    """
    turtle.title("Life Expectancy Versus Region")
    turtle.up()
    turtle.forward(510)
    turtle.right(90)
    turtle.forward(25)
    idx = 0
    regions = ["East Asia & Pacific", "North America", "Middle East & North Africa", "Latin America & Caribbean",
               "Europe & Central Asia", "South Asia", "Sub-Saharan Africa"]
    colors = ["purple", "yellow", "black", "orange", "green", "red", "blue"]
    while idx <= 6:
        turtle.color(colors[idx])
        turtle.write(regions[idx], font=("Arial", 12, "bold"))
        turtle.left(90)
        turtle.forward(5)
        turtle.right(90)
        turtle.forward(250)
        turtle.down()
        turtle.forward(75)
        turtle.up()
        turtle.back(325)
        turtle.left(90)
        turtle.forward(20)
        turtle.right(90)
        idx += 1


def legend_income():
    """
    This function creates a legend for the incomes using turtle.
    :return:
    """
    turtle.title("Life Expectancy Versus Income")
    turtle.up()
    turtle.forward(510)
    turtle.right(90)
    turtle.forward(25)
    idx = 0
    incomes = ["Low income", "Upper middle income", "Lower middle income", "High income"]
    colors = ["blue", "red", "green", "orange"]
    while idx <= 3:
        turtle.color(colors[idx])
        turtle.write(incomes[idx], font=("Arial", 12, "bold"))
        turtle.left(90)
        turtle.forward(5)
        turtle.right(90)
        turtle.forward(250)
        turtle.down()
        turtle.forward(75)
        turtle.up()
        turtle.back(325)
        turtle.left(90)
        turtle.forward(20)
        turtle.right(90)
        idx += 1


def plot_line(data, color):
    """
    This function takes data and a color and will plot a single line based on the life expectancies for each year of
    every country that is located in the given data.
    :param data:
    :param color:
    :return:
    """
    year = 1960
    x = -363
    y = -300
    general_data = read_data("worldbank_life_expectancy")
    country_data = general_data[1]
    turtle.color(color)
    turtle.down()
    while year <= 2015:
        value_lst = []
        for item in data:
            individual_country = country_data[item[0]]
            for value in individual_country.values:
                if value[0] == year:
                    if value[1] != "":
                        value_lst += [float(value[1])]
        value_lst = sorted(value_lst)
        if len(value_lst) % 2 == 0:
            if len(value_lst) == 2:
                median = (value_lst[0] + value_lst[1])/2
                median = median*6
            else:
                a = len(value_lst)//2
                b = a + 1
                median = (value_lst[a] + value_lst[b])/2
                median = median*6
        else:
            median = value_lst[(len(value_lst))//2]
            median = median*6
        if year == 1960:
            turtle.up()
        if year > 1960:
            turtle.down()
        year += 1
        x += 13
        turtle.goto(x, y + median)


def main():
    turtle.pensize(5)
    turtle.speed(0)
    init_turtle()
    legend_region()

    general_data = read_data("worldbank_life_expectancy")

    data_asia = filter_region(general_data, "South Asia")
    data_middle = filter_region(general_data, "Middle East & North Africa")
    data_europe = filter_region(general_data, "Europe & Central Asia")
    data_america = filter_region(general_data, "North America")
    data_caribbean = filter_region(general_data, "Latin America & Caribbean")
    data_pacific = filter_region(general_data, "East Asia & Pacific")
    data_africa = filter_region(general_data, "Sub-Saharan Africa")

    plot_line(data_asia, "red")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_middle, "black")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_europe, "green")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_america, "yellow")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_caribbean, "orange")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_pacific, "purple")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_africa, "blue")

    next_graph = input("Hit enter to see income graph: ")
    if next_graph == "":
        turtle.reset()

    turtle.speed(0)
    turtle.pensize(5)
    init_turtle()
    legend_income()

    data_low = filter_income(general_data, "Low income")
    data_upper = filter_income(general_data, "Upper middle income")
    data_lower = filter_income(general_data, "Lower middle income")
    data_high = filter_income(general_data, "High income")

    plot_line(data_low, "blue")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_upper, "red")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_lower, "green")
    turtle.up()
    turtle.goto(-350, -300)
    plot_line(data_high, "orange")

    turtle.done()


main()
