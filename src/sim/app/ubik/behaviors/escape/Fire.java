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

import java.util.List;
import javax.swing.JOptionPane;

import ocp.util.Configuration;
import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.people.Person;
import sim.app.ubik.representation.Representation;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Int2D;
import sim.util.MutableInt2D;
import ubik3d.model.HomePieceOfFurniture; 



public class Fire implements Steppable, Stoppable {
    protected Ubik ubik;
    protected int steps=0;
    protected Int2D initialPosition;
    protected HomePieceOfFurniture fireFurniture;
    protected int advanceOfFirePerStep=1;
    protected int startingSizeOfFire=1;
    protected int delay=0;
    protected boolean isActive = false;
    
    
    
    public Fire(Ubik ubik){
        this.ubik=ubik;
        delay=ubik.getDelayFire();
        insertInRandomPosition();
        register();
    }
/**
 * Method with the actions to be performed by fire in each step.
 * @param ss 
 */
    public void step(SimState ss) {
    	
    	if (ss.schedule.getSteps()<delay) {	
    	}
    	else if (ss.schedule.getSteps() == delay) {
    		isActive=true;
    		ubik.setFireActivity(isActive);
    	}
    	else {
    		spreadFire();
            steps++; 
    	}
        
    }
    
    /**
     * Method called to check if the person is touching fire
     * @param p
     * @return 
     */
    
    public boolean tauchingFire(Person p) {      
      return PositionTools.isNeighboring(p.getPosition().x, p.getPosition().y, initialPosition.x, initialPosition.y,steps*advanceOfFirePerStep + startingSizeOfFire );
      
    }
    

 /**
  * Register the fire in the schedule (to make the simulation call the step method in fire for each step).
  */
    
    public void register() {        
        ubik.schedule.scheduleRepeating(this, 1);
    }
     
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
/**
 * Calculate a random position in the map for the fire.
 */
    private void insertInRandomPosition() {
        List<Person> people = ubik.getBuilding().getFloor(0).getPersonHandler().getPersons();
        if (ubik.getFirePos()==null) {
        	initialPosition = PositionTools.getRandomPositionInRandomRoom(people.get(0));//getting a random position for the fire                
        }
        else {
        	initialPosition = ubik.getFirePos();
        }
    }
    
    private void setInitialSize() {
        //List<Person> people = ubik.getBuilding().getFloor(0).getPersonHandler().getPersons();
        if (ubik.getFireSize() == 0) {
        	startingSizeOfFire = 1;
        }
        else {
        	startingSizeOfFire = ubik.getFireSize();
        }
    }
    
    private void setAdvanceOfFire() {
        //List<Person> people = ubik.getBuilding().getFloor(0).getPersonHandler().getPersons();
        if (ubik.getFireSpeed() == 0){
        	advanceOfFirePerStep = 1;
        }
        else {
        	advanceOfFirePerStep = ubik.getFireSpeed();
        }
    }
    
    /**
     * Insert a fire 3d object in the display. It has to be called if there is GUI
     * Look out the path of the file with the 3D object and the texture
     */
    public void insertInDisplay(){
          try{
        	setAdvanceOfFire();
        	setInitialSize();
            //object 3d imported, it must be in the same folder than the second parameter (inside src and build)
            fireFurniture = Representation.createModel("Fire", Fire.class, "Fire.dae");//includes a 3D object in the display
            MutableInt2D point3D = PositionTools.pointMasonToPoint3D(ubik.getCellSize(),initialPosition.x, initialPosition.y);//the positions in the 3d display are not the same than in the MASON grid
            fireFurniture.setHeight(100);//changing the size of the flame            
            fireFurniture.setWidth(this.startingSizeOfFire*ubik.getCellSize());
            fireFurniture.setDepth(this.startingSizeOfFire*ubik.getCellSize());
            fireFurniture.setX(point3D.x);
            fireFurniture.setY(point3D.y);
            ubik.getBuilding().getFloor(0).getHome().addPieceOfFurniture(fireFurniture);
            JOptionPane.showMessageDialog(null, "There is a fire in position " + initialPosition.x + "," +  initialPosition.y);
        }
        catch(Exception ex){            
            ex.printStackTrace();//"Exception inserting the fire in the display"
        }    
    }
    
    
    private void spreadFire(){      
            if(fireFurniture!=null){
                ubik.getBuilding().getFloor(0).getHome().deletePieceOfFurniture(fireFurniture);
            
            fireFurniture.setWidth(fireFurniture.getWidth()+advanceOfFirePerStep*ubik.getCellSize());
            fireFurniture.setDepth(fireFurniture.getDepth()+advanceOfFirePerStep*ubik.getCellSize());
            ubik.getBuilding().getFloor(0).getHome().addPieceOfFurniture(fireFurniture);           
            }
    }
    
}
