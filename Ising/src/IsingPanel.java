import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;


public class IsingPanel extends JPanel{

	private Lattice l;
	private int height;
	private int width;
	
	public IsingPanel(Lattice l){
		this.l=l;
		this.width = l.getwidth();
		this.height=l.getheight();
	}

	public void paintComponent(Graphics g) {
        for (int i=0; i < height; i++){
    		for (int j=0; j < width; j++){
    	g.setColor(getColor(i,j,l));
        g.fillRect(10+j*10, 10+ i*10, 10,10);
    		}
        }
    }

	private Color getColor(int i, int j, Lattice l) {
		if (l.getSpin(i,j)==-1){return Color.green;}
		else{return Color.blue;}
	}  

}
