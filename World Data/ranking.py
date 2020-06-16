"""
Ryan Carnation

ranking.py
"""


from utils import *


def sorted_ranking_data(data, year):
    """
    This function takes data and a year and ranks the life expectancies for each country in the given data.
    :param data:
    :param year:
    :return:
    """
    data_all = read_data("worldbank_life_expectancy")
    country_names = data_all[1]
    lst = []
    for item in data:
        if item[0] in country_names:
            individual_country = country_names[item[0]]
            for value in individual_country.values:
                if value[0] == year:
                    if value[1] != "":
                        lst += [CountryValue(item[0], float(value[1]))]
    return sorted(lst, key=country_value, reverse=True)


def main():
    year = int(input("Enter year of interest (-1 to quit): "))
    region = input("Enter region (type 'all' to consider all): ")
    income = input("Enter income category (type 'all' to consider all): ")

    data = read_data("worldbank_life_expectancy")

    while year != -1:
        region_data = filter_region(data, region)
        income_data = filter_income(region_data, income)
        final = sorted_ranking_data(income_data, year)
        idx = 1
        idx2 = len(final)
        print("\n")
        print("Top 10 Life Expectancy for", year)
        if len(final) >= 10:
            for item in final[:10]:
                print(idx, item.country, item.value)
                idx += 1
            print("\n")
            print("Bottom 10 Life Expectancy for", year)
            idx3 = idx2 - 11
            for item in final[:idx3:-1]:
                print(idx2, item.country, item.value)
                idx2 -= 1
        else:
            for item in final:
                print(idx, item.country, item.value)
                idx += 1
            print("\n")
            print("Bottom 10 Life Expectancy for", year)
            for item in final[::-1]:
                print(idx2, item.country, item.value)
                idx2 -= 1
        print("\n")
        year = int(input("Enter year of interest (-1 to quit): "))
        if year == -1:
            break
        region = input("Enter region (type 'all' to consider all): ")
        income = input("Enter income category (type 'all' to consider all): ")


if __name__ == '__main__':
    main()
