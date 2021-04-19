package javagames.example.ch11;

import javagames.util.SafeKeyboardFramework;
import javagames.util.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class SafeKeyboardInputExample extends SafeKeyboardFramework {

    private int spacesCount;
    private Vector<String> strings;
    private float blink;
    private boolean drawCursor;

    public SafeKeyboardInputExample() {
        appBackground = Color.black;
        appWidth = 800;
        appHeight = 600;
        appSleep = 20;
        appTitle = "Safe Keyboard Example";
    }

    @Override
    protected void initialize() {
        super.initialize();
        strings = new Vector<>();
        strings.add( "" );
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);
        while ( keyboard.processEvent() ) {
            if ( keyboard.keyDownOnce( KeyEvent.VK_UP  ) ) {
                appSleep += Math.min( appSleep * 2,  1000L );
            }
            if ( keyboard.keyDownOnce( KeyEvent.VK_DOWN ) ) {
                appSleep -= Math.min( appSleep / 2,  1000L );
            }
            if ( keyboard.keyDownOnce( KeyEvent.VK_SPACE ) ) {
                spacesCount++;
            }
            if ( keyboard.keyDownOnce( KeyEvent.VK_ESCAPE) ) {
                spacesCount = 0;
            }
            processTypedChar();
        }
    }

    private void processTypedChar() {
        Character typedChar = keyboard.getKeyTyped();
        if ( typedChar != null ) {
            if ( Character.isISOControl( typedChar ) ) {
                if ( KeyEvent.VK_BACK_SPACE  == typedChar ) {
                    removeCharacter();
                }
                if ( KeyEvent.VK_ENTER == typedChar ) {
                    strings.add( "" );
                }
            }  else {
                addCharacter( typedChar );
            }
            drawCursor = true;
            blink = 0.0f;
        }
    }

    private void removeCharacter() {
        String line = strings.remove( strings.size() - 1 );
        if ( ! line.isEmpty() ) {
            strings.add( line.substring( 0, line.length() - 1 ) );
        }
        if ( strings.isEmpty() ) {
            strings.add( "" );
        }
    }

    private void addCharacter( Character typedChar) {
        strings.add( strings.remove( strings.size() - 1) + typedChar );
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        blink += t;
        if ( blink > 0.5f ) {
            blink -= 0.5f;
            drawCursor = !drawCursor;
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Graphics2D g2d = ( Graphics2D ) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        Vector<String> text = new Vector<>();
        text.add( "App sleep time: " + appSleep );
        text.add( "Space count: " + spacesCount );
        text.add( "" );
        text.add("total thread: " + Thread.activeCount() );
        g2d.setFont( new Font( "Monospaced", Font.BOLD, 15 ) );
        g2d.setColor( Color.white );
        textPos = Utility.drawString( g2d, 20 , textPos,  text );
        textPos = Utility.drawString( g2d, 20, textPos + 20, strings );
        if ( drawCursor ) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = 20 + fm.stringWidth( strings.lastElement() );
            int y = textPos - fm.getDescent();
            g2d.drawString("_", x, y);
        }
    }

    public static void main(String[] args) {
        launchApp( new SafeKeyboardInputExample() );
    }
}
