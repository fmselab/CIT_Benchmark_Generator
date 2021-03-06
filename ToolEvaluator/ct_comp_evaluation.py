import argparse
from ftplib import error_perm
from gettext import find
from os import listdir
from os.path import isfile, join
import csv
from tarfile import DEFAULT_FORMAT
from numpy import mat
import pandas as pd 
import math
import matplotlib.pyplot as plt

categories = ["MCAC_", "MCA_", "NUMC_", "BOOLC_", "UNIFORM_BOOLEAN_", "UNIFORM_ALL_"]
strengths_vector = [2, 3, 4, 5]

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
    writer.writerow(["ToolName","ModelName","Strength","Iteration","ErrorType","TimeSeconds","Size"])

    for file in onlyfiles:
        execution_info = file.split("_")
        error_type = ""
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

        # Set timeout instances
        if size == 0:
            error_type = "Timeout"
        # Set the Invalid instances
        elif (not is_valid(tool_name, model_name, strength)):
            error_type = "Invalid"

        writer.writerow ([tool_name , model_name , strength , iteraration , error_type, time , size])

    out_file.close
# ====================================================================================================

# ====================================================================================================
def is_valid(tool_name, model_name, strength):
    try:
        f = open("validation_files/const_tests_" + str(strength) + "_new.txt", "r")

        for row in f:
            find_str = tool_name + " - " + model_name + ": Ok"
            if row.strip().startswith(find_str):
                f.close
                return True

    except Exception as e:
         print (str(e))
         return False

    return False
# ====================================================================================================

# ====================================================================================================
# Extracts the best results for each tool over the three iterations
def extract_best_results(output_file):
    df_output = pd.read_csv(output_file + ".csv", delimiter=",")
    df_aggregate = pd.DataFrame(columns=["ToolName","ModelName","Strength","TimeSeconds","Size","ErrorType"])
    model_names = df_output["ModelName"].unique()
    generator_names = df_output["ToolName"].unique()
    strengths = df_output["Strength"].unique()

    for strength in strengths:
        for modelName in model_names:
            for generatorName in generator_names:
                error_type = ""
                filtered_df_output = df_output[(df_output.ToolName.eq(generatorName)) & (df_output.ModelName.eq(modelName)) & \
                    (df_output.Strength.eq(strength))]

                if math.isnan(filtered_df_output.TimeSeconds.min()):
                    print(filtered_df_output)
                
                min_time = filtered_df_output.TimeSeconds.min() if not math.isnan(filtered_df_output.TimeSeconds.min()) else -1
                min_size = filtered_df_output.Size.min() if not math.isnan(filtered_df_output.Size.min()) else -1

                if min_size == -1:
                    continue

                # If the size is 0 we consider it as a timeout
                if min_size == 0:
                    error_type = "Timeout"
                # Set the Invalid instances
                elif (not is_valid(generatorName, modelName, strength)):
                    error_type = "Invalid"

                df_aggregate = df_aggregate.append({
                    "ToolName": generatorName,
                    "ModelName": modelName,
                    "Strength": strength,
                    "TimeSeconds": min_time,
                    "Size": min_size,
                    "ErrorType": error_type
                }, ignore_index=True)

    df_aggregate.to_csv(output_file + "_best.csv", index = False)

    print_plots(df_aggregate)
    print_plots_categories(df_aggregate, categories)
# ====================================================================================================

# ====================================================================================================
# Print the plots
def print_plots(df_aggregate):
    # Extract a figure showing the behavior of the tools - TIME
    datapMedici = df_aggregate[(df_aggregate.ToolName.eq("pmedici")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    dataCAGen = df_aggregate[(df_aggregate.ToolName.eq("cagen")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    dataAppts = df_aggregate[(df_aggregate.ToolName.eq("appts")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    dataIPOSolver = df_aggregate[(df_aggregate.ToolName.eq("iposolver")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    
    fig, ax = plt.subplots()
    ax.set_title('Generation time')
    plt.xlabel("Generator")
    plt.ylabel("Time [sec.]")
    ax.set_xticklabels(["pMEDICI", "CAGen", "Appts", "IPO Solver"])
    ax.boxplot([datapMedici, dataCAGen, dataAppts, dataIPOSolver])
    fig.savefig("figs/Tools_time.png")

    # Extract a figure showing the behavior of the tools - Size
    datapMedici = df_aggregate[(df_aggregate.ToolName.eq("pmedici")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    dataCAGen = df_aggregate[(df_aggregate.ToolName.eq("cagen")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    dataAppts = df_aggregate[(df_aggregate.ToolName.eq("appts")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    dataIPOSolver = df_aggregate[(df_aggregate.ToolName.eq("iposolver")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()

    fig, ax = plt.subplots()
    ax.set_title('Test suite size')
    plt.xlabel("Generator")
    plt.ylabel("# Test cases")
    ax.set_xticklabels(["pMEDICI", "CAGen", "Appts", "IPO Solver"])
    ax.boxplot([datapMedici, dataCAGen, dataAppts, dataIPOSolver])
    fig.savefig("figs/Tools_size.png")
# ====================================================================================================

# ====================================================================================================
# Print the plots filtered on categories
def print_plots_categories(df_aggregate, categories):
    for category in categories:
        filtered_df = df_aggregate[df_aggregate.ModelName.str.contains(category)]

        # Extract a figure showing the behavior of the tools - TIME
        datapMedici = filtered_df[(filtered_df.ToolName.eq("pmedici")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        dataCAGen = filtered_df[(filtered_df.ToolName.eq("cagen")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        dataAppts = filtered_df[(filtered_df.ToolName.eq("appts")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        dataIPOSolver = filtered_df[(filtered_df.ToolName.eq("iposolver")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        
        fig, ax = plt.subplots()
        plt.xlabel("Generator")
        plt.ylabel("Time [sec.]")
        ax.set_title('Generation time')
        ax.set_xticklabels(["pMEDICI", "CAGen", "Appts", "IPO Solver"])
        ax.boxplot([datapMedici, dataCAGen, dataAppts, dataIPOSolver])
        fig.savefig("figs/Tools_time_" + category + ".png")

        # Extract a figure showing the behavior of the tools - Size
        datapMedici = filtered_df[(filtered_df.ToolName.eq("pmedici")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        dataCAGen = filtered_df[(filtered_df.ToolName.eq("cagen")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        dataAppts = filtered_df[(filtered_df.ToolName.eq("appts")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        dataIPOSolver = filtered_df[(filtered_df.ToolName.eq("iposolver")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()

        fig, ax = plt.subplots()
        ax.set_title('Test suite size')
        ax.set_xticklabels(["pMEDICI", "CAGen", "Appts", "IPO Solver"])
        plt.xlabel("Generator")
        plt.ylabel("# Test cases")
        ax.boxplot([datapMedici, dataCAGen, dataAppts, dataIPOSolver])
        fig.savefig("figs/Tools_size_" + category + ".png")
# ====================================================================================================

# ====================================================================================================
# Extracts the ranking for each model
def extract_ranking(output_file):
    df_output = pd.read_csv(output_file + "_best.csv", delimiter=",")
    model_names = df_output["ModelName"].unique()
    tool_names = df_output["ToolName"].unique()
    strengths = df_output["Strength"].unique()
    tool_names = ["pMEDICI", "CAGen", "Appts", "IPO Solver"]
    df_ranking_time = pd.DataFrame(columns=["ToolName","ModelName","Strength","Score", "ErrorType"])
    df_ranking_size = pd.DataFrame(columns=["ToolName","ModelName","Strength","Score", "ErrorType"])

    for strength in strengths:
        for modelName in model_names:
            # Sort the results by time
            vector = df_output[(df_output.ModelName.eq(modelName)) & (df_output.Strength.eq(strength))].sort_values(by=['TimeSeconds'], ascending=False).to_numpy()
            point_counter = 0
            score = 0
            for res in vector:
                error_type = ""
                if res[5] in ["Timeout", "Invalid"]:
                    score = 0
                    error_type = res[5]
                else:
                    score = point_counter + 1
                    point_counter += 1
                # Add in the dataframe
                df_ranking_time = df_ranking_time.append({
                    "ToolName": res[0],
                    "ModelName": modelName,
                    "Strength": strength,
                    "Score": score,
                    "ErrorType": error_type
                }, ignore_index=True)

            # Sort the results by size
            point_counter = 0
            vector = df_output[(df_output.ModelName.eq(modelName)) & (df_output.Strength.eq(strength))].sort_values(by=['Size'], ascending=False).to_numpy()
            score = 0
            for res in vector:
                error_type = ""
                if res[5] in ["Timeout", "Invalid"]:
                    score = 0
                    error_type = res[5]
                else:
                    score = point_counter + 1
                    point_counter += 1
                # Add in the dataframe
                df_ranking_size = df_ranking_size.append({
                    "ToolName": res[0],
                    "ModelName": modelName,
                    "Strength": strength,
                    "Score": score,
                    "ErrorType": error_type
                }, ignore_index=True)

    df_ranking_size.to_csv(output_file + "_ranking_size.csv", index = False)
    df_ranking_time.to_csv(output_file + "_ranking_time.csv", index = False)
    return [df_ranking_size, df_ranking_time]
# ====================================================================================================

# ====================================================================================================
# General ranking
def overall_ranking(size, time):
    out_file = open("data/" + "OVERALL_allStrengths.csv", 'w')
    writer = csv.writer(out_file)
    writer.writerow(["ToolName","EntryType","Score"])

    print("********** Size ranking: **********")
    pdserie = size.groupby(by="ToolName").Score.sum()
    print(pdserie)      
    for i, v in pdserie.iteritems():
        writer.writerow([i,"Size",v])

    print("********** Time ranking: **********")
    pdserie = time.groupby(by="ToolName").Score.sum()
    print(pdserie)      
    for i, v in pdserie.iteritems():
        writer.writerow([i,"Time",v])

    print("********** Overall ranking: **********")
    pdserie = size.groupby(by="ToolName").Score.sum() * 0.5 + time[time.Strength != 5].groupby(by="ToolName").Score.sum() * 0.5
    print(pdserie)      
    for i, v in pdserie.iteritems():
        writer.writerow([i,"Overall",v])

    out_file.close
# ====================================================================================================

# ====================================================================================================
# General ranking per strength
def overall_ranking_strength(size, time):
    strengths = size["Strength"].unique()

    for strength in strengths:

        out_file = open("data/" + "OVERALL_" + str(strength) + ".csv", 'w')
        writer = csv.writer(out_file)
        writer.writerow(["ToolName","EntryType","Score"])

        sizenew = size[size.Strength.eq(strength)]
        timenew = time[time.Strength.eq(strength)]

        print("***********************************")
        print("*** STRENGTH: " + str(strength) + "***")
        print("***********************************")
        print("********** Size ranking: **********")
        pdserie = sizenew.groupby(by="ToolName").Score.sum()
        print(pdserie)      
        for i, v in pdserie.iteritems():
            writer.writerow([i,"Size",v])

        print("********** Time ranking: **********")
        pdserie = timenew.groupby(by="ToolName").Score.sum()
        print(pdserie)      
        for i, v in pdserie.iteritems():
            writer.writerow([i,"Time",v])

        print("********** Overall ranking: **********")
        pdserie = sizenew.groupby(by="ToolName").Score.sum() * 0.5 + timenew.groupby(by="ToolName").Score.sum() * 0.5
        print(pdserie)      
        for i, v in pdserie.iteritems():
            writer.writerow([i,"Overall",v])

        out_file.close
# ====================================================================================================

# ====================================================================================================
# Ranking for categories
def ranking_for_categories(size, time, categories):
    for category in categories:

        out_file = open("data/" + category + "allStrengths.csv", 'w')
        writer = csv.writer(out_file)
        writer.writerow(["ToolName","EntryType","Score"])

        sizenew = size[size.ModelName.str.contains(category)]
        timenew = time[time.ModelName.str.contains(category)]

        print("***********************************")
        print("*** CATEGORY: " + category + "***")
        print("***********************************")
        print("********** Size ranking: **********")
        pdserie = sizenew.groupby(by="ToolName").Score.sum()
        print(pdserie)      
        for i, v in pdserie.iteritems():
            writer.writerow([i,"Size",v])

        print("********** Time ranking: **********")
        pdserie = timenew.groupby(by="ToolName").Score.sum()
        print(pdserie)      
        for i, v in pdserie.iteritems():
            writer.writerow([i,"Time",v])

        print("********** Overall ranking: **********")
        pdserie = sizenew.groupby(by="ToolName").Score.sum() * 0.5 + timenew.groupby(by="ToolName").Score.sum() * 0.5
        print(pdserie)      
        for i, v in pdserie.iteritems():
            writer.writerow([i,"Overall",v])

        out_file.close
# ====================================================================================================

# ====================================================================================================
# Ranking for categiries
def ranking_for_categories_and_strength(size, time, categories):
    strengths = size["Strength"].unique()

    out_file2 = open("data/InvalidInstances.csv", 'w')
    out_file3 = open("data/TimedoutInstances.csv", 'w')
    writer2 = csv.writer(out_file2)
    writer2.writerow(["ToolName","Strength","Category","N_Invalid"])
    writer3 = csv.writer(out_file3)
    writer3.writerow(["ToolName","Strength","Category","N_TimeOut"])

    for strength in strengths:

        sizenew = size[size.Strength.eq(strength)]
        timenew = time[time.Strength.eq(strength)]
        print("***********************************")
        print("*** STRENGTH: " + str(strength) + "***")
        print("***********************************")
        for category in categories:
            out_file = open("data/" + category + str(strength) + ".csv", 'w')
            writer = csv.writer(out_file)
            writer.writerow(["ToolName","EntryType","Score"])

            sizenew_c = sizenew[sizenew.ModelName.str.contains(category)]
            timenew_c = timenew[timenew.ModelName.str.contains(category)]

            # Compute the number of not valid instances
            pdserie = sizenew_c[sizenew.ErrorType.eq("Invalid")].groupby(by="ToolName").Score.count()
            for i, v in pdserie.iteritems():
                writer2.writerow([i,strength,category,v])

            # Compute the number of not timed out instances
            pdserie = sizenew_c[sizenew.ErrorType.eq("Timeout")].groupby(by="ToolName").Score.count()
            for i, v in pdserie.iteritems():
                writer3.writerow([i,strength,category,v])

            print("***********************************")
            print("*** CATEGORY: " + category + "***")
            print("***********************************")
            print("********** Size ranking: **********")
            pdserie = sizenew_c.groupby(by="ToolName").Score.sum()
            print(pdserie)      
            for i, v in pdserie.iteritems():
                writer.writerow([i,"Size",v])

            print("********** Time ranking: **********")
            pdserie = timenew_c.groupby(by="ToolName").Score.sum()
            print(pdserie)      
            for i, v in pdserie.iteritems():
                writer.writerow([i,"Time",v])

            print("********** Overall ranking: **********")
            pdserie = sizenew_c.groupby(by="ToolName").Score.sum() * 0.5 + timenew_c.groupby(by="ToolName").Score.sum() * 0.5
            print(pdserie)      
            for i, v in pdserie.iteritems():
                writer.writerow([i,"Overall",v])

            out_file.close
    out_file2.close
    out_file3.close
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

    # Extract the ranking for each file
    [size, time] = extract_ranking(args.o)

    # Sum the results and extract the overall ranking
    overall_ranking(size, time)
    overall_ranking_strength(size, time)

    # Results per category
    ranking_for_categories(size, time, categories)
    ranking_for_categories_and_strength(size, time, categories)
