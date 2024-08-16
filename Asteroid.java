import javax.swing.JComponent;
public class Asteroid{

	private int asteroidX;
	private int asteroidY;
	private boolean isDestroyed;
	private JComponent component;

	public Asteroid(JComponent c){
		asteroidX = (int)(Math.random()*350)+20;
		asteroidY = 0;
		isDestroyed = false;
		component = c;
	}

	public int getAsteroidX(){
		return asteroidX;
	}

	public int getAsteroidY(){
		return asteroidY;
	}

    public void setAsteroidX(int asteroidX){
        asteroidX = asteroidX;
    }

    public void setAsteroidY(int asteroidY){
        asteroidY = asteroidY;
    }

	public boolean getIsDestroyed(){
		return isDestroyed;
	}

	public void setDestroyed(boolean newD){
		isDestroyed = newD;
	}

	public void updateAsteroid(){
		if (!isDestroyed){
			asteroidY += 6;
			if (asteroidY > component.getHeight() - 20){
				asteroidY = 0;
				asteroidX = (int)(Math.random()*360) + 20;
				while ((asteroidX <= 20) && (asteroidX >= 360))
					asteroidX = (int)(Math.random()*360) + 20;
			}
		}
	}

}