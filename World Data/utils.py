"""
Ryan Carnation

utils.py
"""


from myQueue import *
from myStack import *
from rit_lib import *


Country = struct_type("Country",
                      (str, "code"),
                      (list, "values"))


Country2 = struct_type("Country2",
                       (str, "name"),
                       (str, "code"),
                       (str, "income"))


CountryValue = struct_type("CountryValue",
                           (str, "country"),
                           (float, "value"))


Range = struct_type("Range",
                    (str, "country"),
                    (int, "year1"),
                    (int, "year2"),
                    (float, "value1"),
                    (float, "value2"))


def country_value(some_country):
    return some_country.value


def read_data(file_name):
    """
    This function goes through the given filename and adds the two necessary extensions onto them. It then goes through
    each file and gets all the necessary data out of them and returns them as dictionaries.
    :param file_name:
    :return:
    """
    file_name1 = "data/" + file_name + "_data.txt"
    fd1 = open(file_name1)
    file_name2 = "data/" + file_name + "_metadata.txt"
    fd2 = open(file_name2)
    fd1.readline()
    fd2.readline()
    regions = {}
    countries_name = {}
    countries_code = {}
    for line in fd1:
        line = line.strip()
        line = line.split(",")
        line.pop()
        life_exp = []
        year = 1960
        for item in line[2:]:
            life_exp += [(year, item)]
            year += 1
        countries_name[line[0]] = Country(line[1], life_exp)
        countries_code[line[1]] = line[0]
    for line2 in fd2:
        line2 = line2.strip()
        line2 = line2.split(",")
        line2.pop()
        if line2[1] != "":
            if line2[1] not in regions:
                regions[line2[1]] = [Country2(countries_code[line2[0]], line2[0], line2[2])]
            else:
                regions[line2[1]] += [Country2(countries_code[line2[0]], line2[0], line2[2])]
    return regions, countries_name, countries_code


def territories(data):
    """
    This function takes the data from the 'read_data' function and uses it to calculate the amount of territories and
    countries in the world.
    :param data:
    :return:
    """
    mena = len(data[0]["Middle East & North Africa"])
    eca = len(data[0]["Europe & Central Asia"])
    na = len(data[0]["North America"])
    lac = len(data[0]["Latin America & Caribbean"])
    sa = len(data[0]["South Asia"])
    eap = len(data[0]["East Asia & Pacific"])
    ssa = len(data[0]["Sub-Saharan Africa"])
    print("Total number of entities: ", len(data[1]))
    print("Number of countries/territories: ", mena + eca + na + lac + sa + eap + ssa)
    print("\n")
    print("Regions and their country count:")
    print(" Middle East & North Africa: ", mena)
    print(" Europe & Central Asia: ", eca)
    print(" North America: ", na)
    print(" Latin America & Caribbean: ", lac)
    print(" South Asia: ", sa)
    print(" East Asia & Pacific: ", eap)
    print(" Sub-Saharan Africa: ", ssa)


def income_categories(data):
    """
    This function takes the data from the 'read_data' function and uses it to find the number of countries in each of
    the income levels.
    :param data:
    :return:
    """
    print("\n")
    data = data[0]
    low_mid_income = 0
    upp_mid_income = 0
    high_income = 0
    low_income = 0
    for region in data.keys():
        for country in data[region]:
            if country.income == "Lower middle income":
                low_mid_income += 1
            elif country.income == "Upper middle income":
                upp_mid_income += 1
            elif country.income == "High income":
                high_income += 1
            elif country.income == "Low income":
                low_income += 1
            else:
                pass
    print("Income categories and their country count:")
    print("Lower middle income: ", low_mid_income)
    print("Upper middle income: ", upp_mid_income)
    print("High income: ", high_income)
    print("Low income: ", low_income)


def filter_region(data, region):
    """
    This function takes the data from the 'read_data' function and uses it to find and list all of the countries that
    are in the given region.
    :param data:
    :param region:
    :return:
    """
    regions = []
    if type(data) is not list:
        data = data[0]
        if region == "all":
            for region in data.keys():
                for country in data[region]:
                    regions += [(country.name, country.code)]
            return regions
        else:
            if region in data.keys():
                for country in data[region]:
                    regions += [(country.name, country.code)]
                return regions
            else:
                return False
    else:
        new_data = read_data("worldbank_life_expectancy")
        new_data = new_data[0]
        if region == "all":
            return data
        else:
            for region in new_data:
                for country in new_data[region]:
                    for item in data:
                        if item[0] == country.name:
                            regions += [item]
            return regions


def filter_income(data, income):
    """
    This function takes the data from the 'read_data' function and uses it to find the countries that have income level
    that is the same as the income level inputted by the user.
    :param data:
    :param income:
    :return:
    """
    incomes = []
    if type(data) is not list:
        data = data[0]
        if income == "all":
            for region in data.keys():
                for country in data[region]:
                    incomes += [(country.name, country.code)]
            return incomes
        else:
            if income != "Lower middle income" and income != "Upper middle income" and income != "High income" and \
                            income != "Low income":
                return False
            else:
                for region in data.keys():
                    for country in data[region]:
                        if country.income == income:
                            incomes += [(country.name, country.code)]
                return incomes
    else:
        new_data = read_data("worldbank_life_expectancy")
        new_data = new_data[0]
        if income == "all":
            return data
        else:
            for item in data:
                for region in new_data:
                    for country in new_data[region]:
                        if country.name == item[0] and country.income == income:
                            incomes += [item]
            return incomes


def life_expectancy(data, country):
    """
    This function takes the data from the 'read_data' function and uses it to retrieve the life expectancies of the
    country that is inputted into the function.
    :param data:
    :param country:
    :return:
    """
    data_name = data[1]
    data_code = data[2]
    if country not in data_name.keys() and country not in data_code.keys():
        print("'" + country + "'", "is not a valid country name or code")
    if country not in data_name.keys() and country in data_code.keys():
        country = data_code[country]
    if country in data_name.keys():
        index = 0
        print("Data for", country + ":")
        for _ in data_name[country].values:
            if data_name[country].values[index][1] == "":
                index += 1
            else:
                print(" Year: ", data_name[country].values[index][0], " ", "Life expectancy: ",
                      data_name[country].values[index][1])
                index += 1


def main():
    data = read_data("worldbank_life_expectancy")

    territories(data)

    income_categories(data)

    print("\n")

    region = input("Enter region name: ")
    regions = filter_region(data, region)
    print("Countries in the", "'" + region + "'", "region")
    for item in regions:
        print(" ", item[0], "(" + item[1] + ")")

    print("\n")

    income = input("Enter income category: ")
    incomes = filter_income(data, income)
    print("Countries in the", "'" + income + "'", "income category:")
    for item in incomes:
        print(" ", item[0], "(" + item[1] + ")")

    print("\n")

    country = input("Enter name of country or country code (Enter to quit): ")
    while country != "":
        life_expectancy(data, country)
        print("\n")
        country = input("Enter name of country or country code (Enter to quit): ")
        if country == "":
            break


if __name__ == '__main__':
    main()
