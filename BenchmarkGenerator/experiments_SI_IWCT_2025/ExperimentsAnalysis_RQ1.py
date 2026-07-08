import pandas as pd
import seaborn as sns
from scipy.stats import wilcoxon
from math import sqrt
import glob
import matplotlib.pyplot as plt

RQ1A = False
RQ1B = False
RQ1C = True

my_colors = ["#1EBC9F", "#FF8A65", "#8EA5C9"]
my_colors_reduced = ["#1EBC9F",  "#8EA5C9"]

def test(datax, datay, label):
    test_times = wilcoxon(datax, datay, alternative='two-sided', method='approx', zero_method='pratt')
    print ("*** Test on " + label + " ***")
    print (test_times)
    print (datax.mean())
    print (datay.mean())
    print ("Effect size: " + str(test_times.zstatistic / sqrt(len(datax) + len(datay))))


# Fetch all csv files with name ending in 'Experiments_RQ1.csv' and save them into a pandas dataframe
files = glob.glob('*Experiments_RQ1.csv', recursive=True)
# Create an empty list to store dataframes
df_list = []
# Loop through each file and read it
for file in files:
    print(f'Reading file: {file}')
    df_temp = pd.read_csv(file,
                          delimiter=';', 
                          names=['track', 'goal', 'nCompliant', 'time', 'approach', ''])
    df_list.append(df_temp)
# Concatenate all dataframes into a single dataframe
experiments = pd.concat(df_list, axis=0, ignore_index=True)

# Change the goal
experiments['goal'] = experiments['goal'].replace({
    'SOLVABILITY': 'Solvability',
    'TESTRATIO': r'$r_{ts}$',
    'TUPLERATIO': r'$r_{tp}$',
    'TUPLETESTRATIO': 'Both ratios'
})

# Drop the last column because it is empty
experiments = experiments.drop(columns=[''])

if RQ1A:

    # Create a boxplot comparing the number of compliant IPMs for the three approaches ('BENCIGEN', 'BENCIGEN_S', 'BENCIGEN_SMO')
    plt.figure(figsize=(5, 2.3))
    sns.boxplot(data=experiments, x='approach', y='nCompliant', palette='Set2', width=0.4, showfliers=False)
    params = {'mathtext.default': 'regular' }          
    plt.rcParams.update(params)
    plt.xticks([0, 1, 2], ['BENCIGEN', '$BENCIGEN_S$', '$BENCIGEN_{SMO}$'], fontsize=8)
    plt.xlabel('Approach', fontsize=8)
    plt.xticks(fontsize=8)
    plt.yticks(fontsize=8)
    plt.ylabel('# Compliant IPMs', fontsize=8)
    plt.tight_layout()
    plt.savefig('images/SI_RQ1_A.pdf', dpi=300, bbox_inches='tight')
    plt.show()

    # Compute the average number of compliant IPMs for each approach
    bencigen = experiments[experiments['approach'] == 'BENCIGEN']['nCompliant'].mean()
    bencigen_s = experiments[experiments['approach'] == 'BENCIGENS']['nCompliant'].mean()
    bencigen_smo = experiments[experiments['approach'] == 'BENCIGENSMO']['nCompliant'].mean()

    # Compute the standard deviation for each approach
    bencigen_std = experiments[experiments['approach'] == 'BENCIGEN']['nCompliant'].std()
    bencigen_s_std = experiments[experiments['approach'] == 'BENCIGENS']['nCompliant'].std()
    bencigen_smo_std = experiments[experiments['approach'] == 'BENCIGENSMO']['nCompliant'].std()

    print('BENCIGEN:', bencigen)
    print('BENCIGEN_S:', bencigen_s)
    print('BENCIGEN_SMO:', bencigen_smo)
    print('BENCIGEN STD:', bencigen_std)
    print('BENCIGEN_S STD:', bencigen_s_std)
    print('BENCIGEN_SMO STD:', bencigen_smo_std)

    # Do a wilcoxon signed-rank test to compare the three approaches
    test(experiments[experiments['approach'] == 'BENCIGEN']['nCompliant'], experiments[experiments['approach'] == 'BENCIGENS']['nCompliant'],"BENCIGEN vs BENCIGEN_S")
    test(experiments[experiments['approach'] == 'BENCIGEN']['nCompliant'], experiments[experiments['approach'] == 'BENCIGENSMO']['nCompliant'],"BENCIGEN vs BENCIGEN_SMO")
    test(experiments[experiments['approach'] == 'BENCIGENS']['nCompliant'], experiments[experiments['approach'] == 'BENCIGENSMO']['nCompliant'],"BENCIGEN_S vs BENCIGEN_SMO")

if RQ1B:
    # Create a boxplot comparing the number of compliant IPMs for the three approaches ('BENCIGEN', 'BENCIGEN_S', 'BENCIGEN_SMO') and the four goals
    # Set mathtext parameters before plotting
    params = {'mathtext.default': 'regular'}
    plt.rcParams.update(params)
    # Define the desired order
    goal_order = ['Solvability', r'$r_{ts}$', r'$r_{tp}$', 'Both ratios']
    plt.figure(figsize=(7, 2.3))
    sns.boxplot(data=experiments, x='goal', y='nCompliant', hue='approach', 
                palette='Set2', width=0.8, showfliers=False, order=goal_order)
    plt.xlabel('Goal', fontsize=8)
    plt.ylabel('# Compliant IPMs', fontsize=8)
    # Update legend with mathtext formatting
    handles, labels = plt.gca().get_legend_handles_labels()
    plt.legend(handles=handles, labels=[r'$BENCIGEN$',r'$BENCIGEN_S$',r'$BENCIGEN_{SMO}$'], title='Approach', fontsize=8,loc='lower left')
    plt.yticks(fontsize=8)
    plt.xticks(fontsize=8)
    plt.tight_layout()
    plt.savefig('images/SI_RQ1_B.pdf', dpi=300, bbox_inches='tight')
    plt.show()

    # Compute the average number and the standard deviation of compliant IPMs for each approach and goal
    for goal in goal_order:
        for approach in ['BENCIGEN', 'BENCIGENS', 'BENCIGENSMO']:
            subset = experiments[(experiments['goal'] == goal) & (experiments['approach'] == approach)]
            mean_value = subset['nCompliant'].mean()
            std_value = subset['nCompliant'].std()
            print(f'Goal: {goal}, Approach: {approach}, Mean: {mean_value}, Std: {std_value}')

    # Do a wilcoxon signed-rank test to compare the three approaches per goal
    for goal in goal_order:
        subset = experiments[experiments['goal'] == goal]
        test(subset[subset['approach'] == 'BENCIGEN']['nCompliant'], subset[subset['approach'] == 'BENCIGENS']['nCompliant'],f"BENCIGEN vs BENCIGEN_S for {goal}")
        test(subset[subset['approach'] == 'BENCIGEN']['nCompliant'], subset[subset['approach'] == 'BENCIGENSMO']['nCompliant'],f"BENCIGEN vs BENCIGEN_SMO for {goal}")
        test(subset[subset['approach'] == 'BENCIGENS']['nCompliant'], subset[subset['approach'] == 'BENCIGENSMO']['nCompliant'],f"BENCIGEN_S vs BENCIGEN_SMO for {goal}")

if RQ1C:
    # Now only analyze the fils which contains 28 and 82 in their file name. Treat them as two different groups and compare the number of compliant IPMs for the three approaches ('BENCIGEN', 'BENCIGEN_S', 'BENCIGEN_SMO') in the two groups
    files = glob.glob('*82_*Experiments_RQ1.csv', recursive=True)
    # Create an empty list to store dataframes
    df_list = []
    # Loop through each file and read it
    for file in files:
        print(f'Reading file: {file}')
        df_temp = pd.read_csv(file,
                            delimiter=';', 
                            names=['track', 'goal', 'nCompliant', 'time', 'approach', ''])
        df_list.append(df_temp)
    # Concatenate all dataframes into a single dataframe
    experiments28 = pd.concat(df_list, axis=0, ignore_index=True)
    experiments28['goal'] = experiments28['goal'].replace({
        'TUPLETESTRATIO': r'$r_{ts}=0.2 - r_{tp}=0.8$'
    })
    files = glob.glob('*28_*Experiments_RQ1.csv', recursive=True)
    # Create an empty list to store dataframes
    df_list = []
    # Loop through each file and read it
    for file in files:
        print(f'Reading file: {file}')
        df_temp = pd.read_csv(file,
                            delimiter=';', 
                            names=['track', 'goal', 'nCompliant', 'time', 'approach', ''])
        df_list.append(df_temp)
    experiments82 = pd.concat(df_list, axis=0, ignore_index=True)
    experiments82['goal'] = experiments82['goal'].replace({
        'TUPLETESTRATIO': r'$r_{ts}=0.8 - r_{tp}=0.2$'
    })
    experiments = pd.concat([experiments28, experiments82], axis=0, ignore_index=True)
    # Drop the last column because it is empty
    experiments = experiments.drop(columns=[''])
    # Remove all lines with Approach = 'BENCIGENS'
    experiments = experiments[experiments['approach'] != 'BENCIGENS']

    # Create a boxplot comparing the number of compliant IPMs for the two approaches ('BENCIGEN', 'BENCIGEN_SMO') and the two goals
    # Set mathtext parameters before plotting
    params = {'mathtext.default': 'regular'}
    plt.rcParams.update(params)
    # Define the desired order
    goal_order = [r'$r_{ts}=0.2 - r_{tp}=0.8$', r'$r_{ts}=0.8 - r_{tp}=0.2$']
    plt.figure(figsize=(5, 2.3))
    sns.boxplot(data=experiments, x='goal', y='nCompliant', hue='approach', 
                palette=my_colors_reduced, width=0.8, showfliers=False, order=goal_order)
    plt.xlabel('Goal', fontsize=8)
    plt.ylabel('# Compliant IPMs', fontsize=8)
    # Update legend with mathtext formatting
    handles, labels = plt.gca().get_legend_handles_labels()
    plt.legend(handles=handles, labels=[r'$BENCIGEN$',r'$BENCIGEN_{SMO}$'], title='Approach', fontsize=8,loc='upper left')
    plt.yticks(fontsize=8)
    plt.xticks(fontsize=8)
    plt.tight_layout()
    plt.savefig('images/SI_RQ1_C.pdf', dpi=300, bbox_inches='tight')
    plt.show()

    print(experiments.groupby("approach")["nCompliant"].describe())

    # Compute the average number and the standard deviation of compliant IPMs for each approach and goal
    for goal in goal_order:
        for approach in ['BENCIGEN', 'BENCIGENSMO']:
            subset = experiments[(experiments['goal'] == goal) & (experiments['approach'] == approach)]
            mean_value = subset['nCompliant'].mean()
            std_value = subset['nCompliant'].std()
            print(f'Goal: {goal}, Approach: {approach}, Mean: {mean_value}, Std: {std_value}')

    # Do a wilcoxon signed-rank test to compare the three approaches per goal
    for goal in goal_order:
        subset = experiments[experiments['goal'] == goal]
        test(subset[subset['approach'] == 'BENCIGEN']['nCompliant'], subset[subset['approach'] == 'BENCIGENSMO']['nCompliant'],f"BENCIGEN vs BENCIGEN_SMO for {goal}")