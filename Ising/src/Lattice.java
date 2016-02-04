import java.util.Random;


public class Lattice {
private static final double k = 1;
private static final double J = 1;
private int[][] grid;
private int width;
private int height;
private double T;
private double threshold = 400;
private Random random = new Random();
/**
 * constructor initiates with false / down
 */
public Lattice(int width, int height, double T){
	this.width = width;
	this.height=height;
	this.T=T;
	this.grid= new int[height][width];
	fillRandom();
}

private void fillRandom(){
	for (int i=0; i < height; i++){
		for (int j=0; j < width; j++){
			if (random.nextDouble() < .5){
		setSpin(i,j,1);}
			else{setSpin(i,j,-1);
			}
		}
		}
}

public void setT(double T){
	this.T=T;
}
public double getT(){
	return T;}

public void setSpin (int i, int j,int spin){
	if (checkSpin(spin)){grid[i][j]=spin;}
	else{System.out.println("cannot set spin to "+ spin+" , proceeding with +1");
			grid[i][j]=1;}
}

int getSpin(int i, int j){
	//correct for periodic boundary
	if (i==-1){i = height-1;}
	else if (i== height){ i=0;}
	if (j==-1){j= width-1;}
	else if (j== width){ j= 0;}
	
	//check valid spin
	if (checkSpin(grid[i][j])){return grid[i][j];}
	else{System.out.println("tried to get invalid spin "+ grid[i][j] + " at "+ i+", "+j + 
			" , proceeding with +1");
	return 1;}
}

private boolean checkSpin(int spin){
	if (spin == -1 || spin == 1){return true;}
	else {return false;}
}
public void sampleGlauber(){
	int randomi=random.nextInt(height);
	int randomj=random.nextInt(width);
	double Ebefore=totalEnergy();
	
	//can only differ by +- 2, 4, 6, 8?
	
	flip(randomi, randomj);
	double Eafter=totalEnergy();
	
	double difference = Ebefore - Eafter;
	//keep if DifferenceE < 0, 
	//otherwise change back with probablity 1-e^Difference/kT
	if (difference < 0 ){
		double Pthreshold = Math.exp(difference / (k*T));
		if (random.nextDouble() > Pthreshold){
			flip(randomi, randomj);
		}
	}
}

public void fasterSampleGlauber(){
	int randomi=random.nextInt(height);
	int randomj=random.nextInt(width);
	double difference = glauberDifference(randomi, randomj);;
	//keep if DifferenceE < 0, 
	//otherwise change back with probablity 1-e^Difference/kT
	if (difference > 0 ){
		flip(randomi, randomj);
	}
	else{
		double Pthreshold = Math.exp(difference / (k*T));
		if (random.nextDouble() < Pthreshold){
			flip(randomi, randomj);
		}
	}
}

private double glauberDifference(int i, int j){
	int before = leftmultiple(i,j) + rightmultiple(i,j) + downmultiple(i,j) + upmultiple(i,j);
	int after = -leftmultiple(i,j) - rightmultiple(i,j) - downmultiple(i,j) - upmultiple(i,j);
	int numberdiff = before - after;
	double energydiff = -1 * J * numberdiff;
	return energydiff;
}

private void flip(int i, int j) {
	setSpin(i,j, grid[i][j]*-1);
}

public void sampleKawasaki(){
int randomi1=random.nextInt(height);
int randomj1=random.nextInt(width);
int randomi2=random.nextInt(height);
int randomj2=random.nextInt(width);
double Ebefore=totalEnergy();

exchange(randomi1,randomj1,randomi2,randomj2);
double Eafter=totalEnergy();

double difference = Ebefore - Eafter;

//keep if DifferenceE < 0, 
//otherwise change back with probablity 1-e^Difference/kT
if (difference < 0 ){
	double Pthreshold = Math.exp(difference / (k*T));
	if (random.nextDouble() > Pthreshold){
		exchange(randomi1,randomj1, randomi2, randomj2);
	}
}
}

public void fasterSampleKawasaki(){
	int randomi1=random.nextInt(height);
	int randomj1=random.nextInt(width);
	int randomi2=random.nextInt(height);
	int randomj2=random.nextInt(width);
	double difference;
	if (getSpin(randomi1,randomj1)!=getSpin(randomi2,randomj2)){
		if(!checkNeighbors(randomi1, randomj1, randomi2, randomj2)){
		difference = glauberDifference(randomi1, randomj1)+ glauberDifference(randomi2,randomi2);
		}
		else{
			//??? -1? -2?
		difference = glauberDifference(randomi1, randomj1)+ glauberDifference(randomi2,randomi2)-4;
		}
	}
	else{
		difference = 0;
	}

	if (difference > 0 ){
		exchange(randomi1, randomj1, randomi2, randomj2);
	}
	else{
		double Pthreshold = Math.exp(difference / (k*T));
		if (random.nextDouble() < Pthreshold){
			exchange(randomi1, randomj1, randomi2, randomj2);
		}
	}

}

private boolean checkNeighbors(int i1, int j1, int i2,
		int j2) {
	int iup = i2-1;
	int idown=i2+1;
	int jleft=j2-1;
	int jright=j2+1;
	
	if (idown==-1){idown = height-1;}
	else if (iup== height){ iup=0;}
	if (jleft==-1){jleft= width-1;}
	else if (jright== width){ jright= 0;}
	
	if (i1 == i2 && (j1== jright || j1 == jleft)){return true;
	}
	else if  (j1 == j2 && (i1== idown || i1 == iup)){return true;
	}
	else{ return false;}
}

private void exchange(int i1, int j1, int i2, int j2) {
	int spin1 = grid[i1][j1];
	int spin2= grid[i2][j2];
	setSpin(i1,j1, spin2);
	setSpin(i2,j2, spin1);
}

private double totalEnergy(){
	double neighborsum = 0;
	for (int i=0; i < height; i++){
		for (int j=0; j < width; j++){
			neighborsum += upmultiple(i,j);
			neighborsum += leftmultiple(i,j);
		}
	}
	double E = -1 * J * neighborsum;
	return E;
}
private int upmultiple(int i, int j){
	int product = getSpin(i,j) * getSpin (i, j-1);
	return product;
}
private int leftmultiple(int i, int j){
	int product = getSpin(i,j) * getSpin (i-1, j);
	return product;
}
private int downmultiple(int i, int j){
	int product = getSpin(i,j) * getSpin (i, j+1);
	return product;
}
private int rightmultiple(int i, int j){
	int product = getSpin(i,j) * getSpin (i+1, j);
	return product;
}

public int getwidth() {
	return width;
}

public int getheight() {
	return height;
}

public double[] glauberMagnetisation(int stepnumber, double T){
	this.T=T;
	double avgM = 0;
	double avgMsquared=0;
	for (int t = 0; t < 100 ; t++){
		this.fasterSampleGlauber();}
for (int t = 0; t < stepnumber ; t++){
	this.fasterSampleGlauber();
	avgM += getTotalM();
	avgMsquared+= getTotalM()*getTotalM();
	}
avgM = avgM / (double)stepnumber;
avgMsquared = avgMsquared / (double)stepnumber;
double susceptibility = (avgMsquared- (avgM* avgM)) / (width * height * k * this.T );
double[] results = new double[]{avgM,susceptibility};
return results;
}

private void reachEquilibrium() {
	int unchangedCount = 0;
	double lastM = 0;
	while (unchangedCount < 3){
		for (int i = 0; i<100; i++){
		this.fasterSampleGlauber();}
		double thisM = getTotalM();
		System.out.println("M = "+ thisM);
		if (Math.abs(lastM-thisM)< threshold)  {unchangedCount+=1;}
		else{unchangedCount = 0;}
		lastM = thisM;
		}
}

public double getTotalM() {
	double Msum = 0;
	for (int i=0; i < height; i++){
		for (int j=0; j < width; j++){
			Msum += grid[i][j];
		}
	}
	return Msum;
}

}