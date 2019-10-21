public class Tester {
    public static void main(String[] args) {
        //AndTesting andTester = new AndTesting(1.0, 1, 2);
        //andTester.displayResult();

        //ExOrTesting exOrTester = new ExOrTesting(0.05,1, 2);
        //exOrTester.displayResult();

        //AKDigitsTesting ak_digitTester = new AKDigitsTesting(0.1, 0.97, 32, 64, 0.9);
        //ak_digitTester.displayResult();

        MNISTDigitsTesting mnist_digitTester = new MNISTDigitsTesting(0.05, 0.97, 60, 784, 0.9);
        mnist_digitTester.displayResult();
    }
}
