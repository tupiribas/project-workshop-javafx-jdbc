package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.dao.DaoFactory;
import model.entities.Department;

public class DepartmentFormController implements Initializable {

	public Department entity;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	public void setEntity(Department entity) {
		this.entity = entity;
	}

	@FXML
	private Label labelErrorMensage;

	@FXML
	private Button btCancel;

	@FXML
	private Button btSave;

	@FXML
	public void onBtSaveAction() {
		String nome = txtName.getText();
		Department department = new Department(nome);
		DaoFactory.createDepartmentDao().insert(department);
	}

	@FXML
	public void onBtCancelAction() {
		System.out.println("onBtCancelAction");
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
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

}
