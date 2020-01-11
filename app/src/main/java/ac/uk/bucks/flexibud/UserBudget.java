package ac.uk.bucks.flexibud;

import java.util.Date;

public class UserBudget {
    private String userName;
    private Double calculatedCost;
    private Double setBudget;
    private Double remainingBudget;
    private Date dateofLastSubmission;
    private Date dateofBudgetSet;

    public UserBudget() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getCalculatedCost() {
        return calculatedCost;
    }

    public void setCalculatedCost(Double calculatedCost) {
        this.calculatedCost = calculatedCost;
    }

    public Double getSetBudget() {
        return setBudget;
    }

    public void setSetBudget(Double setBudget) {
        this.setBudget = setBudget;
    }

    public Double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(Double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public Date getDateofLastSubmission() {
        return dateofLastSubmission;
    }

    public void setDateofLastSubmission(Date dateofLastSubmission) {
        this.dateofLastSubmission = dateofLastSubmission;
    }

    public Date getDateofBudgetSet() {
        return dateofBudgetSet;
    }

    public void setDateofBudgetSet(Date dateofBudgetSet) {
        this.dateofBudgetSet = dateofBudgetSet;
    }
}
