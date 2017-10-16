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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import sim.app.ubik.utils.GenericLogger;


public class EscapeSimBatch {
    /**
     * Number of experiments for a configuration of parameters
     */
    static int experimentsPerParameters = 3;
    /**
     * Time for each experiment, EscapeMonitorAgent will end simulation at this step
     */
    static int timeForExperiment = 1000;
    /**
     * Extra heading added for each parameters configuration in the data logged by EscapeMonitorAgent
     */
    static ArrayList<String> extraHeadingsPerParameter;
    /**
     * name with output
     */
    static String fileName;




 public static void main(String[] args) throws IOException {
     //name of the batch file
      String date = (new Date()).toString().replace(':', '.');
      fileName = "Batchoutput " + date;          
      ArrayList<GenericLogger> r = experimentsForDifferentParameters();        
        printInFile(r);
        System.exit(0);

}
 
 
 /**
  * Experiments for different parameters of the configuration, example: number of agents
  * @return
  * @throws IOException 
  */
   private static ArrayList<GenericLogger> experimentsForDifferentParameters() throws IOException {
        ArrayList<GenericLogger> r = new ArrayList<GenericLogger>();
        extraHeadingsPerParameter = new ArrayList<String>();
        r.addAll(batchOfExperiments());     
        /*if needed, add a loop with an interation for parameter,  change the simulation parameters,
         * and add an extra heading per configuration to distinguish the different batch in the output file
         */
        extraHeadingsPerParameter.add("");//no extra heading for mean
        extraHeadingsPerParameter.add("");//no extra heading for deviation
        deleteTempFiles();

        return r;
   }

 
   
      /**
     * A batch of experiments.
     * Mean and deviation of experiments are registered in the list of result
     *
     * @return generic loggers with the results registered (mean and deviation)
     */
    private static ArrayList<GenericLogger> batchOfExperiments() {
        ArrayList<GenericLogger> listOfResults = new ArrayList<GenericLogger>();
        for (int i = 0; i < experimentsPerParameters; i++) {
            GenericLogger gl1 = oneExperiment(i * 1000);//seed shoud be equal for different parameters
            listOfResults.add(gl1);  
        }
        ArrayList<GenericLogger> r = new ArrayList<GenericLogger>();
        r.add(GenericLogger.getMean(listOfResults));
        r.add(GenericLogger.getStandardDeviation(listOfResults));
        return r;

    }
    
    
     /**
     * A simple experiment , code based on the main method of EscapeSim
     * @param seed
     * @return
     */
    public static GenericLogger oneExperiment(int seed) {
 
        EscapeSim state = new EscapeSim(seed,timeForExperiment );
        state.start();
        do{
                if (!state.schedule.step(state)) break;
        }while(state.schedule.getSteps() < timeForExperiment*2);//the EscapeMonitorAgent will finish before
        state.finish();             
        return state.ema.getGenericLogger();            
    }

  
 

 
 
/**
 * Print results from the generic logger of each execution assuming that mean and deviation has been logged
 * @param r
 * @throws IOException 
 */
    private static void printInFile(ArrayList<GenericLogger> r) throws IOException {

    
                
        PrintWriter w1 = new PrintWriter(new BufferedWriter(new FileWriter(fileName + " mean.txt", false)));
        PrintWriter w2 = new PrintWriter(new BufferedWriter(new FileWriter(fileName + " sd.txt", false)));
        
        
        //headings
         w1.print("step\t");
           w2.print("step\t");
            for (int i = 0; i < r.size(); i++) {
                if (i % 2 == 0) {//mean in pair positions
                  w1.print(r.get(i).getHeadings(null,extraHeadingsPerParameter.get(i)));  
                }
                else{
                   w2.print(r.get(i).getHeadings(null,extraHeadingsPerParameter.get(i)));  
                }
                              
            }
            w1.println();
            w2.println();
        
        //data row to row
        for (int step = 0; step < timeForExperiment; step++) {
            w1.print(step+1 + "\t");
            w2.print(step+1 + "\t");
            for (int i = 0; i < r.size(); i++) {
                if (i % 2 == 0) {//mean in pair positions
                  w1.print(r.get(i).toString(step));  
                } else {//deviation in even positions
                  w2.print(r.get(i).toString(step));  
                }
            }
            w1.println();
            w2.println();
        }
        
        w1.close();
        w2.close();
    }
    
    
    /**
     *  WARNING!!! without this, if this class is stopped before finishing, the eteck folder in the user account will store temporal files taking a considerable amount of space
     *  change it to put your user folder
     * @throws IOException 
     */
    private static void deleteTempFiles() throws IOException {
        File f = new File("C:\\Users\\esfupm\\eTeks");         
        FileUtils.deleteDirectory(f);                   
    }


}


