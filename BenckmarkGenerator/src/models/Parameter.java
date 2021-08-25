package models;

public abstract class Parameter {
	String name;
		
	public abstract String toString();	
	
	public abstract String getRandomValue();

	public String getName() {
		return name; 
	}
}
