package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String licenseNumber;

    public User(int id, String username, String password, String role, String licenseNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.licenseNumber = licenseNumber;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getLicenseNumber() { return licenseNumber; }

    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
}