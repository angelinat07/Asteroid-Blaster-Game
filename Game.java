import java.util.ArrayList;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JOptionPane;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Game extends JComponent{

    private static final int width = 400;
    private static final int height = 600;
    private static final int playerWidth = 50;
    private static final int playerHeight = 20;
    private static final int asteroidSize = 20;

    private Timer timer;
    private Rectangle playerRectangle;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Rectangle> enemyRectangles;

    private JFrame frame;
    private int lives;
    private int time;
    private int asteroidsDestroyed;
    private int level;
    private int countL;
    private boolean gameOver;

    private int shipX;
    private int shipY;

    public Game(JFrame frame){
        this.frame = frame;
        asteroids = new ArrayList<>();
        projectiles = new ArrayList<>();
        enemyRectangles = new ArrayList<>();
        shipX = 180;
        shipY = 520;
        playerRectangle = new Rectangle(shipX, shipY);
        playerRectangle.setLocation(shipX, shipY);

		lives = 3;
		time = 2000;
		level = 1;
		gameOver = false;
		asteroidsDestroyed = 0;

		setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent event){
                handleKeyPress(event);
            }
        });

		generateNewAsteroid();
        timer = new Timer(10, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
                if (!gameOver){
					updateScreen();
					repaint();
				}
            }
        });
        timer.start();
    }

	public void updateEnemyRectangles(){
		for (int i = 0; i < asteroids.size(); i++){
				enemyRectangles.get(i).setLocation(asteroids.get(i).getAsteroidX(), asteroids.get(i).getAsteroidY());
		}
	}

	private void handleKeyPress(KeyEvent event){
		if (event.getKeyCode() == KeyEvent.VK_LEFT && shipX-10 >= 0){
			shipX -= 10;
			playerRectangle.setLocation(shipX, shipY);
			repaint();
		}
		if (event.getKeyCode() == KeyEvent.VK_RIGHT && shipX+10 <= 350){
			shipX += 10;
			playerRectangle.setLocation(shipX, shipY);
			repaint();
		}
		if (event.getKeyCode() == KeyEvent.VK_UP && shipY-10 >= 0){
			shipY -= 10;
			playerRectangle.setLocation(shipX, shipY);
			repaint();
		}
		if (event.getKeyCode() == KeyEvent.VK_DOWN && shipY+10 <= 580){
			shipY += 10;
			playerRectangle.setLocation(shipX, shipY);
			repaint();
		}
		if (event.getKeyCode() == KeyEvent.VK_SPACE){
			shoot();
			repaint();
		}
		playerRectangle.setLocation(shipX, shipY);
	}

    private void shoot(){
        int projectileX = shipX + (playerWidth / 2);
        Projectile projectile = new Projectile(projectileX, shipY);
        projectiles.add(projectile);
    }

    private void checkForAsteroidCollisions(){
		if (enemyRectangles.size() != 0){
			for (int i = enemyRectangles.size() - 1; i >= 0; i--) {
				Asteroid asteroid = asteroids.get(i);
				if (((enemyRectangles.get(i).getX() >= playerRectangle.getX() && enemyRectangles.get(i).getX() <= playerRectangle.getX() + 50)
				|| (enemyRectangles.get(i).getX() + 20 >= playerRectangle.getX() && enemyRectangles.get(i).getX() + 20 <= playerRectangle.getX() + 50))
				&& (enemyRectangles.get(i).getY() >= playerRectangle.getY() && enemyRectangles.get(i).getY() <= playerRectangle.getY() + 20)){
					removeAsteroid(i);
					lives--;
					repaint();
				}
				if ((lives <= 0) || (asteroids.size() == 0)){
					gameOver = true;
				}
			}
		}
    }

	private void generateNewAsteroid(){
		int random = (int)(Math.random()*9)+1;
		if (random > 2){
			Asteroid asteroid = new Asteroid(this);
			asteroids.add(asteroid);
			Rectangle rect = new Rectangle(asteroid.getAsteroidX(), asteroid.getAsteroidY());
			enemyRectangles.add(rect);
		}
	}

	private void removeAsteroid(int index){
		asteroids.get(index).setDestroyed(true);
		asteroids.remove(index);
		enemyRectangles.remove(index);
	}

	private void updateAsteroidLocation(){
		if (asteroids.size() > 0){
			for (int i = 0; i < asteroids.size(); i++){
				if (asteroids.get(i).getIsDestroyed() == false)
					asteroids.get(i).updateAsteroid();
			}
		}
	}

	private void checkProjectileCollisions(){
			for (int i = asteroids.size()-1; i >= 0; i--){
				for (int j = projectiles.size()-1; j >= 0; j--){
					projectiles.get(j).updateProjectilePosition();
					if (projectiles.get(j).getYPosition() < 0)
						projectiles.remove(j);
					else if (asteroids.size() > 0 && projectiles.size() > 0 && i < asteroids.size() && j < projectiles.size()){
						int xDis = (projectiles.get(j).getXPosition())-(asteroids.get(i).getAsteroidX());
						int yDis = (projectiles.get(j).getYPosition())-(asteroids.get(i).getAsteroidY());
						if ((xDis > -50 && xDis < 50) && (yDis > 0 && yDis <= 20)){
							projectiles.remove(j);
							removeAsteroid(i);
							countL++;
							asteroidsDestroyed++;
							break;
						}
                	}
				}
			}
		}

    private void updateProjectiles(){
        for (int i = 0; i < projectiles.size(); i++)
            projectiles.get(i).updateProjectilePosition();
    }

    private void updateScreen(){
		if (time % 50 == 0){
			generateNewAsteroid();
		}
        checkForAsteroidCollisions();
        updateAsteroidLocation();
        checkProjectileCollisions();
        updateProjectiles();

        checkLevel();
        time--;
        repaint();
    }

	private void drawShip(Graphics graphics){
        int[] xPoints = {shipX, shipX, shipX + 50, shipX + 50};
        int[] yPoints = {shipY, shipY + 20, shipY + 20, shipY};

        if (lives == 3)
            graphics.setColor(Color.GREEN);
        else if (lives == 2)
            graphics.setColor(Color.YELLOW);
        else if (lives == 1)
            graphics.setColor(Color.RED);
        graphics.fillPolygon(xPoints, yPoints, 4);
	}

	private void drawAsteroids(Graphics graphics){
		for (int i = 0; i < asteroids.size(); i++){
			if (!asteroids.get(i).getIsDestroyed()){
				graphics.fillOval(asteroids.get(i).getAsteroidX(), asteroids.get(i).getAsteroidY(), asteroidSize, asteroidSize);
				updateEnemyRectangles();
			}
		}
	}

	private void drawProjectiles(Graphics graphics){
		graphics.setColor(Color.YELLOW);
		for (int i = 0; i < projectiles.size(); i++){
			int projectileX = projectiles.get(i).getXPosition();
			int projectileY = projectiles.get(i).getYPosition();
			graphics.fillRect(projectileX, projectileY, 6, 6);
		}
	}

	private void setEndScreenText(Graphics graphics, String str){
		graphics.setColor(Color.YELLOW);
		graphics.drawString(str, 70, 330);
	}

	private void setGameOver(Graphics graphics){
		if (time <= 0 && asteroids.size() == 0){
			setEndScreenText(graphics, "ALL ASTEROIDS DESTROYED");
			graphics.drawString("YOU WIN!", 150, 350);
		}
		else if (lives == 0)
			setEndScreenText(graphics, "ALL LIVES LOST, YOU LOSE!");
		else if (time <= 0)
			setEndScreenText(graphics, "OUT OF TIME, YOU LOSE!");

		graphics.setColor(Color.BLUE);
		graphics.drawString("GAME OVER!", 140, 310);
	}

    @Override
	protected void paintComponent(Graphics graphics){
		graphics.setFont(new Font("Times New Roman", Font.BOLD, 20));
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, 400, 600);

		graphics.setColor(Color.BLUE);
		graphics.drawString("Asteroids Destroyed: " + asteroidsDestroyed, 10, 90);

		graphics.setColor(Color.WHITE);
		graphics.drawString("Lives: " + lives, 10, 30);
		graphics.drawString("Timer: " + time, 10, 60);
		graphics.drawString("Level: " + level, 10, 120);

		if (time >= 1500){
			graphics.setFont(new Font("Times New Roman", Font.BOLD, 15));
			graphics.setColor(Color.WHITE);
			graphics.drawString("Welcome to my spaceship!", 110, 190);
			graphics.drawString("Many asteroids are in our way", 110, 210);
			graphics.drawString("Change directions with the", 110, 230);
			graphics.drawString("Up, left, right, and down arrows", 110, 250);
			graphics.drawString("Destroy as many asteroids as you can", 110, 270);
			graphics.drawString("Before the time runs out!", 110, 290);
			frame.repaint();
		}

		if (gameOver == false){
			drawShip(graphics);
			drawAsteroids(graphics);
			drawProjectiles(graphics);
		}
		if (time <= 0 || lives == 0){
			gameOver = true;
			setGameOver(graphics);
		}

	}

	private void checkLevel(){
		if (countL == 10){
			countL = 0;
			level++;
		}
	}

	public static void playMusic(String filePath){
		InputStream music;
		try{
			music = new FileInputStream(new File(filePath));
			AudioStream audios = new AudioStream(music);
			AudioPlayer.player.start(audios);
		}
		catch (Exception e){
			JOptionPane.showMessageDialog(null, "Error");
		}
	}

    public static void main(String[]args){
		JFrame frame = new JFrame("Asteroid Shooter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(420, 640);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.add(new Game(frame));
		frame.setVisible(true);

		playMusic("Avengers.wav");
    }

}