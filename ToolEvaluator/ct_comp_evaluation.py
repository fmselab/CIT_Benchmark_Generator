import argparse
from os import listdir
from os.path import isfile, join
import csv
import pandas as pd 
import math

# ====================================================================================================
# Returns the time of the corresponding execution
def get_time(file, file_path):
    # Time file
    try:
        f = open(join(file_path, file), "r")
        time_seconds = 0
        for row in f:
            # Time
            row = row.strip()
            if (row.startswith("Elapsed (wall clock) time (h:mm:ss or m:ss):")):
                time_str = row[len("Elapsed (wall clock) time (h:mm:ss or m:ss):"):].strip()
                # Only minutes and seconds
                if (time_str.count(":") == 1):
                    time_seconds = round(time_seconds + int(time_str.split(":")[0]) * 60 + float(time_str.split(":")[1]), 2)
                else:
                    time_seconds = round(time_seconds + int(time_str.split(":")[0]) * 3600 + int(time_str.split(":")[1]) * 60 + \
                        float(time_str.split(":")[2]), 2)
                break

        f.close
    except:
        time_seconds = -1

    return time_seconds
# ====================================================================================================

# ====================================================================================================
# Returns the size of the corresponding execution
def get_size(file, file_path):
    # Test suite file
    try:
        f = open(join(file_path, file.replace(".time",".out")), "r")
        size = 0
        for row in f:
            size = size + 1

        f.close
    except:
        size = -1

    return size
# ====================================================================================================

# ====================================================================================================
# Main function
def main(file_path, output_file):
    # Fetch the files in the file_path
    onlyfiles = [f for f in listdir(file_path) if (isfile(join(file_path, f)) and f.endswith(".time"))]

    # Output file
    out_file = open(output_file + ".csv", 'w')
    writer = csv.writer(out_file)

    # Header
    writer.writerow(["ToolName","ModelName","Strength","Iteration","TimeSeconds","Size"])

    for file in onlyfiles:
        execution_info = file.split("_")
        tool_name = execution_info[0]
        if (execution_info[1] == "UNIFORM"):
            model_name = execution_info[1] + "_" + execution_info[2] + "_" + execution_info[3].split(".")[0]
            strength = execution_info[4]
            iteraration = execution_info[5].split(".")[0]
        else:
            model_name = execution_info[1] + "_" + execution_info[2].split(".")[0]
            strength = execution_info[3]
            iteraration = execution_info[4].split(".")[0]
        time = get_time(file, file_path)
        size = get_size(file, file_path)
        writer.writerow ([tool_name , model_name , strength , iteraration , time , size])

    out_file.close
# ====================================================================================================

# ====================================================================================================
# Extracts the best results for each tool over the three iterations
def extract_best_results(output_file):
    df_output = pd.read_csv(output_file + ".csv", delimiter=",")
    df_aggregate = pd.DataFrame(columns=["ToolName","ModelName","Strength","TimeSeconds","Size"])
    model_names = df_output["ModelName"].unique()
    generator_names = df_output["ToolName"].unique()
    strengths = df_output["Strength"].unique()

    for strength in strengths:
        for modelName in model_names:
            for generatorName in generator_names:
                filtered_df_output = df_output[(df_output.ToolName.eq(generatorName)) & (df_output.ModelName.eq(modelName)) & \
                    (df_output.Strength.eq(strength))]
                min_time = filtered_df_output.TimeSeconds.min() if not math.isnan(filtered_df_output.TimeSeconds.min()) else -1
                min_size = filtered_df_output.Size.min() if not math.isnan(filtered_df_output.Size.min()) else -1
                df_aggregate = df_aggregate.append({
                    "ToolName": generatorName,
                    "ModelName": modelName,
                    "Strength": strength,
                    "TimeSeconds": min_time,
                    "Size": min_size
                }, ignore_index=True)

    df_aggregate.to_csv(output_file + "_best.csv", index = False)
# ====================================================================================================

# ====================================================================================================
# The program should be called by passing two arguments:
# - the path in which the test suite files and results are stored
# - the name of the output file, in which the aggregated results are stored
if __name__ == "__main__":
    arg_parser = argparse.ArgumentParser()
    arg_parser.add_argument("--r", help="result file path")
    arg_parser.add_argument("--o", help="output file name")
    args = arg_parser.parse_args()

    # Evaluate the results by fetching the files
    main(args.r, args.o)

    # Extract the best results
    extract_best_results(args.o)