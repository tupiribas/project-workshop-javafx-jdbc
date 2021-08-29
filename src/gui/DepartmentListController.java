package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	private DepartmentService departmentService;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew; // Toobar

	private ObservableList<Department> obsDepartmentList;

	@FXML
	public void onButtonNewAction() {
		System.out.println("onButtonNewAction");
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
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
		if (departmentService == null) {
			throw new IllegalStateException("Defect cod.:02>>> service was null");
		} else {
			/*
			 * Cria uma lista local e atribui a ela os mocks criados na classe
			 * DepartmentService
			 */
			List<Department> list = departmentService.findAll();

			// Pega os valores da lista local e joga para a obsDepartmentList
			obsDepartmentList = FXCollections.observableList(list);

			// Pega os valores obsDepartmentList e joga para a tableViewDepartment
			tableViewDepartment.setItems(obsDepartmentList);
		}
	}

}
