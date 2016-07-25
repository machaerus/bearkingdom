class Game {
	
	protected Agent A, B;
	int V, C;

	Game(Agent A, Agent B) {
		this.A = A;
		this.B = B;
		V = 10;
		C = 20;
	}

	void play() {

		/*
		 *		Gra wg schematu jaszcząb-gołąb:
		 *		
		 *  	+-----------+-------+
		 * 		|  V/2-C/2	|  V 	|
		 * 		+-----------+-------+
		 * 		|  0		|  V/2	|
		 * 		+-----------+-------+
		 *
		 * 		0 : jastrząb
		 * 		1 : gołąb
		 * 		
		 */
		
		if( A.strategy() == 0 && B.strategy() == 0 ) {
			A.changeFitness(V/2 - C/2);
			B.changeFitness(V/2 - C/2);
		} else if( A.strategy() == 0 && B.strategy() == 1 ) {
			A.changeFitness(V);
			B.changeFitness(0);
		} else if( A.strategy() == 1 && B.strategy() == 0 ) {
			A.changeFitness(0);
			B.changeFitness(V);
		} else if( A.strategy() == 1 && B.strategy() == 1 ) {
			A.changeFitness(V/2);
			B.changeFitness(V/2);
		} else {
			System.out.println("Nieprawidłowe strategie!");
			System.exit(-1);
		}
	}

}