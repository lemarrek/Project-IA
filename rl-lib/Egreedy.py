import pandas as pd
import matplotlib.pyplot as plt

# Charger les données depuis le fichier CSV pour Epsilon Greedy
df_egreedy = pd.read_csv('egreedy_history.csv')

# Tracer les courbes du nombre de fois où chaque action a été testée
plt.figure(figsize=(12, 6))
for column in df_egreedy.columns[1:]:
    plt.plot(df_egreedy['Iteration'], df_egreedy[column], label=column)

plt.xlabel('Iteration')
plt.ylabel('Times Chosen')
plt.title('Evolution of Times Each Action Was Chosen (Epsilon Greedy)')
plt.legend()
plt.grid(True)
plt.show()

