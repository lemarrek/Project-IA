package fr.polytech.mnia;

import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;

public abstract class Runner {
    protected MyProb animator = MyProb.INJECTOR.getInstance(MyProb.class);
    protected State initial ; // état initial
    protected State state ;   // ce champ représente l'état courant

    public Runner(String filePath){
        try {
            animator.load(filePath) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.initial = animator.getStateSpace().getRoot() ;        
    }

    public void initialise(){
        Transition setup = initial.findTransition(Transition.SETUP_CONSTANTS_NAME);
        if (setup != null) {
            initial = setup.getDestination();
        }

        Transition initialisation = initial.findTransition(Transition.INITIALISE_MACHINE_NAME);
        if (initialisation != null) {
            initial = initialisation.getDestination();
        }

        this.state = initial.exploreIfNeeded() ;
    }

    public State getState(){
        return this.state ;
    }

    public State getInitialState(){
        return this.initial ;
    }

    public StateSpace getStateSpace(){
        return this.animator.getStateSpace() ;
    }

    /*
     * Cette méthode affiche la source et la cible d'une
     * transition sans explorer la cible.
     */
    public void showTransition(Transition t){
        System.out.println("\nTransition : " + t.getId() + " - " + t.getName() + "[" + t.getParameterPredicate() + "]");
        System.out.println("\nSource : ") ; animator.printState(t.getSource()) ;
        System.out.println("\nDestination : ") ; animator.printState(t.getDestination()) ;
    }

    public abstract void execSequence() throws Exception ;
}
