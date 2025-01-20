import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class miniprojectmessmanage{

    private static final ArrayList<Student> students = new ArrayList<>();
    private static final String FILE_NAME = "students_data.txt";

    public static void main(String[] args) {
        loadData();
        JFrame frame = new JFrame("Student Mess Management System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Student Mess Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        JButton addButton = new JButton("Add New Student");
        JButton viewButton = new JButton("View All Students");
        JButton searchButton = new JButton("Search Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);
        buttonPanel.setBackground(Color.BLACK);

        addButton.setBackground(Color.DARK_GRAY);
        addButton.setForeground(Color.WHITE);
        viewButton.setBackground(Color.DARK_GRAY);
        viewButton.setForeground(Color.WHITE);
        searchButton.setBackground(Color.DARK_GRAY);
        searchButton.setForeground(Color.WHITE);
        updateButton.setBackground(Color.DARK_GRAY);
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.DARK_GRAY);
        deleteButton.setForeground(Color.WHITE);
        exitButton.setBackground(Color.DARK_GRAY);
        exitButton.setForeground(Color.WHITE);

        frame.add(buttonPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> addStudentGUI(frame));
        viewButton.addActionListener(e -> viewStudentsGUI());
        searchButton.addActionListener(e -> searchStudentGUI());
        updateButton.addActionListener(e -> updateStudentGUI());
        deleteButton.addActionListener(e -> deleteStudentGUI());
        exitButton.addActionListener(e -> {
            saveData(); 
            System.exit(0);
        });

        frame.setVisible(true);
    }


    private static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                students.add(new Student(details[0], details[1], details[2], details[3], details[4], Boolean.parseBoolean(details[5])));
            }
        } catch (IOException e) {
            System.out.println("No previous data found. Starting fresh.");
        }
    }
    
    private static void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student student : students) {
                writer.write(student.toCSV() + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving data to file.");
        }
    }

    private static void addStudentGUI(JFrame parentFrame) {
        JFrame frame = new JFrame("Add New Student");
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(7, 2, 5, 5));

        JTextField nameField = new JTextField();
        JTextField rollNumberField = new JTextField();
        JTextField branchField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField validityField = new JTextField();
        JCheckBox feePaidBox = new JCheckBox("Fee Paid");

        frame.add(new JLabel("Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Roll Number:"));
        frame.add(rollNumberField);
        frame.add(new JLabel("Branch:"));
        frame.add(branchField);
        frame.add(new JLabel("Phone:"));
        frame.add(phoneField);
        frame.add(new JLabel("Validity (MM/YYYY):"));
        frame.add(validityField);
        frame.add(new JLabel("Fee Paid:"));
        frame.add(feePaidBox);

        JButton saveButton = new JButton("Save");
        frame.add(saveButton);
        JButton cancelButton = new JButton("Cancel");
        frame.add(cancelButton);

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String rollNumber = rollNumberField.getText();
            String branch = branchField.getText();
            String phone = phoneField.getText();
            String validity = validityField.getText();
            boolean feePaid = feePaidBox.isSelected();

            if (name.isEmpty() || rollNumber.isEmpty() || branch.isEmpty() || phone.isEmpty() || validity.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled out.");
                return;
            }

            students.add(new Student(name, rollNumber, branch, phone, validity, feePaid));
            JOptionPane.showMessageDialog(frame, "Student added successfully!");
            saveData();
            frame.dispose();
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    
    private static void viewStudentsGUI() {
        JFrame frame = new JFrame("View All Students");
        frame.setSize(800, 400);

        String[] columnNames = {"Name", "Roll Number", "Branch", "Phone", "Validity", "Fee Paid"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Student student : students) {
            tableModel.addRow(new Object[]{student.getName(), student.getRollNumber(), student.getBranch(),
                    student.getPhone(), student.getValidity(), student.isFeePaid() ? "Yes" : "No"});
        }

        JTable table = new JTable(tableModel);
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    
    private static void searchStudentGUI() {
        String input = JOptionPane.showInputDialog("Enter Roll Number or Name to Search:");
        if (input == null || input.isEmpty()) return;

        for (Student student : students) {
            if (student.getRollNumber().equalsIgnoreCase(input) || student.getName().equalsIgnoreCase(input)) {
                JOptionPane.showMessageDialog(null, student.toString());
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "Student not found.");
    }

    
    private static void updateStudentGUI() {
        String rollNumber = JOptionPane.showInputDialog("Enter Roll Number of the Student to Update:");
        if (rollNumber == null || rollNumber.isEmpty()) return;

        for (Student student : students) {
            if (student.getRollNumber().equalsIgnoreCase(rollNumber)) {
                JTextField nameField = new JTextField(student.getName());
                JCheckBox feePaidBox = new JCheckBox("Fee Paid", student.isFeePaid());

                Object[] fields = {
                        "Name:", nameField,
                        "Fee Paid:", feePaidBox
                };

                int option = JOptionPane.showConfirmDialog(null, fields, "Update Student Details", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    student.setName(nameField.getText());
                    student.setFeePaid(feePaidBox.isSelected());
                    saveData();
                    JOptionPane.showMessageDialog(null, "Student details updated successfully!");
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "Student not found.");
    }
    private static void deleteStudentGUI() {
        String rollNumber = JOptionPane.showInputDialog("Enter Roll Number or Name to Delete:");
        if (rollNumber == null || rollNumber.isEmpty()) return;

        students.removeIf(student -> student.getRollNumber().equalsIgnoreCase(rollNumber) || student.getName().equalsIgnoreCase(rollNumber));
        saveData();
        JOptionPane.showMessageDialog(null, "Student deleted successfully!");
    }

    
    static class Student {
        private String name;
        private String rollNumber;
        private String branch;
        private String phone;
        private String validity;
        private boolean feePaid;

        public Student(String name, String rollNumber, String branch, String phone, String validity, boolean feePaid) {
            this.name = name;
            this.rollNumber = rollNumber;
            this.branch = branch;
            this.phone = phone;
            this.validity = validity;
            this.feePaid = feePaid;
        }

        public String getName() {
            return name;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public String getBranch() {
            return branch;
        }

        public String getPhone() {
            return phone;
        }

        public String getValidity() {
            return validity;
        }

        public boolean isFeePaid() {
            return feePaid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFeePaid(boolean feePaid) {
            this.feePaid = feePaid;
        }

        public String toCSV() {
            return name + "," + rollNumber + "," + branch + "," + phone + "," + validity + "," + feePaid;
        }

        
        public String toString() {
            return "Name: " + name + "\nRoll Number: " + rollNumber + "\nBranch: " + branch +
                    "\nPhone: " + phone + "\nValidity: " + validity + "\nFee Paid: " + (feePaid ? "Yes" : "No");
}
}
}



