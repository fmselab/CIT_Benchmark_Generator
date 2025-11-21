import pandas as pd
import seaborn as sns
from scipy.stats import wilcoxon
from math import sqrt
import glob
import matplotlib.pyplot as plt

def test(datax, datay, label):
    test_times = wilcoxon(datax, datay, alternative='two-sided', method='approx', zero_method='pratt')
    print ("*** Test on " + label + " ***")
    print (test_times)
    print (datax.mean())
    print (datay.mean())
    print ("Effect size: " + str(test_times.zstatistic / sqrt(len(datax) + len(datay))))


# Fetch all csv files with name ending in 'Experiments_RQ1.csv' and save them into a pandas dataframe
files = glob.glob('*Experiments_RQ2.csv', recursive=True)
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

print(experiments[experiments['goal'] == 'Both ratios'].head())

# Drop the last column because it is empty
experiments = experiments.drop(columns=[''])

# Create a boxplot comparing the time for the three approaches ('BENCIGEN', 'BENCIGEN_S', 'BENCIGEN_SMO') and the four goals
# Set mathtext parameters before plotting
params = {'mathtext.default': 'regular'}
plt.rcParams.update(params)
# Define the desired order
goal_order = ['Solvability', r'$r_{ts}$', r'$r_{tp}$', 'Both ratios']
plt.figure(figsize=(7, 2.3))
sns.boxplot(data=experiments, x='goal', y='time', hue='approach', 
            palette='Set2', width=0.8, showfliers=False, order=goal_order)
plt.xlabel('Goal', fontsize=8)
plt.ylabel('Time [ms]', fontsize=8)
plt.yscale('log')
# Update legend with mathtext formatting
handles, labels = plt.gca().get_legend_handles_labels()
plt.legend(handles=handles, labels=[r'$BENCIGEN$',r'$BENCIGEN_S$',r'$BENCIGEN_{SMO}$'], title='Approach', fontsize=8,loc='lower left')
plt.yticks(fontsize=8)
plt.xticks(fontsize=8)
plt.tight_layout()
plt.savefig('images/SI_RQ2_A.pdf', dpi=300, bbox_inches='tight')
plt.show()

# Compute the average number of compliant IPMs for each approach
bencigen = experiments[experiments['approach'] == 'BENCIGEN']['time'].mean()
bencigen_s = experiments[experiments['approach'] == 'BENCIGENS']['time'].mean()
bencigen_smo = experiments[experiments['approach'] == 'BENCIGENSMO']['time'].mean()

# Compute the standard deviation for each approach
bencigen_std = experiments[experiments['approach'] == 'BENCIGEN']['time'].std()
bencigen_s_std = experiments[experiments['approach'] == 'BENCIGENS']['time'].std()
bencigen_smo_std = experiments[experiments['approach'] == 'BENCIGENSMO']['time'].std()

print('BENCIGEN:', bencigen)
print('BENCIGEN_S:', bencigen_s)
print('BENCIGEN_SMO:', bencigen_smo)
print('BENCIGEN STD:', bencigen_std)
print('BENCIGEN_S STD:', bencigen_s_std)
print('BENCIGEN_SMO STD:', bencigen_smo_std)

# Do a wilcoxon signed-rank test to compare the three approaches
test(experiments[experiments['approach'] == 'BENCIGEN']['time'], experiments[experiments['approach'] == 'BENCIGENS']['time'],"BENCIGEN vs BENCIGEN_S")
test(experiments[experiments['approach'] == 'BENCIGEN']['time'], experiments[experiments['approach'] == 'BENCIGENSMO']['time'],"BENCIGEN vs BENCIGEN_SMO")
test(experiments[experiments['approach'] == 'BENCIGENS']['time'], experiments[experiments['approach'] == 'BENCIGENSMO']['time'],"BENCIGEN_S vs BENCIGEN_SMO")

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
plt.savefig('images/SI_RQ2_B.pdf', dpi=300, bbox_inches='tight')
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
    if (goal != 'Both ratios'):
        test(subset[subset['approach'] == 'BENCIGEN']['nCompliant'], subset[subset['approach'] == 'BENCIGENS']['nCompliant'],f"BENCIGEN vs BENCIGEN_S for {goal}")
        test(subset[subset['approach'] == 'BENCIGENS']['nCompliant'], subset[subset['approach'] == 'BENCIGENSMO']['nCompliant'],f"BENCIGEN_S vs BENCIGEN_SMO for {goal}")
    test(subset[subset['approach'] == 'BENCIGEN']['nCompliant'], subset[subset['approach'] == 'BENCIGENSMO']['nCompliant'],f"BENCIGEN vs BENCIGEN_SMO for {goal}")

""" 
# Reshape the data into a long format for Seaborn
long_format = experiments.melt(
    id_vars=['track', 'goal'],  # Optional, keep track of metadata if needed
    value_vars=['nCompliantOriginal', 'nCompliantSearch'], 
    var_name='Type', 
    value_name='Value'
)

# Create the boxplot
plt.figure(figsize=(3.5, 2.3))
sns.boxplot(data=long_format, x='Type', y='Value', palette='Set2', width=0.4)
params = {'mathtext.default': 'regular' }          
plt.rcParams.update(params)
plt.xticks([0, 1], ['BENCIGEN', '$BENCIGEN_S$'], fontsize=8)
plt.xlabel('Approach', fontsize=8)
plt.xticks(fontsize=8)
plt.yticks(fontsize=8)
plt.ylabel('# Compliant IPMs', fontsize=8)
plt.tight_layout()
plt.savefig('images/RQ1_A.pdf', dpi=300, bbox_inches='tight')
plt.show()

# Compute the average number of compliant IPMs
original = experiments['nCompliantOriginal'].mean()
search = experiments['nCompliantSearch'].mean()
print('Original:', original)
print('Search:', search)

# Do a wilcoxon signed-rank test to compare the two approaches
test(experiments['nCompliantOriginal'], experiments['nCompliantSearch'],"NumberOfCompliantIPMs")

# Create the boxplot for singele goals
long_df = experiments.melt(
    id_vars=['track', 'goal'], 
    value_vars=['nCompliantOriginal', 'nCompliantSearch'], 
    var_name='Methodology', 
    value_name='nCompliant'
)

# Replace methodology names for better readability
long_df['Methodology'] = long_df['Methodology'].replace({
    'nCompliantOriginal': 'BENCIGEN',
    'nCompliantSearch': '$BENCIGEN_S$'
})

# Create the boxplot
plt.figure(figsize=(3.5, 2.3))
sns.boxplot(
    data=long_df, 
    x='goal', 
    y='nCompliant', 
    hue='Methodology', 
    palette='Set2',
    width=0.8
)

# Customize the plot
plt.xlabel('Goal', fontsize=8)
plt.ylabel('# Compliant IPMs', fontsize=8)
plt.legend(title='Methodology', fontsize=8)
plt.xticks(fontsize=8)
plt.yticks(fontsize=8)
plt.tight_layout()
plt.savefig('images/RQ1_B.pdf', dpi=300, bbox_inches='tight')
plt.show()

# Compute the average number of compliant IPMs per goal
original_goals = experiments.groupby('goal')['nCompliantOriginal'].mean()
search_goals = experiments.groupby('goal')['nCompliantSearch'].mean()
print('Original:', original_goals)
print('Search:', search_goals)

# Wilcoxon signed rank test for goals
test(experiments[experiments['goal'] == 'SOLVABILITY']['nCompliantOriginal'], experiments[experiments['goal'] == 'SOLVABILITY']['nCompliantSearch'],"Solvable")
test(experiments[experiments['goal'] == 'TESTRATIO']['nCompliantOriginal'], experiments[experiments['goal'] == 'TESTRATIO']['nCompliantSearch'],"Test Ratio")
test(experiments[experiments['goal'] == 'TUPLERATIO']['nCompliantOriginal'], experiments[experiments['goal'] == 'TUPLERATIO']['nCompliantSearch'],"Tuple Ratio") """