import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


interface Commons
{
    int DELAY = 50;
}

interface Size
{
    int WIDTH  = 600;
    int HEIGHT = 600;

    int TILE = 25;
    int GAME = ( WIDTH * HEIGHT ) / TILE;
}


class GamePanel extends JPanel implements KeyListener, ActionListener
{

    private int foodX, foodY;
    private Random rand;

    private boolean isPlay;

    private Timer timer;

    private boolean hasLost = false;
    
    private int bodyParts = 3;
    private int score = 0;
    
    private int snakeX[] = new int[ Size.GAME ];
    private int snakeY[] = new int[ Size.GAME ];

    private char dir = 'r';

    public GamePanel()
    {
        addKeyListener( this );
        setFocusable( true );
        setFocusTraversalKeysEnabled( false );
        timer = new Timer( Commons.DELAY, this );
        rand = new Random();
        setBackground( Color.BLACK );
        startGame();
    }

    public void startGame()
    {
        isPlay = true;
        timer.start();
        makeFood();
    }

    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );
        draw( g );
    }


    
    public void draw( Graphics g )
    {

        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(
                             RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON );

        // You can also enable antialiasing for text:

        g2d.setRenderingHint(
                             RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        
        // Score
        String s1 = String.format( "%02d", score );
        g.setColor( Color.BLUE );
        g.setFont( new Font( "MS Gothic", Font.PLAIN, 40 ) );
        g.drawString( "SCORE " + s1, Size.WIDTH / 2 - 100, 50 );
        
        // Food
        g.setColor( Color.RED );
        g.fillRect( foodX, foodY, Size.TILE, Size.TILE );

        // Snake
        for ( int i = 0; i < bodyParts; i++ ) {
            if ( i == 0 ) {
                g.setColor( Color.WHITE );
                g.fillRect( snakeX[ i ], snakeY[ i ], Size.TILE, Size.TILE );
            } else {
                g.setColor( Color.GRAY );
                g.fillRect( snakeX[ i ], snakeY[ i ], Size.TILE, Size.TILE );
            }
        }

        // Game over
        if ( !isPlay ) {
            hasLost = true;
            g.setColor( Color.YELLOW );
            g.setFont( new Font( "MS Gothic", Font.BOLD, 50 ) );
            g.drawString( "GAME OVER", Size.WIDTH / 2 - 150, Size.HEIGHT / 2 );
            g.setFont( new Font( "MS Gothic", Font.BOLD, 20 ) );
            g.drawString( "Press SPACE to replay", Size.WIDTH / 2 - 150, Size.HEIGHT / 2 - 80 );            
        }
    }


    public void makeFood()
    {
        foodX = rand.nextInt( (int)( Size.WIDTH  / Size.TILE ) ) * Size.TILE;
        foodY = rand.nextInt( (int)( Size.HEIGHT / Size.TILE ) ) * Size.TILE;

        // System.out.println( foodX );
        // System.out.println( foodY );

        if ( foodX % Size.TILE != 0 ) makeFood(); 
        if ( foodY % Size.TILE != 0 ) makeFood();

        if ( foodX >= 575 ) makeFood();
        if ( foodY >= 550 ) makeFood();
    }


    public void move()
    {
        for ( int i = bodyParts; i > 0; i-- ) {
            snakeX[ i ] = snakeX[ i - 1 ];
            snakeY[ i ] = snakeY[ i - 1 ];
        }

        switch( dir ) {
        case 'l':
            snakeX[ 0 ] -= Size.TILE;
            break;
        case 'r':
            snakeX[ 0 ] += Size.TILE;
            break;
        case 'u':
            snakeY[ 0 ] -= Size.TILE;
            break;
        case 'd':
            snakeY[ 0 ] += Size.TILE;
            break;
        }
    }

    public void checkCollision()
    {
        // Eat food
        if ( snakeX[ 0 ] == foodX && snakeY[ 0 ] == foodY ) {
            bodyParts++;
            score++;
            makeFood();
        }


        // Die
        /*
        if ( snakeX[ 0 ] < 0 ) {
            isPlay = false;
        }

        if ( snakeX[ 0 ] > Size.WIDTH ) {
            isPlay = false;
        }

        if ( snakeY[ 0 ] < 0 ) {
            isPlay = false;
        }

        if ( snakeY[ 0 ] > Size.HEIGHT ) {
            isPlay = false;
        }
        */


        
        if ( snakeX[ 0 ] < 0 ) {
            snakeX[ 0 ] = Size.WIDTH - Size.TILE;
        }

        if ( snakeX[ 0 ] > Size.WIDTH ) {
            snakeX[ 0 ] = 0 + Size.TILE;
        }

        if ( snakeY[ 0 ] < 0 ) {
            snakeY[ 0 ] = Size.HEIGHT - Size.TILE;
        }

        if ( snakeY[ 0 ] > Size.HEIGHT ) {
            snakeY[ 0 ] = 0 + Size.TILE;
        }

        

        // Kill itself
        for ( int i = bodyParts; i > 0; i-- ) {
            if ( snakeX[ 0 ] == snakeX[ i ] && snakeY[ 0 ] == snakeY[ i ] ) {
                isPlay = false;
            }
        }
    }

    
    @Override public void actionPerformed( ActionEvent e )
    {
        if ( isPlay ) {
            move();
            checkCollision();
        } else if ( !isPlay ) {
            timer.stop();
        }
        repaint();
    }

    @Override public void keyPressed( KeyEvent e )
    {
        if ( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
            if ( dir != 'l' ) dir = 'r';
        }

        if ( e.getKeyCode() == KeyEvent.VK_LEFT ) {
            if ( dir != 'r' ) dir = 'l';
        }

        if ( e.getKeyCode() == KeyEvent.VK_UP ) {
            if ( dir != 'd' ) dir = 'u';
        }

        if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
            if ( dir != 'u' ) dir = 'd';
        }

        if ( e.getKeyCode() == KeyEvent.VK_SPACE ) {
            if ( hasLost ) {
                timer.start();
                isPlay = true;
                snakeX[ 0 ] = Size.TILE;
                score = 0;
                bodyParts = 3;
                hasLost = false;
            }
        }
    }

    @Override public void keyTyped( KeyEvent e )
    {
    }

    @Override public void keyReleased( KeyEvent e )
    {
    }

}


class Snake extends JFrame
{

    public Snake()
    {
        add( new GamePanel() );
        pack();
        setBounds( 100, 10, Size.WIDTH, Size.HEIGHT );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setResizable( false );
        setVisible( true );
    }
    
    public static void main( String[] args )
    {
        new Snake();
    }
}
