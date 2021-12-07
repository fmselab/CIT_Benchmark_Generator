package models.constraints;

public class NotConstraint extends AtomicConstraint {

	private static final String NOT = "not";

	public NotConstraint() {
		this.expression = "";
		this.symbol = NOT;
	}

	public NotConstraint(String expression) {
		this.expression = expression;
		this.symbol = NOT;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String toString() {
		return "(" + NOT + " (" + expression + "))";
	}
}
