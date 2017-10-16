/*
 * UbikSim2 has been developed by:
 * 
 * Juan A. Bot√≠a , juanbot[at] um.es
 * Pablo Campillo, pablocampillo[at] um.es
 * Francisco Campuzano, fjcampuzano[at] um.es
 * Emilio Serrano, emilioserra [at] dit.upm.es
 * 
 * This file is part of UbikSimIDE.
 * 
 *     UbikSimIDE is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     UbikSimIDE is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with UbikSimIDE.  If not, see <http://www.gnu.org/licenses/>
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
 * Teacher person example: uses the pathfinding to reach 5 random positions
 * at the environment before finishing and get in red. The 2D display can be
 * used to fixed new positions to be reached. You can use it in any environment with an 
 * object teacher, but it has been tested with primeraPlantaUMU_DIIC.ubiksim.
 * Moreover, selecting teachers at the 2D view, new goals can be added to make the agent
 * go there.
 *
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class Teacher extends Person {

    private int numberOfGoalsToEnd = 5;
    private List<Int2D> goals;
    private Int2D currentGoal=null;
    private static final Logger LOG = Logger.getLogger(Teacher.class.getName());
    private Pathfinder pf;
    private boolean buscaSalida=false;
    private static String exits[] = {"Exit1", "Exit2", "Exit3"}; //list of poosible goals
    private String localGoal=null;//local goal if no global is fixed
    private List<String> roomList;
    private String newGoal;

    public Teacher(int floor, HomePieceOfFurniture person3DModel, Ubik ubik) {
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
             this.localGoal=this.getMaxExit();//random exit            
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

    
    /**
     * Generate random goals
     * @param numberOfGoalsToEnd 
     */
    
    private void generateGoals(int numberOfGoalsToEnd) {
        goals = new ArrayList();
        for (int i = 0; i < numberOfGoalsToEnd; i++) {
            goals.add(PositionTools.getRandomPositionInRandomRoom(this));
        }    
    }
    /**
     * Devuelve el nombre de la salida m·s cercana
     *
     */
    private String getMinExit(){
    	String exit="";
    	String min="";
    	int mindist = 0;
    	for (int i=0; i<exits.length; i++) {
    		exit = exits[i];
    		Int2D pos = PositionTools.getRoom(this,exit).getCenter();
    		int distance = PositionTools.getDistance(this.getPosition().x, this.getPosition().y, pos.x, pos.y);
    		if (distance <mindist || mindist==0) {
    			mindist=distance;
    			min = exits[i];    			
    		}
    	}
        return min;
    }
    /**
     * Devuelve el nombre de la salida m·s alejada del fuego
     *
     */
    private String getMaxExit(){
    	String exit="";
    	String max="";
    	int maxdist = 0;
    	Int2D pos = this.getUbik().getFirePos();
    	for (int i=0; i<exits.length; i++) {
    		exit = exits[i];
        	Int2D pos2 = PositionTools.getRoom(this,exit).getCenter();
    		int distance = PositionTools.getDistance(pos.x, pos.y, pos2.x , pos2.y );
    		if (distance >maxdist) {
    			maxdist=distance;
    			max = exits[i];    			
    		}
    	}
        return max;
    }
    /**
     * This allow checking a list of rooms selecting an agent in the 2D view.
     * See inspectors in MASON documentation
     
     * @return
     */
    public List<String> getRooms() {
        if (roomList == null) {
            this.roomList = new ArrayList<String>();
            for (Room r : PositionTools.getRooms(this)) {
                roomList.add(r.getName());
            }

        }
        return roomList;
    }

    /**
     * This allow adding a goal for an agent after selecting it in the 2D view,
     * See inspectors in MASON documentation
     *
     * @return
     */
    public void setNewGoal(String room) {
        if (!roomList.contains(room)) {
            return;
        }
        Int2D pos = PositionTools.getRandomPositionInRoom(this, PositionTools.getRoom(this, room));
        goals.add(pos);
        this.setColor(Color.MAGENTA);
        this.newGoal = room;
    }

    
    
    public String getNewGoal() {
        return newGoal;
    }
}
