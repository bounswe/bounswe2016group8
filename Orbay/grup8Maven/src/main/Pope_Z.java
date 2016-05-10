public class Pope_Z {
	private int ordinal;
	private int ageOfDeath;
	String name;
	Pope_Z(int o, int a, String _name){
		ordinal = o;
		ageOfDeath = a;
		name =  _name;
	}
	public int getAgeOfDeath() {
		return ageOfDeath;
	}
	public String getName() {
		return name;
	}
	public int getOrdinal() {
		return ordinal;
	}
}
