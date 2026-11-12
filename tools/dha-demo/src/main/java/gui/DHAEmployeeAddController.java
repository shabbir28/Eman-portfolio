package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import repository.BranchRepository;
import repository.DHAEmployeeRepository;

public class DHAEmployeeAddController {

    @FXML private TextField dhaIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<BranchRepository.BranchItem> branchComboBox;
    @FXML private TextField designationField;
    @FXML private TextField scaleField;

    private final BranchRepository branchRepository = new BranchRepository();
    private final DHAEmployeeRepository employeeRepository = new DHAEmployeeRepository();
    
    // To track if we are in edit mode (disable ID editing if needed, or track old ID)
    private boolean isEditMode = false;

    public void initialize() {
        loadBranches();

        // If there are branches, select the first by default so user doesn't forget
        if (branchComboBox.getItems() != null && !branchComboBox.getItems().isEmpty()) {
            branchComboBox.getSelectionModel().selectFirst();
        } else {
            // Inform user there are no branches configured
            DisplayAlert.showInfo("Info", "No branches found. Please add branches before adding DHA employees.");
        }
    }

    private void loadBranches() {
        ObservableList<BranchRepository.BranchItem> branches = FXCollections.observableArrayList(branchRepository.findAll());
        branchComboBox.setItems(branches);
    }

    public void setInitialData(repository.DHAEmployeeRepository.DHAEmployeeItem data) {
        if (data != null) {
            isEditMode = true;
            dhaIdField.setText(data.getDhaId());
            dhaIdField.setDisable(true); // Don't allow changing PK in edit mode
            nameField.setText(data.getEmpName());
            designationField.setText(data.getDesignation());
            scaleField.setText(String.valueOf(data.getScale()));

            // Select branch
            for (BranchRepository.BranchItem b : branchComboBox.getItems()) {
                if (b.getId() == data.getBranchId()) {
                    branchComboBox.getSelectionModel().select(b);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validation
            String dhaId = dhaIdField.getText().trim();
            String name = nameField.getText().trim();
            BranchRepository.BranchItem branch = branchComboBox.getValue();
            String designation = designationField.getText().trim();
            String scaleText = scaleField.getText().trim();

            if (dhaId.isEmpty() || name.isEmpty() || branch == null || designation.isEmpty() || scaleText.isEmpty()) {
                DisplayAlert.showError("Validation", "Please fill all fields");
                return;
            }

            int scale;
            try {
                scale = Integer.parseInt(scaleText);
            } catch (NumberFormatException e) {
                DisplayAlert.showError("Validation", "Scale must be a number");
                return;
            }

            // Debug: show what will be saved
            DisplayAlert.showInfo("Debug", "Saving employee:\nDHA ID: " + dhaId + "\nName: " + name + "\nBranch ID: " + branch.getId() + "\nDesignation: " + designation + "\nScale: " + scale);

            // Check duplicate if new
            if (!isEditMode && employeeRepository.exists(dhaId)) {
                DisplayAlert.showError("Duplicate", "DHA ID " + dhaId + " already exists.");
                return;
            }

            // Save
            repository.DHAEmployeeRepository.DHAEmployeeItem item = new repository.DHAEmployeeRepository.DHAEmployeeItem(
                dhaId, name, branch.getId(), branch.getBranchName(), designation, scale
            );

            if (isEditMode) {
                employeeRepository.update(item);
                DisplayAlert.showInfo("Success", "Employee updated successfully");
            } else {
                employeeRepository.save(item);
                DisplayAlert.showInfo("Success", "Employee added successfully");
            }

            goBack();

        } catch (Exception e) {
            DisplayAlert.showError("Error", "Operation failed: " + e.getMessage());
            System.err.println("DHAEmployeeAddController: error in handleSave - " + e);
        }
    }

    @FXML
    private void handleCancel() {
        goBack();
    }

    private void goBack() {
        try {
            AnchorPane contentPane = (AnchorPane) dhaIdField.getScene().lookup("#contentPane"); // This matches ID in dha_employee.fxml
            if (contentPane != null && contentPane.getUserData() instanceof DHAEmployeeController) {
                 ((DHAEmployeeController) contentPane.getUserData()).showSelection();
            }
        } catch (Exception e) {
            System.err.println("DHAEmployeeAddController: error in goBack - " + e);
        }
    }
}
