class Example {
    private double[] inputs; //what my examples actually are
    private int[] correct;

    Example(double[] inputs, int output, int numOutputs) {
        this.inputs = inputs;
        correct = new int[numOutputs];
        java.util.Arrays.fill(correct, 0);
        correct[output] = 1;
    }

    double[] getInputs() {
        return inputs;
    }

    int[] getCorrect() { return correct;}
}