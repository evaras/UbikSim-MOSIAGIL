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


package ubiksimdist;


import sim.app.ubik.Ubik;
import sim.app.ubik.UbikSimWithUI;
import sim.display.Console;


public class EscapeSimWithGUI extends UbikSimWithUI {

   
    static EscapeSim escapesim;
    
    public EscapeSimWithGUI(Ubik ubik) {
		super(ubik);		
	}
    
    /**
     * Method called after pressing pause (the building variables are instantiated) but before executing simulation.
     * Any JFrame can be registered to be shown in the display menu
     */
    @Override
    public void start() {
        super.start();
        //myDisplay = new MyDiplay(this); 
        //controller.registerFrame(myDisplay);  
        escapesim.fire.insertInDisplay();
        
    }
    
    /**
     * Method to finish the simulation
     */
    @Override
    public void finish() {
        super.finish();        
        //controller.unregisterFrame(myDisplay);
    }


/**
 * Executing simulation with GUI, it delegates to EscapeSim, simulation without GUI
 * @param args 
 */
    public static void main(String []args) {
    	escapesim = new EscapeSim(System.currentTimeMillis());
        EscapeSimWithGUI vid = new EscapeSimWithGUI(escapesim);
        Console c = new Console(vid);	
        c.setIncrementSeedOnStop(false);
        c.setVisible(true);
    }
    
    
    
    
    

    
    
 
}
