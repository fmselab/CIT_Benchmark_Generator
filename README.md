# CIT Benchmarks Generation and CIT Generators Evaluation

## BenCIGen

* `BenchmarkGenerator`: Java code of the `BenCIGen' CLI tool for the generation of benchmarks, with different categories and characteristics
   - `test/TestBenchmarkGeneratorCLI.java`: contains the code executing unit test cases as presented in the JSS paper (see below).
   - `test/TestExternalValidation.java`: contains the external validation process as presented in the paper JSS paper.
   - [README](https://github.com/fmselab/CIT_Benchmark_Generator/blob/main/BenchmarkGenerator/readme.md) contains all the options and parameters for the CLI version
* `BenchmarkGeneratorGUI`: Java code of the `BenCIGen' GUI tool for the generation of benchmarks, with different categories and characteristics

### Cite
If you want to cite our work, please use:   
*Andrea Bombarda, Angelo Gargantini*   
**Design, implementation, and validation of a benchmark generator for combinatorial interaction testing tools**  
Journal of Systems and Software, Volume 209, 2024, 111920,
https://doi.org/10.1016/j.jss.2023.111920.

## Executable versions
If you want to execute the tool, there are two versions, both as jar files:
- a command line interface:   
https://github.com/fmselab/CIT_Benchmark_Generator/blob/main/BenchmarkGenerator/dist/BenchmarkGeneratorCLI.jar
- a GUI with a nicer interface:   
https://github.com/fmselab/CIT_Benchmark_Generator/blob/main/BenchmarkGeneratorGUI/dist/BenchmarkGenerator.jar


## CT-Competition

* `Benchmarks_CITCompetition_2022`: benchmarks used during the [first edition of the CT Competition](https://fmselab.github.io/ct-competition/index2022.html)
* `Benchmarks_FollowUp_CITCompetition_2022`: benchmarks used during the follow-up of the first edition of the CT Competition
* `Benchmarks_CITCompetition_2023`: benchmarks used during the [second edition of the CT Competition](https://fmselab.github.io/ct-competition/)
* `ToolEvaluator`: given the test suites and the generation times, extract the ranking for each tool
