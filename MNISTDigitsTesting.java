import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class MNISTDigitsTesting implements TestingDevice {
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

    MNISTDigitsTesting(double learningRate, double desiredAccuracy, int hiddenNum, int sensorNum, double portionUsedForLearning) {
        this.learningRate = learningRate;
        this.desiredAccuracy = desiredAccuracy;
        this.hiddenNum = hiddenNum;
        this.portionUsedForLearning = portionUsedForLearning;
        this.sensorNum = sensorNum;

        System.out.println("MNIST Digit Testing results: ");
        System.out.println("    - " + hiddenNum + " hidden neurons");
        System.out.println("    - " + "Learning rate of: " + learningRate);
        System.out.println("    - " + "Validation threshold of: " + desiredAccuracy);

    }


    List<Example> readData(String labelFileName, String imageFileName) {
        DataInputStream labelStream = openFile(labelFileName, 2049);
        DataInputStream imageStream = openFile(imageFileName, 2051);

        List<Example> examples = new ArrayList<>();

        try {
            int numLabels = labelStream.readInt();
            int numImages = imageStream.readInt();
            assert(numImages == numLabels) : "lengths of label file and image file do not match";

            int rows = imageStream.readInt();
            int cols = imageStream.readInt();
            assert(rows == cols) : "images in file are not square";
            assert(rows == 28) : "images in file are wrong size";

            for (int i = 0; i < numImages; i++) {
                int categoryLabel = Byte.toUnsignedInt(labelStream.readByte());
                double[] inputs = new double[rows * cols];
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        int pixel = 255 - Byte.toUnsignedInt(imageStream.readByte());
                        inputs[r * rows + c] = pixel / 255.0;
                    }
                }
                examples.add(new Example(inputs, categoryLabel, 10));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return examples;
    }

    DataInputStream openFile(String fileName, int expectedMagicNumber) {
        DataInputStream stream = null;
        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
            int magic = stream.readInt();
            if (magic != expectedMagicNumber) {
                throw new RuntimeException("file " + fileName + " contains invalid magic number");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file " + fileName + " was not found");
        } catch (IOException e) {
            throw new RuntimeException("file " + fileName + " had exception: " + e);
        }
        return stream;
    }

    @Override
    public void displayResult() {
        run();
        System.out.println("    - Validation accuracy: " + validationAccuracy);
        System.out.println("    - Learning completed in " + learningEpochs  + " epochs");
        System.out.println("    - Final testing accuracy " + testingAccuracy);
    }

    @Override
    public void run() {
        training = readData("train-labels-idx1-ubyte", "train-images-idx3-ubyte").toArray(new Example[0]);
        testing = readData("t10k-labels-idx1-ubyte", "t10k-images-idx3-ubyte").toArray(new Example[0]);
        AviNeuralNet tester = new AviNeuralNet(sensorNum, hiddenNum, 10, learningRate);
        learningEpochs = tester.learn(training, portionUsedForLearning, desiredAccuracy);
        testingAccuracy = tester.test(testing);
        validationAccuracy = tester.getValidationAccuracy();
        System.out.println();
    }
}
