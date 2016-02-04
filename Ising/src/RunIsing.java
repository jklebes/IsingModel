import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class RunIsing{
	
	static String line;
static double Tfrom = 0;
static double Tto= 10;
static double Tinterval = .5;
static int equilsteps =200;
static int runsteps= 50000;
static int repeats = 20;
static int width = 50;
static int height = 50;
static Stats stats = new Stats();
private static IsingFrame f;

public static void main(String[] args) throws IOException{

	Lattice lattice = new Lattice(width, height, 0);
	
	f = new IsingFrame(lattice);
	
	File file = new File("glaubertemps.txt");
	if (!file.exists()) {
		file.createNewFile();
	}
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);
	String line1 = "# run for "+ equilsteps+ " equilsteps, "+ runsteps+" steps, averages of "+ repeats + "repeats";
	String line1b= "#T M S [sterror in M(t), avg over repeats] [sterror in S over repeats]";
	bw.write(line1);
	bw.newLine();
	bw.write(line1b);
	for (int i = 0; i < (int)((Tto - Tfrom)/Tinterval); i++){
		double T = Tfrom + i* Tinterval;
		double[] Ms= new double[repeats];
		double[] Ss=new double[repeats];
		double[] Merrors = new double[repeats];
		double[] Serrors = new double [repeats];
		lattice.setT(T);
		for (int n = 0; n < repeats; n++){
			lattice.fillRandom();
			f.setTitle("T = "+T+", run "+n+"/"+repeats+ ", equilibrating");
			for (int j = 0; j < equilsteps; j++){
				lattice.fasterSampleGlauber();
				f.step();
			}
			f.setTitle("T = "+T+", run "+(n+1)+"/"+repeats+  ", measuring");
			double[] results = lattice.glauberMagnetisation(runsteps,T,f);
			Ms[n] = results[0];
			Ss[n] = results[1];
			Merrors[n] = results[2];
		}
		double Mavg = stats.avg(Ms);
		double Savg = stats.avg(Ss);
		double Savgerror = stats.sterror(Ss);
		double Mavgerror = stats.avg(Merrors);
			bw.newLine();
			line = T + " " + Mavg + " " + Savg + " "+Mavgerror+" " + Savgerror;
			bw.write(line);
		
	}
	bw.close();
	
}
}