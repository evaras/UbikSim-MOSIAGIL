/*
 * UbikSim2 has been developed by:
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
 */
package sim.app.ubik;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.ubik.behaviors.Automaton;

import sim.app.ubik.building.Building;
import sim.app.ubik.clock.InitialDate;

import sim.engine.MakesSimState;
import sim.engine.SimState;
import sim.util.Int2D;
import ubik3d.io.HomeFileRecorder;
import ubik3d.model.Home;
import ubik3d.model.HomePieceOfFurniture;
import ubik3d.model.RecorderException;
import sim.app.ubik.clock.UbikClock;
import sim.app.ubik.ocp.OCPProxyProducer;
import sim.app.ubik.people.Person;
import sim.app.ubik.people.PersonHandler;
import sim.app.ubik.utils.Configuration;

public class Ubik extends SimState {

	/*
	 * El espacio de trabajo de un trabajador serÃ¡n al menos los 8 que le rodean
	 * . luego el mÃ¡ximo de trabajadores por planta es relativo a estos valores
	 */
    private Configuration configuration;
    private static final Logger LOG = Logger.getLogger(Ubik.class.getName());
    
	private long seedFromFile;
	private int cellSize;
	private InitialDate initialDate;
	private Building building;
	private List<Home> homes;
	protected String pathScenario;

	private String ipOCP = null;
	private boolean useOCP = false;
	private double speed = 1.0;
	/**
	 * Variables del fuego por properties
	 */
	private int xFire;
	private int yFire;
	private int sizeFire;
	private int speedFire;
	private int delayFire;
	private boolean isFireActive = false;
	/**
	 * 
	 */
	protected List<Int2D> posPeople;

	private UbikClock clock; // Reloj para registrarse y que te avisa cuando se
								// alcanza el tiempo indicado
        
  

	public static void main(String[] args) {
		SimState.doLoop(new MakesSimState() {
			@Override
			public SimState newInstance(long seed, String[] args) {
				try {
					Ubik ubik = new Ubik(0);
					return ubik;
				} catch (Exception e) {
					throw new RuntimeException(
							"Exception occurred while trying to construct the simulation: "
									+ e);
				}
			}

			@Override
			public Class simulationClass() {
				return Ubik.class;
			}
		}, new String[0]);
		System.exit(0);
	}

        public Ubik(Configuration configuration) {
            super(configuration.getSeed());
            loadConfig(configuration);
       }
        
	public Ubik() {
		this(System.currentTimeMillis());
	}

	public Ubik(long seed) {
		super(seed);
		loadConfig(new Configuration());
	}

	private void loadConfig(Configuration configuration) {
		cellSize = configuration.getCellSize();
		seedFromFile = configuration.getSeed();
		initialDate = new InitialDate(configuration.getInitialDate());
		pathScenario = configuration.getPathScenario();
		ipOCP = configuration.getIpOCP();
		useOCP = configuration.isOCP();
		xFire = configuration.getXFire();
		yFire = configuration.getYFire();
		sizeFire = configuration.getSizeFire();
		speedFire = configuration.getSpeedFire();
		posPeople = configuration.getPosPeople();
		delayFire = configuration.getDelayFire();
	}
	
	/**
	 * Devuelve el valor de la posición inicial del fuego marcado en la hoja de configuración
	 * @return
	 */
	public Int2D getFirePos() {
		int x= xFire;
		int y = yFire;
		
		Int2D position = new Int2D(x,y);
		return position;
		
	}
	
	public boolean getFireActivity() {	
		return isFireActive;
	}
	
	public void setFireActivity(boolean activity) {
		this.isFireActive=activity;
	}
	
	public int getDelayFire() {
		
		return delayFire;
		
	}
	
	/**
	 * Devuelve el tamaño inicial del fuego marcado en la hoja de config
	 * @return
	 */
	public int getFireSize() {
		return sizeFire;
	}
	
	/**
	 * Devuelve el valor inicial de la velocidad de avance del fuego marcado en la hoja de configuración
	 * @return
	 */
	
	public int getFireSpeed() {
		return speedFire;
	}
	
	public List<Int2D> getPositionPeople() {
		return posPeople;
	}
        

	public Ubik(long seed, List<Home> homes, int cellSize, int mode) {
		this(seed);
		this.homes = homes;
		this.cellSize = cellSize;
	}

	/**
	 * Constructor Ubik para usar OCP
	 * 
	 * @param seed
	 * @param home
	 * @param floors
	 * @param bc
	 */
	public Ubik(long seed, List<Home> homes, String ipOCP, int cellSize,
			int mode, boolean useOCP) {
		this(seed, homes, cellSize, mode);
		// this(seed, homes, cellSize, 0);
		this.ipOCP = ipOCP;
		this.useOCP = useOCP;
	}

	@Override
	public void start() {
		LOG.config("Ubik.start()");
		LOG.info("ubik.seed = " + seed());
		super.start();
		init();
                Automaton.setEcho(false);
	}

	@Override
	public void finish() {
		LOG.config("Ubik.finish()");
		finish(true);
	}

	public void finish(boolean clearHome) {
		super.finish();
		clear(clearHome);
	}

	private void clear(boolean clearHome) {
		LOG.config("Ubik.clear(" + clearHome + ")");
		if (clearHome && building != null)
			building.clearHomes();
		if (homes != null)
			homes.clear();
		building = null;
		homes = null;
		System.gc();
	}

	public void init() {
		homes = new ArrayList<Home>();
		HomeFileRecorder h = new HomeFileRecorder();
		try {
			Home home = h.readHome(pathScenario);
			homes.add(home);
		} catch (RecorderException ex) {
                        System.err.println(   ex.toString());                 
			return;
		}

		clock = new UbikClock();
		clock.fixStoppable(schedule);
                clock.setDate(initialDate.getYear(), initialDate.getMonth(), initialDate.getDay(), initialDate.getHour(), initialDate.getMinute(), initialDate.getSecond());

		this.building = new Building(homes, this, cellSize);
		this.building.createEntities();

		// OSGi vs normal UBIK
		String sd = this.getClass().getClassLoader().toString();
		/*
		 * if (sd.contains("BundleClassLoader")) { building.clearOsgi(); }
		 */
		
		float floors = this.building.numberOfFloors();

		//Obtención de persona por Nombre:
		//Person cantante = this.building.getFloor(0).getPersonHandler().getPersonByName("cantante");

		//Creación de fuego:
		//Fire fire = new Fire(this);
		//fire.insertInDisplay();
		
		//Creación de personas en masa colocados de forma aleatoria:
		//this.building.getFloor(0).getPersonHandler().addPersons(100, true, cantante);
		//this.building.getFloor(0).getPersonHandler().addPersons(100, true, (HomePieceOfFurniture)null); 
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public Building getBuilding() {
		return this.building;
	}

	public UbikClock getClock() {
		return clock;
	}

	public List<Home> getHomes() {
		return homes;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	public OCPProxyProducer createOCPProxyProducer(String id) {
		if (useOCP) {
			if (ipOCP != null) {
				return new OCPProxyProducer(id, ipOCP);
			}
		}
		return null;
	}

	public String isIpOCP() {
		return ipOCP;
	}

	public void setIpOCP(String ip) {
		this.ipOCP = ip;
	}

	

	public InitialDate getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(InitialDate initialDate) {
		this.initialDate = initialDate;
	}

	public String getPathScenario() {
		return pathScenario;
	}

	public void setPathScenario(String pathScenario) {
		this.pathScenario = pathScenario;
	}

	public long getSeedFromFile() {
		return seedFromFile;
	}

	public void setSeedFromFile(long seedFromFile) {
		this.seedFromFile = seedFromFile;
		setSeed(seedFromFile);
	}

	public void launch0D(final SimState simState, String [] args) {
		SimState.doLoop(new MakesSimState() {
			@Override
			public SimState newInstance(long seed, String[] args) {
				try {
					return simState;
				} catch (Exception e) {
					throw new RuntimeException(
							"Exception occurred while trying to construct the simulation: "
									+ e);
				}
			}

			@Override
			public Class simulationClass() {
				return simState.getClass();
			}
		}, args);
	}
	
	public Ubik getUbik() {
		return this;
	}
}
