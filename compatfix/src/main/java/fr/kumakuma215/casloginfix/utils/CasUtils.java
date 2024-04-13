package fr.kumakuma215.casloginfix.utils;

import java.util.Map;

public class CasUtils {

	private static final Map<String, String> diplomaToAccessory = Map.of(
			"IIEIN", "info",
			"IIEEL", "elec",
			"IIETE", "telecom",
			"IIEMM", "matmeca",
			"IAERS", "rsi",
			"IAEEE", "see"
	);

	public static String getAccessoryFromDiploma(String diploma){
		diploma = diploma.substring(0, diploma.length()-1);
		return diplomaToAccessory.getOrDefault(diploma, "other");
	}
}
