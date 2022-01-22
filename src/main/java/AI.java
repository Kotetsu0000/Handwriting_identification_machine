import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;

public class AI extends Thread{

    ComputationGraph model;
    int imageSize = 28;
    float[][] image = new float[imageSize][imageSize];
    float[][] pred_board = new float[imageSize][imageSize];
    double[] score = new double[10];

    double[][] gauss = {{1.0/16.0,2.0/16.0,1.0/16.0},{2.0/16.0,4.0/16.0,2.0/16.0},{1.0/16.0,2.0/16.0,1.0/16.0}};

    AI() throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        this.reset();
        System.out.println("AI_class start");
        String simpleMlp = new ClassPathResource("model.h5").getFile().getPath();
        this.model = KerasModelImport.importKerasModelAndWeights(simpleMlp);
        System.out.println("AI Load success!!");
    }

    public void run(){
        System.out.println("AI predict start");
        while (true){
            if(this.painted()){
                this.pred_board = this.image;
                score = predict();
            }
            else{
                for(int i=0;i<10;i++){
                    this.score[i] = 0;
                }
            }

        }
    }

    //画像配列のリセット
    void reset(){
        for(int i=0;i<imageSize;i++){
            for(int j=0;j<imageSize;j++){
                this.image[i][j] = 0;
                this.pred_board[i][j] = 0;
            }
        }
        for(int i=0;i<10;i++){
            this.score[i] = 0;
        }
    }

    //配列の形式をAIで推論できる形に変形する関数
    INDArray deformation(){
        double[][][][] return_list = new double[1][imageSize][imageSize][1];
        for(int i=0;i<imageSize;i++){
            for(int j=0;j<imageSize;j++){
                return_list[0][i][j][0] = this.pred_board[j][i];
            }
        }
        //INDArray return_list = Nd4j.create(new double[][][]{this.pred_board});
        return Nd4j.create(return_list);
    }

    //評価を行う関数
    double[] predict(){
        return model.output(deformation())[0].toDoubleMatrix()[0];
    }

    float[][] conv2d(float[][] canvas, double[][] filter){
        float[][] returnCanvas = new float[canvas.length][canvas.length];
        for(int i=0;i<canvas.length;i++){
            for(int j=0;j<canvas.length;j++){
                for(int x=-1;x<2;x++){
                    for(int y=-1;y<2;y++){
                        if(-1<i+x&&i+x<imageSize&&-1<j+y&&j+y<imageSize){
                            returnCanvas[i][j] += filter[x+1][y+1]*canvas[i+x][j+y];
                        }
                    }
                }
            }
        }
        return returnCanvas;
    }

    int maxScoreIndex(){
        double max = 0;
        int maxIndex = 0;
        for (int i = 0; i < this.score.length; i++){
            if (max<this.score[i]){
                max = this.score[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    boolean painted(){
        for(int i=0;i<this.imageSize;i++){
            for(int j=0;j<this.imageSize;j++){
                if(this.image[i][j] == 1){
                    return true;
                }
            }
        }
        return false;
    }
}
