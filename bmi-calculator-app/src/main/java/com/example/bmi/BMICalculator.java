package com.example.bmi;

import java.util.Scanner;

import com.example.common.Calculator;


public class BMICalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calc = new Calculator();

        System.out.print("Enter weight in kg: ");
        double weight = scanner.nextDouble();

        System.out.print("Enter height in meters: ");
        double height = scanner.nextDouble();

        double heightSquared = calc.multiply(height, height);
        double bmi = calc.divide(weight, heightSquared);

        System.out.println("Your BMI is: " + bmi);
    }
}
