package models.constraints;

public class Constraint {
	Constraint left;
	Constraint right;
	String symbol;
	
	public Constraint() {
		this.left = null;
		this.right = null;
		this.symbol = "";
	}
	
	public Constraint(Constraint left, Constraint right, String symbol) {
		this.left =left;
		this.right = right;
		this.symbol = symbol;
	}
	
	public Constraint getLeft() {
		return left;
	}
	
	public void setLeft(Constraint left) {
		this.left = left;
	}
	
	public Constraint getRight() {
		return right;
	}
	
	public void setRight(Constraint right) {
		this.right = right;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String toString() {
		return "(" + left.toString() + " " + this.symbol + " " + right.toString() + ")";
	}
}
