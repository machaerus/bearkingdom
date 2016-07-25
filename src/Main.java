import java.util.Random;
import java.lang.Math;
import java.util.Vector;
import java.awt.Point;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.lang.Thread;


public class Main {

	// conf //

	public static int UNIT = 8; 			// podstawowa jednostka miary /połowa wysokości pola/

	public static int POPULATION = 600;
	public static int DIE_CHANCE = 7000;	// im większa liczba tym trudniej
	public static int BREED_CHANCE = 2000;

	//////////////////////

	public static double TOURS;			// licznik tur

	public static Random rand;

	public static int SPEED;
	public static int Q;					// spowalnianie czasu /robocze/

	public static Scene scene;
	public static ConcurrentLinkedDeque<Agent> agents;

	public static boolean hasActiveObject = false;		// wtf

	public static int strategiesNum;					// ilość strategii cząstkowych
	public static int[] strategyCount;					// aktualna strategia populacji
	public static int[] summaryFitness;				// suma dostosowania poszczególnych strategii cząstkowych
	public static int currPopulation;					// aktualna liczność populacji
	public static double averPopulation;				// średnia w czasie liczność populacji
	public static String stats;						// wyświetlanie statystyk
	

	public static void draw() {

		// aktualizowanie zajętości pól
		for(int i = 0; i < scene.height(); i++) {
			for(int j = 0; j < scene.width(); j++) {
				scene.get(j,i).setOccupied(false);
			}
		}
		for( Agent agent : agents ) {
			agent.markOccupation();
		}

		// wojenne niedźwiadki
		
		if ( Q % SPEED == 0 ) {

			TOURS++;

			// nikt nie jest zajęty na początku rundy,
			// starzejemy się, rozmnażamy, umieramy
			// obliczamy statystyki
			strategyCount[0] = 0;
			strategyCount[1] = 0;
			summaryFitness[0] = 0;
			summaryFitness[1] = 0;

			for( Agent agent : agents ) {
				agent.setBusy(false);
				agent.age();
				agent.checkIdleness();
				if( agent.die ) {
					// System.out.println("umieram");
					agents.remove(agent);
					continue;
				}
				if( agent.breed ) {
					// System.out.println("rozmnazam sie");
					agent.breed();
				}
				// System.out.println("licze strategie");
				strategyCount[agent.strategy()]++;
				summaryFitness[agent.strategy()] += agent.fitness();
			}

			currPopulation = strategyCount[0]+strategyCount[1];
			averPopulation = (averPopulation*(TOURS-1) + currPopulation) / TOURS;

			// następnie przydzielamy zadania
			for( Agent agent : agents ) {
				if( !agent.isBusy() ) {
					// jeśli agent nie jest zajęty, to znajdujemy mu zajęcie
					if( !agent.isRunning() ) {
						// jeśli agent nie jest w trakcie ucieczki, to sprawdzamy czy może walczyć
						for( Agent enemy : agents ) {
							if( enemy.equals(agent) ) continue;
							if( Math.abs(enemy.getX() - agent.getX()) + Math.abs(enemy.getY() - agent.getY()) == 1 ) {
								if( !enemy.isBusy() ) {
									fight(agent, enemy);
									break;
								}
							}
						}
					}
					// jeśli wciąż nie jest zajęty (nie walczył) to niech sam wybierze, co chce robić
					if( !agent.isBusy() ) agent.think();
				}
			}
			Q = 1;
		} else {
			Q++;
		}

		// // wyświetlanie agentów	
		// for(Agent agent : agents) {
		// 	agent.calculateISO();
		// 	// agent.display();
		// }

		// wyświetlanie statystyk
		stats = "jastrzębie: " + strategyCount[0] + " / gołębie: " + strategyCount[1]
			+ "\nliczebnosc populacji: " + currPopulation
			+ "\nśrednie dop. jastrzębi: " + (strategyCount[0] > 0 ? summaryFitness[0]/strategyCount[0] : 0)
			+ "\nśrednie dop. gołębi: " + (strategyCount[1] > 0 ? summaryFitness[1]/strategyCount[1] : 0)
			+ "\nratio: " + (strategyCount[1] > 0 ? (float)strategyCount[0]/strategyCount[1] : 0)
			+ "\n\nIlość tur: " + (int)TOURS
			+ "\nśrednia populacja: " + (int)averPopulation
			;
		
		System.out.println(stats);
	}

	public static void fight(Agent A, Agent B) {
		A.setBusy(true);
		B.setBusy(true);

		if( B.getX() - A.getX() == 1 ) {
			B.setDir(3);
			A.setDir(1);
		} else if( B.getX() - A.getX() == -1 ) {
			B.setDir(1);
			A.setDir(3);
		} else if( B.getY() - A.getY() == 1 ) {
			B.setDir(0);
			A.setDir(2);
		} else {
			B.setDir(2);
			A.setDir(0);
		}

		Game game = new Game(A, B);
		game.play();

		A.run();
		B.run();
	}


	public static void main(String[] args) {
	
		SPEED = 2;
		Q = 0;
		TOURS = 0;
		currPopulation = POPULATION;
		averPopulation = POPULATION;

		rand = new Random();
		scene = new Scene();

		strategiesNum = 2;
		strategyCount = new int[strategiesNum];
		summaryFitness = new int[strategiesNum];
		strategyCount[0] = 0;
		strategyCount[1] = 0;
		summaryFitness[0] = 0;
		summaryFitness[1] = 0;

		agents = new ConcurrentLinkedDeque<Agent>();
		
		int strategy;
		for(int i = 0; i < POPULATION; i++) {
			strategy = i % 2 == 0 ? 0 : 1;
			agents.add(new Agent(
				strategy, 
				new Point(
					(int)Math.floor(rand.nextFloat() * (scene.width()-1)),
					(int)Math.floor(rand.nextFloat() * (scene.height()-1))
				),
				scene,
				agents,
				DIE_CHANCE,
				BREED_CHANCE ));
			strategyCount[strategy]++;
			summaryFitness[strategy] += 100;
		}

		while(true) {
			try {
				Thread.sleep(50);
				System.out.print(String.format("\033[2J"));
				draw();
			} catch(Exception e) {
				System.out.println(e);
			}
		}

	}

	
}