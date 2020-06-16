"""
Ryan Carnation

drop.py
"""


from utils import *


def sort_values(some_range):
    """
    This function just takes a Range and returns it so it can be sorted in order of highest drop rate to
    lowest drop rate.
    :param some_range:
    :return:
    """
    return some_range.value2 - some_range.value1


def sorted_drop_data(data):
    """
    This function takes a list of tuples that contains a country name and country code. It takes that country name and
    finds its greatest drop rate by comparing every year's life expectancy with every other year's life expectancy and
    finds the points where the drop rate is the largest.
    :param data:
    :return:
    """
    data_all = read_data("worldbank_life_expectancy")
    country_names = data_all[1]
    country_codes = data_all[2]
    regions = data_all[0]
    lst = list()
    for country in data:
        country_values = []
        for region in regions.keys():
            for item in regions[region]:
                if item.name == country[0]:
                    country_values += [item.code]
        drop_rate = 999
        empty_strings = 0
        empty_years = 0
        country2 = country[1]
        country = country[0]
        individual_country = country_names[country]
        country_code = country_codes[country2]
        len_values = len(individual_country.values)
        if country in country_names:
            for value in range(len_values):
                if individual_country.values[value][1] == "" and individual_country.values[value][0] > 1959:
                    empty_strings = empty_strings + 1
                    country_code += ""
                    continue
                else:
                    for year in range(len_values):
                        if year < value:
                            empty_years += 1
                            if individual_country.values[year][1] == "":
                                country_code += ""
                                continue
                            if drop_rate == 999:
                                drop_rate += 1
                                empty_years += 1
                                drop_rate = float(individual_country.values[value][1]) - float(individual_country.values[year][1])
                                create_range = Range(country, year + 1960, value + 1960,
                                                     float(individual_country.values[year][1]), float(individual_country.values[value][1]))
                            if float(individual_country.values[value][1]) - float(individual_country.values[year][1]) < drop_rate:
                                drop_rate -= 1
                                empty_years -= 1
                                drop_rate = float(individual_country.values[value][1]) - float(individual_country.values[year][1])
                                create_range = Range(country, year + 1960, value + 1960,
                                                    float(individual_country.values[year][1]), float(individual_country.values[value][1]))
                        else:
                            country_code += ""
                            if individual_country.values[year][1] != "" and individual_country.values[value][1] != "":
                                country_values += [country_code]
                                Range(country, year, value, float(individual_country.values[year][1]), float(individual_country.values[value][1]))
            if empty_strings <= 54:
                lst += [create_range]
            elif empty_strings > 54:
                empty_years += 1
            else:
                continue
    lst = sorted(lst, key=sort_values, reverse=False)
    return lst


def main():
    data = read_data("worldbank_life_expectancy")

    region_data = filter_region(data, "all")

    value = sorted_drop_data(region_data)

    print("Worst life expectancy drops: 1960 to 2015:")

    idx = 1
    for item in value:
        if idx == 11:
            break
        print(idx, "", item.country, "from", item.year1, "(", item.value1, ")", "to", item.year2, "(", item.value2, ") :",
              item.value2 - item.value1)
        idx += 1


if __name__ == '__main__':
    main()
