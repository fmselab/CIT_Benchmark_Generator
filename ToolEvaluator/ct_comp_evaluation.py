import argparse
from os import listdir
from os.path import isfile, join
import csv

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
                    time_seconds = time_seconds + int(time_str.split(":")[0]) * 60 + float(time_str.split(":")[1])
                else:
                    time_seconds = time_seconds + int(time_str.split(":")[0]) * 3600 + int(time_str.split(":")[1]) * 60 + \
                        float(time_str.split(":")[2])
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