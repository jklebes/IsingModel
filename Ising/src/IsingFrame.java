import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class IsingFrame extends JFrame {

	
	private Lattice l;
	private int height;
	private int width;
	IsingPanel pan ;
	
	public IsingFrame(Lattice l){
		this.l=l;
		this.width = l.getwidth();
		this.height=l.getheight();
		this.pan = new IsingPanel(l);
		this.add(pan);
		this.setSize(10*width+20,10*height+20+ 30);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void step(){
		pan.repaint();
	}

}

