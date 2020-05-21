import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

public class iris_app {
    public static void main(String[] args) throws Exception {
        //System.out.println(System.getProperty("sun.arch.data.model"));
        Init(500);
    }

    public static void Init(int nEpochs) throws Exception {
        double learningRate=0.001;
        int numIn=4,numOut=3,nHidden=10,seed=1234;
        System.out.println("Creation of model-------------------------------------------------------------------------");
        MultiLayerConfiguration configuration=new NeuralNetConfiguration.Builder()
                //For keeping the network outputs reproducible during runs by initializing weights and other network randomizations through a seed
                .seed(seed)
                //Algorithm to be used for updating the parameters
                .updater(new Adam(learningRate))
                //For configuring MultiLayerNetwork we call the list method
                .list()
                //The first parameter is the index of the position where the layer needs to be added. The second parameter is the type of layer we need to add to the network.
                .layer(0, new DenseLayer.Builder()
                        .nIn(numIn)
                        .nOut(nHidden)
                        .activation(Activation.SIGMOID).build()
                )
                //Loss function(The group of functions that are minimized) is used as measurement of how good a prediction model does in terms of being able to predict the expected outcome
                .layer(1,new OutputLayer.Builder(LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR)
                        .nIn(nHidden)
                        .nOut(numOut)
                        .activation(Activation.SOFTMAX).build()
                ).build();
        MultiLayerNetwork model=new MultiLayerNetwork(configuration);
        model.init();
        System.out.println(configuration.toJson());
        System.out.println("End of creation---------------------------------------------------------------------------");

        System.out.println("##########################################################################################");

        System.out.println("Monitoring learning-----------------------------------------------------------------------");
        UIServer uiServer = UIServer.getInstance();
        InMemoryStatsStorage statsStorage=new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        model.setListeners(new StatsListener(statsStorage));
        System.out.println("End of monitoring-------------------------------------------------------------------------");

        System.out.println("##########################################################################################");

        System.out.println("Training----------------------------------------------------------------------------------");
        RecordReader rr=new CSVRecordReader();
        rr.initialize(new FileSplit(new ClassPathResource("iris_train.csv").getFile()));
        //Separate data
        int batchSize=1;
        int classIndex = 4;
        DataSetIterator dataSetTrain=new RecordReaderDataSetIterator(rr,batchSize,classIndex,numOut);
        while(dataSetTrain.hasNext()){
            System.out.println("-----------------------------------------------------------");
            DataSet dataSet = dataSetTrain.next();
            System.out.println(dataSet.getFeatures());
            System.out.println("///////////////////////////////////////////////////////////");
            System.out.println(dataSet.getLabels());
            System.out.println("-----------------------------------------------------------");
        }
        for (int i = 0; i <nEpochs ; i++) model.fit(dataSetTrain);
        System.out.println("End of training---------------------------------------------------------------------------");

        System.out.println("##########################################################################################");

        System.out.println("Model Evaluation--------------------------------------------------------------------------");
        RecordReader rrTest=new CSVRecordReader();
        rrTest.initialize(new FileSplit(new ClassPathResource("iris_test.csv").getFile()));
        DataSetIterator dataSetTest=new RecordReaderDataSetIterator(rrTest,batchSize,classIndex,numOut);
        Evaluation evaluation=new Evaluation();
        while(dataSetTest.hasNext()){
            DataSet dataSet=dataSetTest.next();
            INDArray features=dataSet.getFeatures();
            INDArray targetLabels=dataSet.getLabels();
            INDArray predictedLabels=model.output(features);
            evaluation.eval(predictedLabels,targetLabels);
        }
        System.out.println(evaluation.stats());
        System.out.println("End of evaluation-------------------------------------------------------------------------");
        ModelSerializer.writeModel(model,"irisModel.zip",true);
    }
}
