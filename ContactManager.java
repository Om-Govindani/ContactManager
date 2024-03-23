import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

class Contact {
    String name;
    String phoneNumber;
    String email;

    Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}

class ContactManager {
    private List<Contact> contacts;

    ContactManager() {
        contacts = new ArrayList<>();
    }

    void createContact(String name, String phoneNumber, String email) {
        Contact newContact = new Contact(name, phoneNumber, email);
        contacts.add(newContact);
    }

    List<Contact> getContacts() {
        return contacts;
    }

    void generatePDF() {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont font = PDType1Font.HELVETICA_BOLD;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(font, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Contacts");
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                int lineNumber = 0;
                for (Contact contact : contacts) {
                    contentStream.showText("Name: " + contact.name + ", Phone: " + contact.phoneNumber + ", Email: " + contact.email);
                    contentStream.newLine();
                    lineNumber++;
                    if (lineNumber % 40 == 0) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream.moveTo(100, 700);
                        contentStream.beginText();
                    }
                }

                contentStream.endText();
            }

            document.save("Contacts.pdf");
            document.close();

            System.out.println("PDF generated successfully!");

            // Open the generated PDF
            File pdfFile = new File("Contacts.pdf");
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                System.out.println("PDF file not found!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Contact Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.setLayout(new GridLayout(4, 2, 5, 5));

            JTextField nameField = new JTextField();
            JTextField phoneField = new JTextField();
            JTextField emailField = new JTextField();

            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);

            JButton addButton = new JButton("Add Contact");
            JButton pdfButton = new JButton("Generate PDF");

            ContactManager contactManager = new ContactManager();

            addButton.addActionListener(e -> {
                String name = nameField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();
                contactManager.createContact(name, phone, email);
                nameField.setText("");
                phoneField.setText("");
                emailField.setText("");
            });

            pdfButton.addActionListener(e -> {
                contactManager.generatePDF();
            });

            panel.add(addButton);
            panel.add(pdfButton);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}
