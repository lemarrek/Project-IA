package fr.polytech.mnia;

import java.util.List;

import de.prob.statespace.State;
import de.prob.statespace.Transition;

public class Evironnement {
    private State state ; 

    public Evironnement(Runner runner){
        this.state = runner.state ;
    }
    public void runAction(Transition t){
        state = t.getDestination().explore() ;
    }

    public List<Transition> getActions(){
        return this.state.getOutTransitions() ;
    }

    public State getState(){
        return this.state ;
    }
}
