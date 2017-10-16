/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.ubik.people;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.behaviors.pathfinderDemos.Pathfinder;
import sim.app.ubik.behaviors.pathfinderDemos.PathfinderThread;
import sim.app.ubik.building.rooms.Room;
import sim.engine.SimState;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;

/**
 * Worker person example: uses the pathfinding (as a thread) to reach a random exit (assumed rooms with names
 * "Exit1" to "Exit4"). This goal can be changed online. You can use it in any environment
 * exits named as described, or change the exits name at this class according to your environment. 
 * Moreover, the environment has to include an object Worker. It has been tested with mapExample.ubiksim.
 * Agents get out after reaching a goal
 *
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class Worker extends  Person{
    private static final Logger LOG = Logger.getLogger(Worker.class.getName());
    private static String globalGoal=null; //goal for all agents     
    private static String exits[] = {"Exit1", "Exit2", "Exit3"}; //list of poosible goals
    private int numberOfGoalsToEnd = 5;
    private List<Int2D> goals;
    private Int2D currentGoal=null;
    private Pathfinder pf;      
    private String localGoal=null;//local goal if no global is fixed
    private boolean buscaSalida=false;



    public Worker(int floor, HomePieceOfFurniture person3DModel, Ubik ubik) {
        super(floor, person3DModel, ubik);
       
    }

    public void step(SimState state) {
        super.step(state);
        if (pf==null) {
        	pf = new PathfinderThread(this);
        }
        generateGoals(numberOfGoalsToEnd);//Generamos los objetivos
        
        if (goals.isEmpty()) {//if no quedan objetivos genera nuevos       
            generateGoals(numberOfGoalsToEnd);
        }
        
        if ((currentGoal == null || pf.isInGoal()) && !ubik.getFireActivity()){//if no current goal or it is in a goal, remove current goal and replace it
            goals.remove(currentGoal);
            currentGoal=null;
            if (!goals.isEmpty()) {
                currentGoal = goals.get(0);
                pf.setGoalAndGeneratePath(currentGoal);
            }
            else{this.setColor(Color.BLUE);}//blue to say that agent has accomplished all goals
        }
        else if(ubik.getFireActivity()&&!buscaSalida) {
        	goals.clear();
            currentGoal=null;
            this.localGoal=exits[state.random.nextInt(exits.length)];//random exit            
            goals.add(PositionTools.getRoom(this,localGoal).getCenter());
            currentGoal = goals.get(0);
            pf.setGoalAndGeneratePath(currentGoal);
            /*if(pf.isInGoal()){
 	           this.stop();//stop agent and make it get out of the simulation       
 	           PositionTools.getOutOfSpace(this);
 	           LOG.info(name + " has leave the building using " + localGoal);
 	           return;
 	        }*/
            buscaSalida=true;
        }
        else if(pf.isInGoal() && buscaSalida) {
        	 this.stop();//stop agent and make it get out of the simulation       
	           PositionTools.getOutOfSpace(this);
	           LOG.info(name + " has leave the building using " + localGoal);
	           return;
        }
        else{ 
            pf.step(state);//take steps to the goal (step can regenerate paths) 
        }
 
    }

    public static void setGlobalGoal(String globalGoal) {
        Worker.globalGoal = globalGoal;
        
    }
    
    private void generateGoals(int numberOfGoalsToEnd) {
        goals = new ArrayList();
        for (int i = 0; i < numberOfGoalsToEnd; i++) {
            goals.add(PositionTools.getRandomPositionInRandomRoom(this));
        }    
    }


  
}
