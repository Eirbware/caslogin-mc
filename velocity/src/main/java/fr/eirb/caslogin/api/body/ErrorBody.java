package fr.eirb.caslogin.api.body;

import fr.eirb.caslogin.exceptions.api.Errors;

public class ErrorBody {
	private Errors error;

	public Errors getError() {
		return error;
	}
}
