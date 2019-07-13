package bartboersma.calculator.model;

import lombok.Data;

@Data
public class CalculationModel {

    private double firstNumber;

    private double secondNumber;

    private CalculationType calculationType;

    private Double result;
}
