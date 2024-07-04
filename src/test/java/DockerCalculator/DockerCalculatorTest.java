package DockerCalculator;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DockerCalculatorTest {

    @BeforeClass
    public void setUp() {
        System.out.println("Setting up the test environment...");
    }

    @AfterClass
    public void tearDown() {
        System.out.println("Cleaning up the test environment...");
    }

   @Test
    public void testAdd() {
        runDockerCommand("add", 8, 5, "Result: 13");
        runDockerCommand("add", 1.0000001, 1.0000001, "Result: 2.0000002");
        runDockerCommand("add", 1.00000001, 1.00000001, "Result: 2.0"); // Rounding error
    }

    @Test
    public void testSubtract() {
        runDockerCommand("subtract", 8, 5, "Result: 3");
        runDockerCommand("subtract", 5, 8, "Result: -3");
    }

    @Test
    public void testMultiply() {
        runDockerCommand("multiply", 8, 5, "Result: 40");
        runDockerCommand("multiply", 1e8, 1e8, "Result: 10000000000000000"); // Scientific notation
    }

    @Test
    public void testDivide() {
        runDockerCommand("divide", 10, 5, "Result: 2");
        runDockerCommand("divide", 1, 3, "Result: 0.33333333");
        runDockerCommand("divide", 1, 0, "Error: Cannot divide by zero"); // Division by zero
        runDockerCommand("divide", 1, 10000000, "Result: 0.0000001");
        runDockerCommand("divide", 1, 100000000, "Result: 0"); // Rounding error
    }

    @Test
    public void testInvalidOperands() {
        runDockerCommandWithInvalidOperands("add a b", "Invalid argument. Must be a numeric value.");
        runDockerCommandWithInvalidOperands("add ", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("add 8", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("add 8 8 8", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("subtract ", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("subtract a b", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("subtract 8", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("subtract 8 8 8", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("multiply ", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("multiply a b", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("multiply 8", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("multiply 8 8 8", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("divide a b", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("divide ", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("divide 8", "Error: Commands take exactly two numbers.");
        runDockerCommandWithInvalidOperands("divide 8 8 8", "Error: Commands take exactly two numbers.");
    }

    private void runDockerCommand(String operation, double num1, double num2, String expectedMessage) {
        try {
            System.out.println("docker run --rm public.ecr.aws/l4q9w4c5/loanpro-calculator-cli " + operation + " " + num1 + " " + num2);
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd", "/c", "docker run --rm public.ecr.aws/l4q9w4c5/loanpro-calculator-cli " + operation + " " + num1 + " " + num2);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            System.out.println("(Output) " + output);
            System.out.println("(Expected Message) " + expectedMessage + "\n");
            int exitCode = process.waitFor();
            Assert.assertTrue(output.toString().contains(expectedMessage), "The expected result is not present in the output.");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("An error occurred while executing the Docker command.");
        }
    }

    private void runDockerCommandWithInvalidOperands(String invalidOperation, String expectedMessage) {
        try {
            System.out.println("docker run --rm public.ecr.aws/l4q9w4c5/loanpro-calculator-cli " + invalidOperation);
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd", "/c", "docker run --rm public.ecr.aws/l4q9w4c5/loanpro-calculator-cli " + invalidOperation);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            System.out.println("(Output) " + output);
            System.out.println("(Expected Message) " + expectedMessage + "\n");
            int exitCode = process.waitFor();
            Assert.assertTrue(output.toString().contains(expectedMessage), "The expected result is not present in the output.");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("An error occurred while executing the Docker command.");
        }
    }
}