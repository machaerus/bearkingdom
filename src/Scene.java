import java.util.Random;
import java.lang.Math;
import java.awt.Point;

class Scene {

	private int width;
	private int height;
	private Tile[][] tileTable;

	Scene() {
		generate();
	}

	void generate() {
		this.height = 100;
		this.width = 100;
		tileTable = new Tile[width][height];
		Random rand = new Random();

		// jakieś losowe generowanie
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				tileTable[j][i] = new Tile(
					new Point(j,i), 
					false, 
					(int)Math.ceil(rand.nextFloat()*1.9 + 2.099)
					// liczba od 0 do 1.9
					// +0.099, żeby zmieścić się w (0,2], dzięki temu z ceil mamy zawsze 1 albo 2
					// +2, bo chcemy 3 albo 4 (0, 1 i 2 to occupied, active i hover)
				);
			}
		}
	}

	int width() {
		return width;
	}

	int height() {
		return height;
	}

	Tile get(int x, int y) {
		return tileTable[x][y];
	}

}