"""
Ryan Carnation

growth.py
"""


from utils import *


def sorted_growth_data(data, year1, year2):
    """
    This function takes data and two years and outputs the growth rate of each country in the given data between those
    two years and ranks them.
    :param data:
    :param year1:
    :param year2:
    :return:
    """
    data_all = read_data("worldbank_life_expectancy")
    country_names = data_all[1]
    lst = []
    starting_year = 0
    ending_year = 0
    for country in data:
        individual_country = country_names[country[0]]
        for value in individual_country.values:
            if value[0] == year1 and value[1] != "":
                starting_year += float(value[1])
            elif value[0] == year2 and value[1] != "":
                ending_year += float(value[1])
        if starting_year == 0 or ending_year == 0:
            starting_year = 100
            ending_year = 50
        final_value = ending_year - starting_year
        starting_year = 0
        ending_year = 0
        if final_value > 0:
            lst += [CountryValue(country[0], final_value)]
    return sorted(lst, key=country_value, reverse=True)


def main():
    starting_year = int(input("Enter staring year of interest (-1 to quit): "))
    ending_year = int(input("Enter ending year of interest (-1 to quit): "))
    region = input("Enter region (type ’all’ to consider all): ")
    income = input("Enter income category (type ’all’ to consider all): ")

    data = read_data("worldbank_life_expectancy")

    while starting_year != -1 and ending_year != -1:
        region_data = filter_region(data, region)
        income_data = filter_income(region_data, income)
        final = sorted_growth_data(income_data, starting_year, ending_year)
        idx = 1
        idx2 = len(final)
        print("\n")
        print("Top 10 Life Expectancy Growth:", starting_year, "to", ending_year)
        if len(final) >= 10:
            for item in final[:10]:
                print(idx, item.country, item.value)
                idx += 1
            print("\n")
            print("Bottom 10 Life Expectancy Growth:", starting_year, "to", ending_year)
            idx3 = idx2 - 11
            for item in final[:idx3:-1]:
                print(idx2, item.country, item.value)
                idx2 -= 1
        else:
            for item in final:
                print(idx, item.country, item.value)
                idx += 1
            print("\n")
            print("Bottom 10 Life Expectancy Growth:", starting_year, "to", ending_year)
            for item in final[::-1]:
                print(idx2, item.country, item.value)
                idx2 -= 1
        print("\n")
        starting_year = int(input("Enter staring year of interest (-1 to quit): "))
        if starting_year == -1:
            break
        ending_year = int(input("Enter ending year of interest (-1 to quit): "))
        if ending_year == -1:
            break
        region = input("Enter region (type ’all’ to consider all): ")
        income = input("Enter income category (type ’all’ to consider all): ")


if __name__ == '__main__':
    main()
