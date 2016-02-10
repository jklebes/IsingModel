import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class RunIsing{
	
static String filename = "infile.txt";

public static void main(String[] args) throws IOException{

	String line;
	double Tfrom = 0;
	double Tto= 10;
	double Tinterval = .5;
	int equilsteps =1000;
	int runsteps= 5000;
	int repeats = 10;
	int width = 50;
	int height = 50;
	Stats stats = new Stats();
	IsingFrame f;
	Integer type = 1;

	//file reading
	FileReader fr = new FileReader(filename);
	BufferedReader br = new BufferedReader(fr);
	while ((line = br.readLine()) != null){
		String[] lineelements = line.split(" ");
		String name = lineelements[0];
		String value = lineelements[1];
		if (name.equals("T")){
			Tfrom=Double.valueOf(value);
			Tto = Double.valueOf(value)+1;
			Tinterval = 1;
		}
		else if (name.equals("Tfrom")){
			Tfrom=Double.valueOf(value);
		}
		else if (name.equals("Tto")){
			Tto=Double.valueOf(value);
		}
		else if (name.equals("Tinterval")){
			Tinterval=Double.valueOf(value);
		}
		else if (name.equals("repeats")){
			repeats=Integer.valueOf(value);
		}
		else if (name.equals("equilsteps")){
			equilsteps=Integer.valueOf(value);
		}
		else if (name.equals("runsteps")){
			runsteps=Integer.valueOf(value);
		}
		else if (name.equals("width")){
			width=Integer.valueOf(value);
		}
		else if (name.equals("height")){
			height=Integer.valueOf(value);
		}
		else if (name.equals("type")){
			type=Integer.valueOf(value);
		}
	}

	//initialize and run
	Lattice lattice = new Lattice(width, height, 0);
	f = new IsingFrame(lattice);
	runEither(lattice, f, type, equilsteps, runsteps, repeats, Tfrom, Tto, Tinterval);
}

/**
 * Running Glauber equilibrating, running, display, and file writing
 * @param lattice
 * @param f
 * @param equilsteps
 * @param runsteps
 * @param repeats
 * @param Tfrom
 * @param Tto
 * @param Tinterval
 * @throws IOException
 */
public static void runEither(Lattice lattice, IsingFrame f, int type, int equilsteps, int runsteps, int repeats
		, double Tfrom, double Tto, double Tinterval) throws IOException{
	File file;
	if (type == 1){
		file = new File("glaubertempslong.txt");
	}
	else{
		file = new File("kawasakitemps.txt");
	}

	if (!file.exists()) {
		file.createNewFile();
	}
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);
	String line1 = "# run for "+ equilsteps+ " equilsteps, "+ runsteps+" steps, averages of "+ repeats + "repeats";
	String line1b;
	if (type == 1){

		line1b= "#T M S [sterror in M over repeats] [sterror in S over repeats] [sterror in M(t), avg over repeats] ";

	}
	else {
		line1b= "#T E C [sterror in E over repeats] [sterror in C over repeats]  ";
	}
	bw.write(line1);
	bw.newLine();
	bw.write(line1b);
	for (int i = 0; i < (int)((Tto - Tfrom)/Tinterval); i++){
		double T = Tfrom + i* Tinterval;
		double[] Ms= new double[repeats];
		double[] Ss=new double[repeats];
		double[] Merrors = new double[repeats];
		double[] Serrors = new double [repeats];
		double[] Es= new double[repeats];
		double[] Cs=new double[repeats];
		double[] Eerrors = new double[repeats];
		double[] Cerrors = new double [repeats];
		lattice.setT(T);
		for (int n = 0; n < repeats; n++){
			lattice.fillRandom();
			
			
			f.setTitle("T = "+T+", run "+(n+1)+"/"+repeats+ ", equilibrating");
			if (type == 1){
				for (int j = 0; j < equilsteps; j++){
					lattice.fasterSampleGlauber();
					f.step();
				}
			}
			else{
				for (int j = 0; j < equilsteps; j++){
					lattice.fasterSampleKawasaki();
					f.step();
				}
			}
			
			f.setTitle("T = "+T+", run "+(n+1)+"/"+repeats+  ", measuring");
			double[] results ;
			results = lattice.bothStats(type, runsteps,T,f);
			Ms[n] = results[0];
			Ss[n] = results[1];
			Merrors[n] = results[2];
			Es[n] = results[3];
			Cs[n] = results[4];
			Eerrors[n]=results[5];
		}
		Stats stats = new Stats();
		String line;
		double Mavg = stats.avg(Ms);
		double Savg = stats.avg(Ss);
		double Savgerror = stats.sterror(Ss);
		double Mavgerror = stats.sterror(Ms);
		double Merroravg = stats.avg(Merrors);
		double Eerroravg = stats.avg(Eerrors);
		double Eavg = stats.avg(Es);
		double Cavg = stats.avg(Cs);
		double Cavgerror = stats.sterror(Cs);
		double Eavgerror = stats.sterror(Es);
		bw.newLine();
		if (type ==1 ){
			line = T + " " + Mavg + " " + Savg + " "+Mavgerror+" " + Savgerror+" " + Merroravg;}
		else{line = T + " " + Eavg + " " + Cavg + " "+Eavgerror+" " + Cavgerror+" " + Eerroravg;}
		bw.write(line);

	}
	bw.close();
}
}