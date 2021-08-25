package main;

import generators.Category;
import generators.Generator;
import generators.WithConstraintGenerator;
import models.Model;

public class Main {
	
	static int N_MODELS = 50;
	 
	public static void main(String[] args) {
		
		Generator g = new WithConstraintGenerator();
		Model m1 = g.generate(Category.ONLY_BOOLEAN);
		System.out.println(m1.toString());
		
		Model m2 = g.generate(Category.ALSO_ENUMS);
		System.out.println(m2.toString());
		
		Model m3 = g.generate(Category.ALSO_INTEGERS);
		System.out.println(m3.toString());
		
		Model m4 = g.generate(Category.CONSTRAINTS_WITH_RELATIONAL);
		System.out.println(m4.toString());
	}
}
