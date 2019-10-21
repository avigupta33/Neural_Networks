class Neuron {
    double[] inputWeights;
    private double bias;

    Neuron(int inputNum) {
        inputWeights = new double[inputNum];
        initializeWeights();
       //System.out.println("Neuron's Input Weights: " + Arrays.toString(inputWeights));
    }

    private void initializeWeights() {
        for (int i = 0; i<inputWeights.length; i++) {
            inputWeights[i] = (Math.random()-0.5)/10;
        }
        bias = (Math.random()-0.5)/10;
    }

    private double activation(double val) {
        return 1/(1+Math.exp(-1*val));
    }

    double fire(double[] inputs) {
        double total = 0;
        for (int i = 0; i<inputs.length; i++) {
            //System.out.println("Adding: " + inputWeights[i]*inputs[i]);
            total += inputWeights[i]*inputs[i];
        }
        total += bias;
        return activation(total);
    }

    void addToBias(double addend) {bias+=addend;}
}
