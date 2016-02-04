
public class Stats {

	public Stats(){}
	
	public double total(double[] values) {
		double total = 0;
		for (int i =0; i< values.length; i++){
			total += values[i];
		}
		return total;
	}
	
	public double avg(double[] values){
		double avg = total(values)/(double)(values.length);
		return avg;
	}
	
	public double stdev(double[] values) {
		double avg = avg(values);
		double diffsquaresum = 0;
		for (int i =0; i< values.length; i++){
			diffsquaresum += (values[i]-avg)*(values[i]-avg);
		}
		double variance =diffsquaresum /(double)(values.length -1);
		double stdev = Math.sqrt(variance);
		return stdev;
	}

	public double sterror(double[] values){
		double sterror = stdev(values) / Math.sqrt((double)values.length);
		return sterror;
	}
	
}
