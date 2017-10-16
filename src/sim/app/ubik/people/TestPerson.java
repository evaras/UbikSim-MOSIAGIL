/**
/* * UbikSim2 has been developed by:
 * 
 * Juan A. BotÃ­a , juanbot[at] um.es
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
 *

package sim.app.ubik.people;

import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.AutomatonTestPerson;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;

public class TestPerson extends Person {
	


	public TestPerson(int floor, HomePieceOfFurniture w, Ubik ubik) {
		super(floor, w, ubik);

	}
	
	
	public void step(SimState state) {
            super.step(state);
            if(getKeyControlPerson() != null)   // If the agent is being controlled using keyboard, 
                return;                         // don't continue the step execution.
	    if(automaton==null) automaton = new AutomatonTestPerson(this);
            automaton.nextState(state);
	}

	
}*/
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


package sim.app.ubik.people;

import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import sim.app.ubik.building.connectionSpace.Door;
import sim.app.ubik.building.rooms.Room;


import sim.engine.SimState;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;


/**
 * TestPerson is an example of agent in UbikSim representing a person. It delegates to an Automaton called Automaton person.
 * @author Emilio Serrano, Ph.d.; eserrano@gsi.dit.upm.es
 */

public class TestPerson  extends Person  {
   /**
    * The initial position of the person just in case it has been added out of the editor (see start method of EscapeSim).
    */
   protected Room initialPosition=null;   
   /**
    * Boolean indicating if the person has been close to fire. 
    */
   protected boolean hasBeenCloseToFire;
 
   
    public TestPerson(int floor, HomePieceOfFurniture w, Ubik ubik) { 
      
        super(floor, w, ubik);        
        this.speed=0.5;                    

    }
    /**
     * MASON calls this method for each person. Each person creates the automaton, if it is null yet, and calls the method nextState of the automaton
     * with its behaviour.
     * @param state 
     */
    @Override
    public void step(SimState state) {
        //calculate the initial position of the person
        loadInitialPosition();                                
        super.step(state);
        if(keyControlPerson != null) {
            return;
        }
        if(automaton==null) {
             automaton = new sim.app.ubik.behaviors.escape.AutomatonTestPerson(this);            
        }
        automaton.nextState(state);
    }


    public String toString(){
        return name;
    }

   public Room getInitialPosition() {
        return initialPosition;
    }

   
   /**
    * Calculate the initial position of the person
    */
    private void loadInitialPosition() {
          if(initialPosition==null) {
            SpaceArea sa = (SpaceArea) ubik.getBuilding().getFloor(0).getSpaceAreaHandler().getSpaceArea(position.x, position.y);
            if(sa instanceof Room) {
                initialPosition = (Room) sa;
            } else if(sa instanceof Door) {
                Door door = (Door) sa;
                Int2D point = door.getAccessPoints()[0];
                initialPosition = (Room) ubik.getBuilding().getFloor(0).getSpaceAreaHandler().getSpaceArea(point.x,point.y);
            }
        }
    }

    public boolean hasBeenCloseToFire() {
        return hasBeenCloseToFire;
    }

    /**
     * This method is used to indicate that the person has been close to fire
     * @param b 
     */
     public void setCloseToFire(boolean b) {
         hasBeenCloseToFire=b;
   }
}