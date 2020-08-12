package pl.cekus.antologicproject.form;

import javax.validation.constraints.Positive;

public class UserFilterForm extends UserBaseForm {

    @Positive(message = "cost per hour must be greater than zero")
    private Double minCost;

    @Positive(message = "cost per hour must be greater than zero")
    private Double maxCost;

    public UserFilterForm(String login, String firstName, String lastName, String role,
                          Double minCost, Double maxCost) {
        super(login, firstName, lastName, role);
        this.minCost = minCost;
        this.maxCost = maxCost;
    }

    public Double getMinCost() {
        return minCost;
    }

    public Double getMaxCost() {
        return maxCost;
    }
}
