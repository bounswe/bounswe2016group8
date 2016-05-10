package webapp;
public class Data{
	String cityName;
	int year;
	int population;
	int id;
	public int yearDiff;
	public int popuDiff;
	double elevation,coor1,coor2;
	public Data(int id, String cityName,int year,int population) {
		this.id = id;
		this.cityName =cityName;
		this.year =year;
		this.population=population;
		yearDiff = 0;
		popuDiff = 0;
	}
	public int getID(){
		return id;
	}
	public String getName(){
		return cityName;
	}
	public int getYear(){
		return year;
	}
	public int getPopulation(){
		return population;
	}
}