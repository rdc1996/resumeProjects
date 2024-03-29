################################################################
# math_recoding.py
#
# Dictionaries and functions to recode formula encodings (e.g.,
# LaTeX) for use with PyTerrier, which removes punctuation by
# default.
#
# Author:
# R. Zanibbi, April 2022 (CSCI 539, Information Retrieval, RIT)
################################################################

################################################################
# LaTeX Symbol Map
################################################################
# Warning - mapping may be incomplete 
# Commands will be tokenized as-is 
# (e.g., \geq -> backslash ge )
#
# Modification:
#
# Also mapping punctuation to full words, in hopes of better matching
# tokenization in existing BERT models, and to capture more coreferences
# when text and formulas refer to the same thing/concept (i.e., increasing
# the likelihood of the 'same name for the same thing').
latex_symbol_map = {
        "+" : " plus ",
        "-" : " minus ",
        "^" : " power ",
        "*" : " star ",

        "#" : " hash ",
        "!" : " exclaim ",
        "?" : " question ", 
        "," : " comma ",
        "." : " period ",
        "/" : " forward slash ",
        ":" : " colon ",
        ";" : " semicolon ",
        "&" : " ampersand ",
        "~" : " tilde ",
        "`" : " backquote ",
        "@" : " at ",
        "$" : " dollar ",
        "%" : " percent ",
        '"' : " double quote ",
        "'" : " single quote ",
        "|" : " vertical bar ",

        "=" : " equals ",
        ">" : " greater than ",
        "<" : " less than ",

        "{" : " open brace ",
        "}" : " close brace ",
        "[" : " open square bracket ",
        "]" : " close square bracket ",
        "(" : " open parenthesis ",
        ")" : " close parenthesis ",

        "_" : " underscore ",
        "\u2026" : " ellipsis ",
        "\u2286" : " contains ",
        "\u2208" : " arrow ",
        "\u2192" : " contains ",
        "\u2282" : " is element of ",
        
        "\\\\" : " double backslash ",
        "\\" : " backslash "
    }

# 'Mini' query language for formulas, replacing pyterrier ops        
# e.g., using '_ptand' for the AND (+) operator
# e.g., using '_ptnot' for the NOT (-) operator
pyterrier_symbol_map = {
        "_pand":       "+",
        "_pnot":       "-",
        "_pobrace":    "{",
        "_pcbrace":    "}"
    }


################################################################
# Functions to transform strings encoding math
################################################################
def rewrite_symbols( in_string, map_dict ):
    out_string = in_string

    # Brute force - apply rules one-at-a-time
    for key in map_dict:
        out_string = out_string.replace( key, map_dict[ key ] )

    #out_string.encode("ascii")
    #out_string.decode("utf-8")
    #out_string = out_string.replace("\u2026", " ellipsis ")
    

    return out_string

def translate_latex( TeXstring ):
    # Replace LaTeX symbols in a string by text tokens
    return rewrite_symbols( TeXstring, latex_symbol_map )

def translate_query( query):
    # Translate query string to 'arqmath pyterrier query language' representation
    # (translate TeX symbols + 'meta' operators (e.g., _pand for '+' in pyterrier query language)
    return rewrite_symbols(
            rewrite_symbols( query, latex_symbol_map ),
            pyterrier_symbol_map 
        )

def translate_qlist( query_list ):
    # For batch retrieval
    return list( map( translate_query, query_list ) )
