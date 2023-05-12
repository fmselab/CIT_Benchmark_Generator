package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSConstraintTranslator;
import ctwedge.util.ext.Utility;
import models.BooleanParameter;
import models.EnumerativeParameter;
import models.Model;
import models.Parameter;

/**
 * This class provides a translator for models into ACTS format without any
 * dependency from external ACTS.jar
 * 
 * @author andrea
 *
 */
public class ACTSModelTranslator {

	/**
	 * Construct an ACTSModelTranslator object
	 */
	public ACTSModelTranslator() {
		
	}
	
	/**
	 * Given a model and a destination path, it saves the model in the ACTS format
	 * 
	 * @param model   the model
	 * @param dirPath the destination path
	 * @return the saved file
	 * @throws IOException
	 */
	public File saveActsTXTonlyModel(Model model, String dirPath) throws IOException {
		// The ACTS file is saved with the same name of the model and with TXT extension
		String pathToFile = model.getName() + ".txt";
		File txtFile = new File(dirPath + File.separator + pathToFile);

		// Fill the model file
		try {
			// Initial information
			PrintWriter p = new PrintWriter(txtFile);
			p.println("[System]");
			p.println("-- specify system name");
			p.println("Name: " + model.getName());
			p.println();
			// Parameters
			p.println("[Parameter]");
			p.println("-- general syntax is parameter_name : value1, value2, ...");
			String origStr;
			for (Parameter parameter : model.getParameters()) {
				// Check the parameter type
				String ptypeStr = "";
				if (parameter instanceof BooleanParameter)
					ptypeStr = "boolean";
				else if (parameter instanceof EnumerativeParameter)
					ptypeStr = "enum";
				else
					ptypeStr = "int";

				ArrayList<String> validValues = new ArrayList<String>();
				for (int i = 0; i < parameter.getValues().size(); i++) {
					validValues.add(parameter.getValues().get(i));
				}
				origStr = validValues.toString();
				String valueStr = origStr.substring(1, origStr.length() - 1);

				p.println(parameter.getName() + " (" + ptypeStr + ") : " + valueStr);
			}
			p.println();

			// Now, convert the constraints into the ACTS format and write them into the
			// file
			CitModel citModel = Utility.loadModel(model.toString());
			ACTSConstraintTranslator translator = new ACTSConstraintTranslator(citModel);

			if (model.getConstraints().size() > 0) {
				p.println("[Constraint]");
				p.println("-- this section is also optional");
				for (ctwedge.ctWedge.Constraint constraint : citModel.getConstraints()) {
					String cnStr = translator.doSwitch(constraint);
					p.println(cnStr);
				}
			}
			p.println();
			p.close();
		} catch (Exception e) {
			e.printStackTrace();

		}

		return txtFile;
	}
}
