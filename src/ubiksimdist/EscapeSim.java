/*
* 
* 
* This file is part of EscapeSim. EscapeSim is a UbikSim library. 
* 
* EscapeSim has been developed by members of the research Group on 
* Intelligent Systems [GSI] (Grupo de Sistemas Inteligentes), 
* acknowledged group by the  Technical University of Madrid [UPM] 
* (Universidad PolitÃ©cnica de Madrid) 
* 
* Authors:
* Mercedes Garijo
* Geovanny Poveda
* Emilio Serrano
* 
* 
* Contact: 
* http://www.gsi.dit.upm.es/;
* 
* 
* 
* EscapeSim, as UbikSim, is free software: 
* you can redistribute it and/or modify it under the terms of the GNU 
* General Public License as published by the Free Software Foundation, 
* either version 3 of the License, or (at your option) any later version. 
*
* 
* EscapeSim is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with VoteSim. If not, see <http://www.gnu.org/licenses/>
 */

package ubiksimdist;

import annas.graph.DefaultArc;
import annas.graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.Configuration;

import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.Automaton;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.behaviors.escape.EscapeMonitorAgent;
import sim.app.ubik.behaviors.escape.Fire;
import sim.app.ubik.graph.Node;
import sim.app.ubik.people.Person;
import sim.app.ubik.people.PersonHandler;
import sim.app.ubik.people.Teacher;
import sim.app.ubik.people.Worker;
import sim.util.Int2D;
import sim.util.MutableInt2D;
import ubik3d.model.HomePieceOfFurniture;
import sim.app.ubik.furniture.Furniture;
 import sim.app.ubik.graph.*;

public class EscapeSim extends Ubik {

     static int maxTimeForExecution=1500;
     
     /**
      * Object with information about execution and, if needed,
      * to finish the execution
      */     
     EscapeMonitorAgent ema ;
     Fire fire;
    
    /**
     * Passing a random seed
     * @param seed 
     */
    public EscapeSim(long seed)   {
        super(seed);
        
    }
    
      /**
     * Passing a random seed and time to make EscapeMonitorAgent to finish simulation
     * This time must be less than maxTimeForExecution
     * @param seed 
     */
    public EscapeSim(long seed, int timeForSim)   {
        super(seed);
        EscapeMonitorAgent.setStepToStop(timeForSim);
        
    }
    

    /**
     * Using seed from config.pros file
     */
    
     public EscapeSim() {         
           super();
           setSeed(getSeedFromFile());         
    }
     
     /**
      * 
     * Adding things before running simulation.   
     * Method called after pressing pause (the building variables are instantiated) but before executing simulation.
 
      */
   public void start() {               
        super.start();      
        ema= new EscapeMonitorAgent(this); 
        fire= new Fire(this);
        Automaton.setEcho(false);
        
        //add more people
        PersonHandler ph=  getBuilding().getFloor(0).getPersonHandler();
        //ph.addPersons(5, true, ph.getPersons().get(0));
        //ph.addPersons(5, true, ph.getPersons().get(1));
        
        //Obtención del modelo de una persona a partir de una persona ya existente en la simulación
        HomePieceOfFurniture model1 = ph.getPersons().get(0).getPerson3DModel();
        Ubik ubik = super.getUbik();
        
        Person[] people = new Person[posPeople.size()];
        for (int i=0; i<people.length; i++) {
        	people[i]= (sim.app.ubik.people.Worker) ph.createPerson(0, new HomePieceOfFurniture(model1), ubik);
        	if(!people[i].setPosition(posPeople.get(0))){
        		people[i].stop();
        	}
        	posPeople.remove(0);
        	PositionTools.putInSpace(people[i], people[i].getPosition().x, people[i].getPosition().y);   
        }
        /*
        //Creación de nuevas personas en base al modelo anterior
        Worker worker = (sim.app.ubik.people.Worker) ph.createPerson(0, new HomePieceOfFurniture(model1), ubik);
        Worker worker1 = (sim.app.ubik.people.Worker) ph.createPerson(0, new HomePieceOfFurniture(model1), ubik);
        //Colocación de la persona en la simulación
        worker.setPosition(50, 50);
        ph.add(worker);
        boolean aux = PositionTools.putInSpace(worker, worker.getPosition().x, worker.getPosition().y);
         
        worker1.setPosition(80, 80);
        ph.add(worker1);
        boolean aux2 = PositionTools.putInSpace(worker, worker1.getPosition().x, worker1.getPosition().y);
		//PositionTools.getOutOfSpace(worker1);
		//ph.addPersons(10, true, worker);
		//ph.addPersonInRandomPosition(worker);
        //Worker worker = Worker(0,  ,super());
        */
		//this.getBuilding().getFloor(0).getPersonHandler().addPersons(100, true, (HomePieceOfFurniture)null); 
        //change their name
        ph.changeNameOfAgents("a");

   }
    
   
   /**
 * Default execution without GUI. It executed the simulation for maxTimeForExecution steps.
 * @param args 
 */
    public static void main(String []args) {
       
       EscapeSim state = new EscapeSim(System.currentTimeMillis());
       state.start();
        do{
                if (!state.schedule.step(state)) break;
        }while(state.schedule.getSteps() < maxTimeForExecution);//
        state.finish();     
      
     
    }
    
    /**
     * Get the Fire object from the simulation object
     * @return 
     */
        
    public Fire getFire(){
        return fire;
    }
    

    /**
     * Get the monitor agent (agent logging data) from the simulation object
     * @return 
     */
  public EscapeMonitorAgent getMonitorAgent(){
        return ema;
    }
    
    
  


}
