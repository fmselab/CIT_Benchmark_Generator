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

                # If timeout, then it is the worst
                if min_time > 300:
                    min_time = -1
                    min_size = -1

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
# Extracts the ranking for each model
def extract_ranking(output_file):
    df_output = pd.read_csv(output_file + "_best.csv", delimiter=",")
    model_names = df_output["ModelName"].unique()
    tool_names = df_output["ToolName"].unique()
    strengths = df_output["Strength"].unique()
    df_ranking_time = pd.DataFrame(columns=["ToolName","ModelName","Strength","Score"])
    df_ranking_size = pd.DataFrame(columns=["ToolName","ModelName","Strength","Score"])

    for strength in strengths:
        for modelName in model_names:
            # Sort the results by time
            vector = df_output[(df_output.ModelName.eq(modelName)) & (df_output.Strength.eq(strength))].sort_values(by=['TimeSeconds'], ascending=False).to_numpy()
            point_counter = 0
            score = 0
            for res in vector:
                if res[3] == -1:
                    score = 0
                else:
                    score = point_counter + 1
                    point_counter += 1
                # Add in the dataframe
                df_ranking_time = df_ranking_time.append({
                    "ToolName": res[0],
                    "ModelName": modelName,
                    "Strength": strength,
                    "Score": score
                }, ignore_index=True)

            # Sort the results by size
            point_counter = 0
            vector = df_output[(df_output.ModelName.eq(modelName)) & (df_output.Strength.eq(strength))].sort_values(by=['Size'], ascending=False).to_numpy()
            score = 0
            for res in vector:
                if res[4] == -1:
                    score = 0
                else:
                    score = point_counter + 1
                    point_counter += 1
                # Add in the dataframe
                df_ranking_size = df_ranking_size.append({
                    "ToolName": res[0],
                    "ModelName": modelName,
                    "Strength": strength,
                    "Score": score
                }, ignore_index=True)

    df_ranking_size.to_csv(output_file + "_ranking_size.csv", index = False)
    df_ranking_time.to_csv(output_file + "_ranking_time.csv", index = False)
    return [df_ranking_size, df_ranking_time]
# ====================================================================================================

# ====================================================================================================
# General ranking
def general_ranking(size, time):
    print("********** Size ranking: **********")
    print(size.groupby(by="ToolName").Score.sum())
    print("********** Time ranking: **********")
    print(time.groupby(by="ToolName").Score.sum())
    print("********** Overall ranking: **********")
    print(size.groupby(by="ToolName").Score.sum() * 0.5 + time.groupby(by="ToolName").Score.sum() * 0.5)
# ====================================================================================================


def ranking_for_categories(size, time, categories):
    for category in categories:
        sizenew = size[size.ModelName.str.contains(category)]
        timenew = time[time.ModelName.str.contains(category)]

        print("***********************************")
        print("*** CATEGORY: " + category + "***")
        print("***********************************")
        print("********** Size ranking: **********")
        print(sizenew.groupby(by="ToolName").Score.sum())
        print("********** Time ranking: **********")
        print(timenew.groupby(by="ToolName").Score.sum())
        print("********** Overall ranking: **********")
        print(sizenew.groupby(by="ToolName").Score.sum() * 0.5 + timenew.groupby(by="ToolName").Score.sum() * 0.5)


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

    # Extract the ranking
    [size, time] = extract_ranking(args.o)

    # Sum the results
    general_ranking(size, time)

    # Results per category
    categories = ["MCAC_", "MCA_", "NUMC_", "BOOLC_", "UNIFORM_BOOLEAN_", "UNIFORM_ALL_"]
    ranking_for_categories(size, time, categories)
