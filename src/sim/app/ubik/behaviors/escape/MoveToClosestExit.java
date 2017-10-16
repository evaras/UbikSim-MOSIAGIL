/*
* 
* 
* This file is part of EscapeSim. EscapeSim is a UbikSim library. 
* 
* EscapeSim has been developed by members of the research Group on 
* Intelligent Systems [GSI] (Grupo de Sistemas Inteligentes), 
* acknowledged group by the  Technical University of Madrid [UPM] 
* (Universidad Politécnica de Madrid) 
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


package sim.app.ubik.behaviors.escape;

import java.util.ArrayList;
import sim.app.ubik.behaviors.Automaton;
import sim.app.ubik.behaviors.Move;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.people.Person;
import sim.app.ubik.people.TestPerson;
import sim.engine.SimState;


public class MoveToClosestExit extends Automaton {
    /**
     * Stairs in the environment example hotel.ubiksim
     */
    protected static final String STAIRS[] = {"exit1","exit2","exit3"};
    /**
     * Person whose behaviour is modelled in this automaton
     */
    protected TestPerson p =  (TestPerson) personImplementingAutomaton; 
    
    /**
     * Constructor
     * @param personImplementingAutomaton Person whose behaviour is modelled in this automaton
     * @param priority Priority of the behaviour to select it among others 
     * @param duration Duration given to the behaviour if it does not finished before (isFinished()) or if it does not run out of transitions
     * @param name Name given to the behaviour
     */
    public MoveToClosestExit(Person personImplementingAutomaton, int priority, int duration, String name){
        super(personImplementingAutomaton,priority, duration, name);
    }
    /**
     * Default state, given if there is no transitions in the list of pending transitions. If it returns null and there are not states in the list, 
     * this automaton finishes and the control is given to the automaton father
     * @param ss
     * @return 
     */
    @Override
    public Automaton getDefaultState(SimState ss) {
      return null;    
    }
/**
 * Method called in each step to include new transions in list of pending transitions. Here,  Move and Disappear are inserted if they have not before.
 * @param ss
 * @return 
 */
    @Override
    public ArrayList<Automaton> createNewTransitions(SimState ss) {
        ArrayList<Automaton> r=null;
      if(!this.isTransitionPlanned("goToExit")){
        r=new ArrayList<Automaton>();            
        Room exit= PositionTools.closestRoom(p, STAIRS);
        r.add(new Move(p,10,-1,"goToExit",exit));
        r.add(new Disappear(p, " escaped ", p.getName() + " escaped using " + exit.getName()));     
      }
      
      return r;
    }

}
