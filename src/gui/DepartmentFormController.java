package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exception.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;

	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	private Label labelErrorMensage;

	@FXML
	private Button btCancel;

	@FXML
	private Button btSave;

	private Department getFormData() {
		Department department = new Department();
		ValidationException exception = new ValidationException("Validation Error");
		
		department.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrorMessage("name", "Field cant't be empty!");
		}
		department.setName(txtName.getText());
		
		if (exception.getErrorMessages().size() > 0) {
			throw exception;
		}
		
		return department;
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Defalt cod.:04 >>>Entity was null!");
		}
		if (service == null) {
			throw new IllegalStateException("Defalt cod.:03 >>>Service was null!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			Utils.currentStage(event).close();
			notifyDataChangeListener();
			Alerts.showAlert("Situation", null, "Department has been updated successfully.", AlertType.INFORMATION);
		}
		catch (ValidationException e) {
			setErrorMessage(e.getErrorMessages());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, "Connect to the database", AlertType.ERROR);
		}
	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setFieldMaxLength(txtName, 30);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Defalt cod.:02 >>>Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

	private void setErrorMessage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorMensage.setText(errors.get("name"));
		}
	}
}
