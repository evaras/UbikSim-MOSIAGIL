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
import sim.app.ubik.behaviors.DoNothing;
import sim.app.ubik.behaviors.Move;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.people.Person;
import sim.app.ubik.people.TestPerson;
import sim.engine.SimState;
import ubiksimdist.EscapeSim;



public class AutomatonTestPerson extends Automaton {

    /**
     * List of stairs in hotel.ubiksim
     */
    protected static final String STAIRS[] = {"exit1","exit2","exit3"};
      /**
     * Person whose behaviour is modelled in this automaton
     */
    protected TestPerson p =  (TestPerson) personImplementingAutomaton; 
    /**
     * Object with the simulation
     */
    protected EscapeSim sim= (EscapeSim) p.getUbik();

    

    /**
     * Constructor. Since it is the main automaton, no priority is needed (there are not other automata at the same level)
     * @param personImplementingAutomaton 
     */
   public AutomatonTestPerson(Person personImplementingAutomaton) {
        super(personImplementingAutomaton);
    }
   
   /**
    * Default behaviour if pending transitions are empty: do nothing one step.
    * @param simState
    * @return 
    */
    @Override
    public Automaton getDefaultState(SimState simState) {
        return new DoNothing(p,0,1,"doNothing");
    }
     
      /**
       * This fills the pending transition list.
       * If the agent is not going to exit, plan to go to the closest stairs (priority of 10) and plan to disappear
       * 
       * Besides, since this method is called in each step, if the person is close to fire, the counter in the monitor agent is incremented
       * @param simState
       * @return 
       */
    @Override
    public ArrayList<Automaton> createNewTransitions(SimState simState) {
      ArrayList<Automaton> r=null;
      if(!this.isTransitionPlanned("goToExit")){
        r=new ArrayList<Automaton>();            
        Room exit= PositionTools.closestRoom(p, STAIRS);
        r.add(new Move(p,10,-1,"goToExit",exit));
        //r.add(new MoveToClosestExit(p,10,-1,"goToExit"));
        r.add(new Disappear(p, " escaped ", p.getName() + " escaped using " + exit.getName()));     
      }
      
      
      if(!p.hasBeenCloseToFire()){
          if (sim.getFire().tauchingFire(personImplementingAutomaton)){
              p.setCloseToFire(true);
              sim.getMonitorAgent().peopleCloseToFire++;
          }
      }
      
      return r;
    }

 
    


    
  
}
