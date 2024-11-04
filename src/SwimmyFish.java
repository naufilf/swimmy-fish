import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SwimmyFish extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image fishImg;
    Image topCoralImg;
    Image bottomCoralImg;

    //Fish
    int fishX = boardWidth/8;
    int fishY = boardHeight/2;
    int fishWidth = 34;
    int fishHeight = 24;

    //corals
    int coralX = boardWidth;
    int coralY = 0;
    int coralWidth = 64;
    int coralHeight = 512;

    class Coral {
        int x = coralX;
        int y = coralY;
        int width = coralWidth;
        int height = coralHeight;
        Image img;
        boolean passed = false;

        Coral(Image img) {
            this.img = img;
        }
    }

    // game mechanics
    Fish fish;
    int velocityX = -4;
    int velocityY = -9;
    int gravity = 1;
    Timer gameLoop;
    Timer placeCoralsTimer;
    boolean gameOver = false;
    double score = 0;

    ArrayList<Coral> corals;
    Random random = new Random();

    class Fish {
        int x = fishX;
        int y = fishY;
        int width = fishWidth;
        int height = fishHeight;
        Image img;

        Fish (Image img) {
            this.img = img;
        }
    }

    SwimmyFish() {
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setFocusable(true);
        addKeyListener(this);
        // assign images
        backgroundImg = new ImageIcon(getClass().getResource("./swimmyfishbg.png")).getImage();
        fishImg = new ImageIcon(getClass().getResource("./swimmyfish.png")).getImage();
        topCoralImg = new ImageIcon(getClass().getResource("./topcoral.png")).getImage();
        bottomCoralImg = new ImageIcon(getClass().getResource("./bottomcoral.png")).getImage();

        // fish
        fish = new Fish(fishImg);
        corals = new ArrayList<Coral>();

        //place coral timer
        placeCoralsTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCorals();
            }
        });
        placeCoralsTimer.start();

        //game timer
        gameLoop = new Timer(1000/60,this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }   

    public void placeCorals() {
        int randomCoralY = (int) (coralY - coralHeight / 4 - Math.random() * (coralHeight / 2)  );
        int space = boardHeight/4;

        // Top coral
        Coral topCoral = new Coral(topCoralImg);
        topCoral.y = randomCoralY;
        corals.add(topCoral);

        // Bottom coral
        Coral bottomCoral = new Coral(bottomCoralImg);
        bottomCoral.y = topCoral.y + space + coralHeight;
        corals.add(bottomCoral);
    }

    public boolean collision(Fish f, Coral c) {
        return f.x < c.x + c.width && f.x + f.width > c.x && f.y < c.y + c.height && f.y + f.height > c.y;
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //fish
        g.drawImage(fish.img, fish.x, fish.y, fish.width, fish.height, null);

        //corals
        for(int i = 0; i < corals.size(); i++) {
            Coral coral = corals.get(i);
            g.drawImage(coral.img, coral.x, coral.y, coral.width, coral.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Comic Sans", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game over: " + String.valueOf((int)score), 10, 35);
            g.drawString("Press R to restart", 10, 65);
        } else {
            g.drawString(String.valueOf((int)score), 10, 35);
        }
    }

    public void move() {
        //fish
        velocityY += gravity;
        fish.y += velocityY;
        fish.y = Math.max(fish.y,0);

        //corals
        for (int i = 0; i < corals.size(); i++) {
            Coral coral = corals.get(i);
            coral.x += velocityX;

            if (!coral.passed && fish.x > coral.width + coral.x) {
                coral.passed = true;
                score += 0.5; // each pipe gives 1/2 a point
            }

            if (collision(fish,coral)) {
                gameOver = true;
            }
        }

        if (fish.y > boardHeight) {
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            placeCoralsTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
       if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            System.out.println("Jump!"); 
       }

       if (e.getKeyCode() == KeyEvent.VK_R) {
        if (gameOver) {
            // reset game values
            velocityY = 0;
            fish.y = fishY;
            corals.clear();
            score = 0;
            gameOver = false;
            gameLoop.start();
            placeCoralsTimer.start();
        }
       }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
