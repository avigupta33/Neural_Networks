public class AndTesting implements TestingDevice {
    private double learningRate;
    private double desiredAccuracy;
    private int hiddenNum;
    private int learningEpochs;
    private double testingAccuracy;

    AndTesting(double learningRate, double desiredAccuracy, int hiddenNum) {
        this.learningRate = learningRate;
        this.desiredAccuracy = desiredAccuracy;
        this.hiddenNum = hiddenNum;
        System.out.println("And Digit Testing results: ");
        System.out.println("    - " + hiddenNum + " hidden neurons");
        System.out.println("    - " + "Learning rate of: " + learningRate);
        System.out.println("    - " + "Validation threshold of: " + desiredAccuracy);
    }

    @Override
    public void run () {
        Example[] and = new Example[]{
                new Example(new double[]{1, 1}, 1, 2),
                new Example(new double[]{0, 0}, 0, 2),
                new Example(new double[]{1, 0}, 0, 2),
                new Example(new double[]{0, 1}, 0, 2)};
        AviNeuralNet tester = new AviNeuralNet(2, hiddenNum, 2, learningRate);
        learningEpochs = tester.learn(and, 1, desiredAccuracy);
        testingAccuracy = tester.test(and);
    }

    @Override
    public void displayResult() {
        run();
        System.out.println("    - Learning completed in " + learningEpochs  + " epochs");
        System.out.println("    - Final testing accuracy " + testingAccuracy);
        System.out.println();
    }
}