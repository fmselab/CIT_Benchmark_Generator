import argparse
from ftplib import error_perm
from gettext import find
from nis import cat
from os import listdir
from os.path import isfile, join
import csv
from tarfile import DEFAULT_FORMAT
from numpy import mat
import pandas as pd 
import math
import matplotlib.pyplot as plt
import seaborn as sns

categories = ["MCAC_", "MCA_", "NUMC_", "BOOLC_", "UNIFORM_BOOLEAN_", "UNIFORM_ALL_"]
tools = ["appts", "acts", "pict", "iposolver", "cagen.new", "cagen", "pmedici", "pmedici.new", "casa"]
strengths_vector = [2, 3, 4, 5, 6]
#validation_files_path = "validation_files/"
#output_files_path = "data/"
#output_figs_path = "figs/"
validation_files_path = "/Users/andrea/Desktop/CTCompFollowUp/ValidationResults/"
output_files_path = "/Users/andrea/Desktop/CTCompFollowUp/data/"
output_figs_path = "/Users/andrea/Desktop/CTCompFollowUp/figs/"
compute_data = False

# ====================================================================================================
# Returns the time of the corresponding execution - Extracts it from the .time files
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
        # If time is not found, return -1 to signal the error
        time_seconds = -1

    return time_seconds
# ====================================================================================================

# ====================================================================================================
# Returns the size of the corresponding execution, by counting the number of rows of the .out file 
# containing the test suite
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
# Determines if the given file is meaningful, i.e., if the number of patameter is greater than the
# strength of the test
def is_meaningful(file):
    execution_info = file.split("_")
    tool_name, model_name, strength, iteraration = extract_info_from_filename(execution_info)
    
    # Only the models having more parameters than the used strength are meaningful
    df_output = pd.read_csv(validation_files_path + "ModelComplexity.csv", delimiter=";")
    if df_output.loc[df_output['Model'] == model_name, 'nParams'].values[0] >= int(strength):
        return True

    return False
# ====================================================================================================

# ====================================================================================================
# Exports, for each tool and for each strength the number of meaningful instances, i.e., those models 
# for which the number of parameters is higher than the strength
def export_statistics(only_files):
    out_file = open(output_files_path + "Meaningful_files.csv", 'w')
    models_considered = []
    for tool in tools:
        for strength in strengths_vector:
            n_models = 0
            for file in only_files:
                execution_info = file.split("_")
                toolname, model_name, strength_file, iteration = extract_info_from_filename(execution_info)
                if (toolname == tool):
                    if (int(strength_file) == strength and model_name not in models_considered):
                        n_models = n_models + 1
                        models_considered.append(model_name)
            out_file.write(str(strength) + "," + tool + "," + str(n_models) + "\n")
            models_considered.clear()   
    out_file.close()                
# ====================================================================================================

# ====================================================================================================
# Main function
def main(file_path, output_file):
    # Fetch the files in the file_path
    onlyfiles = [f for f in listdir(file_path) if (isfile(join(file_path, f)) and f.endswith(".time"))]

    # Exclude from the onlyfiles list the models where k<t
    onlyfiles = [f for f in onlyfiles if is_meaningful(f)]

    # Export the statistics
    export_statistics(onlyfiles)

    # Output file
    out_file = open(output_file + ".csv", 'w')
    writer = csv.writer(out_file)

    # Header
    writer.writerow(["ToolName","ModelName","Strength","Iteration","ErrorType","TimeSeconds","Size"])

    for file in onlyfiles:
        execution_info = file.split("_")
        error_type = ""
        tool_name, model_name, strength, iteraration = extract_info_from_filename(execution_info)

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
# Extracts info from the file name: the model name, the generator, the strength and the iteration 
# number
def extract_info_from_filename(execution_info):
    tool_name = execution_info[0]
    if (execution_info[1] == "UNIFORM"):
        model_name = execution_info[1] + "_" + execution_info[2] + "_" + execution_info[3].split(".")[0]
        strength = execution_info[4]
        iteraration = execution_info[5].split(".")[0]
    else:
        model_name = execution_info[1] + "_" + execution_info[2].split(".")[0]
        strength = execution_info[3]
        iteraration = execution_info[4].split(".")[0]
    return tool_name,model_name,strength,iteraration
# ====================================================================================================

# ====================================================================================================
# It searches in the validation files if the generator, with a given given and streght, produces
# a valid test suite (i.e., if in the validation file there is an OK)
def is_valid(tool_name, model_name, strength):
    try:
        f = open(validation_files_path + "const_tests_" + str(strength) + "_new.txt", "r")

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
def export_boxplot(plot_title, ylabel, data, file_name):
    fig, ax = plt.subplots()
    ax.set_title(plot_title)
    plt.xlabel("Generator")
    plt.ylabel(ylabel)
    ax.set_xticklabels(["pMEDICI", "CAGen", "Appts", "IPO Solver", "CAGen (new)", "pMEDICI (new)", "PICT"])
    plt.xticks(rotation=90)
    ax.boxplot(data, showfliers=False)
    fig = ax.get_figure()
    fig.tight_layout()
    fig.savefig(output_figs_path + file_name+ ".png")
# ====================================================================================================

# ====================================================================================================
# Print the plots
def print_plots(df_aggregate):
    # Extract a figure showing the behavior of the tools - TIME
    datapMedici = df_aggregate[(df_aggregate.ToolName.eq("pmedici")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    dataCAGen = df_aggregate[(df_aggregate.ToolName.eq("cagen")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    dataACTS = df_aggregate[(df_aggregate.ToolName.eq("acts")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    dataIPOSolver = df_aggregate[(df_aggregate.ToolName.eq("iposolver")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    datapMediciNew = df_aggregate[(df_aggregate.ToolName.eq("pmedici.new")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    dataCAGenNew = df_aggregate[(df_aggregate.ToolName.eq("cagen.new")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
    dataPICT = df_aggregate[(df_aggregate.ToolName.eq("pict")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()

    export_boxplot('Generation time', 'Time [sec.]', [datapMedici, dataCAGen, dataACTS, dataIPOSolver, dataCAGenNew, datapMediciNew, dataPICT], 'Tools_time')

    # Extract a figure showing the behavior of the tools - Size
    datapMedici = df_aggregate[(df_aggregate.ToolName.eq("pmedici")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    dataCAGen = df_aggregate[(df_aggregate.ToolName.eq("cagen")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    dataACTS = df_aggregate[(df_aggregate.ToolName.eq("acts")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    dataIPOSolver = df_aggregate[(df_aggregate.ToolName.eq("iposolver")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    datapMediciNew = df_aggregate[(df_aggregate.ToolName.eq("pmedici.new")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    dataCAGenNew = df_aggregate[(df_aggregate.ToolName.eq("cagen.new")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
    dataPICT = df_aggregate[(df_aggregate.ToolName.eq("pict")) & (~df_aggregate.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()

    export_boxplot('Test suite size', '# Test cases', [datapMedici, dataCAGen, dataACTS, dataIPOSolver, dataCAGenNew, datapMediciNew, dataPICT], 'Tools_size')
# ====================================================================================================

# ====================================================================================================
# Print the plots filtered on categories
def print_plots_categories(df_aggregate, categories):
    for category in categories:
        filtered_df = df_aggregate[df_aggregate.ModelName.str.contains(category)]

        # Extract a figure showing the behavior of the tools - TIME
        datapMedici = filtered_df[(filtered_df.ToolName.eq("pmedici")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        dataCAGen = filtered_df[(filtered_df.ToolName.eq("cagen")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        dataACTS = filtered_df[(filtered_df.ToolName.eq("acts")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        dataIPOSolver = filtered_df[(filtered_df.ToolName.eq("iposolver")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        datapMediciNew = filtered_df[(filtered_df.ToolName.eq("pmedici.new")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        dataCAGenNew = filtered_df[(filtered_df.ToolName.eq("cagen.new")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        dataPICT = filtered_df[(filtered_df.ToolName.eq("pict")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].TimeSeconds.to_numpy()
        
        export_boxplot('Generation time', 'Time [sec.]', [datapMedici, dataCAGen, dataACTS, dataIPOSolver, dataCAGenNew, datapMediciNew, dataPICT], "Tools_time_" + category)

        # Extract a figure showing the behavior of the tools - Size
        datapMedici = filtered_df[(filtered_df.ToolName.eq("pmedici")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        dataCAGen = filtered_df[(filtered_df.ToolName.eq("cagen")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        dataACTS = filtered_df[(filtered_df.ToolName.eq("acts")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        dataIPOSolver = filtered_df[(filtered_df.ToolName.eq("iposolver")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        datapMediciNew = filtered_df[(filtered_df.ToolName.eq("pmedici.new")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        dataCAGenNew = filtered_df[(filtered_df.ToolName.eq("cagen.new")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        dataPICT = filtered_df[(filtered_df.ToolName.eq("pict")) & (~filtered_df.ErrorType.isin(["Timeout", "Invalid"]))].Size.to_numpy()
        
        export_boxplot('Test suite size', '# Test cases', [datapMedici, dataCAGen, dataACTS, dataIPOSolver, dataCAGenNew, datapMediciNew, dataPICT], 'Tools_size_' + category)
# ====================================================================================================

# ====================================================================================================
# Extracts the ranking for each model
def extract_ranking(output_file):
    df_output = pd.read_csv(output_file + "_best.csv", delimiter=",")
    model_names = df_output["ModelName"].unique()
    strengths = df_output["Strength"].unique()
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
# Extracts the ranking for each model
def get_stats(pdserie, writer, item_name):
    print(pdserie)      
    for i, v in pdserie.iteritems():
        writer.writerow([i,item_name,v])
# ====================================================================================================

# ====================================================================================================
# General ranking
def overall_ranking(size, time):
    out_file = open(output_files_path + "OVERALL_allStrengths.csv", 'w')
    writer = csv.writer(out_file)
    writer.writerow(["ToolName","EntryType","Score"])

    print("********** Size ranking: **********")
    pdserie = size.groupby(by="ToolName").Score.sum()
    get_stats(pdserie, writer, "Size")

    print("********** Time ranking: **********")
    pdserie = time.groupby(by="ToolName").Score.sum()
    get_stats(pdserie, writer, "Time")

    print("********** Overall ranking: **********")
    pdserie = size.groupby(by="ToolName").Score.sum() * 0.5 + time[time.Strength != 5].groupby(by="ToolName").Score.sum() * 0.5
    get_stats(pdserie, writer, "Overall")

    out_file.close
# ====================================================================================================

# ====================================================================================================
# General ranking per strength
def overall_ranking_strength(size, time):
    for strength in strengths_vector:

        out_file = open(output_files_path + "OVERALL_" + str(strength) + ".csv", 'w')
        writer = csv.writer(out_file)
        writer.writerow(["ToolName","EntryType","Score"])

        sizenew = size[size.Strength.eq(strength)]
        timenew = time[time.Strength.eq(strength)]

        print("***********************************")
        print("*** STRENGTH: " + str(strength) + "***")
        print("***********************************")
        print("********** Size ranking: **********")
        pdserie = sizenew.groupby(by="ToolName").Score.sum()
        get_stats(pdserie, writer, "Size")

        print("********** Time ranking: **********")
        pdserie = timenew.groupby(by="ToolName").Score.sum()
        get_stats(pdserie, writer, "Time")

        print("********** Overall ranking: **********")
        pdserie = sizenew.groupby(by="ToolName").Score.sum() * 0.5 + timenew.groupby(by="ToolName").Score.sum() * 0.5
        get_stats(pdserie, writer, "Overall")

        out_file.close
# ====================================================================================================

# ====================================================================================================
# Ranking for categories
def ranking_for_categories(size, time, categories):
    for category in categories:

        out_file = open(output_files_path + category + "allStrengths.csv", 'w')
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
# Histograms for categories
def histogram_for_categories():
    for category in categories:
        export_histograms(category, "Size")
        export_histograms(category, "Time")
        export_histograms(category, "Overall")
# ====================================================================================================

# ====================================================================================================
# Histograms Overall
def histogram_overall():
    export_histograms("OVERALL_", "Size")
    export_histograms("OVERALL_", "Time")
    export_histograms("OVERALL_", "Overall")
# ====================================================================================================

# ====================================================================================================
# Histograms Overall for strength
def histogram_overall_for_strength():
    export_histograms_t("OVERALL_", "Size")
    export_histograms_t("OVERALL_", "Time")
    export_histograms_t("OVERALL_", "Overall")
# ====================================================================================================

# ====================================================================================================
# Export the histograms
def export_histograms(category, filter_by):
    # Reads the file out_file in a pandas dataframe
    df = pd.read_csv(output_files_path + category + "allStrengths.csv", delimiter=",")
    # Plot an histogram where EntryType is "Size", use on the x axis the "ToolName" and on the y axis the "Score"
    ax = df[df.EntryType.eq(filter_by)].plot.bar(x="ToolName", y="Score", title=filter_by + " ranking for " + category[:-1])
    # Set labels
    ax.set_xlabel("Tool")
    ax.set_ylabel("Score")
    # Hide the legend of the plots
    ax.legend().set_visible(False)
    # Adapt the plot size to fit the labels
    fig = ax.get_figure()
    fig.tight_layout()
    # Save the histogram to file
    plt.savefig(output_figs_path + category + "Allstrength_" + filter_by + ".png")
# ====================================================================================================

# ====================================================================================================
# Export the histograms grouping data for strength
def export_histograms_t(category, filter_by):
    dfAll = pd.DataFrame()
    for strength in strengths_vector:
        df = pd.read_csv(output_files_path + category + str(strength) + ".csv", delimiter=",")
        df = df[df.EntryType.eq(filter_by)]
        # Add a column to the dataframe, with all values equal to strength
        df["Strength"] = strength
        # Concatenate the df dataframe with dfAll
        dfAll = pd.concat([dfAll, df])
    # Remove the column "EntryType" from the dataframe
    dfAll = dfAll.drop(columns=["EntryType"])

    # Seaborn Barplot with the Scores reached by all the tools for each strength
    ax1 = sns.catplot(x="ToolName", y="Score", hue="Strength", data=dfAll, kind="bar")
    # Set the labels    
    ax1.set_xlabels("Tool")
    ax1.set_ylabels("Score")
    # Set label rotation for the x axis
    ax1.set_xticklabels(rotation=90)
    # Set the title of the plot
    ax1.fig.suptitle(filter_by + " overall ranking ")
    # Show all borders of the plots
    ax1.despine(right=False, top=False, left=False, bottom=False)
    # Hide the catplot legend   
    ax1._legend.set_visible(False)
    # Show the borders around the legend    
    plt.legend(loc='upper right', shadow=True, ncol=2)
    # Adapt the plot size to fit the labels
    plt.tight_layout()
    # Save the histogram to file
    plt.savefig(output_figs_path + category + "PerStrength_" + filter_by + ".png")
# ====================================================================================================

# ====================================================================================================
# Ranking for categories and strengths
def ranking_for_categories_and_strength(size, time, categories):
    strengths = size["Strength"].unique()

    out_file2 = open(output_files_path + "InvalidInstances.csv", 'w')
    out_file3 = open(output_files_path + "TimedoutInstances.csv", 'w')
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
            out_file = open(output_files_path + category + str(strength) + ".csv", 'w')
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
# Extract the number of timed-out and invalid instances for each tool
def summary_invalid_timeout():
    invalid_file = pd.read_csv(output_files_path + "InvalidInstances.csv", delimiter=",")
    timedout_file = pd.read_csv(output_files_path + "TimedoutInstances.csv", delimiter=",")

    timedout_file.groupby(by=["ToolName", "Strength"]).N_TimeOut.sum().to_csv(output_files_path + "TimedoutInstancesGrouped.csv")
    invalid_file.groupby(by=["ToolName", "Strength"]).N_Invalid.sum().to_csv(output_files_path + "InvalidInstancesGrouped.csv")
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

    if compute_data:
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

        # Summary data on invalid and timeout instances
        summary_invalid_timeout()

    # Export historgrams
    histogram_overall()
    histogram_overall_for_strength()
    histogram_for_categories()
