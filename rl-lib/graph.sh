#!/bin/bash

# Exécuter le script Python pour Epsilon Greedy en arrière-plan
python3 Egreedy.py &

# Exécuter le script Python pour Bandit Gradient en arrière-plan
python3 Bgradient.py &

# Attendre que les deux processus se terminent
wait

