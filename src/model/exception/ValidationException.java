package model.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	// k -> nome do campo, V -> mensagem de erro
	private Map<String, String> errors = new HashMap<>();
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	public Map<String, String> getErrorMessages() {
		return errors;
	}
	
	public void addErrorMessage(String fieldName, String msg) {
		errors.put(fieldName, msg);
	}

}
