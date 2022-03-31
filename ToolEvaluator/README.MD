# ToolEvaluator

Tool for performing evaluation on the results of the CT-Competition

## How to execute the tool

`
python ./ct_comp_evaluation.py --r [rf] --o [of]
`

where:
- `[rf]` is the path of the folder containing the file resulting from the execution of the benchmarks
- `[of]` is the name (or path) of the output file to be created

## Folder structure
- *figs* contains the figures produced as output by the evaluation script
- *data* contains all the csv files reporting the partial results during the evaluation
