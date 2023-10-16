# CIT Benchmarks Generation and CIT Generators Evaluation

## BenCIGen

* `BenchmarkGenerator`: Java code of the `BenCIGen' CLI tool for the generation of benchmarks, with different categories and characteristics
   - `test/TestBenchmarkGeneratorCLI.java`: contains the code executing unit test cases as presented in the paper "Design, implementation, and validation of a benchmark generator for combinatorial interaction testing tools" submitted to The Journal of Systems and Software
   - `test/TestExternalValidation.java`: contains the external validation process as presented in the paper "Design, implementation, and validation of a benchmark generator for combinatorial interaction testing tools" submitted to The Journal of Systems and Software
* `BenchmarkGeneratorGUI`: Java code of the `BenCIGen' GUI tool for the generation of benchmarks, with different categories and characteristics

## CT-Competition

* `Benchmarks_CITCompetition_2022`: benchmarks used during the [first edition of the CT Competition](https://fmselab.github.io/ct-competition/index2022.html)
* `Benchmarks_CITCompetition_2023`: benchmarks used during the [second edition of the CT Competition](https://fmselab.github.io/ct-competition/)
* `Benchmarks_FollowUp_CITCompetition_2022`: benchmarks used during the follow-up of the first edition of the CT Competition
* `ToolEvaluator`: given the test suites and the generation times, extract the ranking for each tool
