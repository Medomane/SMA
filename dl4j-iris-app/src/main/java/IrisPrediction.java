import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;

public class IrisPrediction {
    public static void main(String[] args) throws IOException {
        System.out.println("Prediction-------------------------------------------------------");
        String[] labels = {"Iris-setosa","Iris-versicolor","Iris-virginica"};
        INDArray xs= Nd4j.create(new double[][]{
                {5.1,3.5,1.4,0.2},
                {4.9,3.0,1.4,0.2},
                {6.7,3.1,4.4,1.4},
                {5.6,3.0,4.5,1.5},
                {6.0,3.0,4.8,1.8},
                {6.9,3.1,5.4,2.1},
        });
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork("irisModel.zip");
        INDArray ys=model.output(xs);
        int[] classes = ys.argMax(1).toIntVector();
        for (int aClass : classes) {
            System.out.println("Class : " + labels[aClass]);
        }
        System.out.println(ys);
        System.out.println("End of prediction-------------------------------------------------------");
    }
}
