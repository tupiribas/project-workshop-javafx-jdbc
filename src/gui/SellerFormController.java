package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	
	/*Mensagem de erro*/
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	private ObservableList<Department> obsList;

	@FXML
	private Button btCancel;

	@FXML
	private Button btSave;

	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		// Verifica o Nome
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrorMessage("name", "Field cant't be empty!");
		}
		obj.setName(txtName.getText());
		
		// Verifica o Email
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErrorMessage("email", "Field cant't be empty!");
		}
		obj.setEmail(txtEmail.getText());
		
		// Verifica o DatePicker 
		if (dpBirthDate.getValue() == null) {
			exception.addErrorMessage("birthDate", "Field cant't be empty!");
		}
		else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		// Verifica o salário
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addErrorMessage("baseSalary", "Field cant't be empty!");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		// Verificar o comboBox
		obj.setDepartment(comboBoxDepartment.getValue());
		
		// Verifica a quantidade de erros
		if (exception.getErrorMessages().size() > 0) {
			throw exception;
		}
		
		return obj;
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
			Alerts.showAlert("Situation", null, "Seller has been updated successfully.", AlertType.INFORMATION);
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
		Constraints.setFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setFieldMaxLength(txtEmail, 40);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Defalt cod.:02 >>>Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		/* Transforma de Date para instante, além de visualizar 
		 * o fuso horário da máquina do usuário, armazena o valor 
		 * da data em dbBirthDate.
		 * 
		 * */ 
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		
		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	public void loadAssociateObject() {
		if (departmentService == null) {
			throw new IllegalStateException("Defect cod.:07>>> DepartmentService was null");
		}
		
		// Cria uma lista local
		List<Department> list = departmentService.findAll();
		// Atribui  um observador a uma obsList
		obsList = FXCollections.observableArrayList(list);
		// O comboBox armazena o obsList
		comboBoxDepartment.setItems(obsList);
	}
		
	// Mostra o erro no formulário
	private void setErrorMessage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText(fields.contains("name") ? errors.get("name") : "");
		
		labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		
		labelErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");
		
		labelErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");
	}
	
	private void  initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
