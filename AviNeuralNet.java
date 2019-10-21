import java.util.Arrays;

class xAviNeuralNet {
    private int sensorNum;
    private int hiddenNum;
    private int outputNum;

    private double validationAccuracy;

    private double learningRate;

    private Neuron[] hiddenNeurons;
    private Neuron[] outputNeurons;

    private double[] outputErrorSignals;
    private double[] hiddenErrorSignals;

    AviNeuralNet(int sensorNum, int hiddenNum, int outputNum, double learningRate) {
        this.sensorNum = sensorNum;
        this.hiddenNum = hiddenNum;
        this.outputNum = outputNum;
        this.learningRate = learningRate;

        hiddenErrorSignals = new double[hiddenNum];
        outputErrorSignals = new double[outputNum];

        hiddenNeurons = new Neuron[hiddenNum];
        outputNeurons = new Neuron[outputNum];

        for (int i = 0; i<hiddenNum; i++) hiddenNeurons[i] = new Neuron(sensorNum);
        for (int j = 0; j<outputNum; j++) outputNeurons[j] = new Neuron(hiddenNum);
    }

    private double[] getOutputs(Neuron[] layer, double[] inputs) {

        double[] outputs = new double[layer.length];

        for (int i = 0; i<layer.length; i++) outputs[i] =layer[i].fire(inputs);
        return outputs;
    }

    private int[] classifyOneExample(Example e) {
        double[] outputOutputs = getOutputs(outputNeurons,(getOutputs(hiddenNeurons, e.getInputs())));

        double maxValue = 0; //value of the highest output
        int maxIndex = 0; //index of output that has the highest vale

        for (int j = 0; j<outputNum; j++) {
            if (outputOutputs[j]>maxValue) {
                maxValue = outputOutputs[j];
                maxIndex = j;
            }
        }

        int[] toReturn = new int[outputNum];
        java.util.Arrays.fill(toReturn, 0);
        toReturn[maxIndex] = 1;
        //System.out.println("Classify maxIndex: " + maxIndex);
        //System.out.println("Classify maxValue: " + maxValue);

        return toReturn;
    }

    private double calculateAccuracy(Example[] examples)  {
        int total = examples.length;
        int correct = 0;

        for (Example e: examples) {
            /*System.out.println("Example Correct: " + Arrays.toString(e.getCorrect()));
            System.out.println("Example Classified: " + Arrays.toString(classifyOneExample(e)));
            System.out.println("Are they equal? " + (Arrays.equals(classifyOneExample(e),e.getCorrect())));*/
            if (Arrays.equals(classifyOneExample(e),e.getCorrect())) {
                correct++;
            }
        }
        //System.out.println("Number Correct: " + correct);
        return correct*1.0/total;
    }

    private void modify(Example e) {

        double[] hiddenOutputs = getOutputs(hiddenNeurons, e.getInputs());
        double[] outputOutputs = getOutputs(outputNeurons, hiddenOutputs);
        int[] correctOutputs = e.getCorrect();

        //For each output node o, compute the OutputErrorSignal
        for (int o = 0; o<outputNum; o++) {
            double neuronOutput = outputOutputs[o];
            double correctOutput = correctOutputs[o];
            //System.out.println("Output Neuron Output: " + neuronOutput);
            //System.out.println("Proper example output: " + correctOutput);

            double outputErrorSignal = (correctOutput-neuronOutput)*neuronOutput*(1-neuronOutput);
            //System.out.println("Output Error Signal: " + outputErrorSignal);
            outputErrorSignals[o]  = outputErrorSignal;
        }

        //System.out.println(Arrays.toString(outputErrorSignals));

        //For each hidden node h, compute the HiddenErrorSignal
        for (int h = 0; h<hiddenNum; h++) {
            double hiddenErrorSignal = 0;
            double hiddenNeuronOutput = hiddenOutputs[h];

            //for each output node o
            for (int o = 0; o<outputNum; o++) {
                hiddenErrorSignal += outputErrorSignals[o]*outputNeurons[o].inputWeights[h];
                //System.out.println("Adding to hidden error signal: " + outputErrorSignals[k]*outputNeurons[k].inputWeights[k]);
            }

            hiddenErrorSignal*=hiddenNeuronOutput*(1-hiddenNeuronOutput);
            hiddenErrorSignals[h] = hiddenErrorSignal;
            //System.out.println("Hidden Error Signal is: " + hiddenErrorSignal);
        }


        //Update the weights for each output node o
        for (int o = 0;  o< outputNum; o++) {
            Neuron outputNeuron = outputNeurons[o];
            double outputErrorSignal = outputErrorSignals[o];
            //For each hidden node h, update the OutputWeight with the formula
            for (int h = 0; h < hiddenNum; h++) {
                double hiddenOutput = hiddenOutputs[h];
                //System.out.println("Hidden Output: " + hOutput);
                //System.out.println("Output error signal being added to weights: " + outputErrorSignals[z]);
                //System.out.println("Output weight before modification: " +  o.inputWeights[l]);
                outputNeuron.inputWeights[h] += outputErrorSignal*hiddenOutput*learningRate;
                //System.out.println("Output weight after modification: " +  o.inputWeights[l]);
            }
            //System.out.println("Output Bias before: " + o.getBias());
            outputNeuron.addToBias(outputErrorSignal*learningRate);
            //System.out.println("Output Bias after: " + o.getBias());
        }

        //Update the weights for each hidden node h
        for (int h = 0; h< hiddenNum; h++) {
            Neuron hiddenNeuron = hiddenNeurons[h];
            double hiddenErrorSignal = hiddenErrorSignals[h];
            //For each input node i, update the HiddenWeight with the formula
            for (int i = 0; i < sensorNum; i++) {
                //System.out.println("Hidden Error Signal: " +  hiddenErrorSignals[l]);
                //System.out.println("Hidden weight before modification: " +  h.inputWeights[l]);

                hiddenNeuron.inputWeights[i] += hiddenErrorSignal*e.getInputs()[i]*learningRate;
                //System.out.println("Hidden weight after modification: " +  h.inputWeights[l]);

                //System.out.println("Input Weights of hidden neuron: " + Arrays.toString(h.inputWeights));
                //System.out.println("Sensor value: " + sensors[l]);
                //System.out.println("New input weight at this location: " +  h.inputWeights[l]);
                //System.out.println("Adding to hiddens's input weights: " + hiddenErrorSignals[l]*sensors[l]*learningRate);

            }
            //System.out.println("Hidden Bias before: " + h.getBias());
            hiddenNeuron.addToBias(hiddenErrorSignal*learningRate);
            //System.out.println("Hidden Bias after: " + h.getBias());
        }
    }

    int learn(Example[] examples, double portionUsedForLearning, double desiredAccuracy) {
        Example[] learn = examples;
        Example[] validate = examples;
        if (desiredAccuracy != 1) {
            learn = new Example[(int) (examples.length * portionUsedForLearning)];
            validate = new Example[examples.length - learn.length];
            System.arraycopy(examples, 0, learn, 0, learn.length);
            System.arraycopy(examples, learn.length, validate, 0, validate.length);
        }
        //credit to Quinn for array copying

        int epoch = 0;
        //System.out.println("Calculated Accuracy: " + calculateAccuracy(validate));
        while(calculateAccuracy(validate) < desiredAccuracy) {

            //System.out.println("Epoch: " + epoch + " Accuracy: " + calculateAccuracy(validate));
            for (Example e: learn) modify(e);
            epoch+=1;
            //if (epoch>10) break;
        }
        validationAccuracy = calculateAccuracy(validate);
        return epoch;
    }

    double getValidationAccuracy() { return validationAccuracy;}

    double test(Example[] testingData) {
        return calculateAccuracy(testingData);
    }
}