import java.awt.Point;
import java.util.Random;
import java.lang.Math;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

class Agent extends GameObject {
/**
 * Agent - obiekt posiadający cele, zdolny
 * do podejmowania samodzielnych akcji.
 */

	protected Random rand;
	protected ConcurrentLinkedDeque<Agent> agents;
	protected int DIE_CHANCE;	// im większa liczba tym trudniej
	protected int BREED_CHANCE;

 	protected int strategy;
	protected boolean active;		// whatever
	protected int dir;				
	// w jakim kierunku jest zwrócony agent:
	// N - 0
	// E - 1
	// S - 2
	// W - 3
	protected boolean busy;			// czy agent jest zajęty
	protected boolean running;		// czy ucieka po walce
	protected LinkedList<Task> tasks;
	protected int fitness;
	protected int age;
	protected int idleness; 		// ilość tur bezczynności - jeśli zbyt długo się nie rusza, to umiera


	public boolean die, breed;
	
	Agent(int strategy, Point position, Scene scene, ConcurrentLinkedDeque<Agent> agents, int DIE_CHANCE, int BREED_CHANCE) {
		super(1, position, false, scene);
		this.strategy = strategy;
		this.agents = agents;
		this.DIE_CHANCE = DIE_CHANCE;
		this.BREED_CHANCE = BREED_CHANCE;
		active = false;
		busy = false;
		running = false;
		dir = 1;
		tasks = new LinkedList<Task>();
		fitness = 100;
		age = 0;
		idleness = 0;
		Point[] ar = new Point[1];
		ar[0] = new Point(0,0);
		this.setArea(ar);
		this.updateOccupies();
		die = false;
		breed = false;
		rand = new Random();
	}

	void setArea(Point[] area) {
		this.area = area;
	}

	void setBusy(boolean busy) {
		this.busy = busy;
	}

	boolean isBusy() {
		return this.busy;
	}

	boolean isRunning() {
		return this.running;
	}

	void setDir(int dir) {
		this.dir = dir;
	}

	int strategy() {
		return this.strategy;
	}

	int fitness() {
		return this.fitness;
	}

	void changeFitness(int n) {
		this.fitness += n;
	}

	void age() {
		this.age++;
		if( rand.nextInt(DIE_CHANCE) < age ) die = true;
		if( rand.nextInt(BREED_CHANCE) < fitness - 100 ) breed = true;
	}

	void checkIdleness() {
		if( idleness == 2 ) {
			// System.out.println("zaglodzilem sie ;/");
			die = true;
		}
	}

	void breed() {
		fitness = 100;
		agents.add(new Agent(strategy, (Point)position.clone(), scene, agents, DIE_CHANCE, BREED_CHANCE));
		breed = false;
	}

	void think() {
		
		if( tasks.size() != 0 ) {
			Task task = tasks.poll();
			if( task.type().equals("move") ) {
				if( move( Integer.parseInt(task.args()) ) ) {
					idleness = 0;
				} else {
					idleness++;
				}
				running = false;
			}
		} else {

			// wybieramy kierunek marszu
			int nextDir;
			int r = (int)Math.floor(rand.nextFloat()*100);	// losujemy liczbę od 0 do 999
			if( r < 10 ) 		nextDir = (dir + 2) % 4;
			else if( r < 60 ) 	nextDir = dir;
			else if( r < 80 ) 	nextDir = (dir + 1) % 4;
			else 				nextDir = (dir + 3) % 4;

			if( move(nextDir) ) {
				idleness = 0;
			} else {
				idleness++;
			}

			// for(Agent agent : agents) {
			// 	if( agent.equals(this) ) continue;
			// 	if( Math.abs(agent.getX() - this.getX()) + Math.abs(agent.getY() - this.getY()) == 1 ) {

			// 		System.out.println(this.name + ": fight");
			// 		run();

			// 	} else {
			// 		move( (int)Math.floor(rand.nextFloat()*4) );
			// 	}
			// }
		}

	}

	void display() {
		// if(isBusy()) {
		// 	if(dir == 0) image(spriteN_busy, (int)positionISO.getX() - 10, (int)positionISO.getY() - 30);
		// 	if(dir == 1) image(spriteE_busy, (int)positionISO.getX() - 10, (int)positionISO.getY() - 30);
		// 	if(dir == 2) image(spriteS_busy, (int)positionISO.getX() - 10, (int)positionISO.getY() - 30);
		// 	if(dir == 3) image(spriteW_busy, (int)positionISO.getX() - 10, (int)positionISO.getY() - 30);
		// } else {
		// 	if(dir == 0) image(spriteN, (int)positionISO.getX() - 10, (int)positionISO.getY() - 30);
		// 	if(dir == 1) image(spriteE, (int)positionISO.getX() - 10, (int)positionISO.getY() - 30);
		// 	if(dir == 2) image(spriteS, (int)positionISO.getX() - 10, (int)positionISO.getY() - 30);
		// 	if(dir == 3) image(spriteW, (int)positionISO.getX() - 10, (int)positionISO.getY() - 30);
		// }
	}

	boolean move(int dir) {
		this.dir = dir;
		try {
		if ( dir == 0 ) { 		
			if ( position.getY() > 0 ) {
				if ( !scene.get((int)position.getX(), (int)position.getY() - 1).isOccupied() ) {
					position.translate(0,-1);
					updateOccupies();
					return true;
				} else return false;
			} else return false;
		} else if ( dir == 1 ) { 	
			if ( position.getX() < scene.width() - 1 ) {
				if ( !scene.get((int)position.getX() + 1, (int)position.getY()).isOccupied() ) {
					position.translate(1,0);
					updateOccupies();
					return true;
				} else return false;
			} else return false;
		} else if ( dir == 2 ) {	
			if ( position.getY() < scene.height() - 1) {
				if ( !scene.get((int)position.getX(), (int)position.getY() + 1).isOccupied() ) {
					position.translate(0,1);
					updateOccupies();
					return true;
				} else return false;
			} else return false;
		} else if ( dir == 3 ) {	
			if ( position.getX() > 0 ) {
				if ( !scene.get((int)position.getX() - 1, (int)position.getY()).isOccupied() ) {
					position.translate(-1,0);
					updateOccupies();
					return true;
				} else return false;
			} else return false;
		} else return false;
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("move: " + dir);
			System.out.println(e);
			System.exit(0);
		}
		return false;
	}

	void run() {
		int oppositeDir = (this.dir + 2) % 4;
		Task task = new Task("move", Integer.toString(oppositeDir));
		tasks.add(task);
		running = true;
	}

}