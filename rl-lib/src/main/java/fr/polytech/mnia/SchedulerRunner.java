package fr.polytech.mnia;

public class SchedulerRunner extends Runner {
    /*
     * Le constructeur lance ProB sur la machine scheduler_main.mch
     * ensuite initialise les constantes et les variables 
     */
    public SchedulerRunner(){
        super("/Scheduler/scheduler_main.mch") ;
        this.initialise();
    } 

    /*
     * La méthode exec donne un exemple d'interaction avec l'animateur étape
     * par étape. A chaque étape on affiche l'état et les transitions 
     * déclenchables dans cet état.
     */
    public void execSequence() throws Exception {        
        // Here we start the animation
        state = state.perform("start", "pp = process1").explore() ;
        state = state.perform("start", "pp = process2").explore() ;
        state = state.perform("start", "pp = process3").explore() ;
        state = state.perform("activate", "rr = process1").explore() ;
        state = state.perform("activate", "rr = process2").explore() ;
        state = state.perform("activate", "rr = process3").explore() ;
        state = state.perform("step").explore() ;        
        
        System.out.println("processus actifs : " + state.eval("active"));
        System.out.println("processus ready : " + state.eval("ready"));
        animator.printActions(state.getOutTransitions()) ;

        System.out.println("\nState if swap(process2)");
        System.out.println("processus actifs : " + state.getOutTransitions().get(0).getDestination().eval("active"));
        System.out.println("processus ready : " + state.getOutTransitions().get(0).getDestination().eval("ready"));

        System.out.println("\nState if swap(process3)");
        System.out.println("processus actifs : " + state.getOutTransitions().get(1).getDestination().eval("active"));
        System.out.println("processus ready : " + state.getOutTransitions().get(1).getDestination().eval("ready"));
    }
}
