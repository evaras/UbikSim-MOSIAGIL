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

import sim.app.ubik.behaviors.SimpleState;
import sim.engine.SimState;

import sim.app.ubik.people.Person;


public class Disappear extends SimpleState {
/**
 * Message to print after disappearing
 */
    public String message=null;



    /**
     * Message to be printed
     * @param personImplementingAutomaton
     * @param name Name of the behaviour
     * @param message Message to print after disappearing
     */
    public Disappear(Person personImplementingAutomaton,  String name, String message){
       super(personImplementingAutomaton,0,-1, name);
       this.message=message;
    }
    
    /**
     * Stop agent
     *
     * @see Person
     */
    public void nextState(SimState state) {
        //if(ECHO) System.out.println(personImplementingAutomaton.getName() + ", " +  toString() + " automaton step ");
          if(!personImplementingAutomaton.isStopped()) {
             if(message!=null) System.out.println(message);
            personImplementingAutomaton.stop();             
        }        
    }



    /**
     * Ends if person has disappeared
     * 
     * @param state
     * @return
     */
    @Override
    public boolean isFinished(SimState state){    
    
        if(super.isFinished(state)){
            return true;
        }
        return personImplementingAutomaton.isStopped();
    }
}
