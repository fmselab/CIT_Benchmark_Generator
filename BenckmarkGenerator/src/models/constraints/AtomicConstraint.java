package models.constraints;

public class AtomicConstraint extends Constraint {
	String expression;

	public AtomicConstraint() {
		this.expression = "";
	}
	
	public AtomicConstraint(String expression) {
		this.expression = expression;
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public String toString() {
		return expression;
	}
}
