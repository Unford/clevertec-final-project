package ru.clevertec.banking.deposit.util;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class CamelCaseAndUnderscoreNameGenerator extends DisplayNameGenerator.Standard {
    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return replaceCamelCaseAndUnderscore(super.generateDisplayNameForClass(testClass));
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        return replaceCamelCaseAndUnderscore(super.generateDisplayNameForNestedClass(nestedClass));
    }


    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return this.replaceCamelCaseAndUnderscore(testMethod.getName()) + " " +
                DisplayNameGenerator.parameterTypesAsString(testMethod);
    }

    String replaceCamelCaseAndUnderscore(String name) {
        name = name.replaceAll("([A-Z])", " $1");
        name = name.replaceAll("([0-9_]+)", " $1");
        return name;
    }
}
