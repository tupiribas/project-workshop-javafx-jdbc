package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew; // Toobar

	private ObservableList<Department> obsDepartmentList;

	@FXML
	public void onButtonNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.service = departmentService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		// Inicia o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// Ajusta a tabela departamento à janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	public void uppdateTableView() {
		if (service == null) {
			throw new IllegalStateException("Defect cod.:05>>> service was null");
		} 
		/*
		 * Cria uma lista local e atribui a ela os valores criados na classe
		 * DepartmentService
		 */
		List<Department> list = service.findAll();

		// Atribui os valores da lista local e joga para a obsDepartmentList
		obsDepartmentList = FXCollections.observableList(list);

		// Armazena tableViewDepartment os valores de obsDepartmentList
		tableViewDepartment.setItems(obsDepartmentList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// Carregar os dados do obj no formulário
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this); // Atualiza a página
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} 
		catch (IOException e) {
			Alerts.showAlert("IO Exception", null, "Looading view form Department", AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		uppdateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if (obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
			
		});
	}
	
	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Defalt cod.:06 >>>Service was null");
			}
			try {
				service.remove(obj);
				uppdateTableView();
			} 
			catch (DbIntegrityException e) {
				Alerts.showAlert("Database integrity", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
