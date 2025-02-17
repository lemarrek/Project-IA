package fr.polytech.mnia;

/*
 * Cette classe illustre l'exécution de SimpleRL.mch
 */
public class SimpleRunner extends Runner{
    /*
     * Le constructeur lance ProB sur la machine SimpleRL.mch
     * et initialise la machine
     */
    public SimpleRunner() throws Exception{
        super("/Simple/SimpleRL.mch") ;
        this.initialise(); ; 
    } 

    /*
     * La méthode execSequence donne un exemple d'interaction avec l'animateur étape
     * par étape. A chaque étape on affiche l'état et les transitions 
     * déclenchables dans cet état.
     */
    public void execSequence() throws Exception {        
        // Here we start the animation
        this.state = state.perform("choose", "film = A").explore() ;
        animator.printState(state) ;
        animator.printActions(state.getOutTransitions()) ;

        state = state.perform("choose", "film = B").explore() ;
        animator.printState(state) ;
        animator.printActions(state.getOutTransitions()) ;

        state = state.perform("choose", "film = C").explore() ;
        animator.printState(state) ;
        animator.printActions(state.getOutTransitions()) ;

        state = state.perform("choose", "film = A").explore() ;
        animator.printState(state) ;
        animator.printActions(state.getOutTransitions()) ;

        // ici on explore la transition à l'indice 2
        this.showTransition(state.getOutTransitions().get(2));
    }

    
}
