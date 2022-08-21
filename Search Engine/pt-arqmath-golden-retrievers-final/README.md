## Running Evaluation for ARQMath Task 1

These instructions will show how to run our evaluations

1. Issue `make` to install PyTerrier and Python dependencies
2. Issue `make data` to download the collection of ARQMath posts
3. Issue `make posts` to reindex the collection and add query expansion metadata
4. Issue `make eval-knrm` to run the KNRM evaluation on the 2021 dataset
5. Issue `make eval-qe` to run the SDM model on the enhanced index on the 2021 dataset

# File Descriptions

`index_pos_qe.py`: Python file for creating index. Same functionality as `index_arqmath.py`, but user can pass in optional flag "-p" to create the index with word position data.

`math_recoding.py`: Same functionality as before, but has additional terms to translate from original ARQMath text

`run_topics_knrm.py`: Python file for running KNRM re-ranker experiment.

`run_topics_qe.py`: Python file for running SDM query expansion experiment (requires index passed in to have position data). Use optional flag "-b" to run an experiment only on the baseline.

# Result Files
`SDM-BM25.res` contains the results from the SDM pre-processing after the index is generated with metadata

`KNRMBERT.res` contains the results from the KNRM Bert Vocabulary run, does not include SDM pipeline just BM25 and KNRM

`KNRMWordVec_Hash.res` contains the results from the KNRM WordVec_Hash Vocabulary run, does not include SDM pipeline just BM25 and KNRM
