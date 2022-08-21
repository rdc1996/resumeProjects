# Define environment, bash script execution line

python-default: script
	#
	# Python dependencies (pip)
	# First two commands to help with installation on RIT CS systems
	pip install --user numpy --upgrade
	pip install --user packaging --upgrade
	pip install --user python-terrier bs4 tqdm pandas lxml --upgrade
	pip install --upgrade git+https://github.com/Georgetown-IR-Lab/OpenNIR
	pip install -r requirements.txt

script:
	# Creating test scripts...
	@echo "#!`which bash`" > exec_line
	@chmod u+x run-KNRM-2020 run-KNRM-2021 run-SDM-2020 run-SDM-2021
	@rm exec_line
	#
	# Quick run/eval test script is ./run-KNRM-2021
	# Evaluation run script for ARQMath-1 topics w. BM25 is ./run-KNRM-2020
	# Evaluation run script for ARQMath-2 topics w. BM25 is ./run-KNRM-2021
	#
	# Indexing test script is ./pos-arqmath-index

data: collection post-data raw-post-data math-data
	
collection:
	wget https://www.cs.rit.edu/~dprl/data/ARQMath/ARQMath_Collection.zip
	unzip ARQMath_Collection.zip
	rm ARQMath_Collection.zip

# Tokenized with Terrier defaults (stopwords + porter stemmer), English tokenizer
post-data:
	wget https://www.cs.rit.edu/~dprl/data/ARQMath/ARQMath_Collection-post-ptindex.zip
	unzip ARQMath_Collection-post-ptindex.zip
	rm ARQMath_Collection-post-ptindex.zip

# No stopwords or stemming, using English tokenizer
raw-post-data:
	wget https://www.cs.rit.edu/~dprl/data/ARQMath/ARQMath_Collection-post-ptindex-rawtokens.zip
	unzip ARQMath_Collection-post-ptindex-rawtokens.zip
	rm ARQMath_Collection-post-ptindex-rawtokens.zip


math-data:
	wget https://www.cs.rit.edu/~dprl/data/ARQMath/ARQMath_Collection-math-ptindex.zip
	unzip ARQMath_Collection-math-ptindex.zip
	rm ARQMath_Collection-math-ptindex.zip

posts:
	./pos-arqmath-index ./ARQMath_Collection -p -s

eval-knrm:
	./run-KNRM-2021
	
eval-qe:
	./run-SDM-2021

delete-results:
	rm -g *.res.gz

delete-indices:
	rm -rf *-ptindex 
