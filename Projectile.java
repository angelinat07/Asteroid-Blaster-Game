public class Projectile{

	private int xPosition;
	private int yPosition;
	private static int speed = 6;

	public Projectile(int x, int y){
		xPosition = x;
		yPosition = y;
	}

	public int getXPosition(){
		return xPosition;
	}

	public int getYPosition(){
		return yPosition;
	}

	public void updateProjectilePosition(){
		yPosition -= speed;
	}

}