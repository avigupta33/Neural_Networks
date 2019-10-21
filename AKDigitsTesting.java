import java.util.ArrayList;

public class AKDigitsTesting implements TestingDevice {
    private double learningRate;
    private double desiredAccuracy;
    private int hiddenNum;
    private int sensorNum;
    private Example[] training;
    private Example[] testing;
    private double portionUsedForLearning;
    private int learningEpochs;
    private double testingAccuracy;
    private double validationAccuracy;

    AKDigitsTesting(double learningRate, double desiredAccuracy, int hiddenNum, int sensorNum, double portionUsedForLearning) {
        this.learningRate = learningRate;
        this.desiredAccuracy = desiredAccuracy;
        this.hiddenNum = hiddenNum;
        this.portionUsedForLearning = portionUsedForLearning;
        this.sensorNum = sensorNum;

        System.out.println("AK Digit Testing results: ");
        System.out.println("    - " + hiddenNum + " hidden neurons");
        System.out.println("    - " + "Learning rate of: " + learningRate);
        System.out.println("    - " + "Validation threshold of: " + desiredAccuracy);
    }


    private ArrayList<Example> readFile(String filename) {
        ArrayList<Example> ex = new ArrayList<>();
        SimpleFile file = new SimpleFile(filename);

        for(String line: file) {
            double[] inputs = new double[64];
            String[] pieces = line.split(",");
            for (int i = 0; i<64; i++) {
                inputs[i] = Double.parseDouble(pieces[i]) /16;
            }
            int output = Integer.parseInt(pieces[64]);

            ex.add(new Example(inputs, output, 10));
        }
        return ex;
    }

    @Override
    public void run () {
        training = readFile("digits-train.txt").toArray(new Example[0]);
        testing =  readFile("digits-test.txt").toArray(new Example[0]);

        AviNeuralNet tester = new AviNeuralNet(sensorNum, hiddenNum, 10, learningRate);
        learningEpochs = tester.learn(training, portionUsedForLearning, desiredAccuracy);
        testingAccuracy = tester.test(testing);
        validationAccuracy = tester.getValidationAccuracy();

    }

    @Override
    public void displayResult() {
        run();
        System.out.println("    - " + "Validation accuracy: " + validationAccuracy);
        System.out.println("    - Learning completed in " + learningEpochs  + " epochs");
        System.out.println("    - Final testing accuracy " + testingAccuracy);
        System.out.println();
    }
}