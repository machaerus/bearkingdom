import java.awt.Point;

abstract class GameObject {
/**
 * Abstrakcyjny obiekt - każdy obiekt ruchomy lub nieruchomy,
 * który może pojawić się na planszy i wchodzić w jakiekolwiek
 * interakcje z otoczeniem.
 */

	protected int size;				// ilość zajmowanych pól
	protected Point[] area;			// lista względnych współrzędnych pól zajmowanych przez obiekt
	protected Point[] occupies;		// lista pól, które OBECNIE zajmuje obiekt [generowana na bieżąco]
	protected Point position;		// pole od którego liczymy względne odległości i które pozycjonujemy
	// protected Point positionISO;	// pozycja we współrzędnych izometrycznych /do rysowania/
	protected boolean crossable;	// czy agent może przejść przez ten obiekt
	protected Scene scene;

	GameObject(int size, Point position, boolean crossable, Scene scene) {
		this.size = size;
		//area = new Point[size];
		occupies = new Point[size];
		for(int i = 0; i < size; i++) {
			occupies[i] = new Point(0,0);
		}
		this.position = position;
		// positionISO = new Point();
		// calculateISO();
		this.crossable = crossable;
		this.scene = scene;
	}

	// void calculateISO() {
	// 	int x = (int)position.getX() * 4*UNIT;
	// 	int y = (int)position.getY() * 4*UNIT;
	// 	positionISO.setLocation(x - y - CAM_X, (x + y) / 2 - CAM_Y - 6);
	// }

	abstract void display();	// wyświetlanie obiektu
	
	// to wywołujemy kiedy obiekt się przemieści,
	// żeby zaktualizować stan planszy pod kątem tego,
	// które pola są zajęte
	void updateOccupies() {
		for(int i = 0; i < size; i++) {
			occupies[i].setLocation(
				(int)area[i].getX() + (int)position.getX(),
				(int)area[i].getY() + (int)position.getY()
			);
		}
	}

	// aktualizujemy zajętość na mapie - to wywołujemy
	// przed każdym rysowaniem
	void markOccupation() {
		for(int i = 0; i < occupies.length; i++) {
			scene.get((int)occupies[i].getX(), (int)occupies[i].getY()).setOccupied(true);
		}
	}

	int getX() {
		return (int)position.getX();
	}

	int getY() {
		return (int)position.getY();
	}

}