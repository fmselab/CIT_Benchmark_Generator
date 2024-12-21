import pandas as pd
import seaborn as sns
from scipy.stats import wilcoxon
from math import sqrt
import matplotlib.pyplot as plt

def test(datax, datay, label):
    test_times = wilcoxon(datax, datay, alternative='two-sided', method='approx', zero_method='pratt')
    print ("*** Test on " + label + " ***")
    print (test_times)
    print (datax.mean())
    print (datay.mean())
    print ("Effect size: " + str(test_times.zstatistic / sqrt(len(datax) + len(datay))))

# Read the csv file into a pandas dataframe
experiments = pd.read_csv(
    'experiments_RQ2.csv', 
    delimiter=';', 
    names=['track', 'goal', 'nCompliantOriginal', 'timeOriginal', 'nCompliantSearch', 'timeSearch', '']
)
# Drop the last column because it is empty
experiments = experiments.drop(columns=[''])

# Transform the timeOriginal and timeSearch columns to seconds
experiments['timeOriginal'] = experiments['timeOriginal'] / 1000
experiments['timeSearch'] = experiments['timeSearch'] / 1000


# Reshape the data into a long format for Seaborn
long_format = experiments.melt(
    id_vars=['track', 'goal'],  # Optional, keep track of metadata if needed
    value_vars=['timeOriginal', 'timeSearch'], 
    var_name='Type', 
    value_name='Value'
)

# Create the boxplot
plt.figure(figsize=(3.5, 2.3))
sns.boxplot(data=long_format, x='Type', y='Value', palette='Set2', width=0.4)
plt.xticks([0, 1], ['Original', 'Search'], fontsize=8)
plt.xlabel('Approach', fontsize=8)
plt.xticks(fontsize=8)
plt.yticks(fontsize=8)
plt.ylabel('Time [s]', fontsize=8)
plt.tight_layout()
plt.savefig('images/RQ2_A.pdf', dpi=300)
plt.show()

# Compute the average number of compliant IPMs
original = experiments['timeOriginal'].mean()
search = experiments['timeSearch'].mean()
print('Original:', original)
print('Search:', search)

# Do a wilcoxon signed-rank test to compare the two approaches
test(experiments['timeOriginal'], experiments['timeSearch'],"Time")

# Create the boxplot for singele goals
long_df = experiments.melt(
     id_vars=['track', 'goal'], 
     value_vars=['timeOriginal', 'timeSearch'], 
     var_name='Methodology', 
     value_name='time'
 )

# Replace methodology names for better readability
long_df['Methodology'] = long_df['Methodology'].replace({
     'timeOriginal': 'Original',
     'timeSearch': 'Search'
 })

# Create the boxplot
plt.figure(figsize=(3.5, 2.3))
sns.boxplot(
    data=long_df, 
    x='goal', 
    y='time', 
    hue='Methodology', 
    palette='Set2',
    width=0.8
)

# Customize the plot
plt.xlabel('Goal', fontsize=8)
plt.ylabel('Time [s]', fontsize=8)
plt.legend(title='Methodology', fontsize=8)
plt.xticks(fontsize=8)
plt.yticks(fontsize=8)
plt.tight_layout()
plt.savefig('images/RQ2_B.pdf', dpi=300)
plt.show()

# Compute the average number of compliant IPMs per goal
original_goals = experiments.groupby('goal')['timeOriginal'].mean()
search_goals = experiments.groupby('goal')['timeSearch'].mean()
print('Original:', original_goals)
print('Search:', search_goals)

# Wilcoxon signed rank test for goals
test(experiments[experiments['goal'] == 'SOLVABILITY']['timeOriginal'], experiments[experiments['goal'] == 'SOLVABILITY']['timeSearch'],"Solvable")
test(experiments[experiments['goal'] == 'TESTRATIO']['timeOriginal'], experiments[experiments['goal'] == 'TESTRATIO']['timeSearch'],"Test Ratio")
test(experiments[experiments['goal'] == 'TUPLERATIO']['timeOriginal'], experiments[experiments['goal'] == 'TUPLERATIO']['timeSearch'],"Tuple Ratio")

# Create the boxplot for singele goals
long_df = experiments[experiments['goal'] == 'TUPLERATIO'].melt(
     id_vars=['track', 'goal'], 
     value_vars=['nCompliantOriginal', 'nCompliantSearch'], 
     var_name='Methodology', 
     value_name='time'
 )

# Replace methodology names for better readability
long_df['Methodology'] = long_df['Methodology'].replace({
     'nCompliantOriginal': 'Original',
     'nCompliantSearch': 'Search'
 })

# Create the boxplot
plt.figure(figsize=(3.5, 2.3))
sns.boxplot(
    data=long_df, 
    x='Methodology', 
    y='time', 
    palette='Set2',
    width=0.4
)

# Customize the plot
plt.xlabel('Goal', fontsize=8)
plt.ylabel('# Compliant IPMs', fontsize=8)
plt.xticks(fontsize=8)
plt.yticks(fontsize=8)
plt.tight_layout()
plt.savefig('images/RQ2_C.pdf', dpi=300)
plt.show()

original_goals = experiments.groupby('goal')['nCompliantOriginal'].mean()
search_goals = experiments.groupby('goal')['nCompliantSearch'].mean()
print('Original:', original_goals)
print('Search:', search_goals)