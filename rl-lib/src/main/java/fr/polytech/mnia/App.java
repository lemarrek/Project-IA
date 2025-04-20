package fr.polytech.mnia;

public class App 
{
    public static void main( String[] args ) throws Exception{           
        
        //SimpleRunner sr = new SimpleRunner() ;
        //sr.execSequence() ; 

        //SchedulerRunner sc = new SchedulerRunner() ;
        //sc.execSequence();

        TicTacToeRunner tr = new TicTacToeRunner() ;
        tr.execSequence();

        //Evironnement env = new Evironnement(new SimpleRunner()) ;

        //System.out.println(env.getState().eval("res"));
        //System.out.println(env.getActions().toString());

        System.exit(0);
    }    
}
