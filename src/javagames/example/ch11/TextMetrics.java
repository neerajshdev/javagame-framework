package javagames.example.ch11;

import javagames.util.SimpleFramework;
import javagames.util.Utility;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class TextMetrics extends SimpleFramework {

    public TextMetrics() {
        appTitle = "Text Metrics Example";
        appWidth = 800;
        appHeight = 600;
        appBackground = Color.darkGray;
    }


    @Override
    protected void render(Graphics g) {
        super.render(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("New Times Roman", Font.ITALIC | Font.BOLD, 40);
        g2d.setFont(font);
        g2d.setColor(Color.GREEN);
        String str = "Groovy Baby BLAH";
        int x = 50;
        int y = 50;
        g2d.drawString(str, x, y);

        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout tl = new TextLayout(str, font, frc);

        float newY = y + tl.getAscent() + tl.getDescent() + tl.getLeading();
        g2d.drawString(str, x, newY);

        // draw centered text
        // first lets draw the center
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int cw = w / 2;
        int ch = h / 2;
        g.setColor(Color.BLACK);
        g.drawLine(0, ch, w, ch);
        g.drawLine(cw, 0, cw, h);

        String center = "Center text is here where your eye is, now";
        int stringWidth = g2d.getFontMetrics().stringWidth(center);
        float dx = stringWidth / 2;
        float dy = g2d.getFontMetrics().getLineMetrics(center, g2d).getBaselineOffsets()[Font.CENTER_BASELINE];
        g2d.setColor(Color.white);
        g2d.drawString(center, cw - dx, ch - dy);

        g2d.setColor( Color.WHITE );
        g2d.fillRect( x - 1, y - 1, 3, 3 );

        ArrayList<String> console = new ArrayList<String>();
        console.add( "Baseline: " + tl.getBaseline() );
        float[] baselineOffsets = tl.getBaselineOffsets();
        console.add( "Baseline-Offset[ ROMAN ]: "
                + baselineOffsets[ Font.ROMAN_BASELINE ] );
        console.add( "Baseline-Offset[ CENTER ]: "
                + baselineOffsets[ Font.CENTER_BASELINE ] );
        console.add( "Baseline-Offset[ HANGING ]: "
                + baselineOffsets[ Font.HANGING_BASELINE ] );
        console.add( "Ascent: " + tl.getAscent() );
        console.add( "Descent: " + tl.getDescent() );
        console.add( "Leading: " + tl.getLeading() );
        console.add( "Advance: " + tl.getAdvance() );
        console.add( "Visible-Advance: " + tl.getVisibleAdvance() );
        console.add( "Bounds: " + toString( tl.getBounds()) );

        Font propFont = new Font( "Courier New", Font.BOLD, 14 );
        g2d.setFont( propFont );
        int xLeft = x;
        int xRight = xLeft + (int)tl.getVisibleAdvance();

        // draw baseline
        g2d.setColor( Color.WHITE );
        int baselineY = y + (int)baselineOffsets[ Font.ROMAN_BASELINE ];
        g2d.drawLine( xLeft, baselineY, xRight, baselineY );
        g2d.drawString( "roman baseline", xRight, baselineY );

        // draw center baseline
        g2d.setColor(Color.red);
        int centerBaselineY = y + (int) baselineOffsets[Font.CENTER_BASELINE];
        g2d.drawLine(xLeft, centerBaselineY, xRight, centerBaselineY);
        g2d.drawString("Center baseline", xRight, centerBaselineY);

        // draw hanging baseline
        g2d.setColor(Color.red);
        int hangingBaselineY = y + (int) baselineOffsets[Font.HANGING_BASELINE];
        g2d.drawLine(xLeft, hangingBaselineY, xRight, hangingBaselineY);
        g2d.drawString("Center baseline", xRight, hangingBaselineY);

        // draw Ascent
        g2d.setColor( Color.YELLOW );
        int propY = y - (int)tl.getAscent();
        g2d.drawLine( xLeft, propY, xRight, propY );
        TextLayout temp = new TextLayout( "hanging baseline", propFont,
                g2d.getFontRenderContext() );
        g2d.drawString( "Ascent", xRight + temp.getVisibleAdvance(), propY );

        // draw Descent
        g2d.setColor( Color.RED );
        propY = y + (int)tl.getDescent();
        g2d.drawLine( xLeft, propY, xRight, propY );
        g2d.drawString( "Descent", xRight, propY );

       // draw Leading
        g2d.setColor( Color.GREEN );
        propY = y + (int)tl.getDescent() + (int)tl.getLeading();
        g2d.drawLine( xLeft, propY, xRight, propY );
        temp = new TextLayout(
                "Descent", propFont, g2d.getFontRenderContext() );
        g2d.drawString( "Leading", xRight + temp.getVisibleAdvance(), propY );


        // draw console output...
        g2d.setColor( Color.LIGHT_GRAY );
        g2d.setFont( new Font( "Courier New", Font.BOLD, 12 ) );
        Utility.drawString( g2d, 20, ch + 50, console );

    }

    private String toString(Rectangle2D r) {
        return "[ x=" + r.getX() + ", y=" + r.getY() + ", w=" + r.getWidth()
                + ", h=" + r.getHeight() + " ]";
    }

    public static void main(String[] args) {
        launchApp(new TextMetrics());
    }
}
