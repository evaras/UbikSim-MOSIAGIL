/*
 * UbikSim2 has been developed by:
 * 
 * Juan A. Botía , juanbot[at] um.es
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
package sim.app.ubik.utils;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.util.Int2D;

/**
 * Loads the Ubik class parameters for simulating a scenario. Once created the
 * class, use get methods to get the properties values.
 *
 */
public class Configuration {
    public static String MODE = "mode";
    public static String CELL_SIZE = "cellSize";
    public static String FLOORS = "floors";
    public static String SEED = "seed";
    public static String IP_OCP = "ipOCP";
    public static String OCP = "ocp";
    public static String mobileIP = "mobileIP";
    public static String mobilePort = "mobilePort";
    public static String CAMERA_MODE = "cameraMode";
    public static String KEYBOARD_CONTROLED = "keyboardControlled";
    public static String INITIAL_DATE = "initialDate";
    private static String CIPB_CONFIGFILE = "config.props";
    private static String XFIRE = "xfire";
    private static String YFIRE = "yfire"; 		
    private static String SIZEFIRE = "sizefire";
    private static String SPEEDFIRE = "speedfire";
    private static String POSPEOPLE = "pospeople";
    private static String DELAYFIRE = "delayfire";
    protected Properties properties;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss");
    private File file;

    /**
     * Loads the properties from the default file config.props
     */
    public Configuration() {
        this(CIPB_CONFIGFILE);
    }

    /**
     * Loads the properties from the given file path.
     *
     * @param filePath
     */
    public Configuration(String filePath) {
        try {
            properties = new Properties();
            // Para mostrar de donde leemos el fichero
            file = new File(filePath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();
            } else {
                System.err.println("Configuration: The file " + file.getAbsolutePath() + " doesn't exist.");
            }
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    /**
     * obtiene una propiedad de la configuracion
     *
     * @param key clave
     * @return valor o devuelve null si no se encuentra la clave
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    // No devuelve nulos
    public String getProperty2(String key) {
        String s = properties.getProperty(key);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Save properties in the file where they were loaded.
     */
    public void saveChanges() {
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            properties.store(os, "UbikSim configuration file");
            os.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean containsProperty(String key) {
        return properties.contains(key);
    }

    public Set<String> getPropertyNames() {
        return properties.stringPropertyNames();
    }

    public int getCellSize() {
        int result = 10;
        if (getProperty(Configuration.CELL_SIZE) != null) {
            result = Integer.parseInt(getProperty(Configuration.CELL_SIZE));
        }
        return result;
    }
    
    public void setCellSize(int cellSize) {
        properties.setProperty(CELL_SIZE, String.valueOf(cellSize));
    }

    public long getSeed() {
        long seed = 0;
        if (getProperty(Configuration.SEED) != null) {
            if (getProperty(Configuration.SEED).equalsIgnoreCase("random")) {
                seed = System.currentTimeMillis();
            } else {
                seed = Integer.parseInt(getProperty(Configuration.SEED));

            }
        }
        return seed;
    }

    public void setSeed(long seed) {
        properties.setProperty(SEED, String.valueOf(seed));
    }
    
    public boolean isOCP() {
        boolean useOCP = false;
        if (getProperty(Configuration.OCP) != null) {
            String ocp = getProperty(Configuration.OCP);
            if (ocp.equalsIgnoreCase("on") || ocp.equalsIgnoreCase("yes")) {
                useOCP = true;
            }
        }
        return useOCP;
    }

    public String getIpOCP() {
        String ipOCP = "127.0.0.1";
        if (getProperty(Configuration.IP_OCP) != null) {
            ipOCP = getProperty(Configuration.IP_OCP);
        }
        return ipOCP;
    }

    public void setIpOCP(String ip) {
        properties.setProperty(Configuration.IP_OCP, ip);
    }
    
    public String getPathScenario() {
        if (getProperty(Configuration.FLOORS) != null) {
            String allNames = getProperty(Configuration.FLOORS);
            String[] names = allNames.split(",");
            for (String n : names) {
                return n;
            }
        }
        return "";
    }
    public int getXFire() {
       if (getProperty(Configuration.XFIRE) != null) {
           String xfire = getProperty(Configuration.XFIRE);
           int x = Integer.parseInt(xfire);
           return x;
       }
        return 0;
    }
    
    public int getDelayFire() {
        if (getProperty(Configuration.DELAYFIRE) != null) {
            String delay = getProperty(Configuration.DELAYFIRE);
            int x = Integer.parseInt(delay);
            return x;
        }
         return 0;
     }
    
    public int getYFire() {
        if (getProperty(Configuration.YFIRE) != null) {
            String yfire = getProperty(Configuration.YFIRE);
            int y = Integer.parseInt(yfire);
            return y;
        }
         return 0;
     }
    
    public int getSizeFire() {
        if (getProperty(Configuration.SIZEFIRE) != null) {
            String sizefire = getProperty(Configuration.SIZEFIRE);
            int size = Integer.parseInt(sizefire);
            return size;
        }
         return 0;
     }
    
    public int getSpeedFire() {
        if (getProperty(Configuration.SPEEDFIRE) != null) {
            String speedfire = getProperty(Configuration.SPEEDFIRE);
            int speed = Integer.parseInt(speedfire);
            return speed;
        }
         return 0;
     }
    public List<Int2D> getPosPeople(){
    	
    	List<Int2D> posiciones = new ArrayList<Int2D>();
    	if (getProperty(Configuration.POSPEOPLE) != null) {
	    	String pos=getProperty(Configuration.POSPEOPLE);
	    	String[] parts = pos.split("/");
	    	for (int i=0; i<parts.length; i++) {
	    		try {
		    		String[] aux = parts[i].split(",");
		    		int x= Integer.parseInt(aux[0]);
		    		int y= Integer.parseInt(aux[1]);
		    		Int2D position = new Int2D(x,y);
		    		posiciones.add(position);
	    		}catch(Exception e) {
	    			
	    		}
	    	}
    	}
    	return posiciones;
    }

    public void setPathScenario(String path) {
        properties.setProperty(Configuration.FLOORS, path);
    }
    
    public Date getInitialDate() {
        Date date = new Date();
        if (getProperty(Configuration.FLOORS) != null) {
            try {
                date = dateFormat.parse(getProperty(Configuration.INITIAL_DATE));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }
    
    public void setInitialDate(Date date) {
        properties.setProperty(Configuration.INITIAL_DATE, date.toString());
    }
}
