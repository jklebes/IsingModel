import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class RunIsing{
	
	static String line;
static double Tfrom = 0;
static double Tto= 10;
static double Tinterval = .5;
static int equilsteps =100;
static int runsteps= 500000;
static int repeats = 100;
static int width = 50;
static int height = 50;

public static void main(String[] args) throws IOException{

	Lattice lattice = new Lattice(width, height, 0);
	
	File file = new File("glaubertemps.txt");
	if (!file.exists()) {
		file.createNewFile();
	}
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);
	String line1 = "# ";
	bw.write(line1);
	for (int i = 0; i < (int)((Tto - Tfrom)/Tinterval); i++){
		double T = Tfrom + i* Tinterval;
		double Mavg=0;
		double Savg=0;
		lattice.setT(T);
		for (int n = 0; n < repeats; n++){

			for (int j = 0; j < equilsteps; j++){
				lattice.fasterSampleGlauber();
			}

			double[] results = lattice.glauberMagnetisation(runsteps,T);
			Mavg += results[0];
			Savg += results[1];
		}
		Mavg /= repeats;
		Savg /= repeats;
			bw.newLine();
			line = T + " " + Mavg + " " + Savg ;
			bw.write(line);
		
	}
	bw.close();
	
}
}