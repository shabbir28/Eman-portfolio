package model;

public class DHAEmployee {
    private String dhaId;
    private String name;
    private String branch;

    public DHAEmployee() {}

    public DHAEmployee(String dhaId, String name, String branch) {
        this.dhaId = dhaId;
        this.name = name;
        this.branch = branch;
    }

    public String getDhaId() { return dhaId; }
    public void setDhaId(String dhaId) { this.dhaId = dhaId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
}
