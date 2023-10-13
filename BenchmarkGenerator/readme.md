# BenCiGen CLI

This repository contains the CLI version of the BenCiGen tool, allowing the generation of benchmark IPMs for testing CT generators.

### How to execute BenCiGen
`java -jar BenchmargGeneratorCLI.jar <trackStr> <nTests> [-acts] [-chkTestRatio] [-chkTupleRatio] [-cnf] [-ctw] [-ft] [-pict] [-cmax=<cmax>] [-cmin=<cmin>] [-d=<destinationFolder>] [-dict=<dict>] [-dmax=<dmax>] [-dmin=<dmin>] [-epsilon=<epsilon>] [-kmax=<kmax>] [-kmin=<kmin>] [-p=<P>] [-r=<r>] [-rTest=<rTest>] [-similar=<similarModel>] [-vIntLow=<vIntLow>] [-vIntUp=<vIntUp>] [-vmax=<vmax>] [-vmin=<vmin>]`

where
      
      <trackStr>                The category for the benchmark to be generated (UNIFORM_BOOLEAN, UNIFORM_ALL, MCA, BOOLC, MCAC, NUMC).
      <nTests>                  The number of benchmarks to generate.
      -acts                     Export in ACTS format. By default it is disabled.
      -chkTestRatio             Check test ratio.
      -chkTupleRatio            Check tuple ratio.
      -cmax=<cmax>              Maximum number of constraints. By default it is 100.
      -cmin=<cmin>              Minimum number of constraints. By default it is 2.
      -cnf                      Only constraints expressed as CNF? By default it is disabled
      -ctw                      Export in CTWedge format. By default it is enabled.
      -d=<destinationFolder>    Destination folder. By default it is ./benchmarks/.
      -dict=<dict>              The JSON file containing the dictionary
      -dmax=<dmax>              Maximum complexity (i.e., number of logical operators) for each constraint. By default it is 10.
      -dmin=<dmin>              Minimum complexity (i.e., number of logical operators) for each constraint. By default it is 2.
      -epsilon=<epsilon>        The accepted error when computing the test validity ratio, if MDDs cannot be used. By default it is 0.1
      -ft                       Only constraints expressed as forbidden tuples? By default it is disabled
      -kmax=<kmax>              Maximum number of parameters. By default it is 500.
      -kmin=<kmin>              Minimum number of parameters. By default it is 1.
      -p=<P>                    Probability for ratio computation. By default it is 0.75
      -pict                     Export in PICT format. By default it is enabled.
      -r=<r>                    Maximum accepted tuple validity ratio. By default it is 0.01.
      -rTest=<rTest>            Maximum accepted test validity ratio. By default it is 0.01.
      -similar=<similarModel>   Given a CTWedge model, it generates a model similar to that.
      -vIntLow=<vIntLow>        Lower bound for integer ranges. By default it is -100.
      -vIntUp=<vIntUp>          Upper bound for integer ranges. By default it is 100.
      -vmax=<vmax>              Maximum cardinality. By default it is 500.
      -vmin=<vmin>              Minimum cardinality. By default it is 2.
