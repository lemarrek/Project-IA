package fr.polytech.mnia;

import java.util.List;
import java.util.Random;

import de.prob.statespace.Transition;

/*
 * Cette classe illustre l'exécution de SimpleRL.mch
 */
public class SimpleRunner extends Runner{
    double epsilon = 0.2; // Exploration rate
    Evironnement env;
    List<Transition> choices;
    int iterations = 1000;
    

    
    /*
     * Le constructeur lance ProB sur la machine SimpleRL.mch
     * et initialise la machine
     */
    public SimpleRunner() throws Exception{
        super("/Simple/SimpleRL.mch") ;
        this.initialise();
        env = new Evironnement(this);
        choices = env.getActions();
        
    } 

    
    @Override
    public void execSequence() throws Exception {
        int choice;
        double reward;
        Transition t;

        Double[] results = new Double[choices.size()];
        int[] TimesChoosen = new int[choices.size()];
        Double[] totalReward = new Double[choices.size()];

        for(int i = 0; i<choices.size(); i++){
            results[i] = 0.0;
            TimesChoosen[i] = 0;
            totalReward[i] = 0.0;
        }

        for(int i = 0; i < iterations; i++){
            choice = choose(results);
            t = choices.get(choice);
            env.runAction(t);

            if(env.getState().getStateRep().equals("( res=OK )")){
                reward = 1.0;
            }
            else{
                reward = 0.0;
            }
            //showTransition(t);
            //animator.printState(env.getState());
            System.out.println("\nChoosen "+t.getParameterPredicate());        //////////
            System.out.println("Reward : "+reward);
            totalReward[choice] += reward;
            TimesChoosen[choice]++;
            results[choice] = totalReward[choice] / TimesChoosen[choice];
        }
			
        for (int i = 0; i < results.length; i++) {
            System.out.println("\nfor "+choices.get(i).getParameterPredicate());
            System.out.println("Choosen : "+TimesChoosen[i]+" times");
            System.out.println("Expected result : "+results[i]);
        }
        
    }

    public int choose(Double[] results){
        Random random = new Random();
        double alea = random.nextDouble();
        int choice;
        if(epsilon<alea) {
            choice = random.nextInt(choices.size());
        }
        else {
            choice = optimal(results);
        }
        return choice;
    }

    public int optimal(Double[] list){
        if(list.length == 0){
            return 0;
        }
        int index = 0;
        for(int i = 1 ; i < list.length ; i++){
            if(list[i-1]<list[i]){
                index = i;
            }
        }
        return index;
    }

    

    
}
