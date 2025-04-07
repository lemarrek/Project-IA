import pandas as pd
import matplotlib.pyplot as plt

# Charger les données depuis le fichier CSV pour Bandit Gradient
df_bgradient = pd.read_csv('bgradient_history.csv')

# Tracer les courbes des probabilités pour chaque action
plt.figure(figsize=(12, 6))
for column in df_bgradient.columns[1:]:
    plt.plot(df_bgradient['Iteration'], df_bgradient[column], label=column)

plt.xlabel('Iteration')
plt.ylabel('Probability')
plt.title('Evolution of Probabilities for Each Action (Bandit Gradient)')
plt.legend()
plt.grid(True)
plt.show()

