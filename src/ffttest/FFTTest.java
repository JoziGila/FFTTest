package ffttest;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.jtransforms.fft.DoubleFFT_1D;

public class FFTTest {
    static JavaPlot plot;
    public static void main(String[] args) {
        // Load the signal
        
        String csvFile = "/Users/Jozi/Desktop/cutregion.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        
        ArrayList<Double> signal = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                signal.add(Double.parseDouble(data[0]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        
        plot = new JavaPlot();
        
        // Cast it to array
        double[] samples = new double[(signal.size() * 2) / 8];
        for (int i = 0; i < samples.length; i++){
            samples[i] = signal.get(i);
        }
        
        // Allocate forwardArray with samples
        double[] forwardArray = new double[samples.length * 2];
        for(int i = 0; i < samples.length; i++){
            forwardArray[2 * i] = samples[i];
            forwardArray[(2 * i) + 1] = 0.0;
        }
        
        // Perform fft
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
        fft.complexForward(forwardArray);
        
        double minBin = (0.3 * ((double)forwardArray.length - 2)) / ((double)samples.length / 8);
        int offset = 2;
        int maxBins = (forwardArray.length - 2) / 4;
        for (int i = 0; i < maxBins; i++){
            if (i < minBin){
                int reIndex = 2 * i;
                int imIndex = (2 * i) + 1;

                // Zero the indices
                forwardArray[offset + reIndex] = 0;
                forwardArray[offset + imIndex] = 0;
                forwardArray[forwardArray.length - 1 - reIndex] = 0;
                forwardArray[forwardArray.length - 1 - imIndex] = 0;
            }
        }
        
        
        // Perform inverse
        double[] inverseArray = forwardArray.clone();
        fft.complexInverse(inverseArray, true);
        
        // Plot original
        plot(samples, (2 * Math.PI) / samples.length);
        
        // Inverse plot
        double[] normalisedInverse = samples.clone();
        for (int i = 0; i < normalisedInverse.length; i++){
            normalisedInverse[i] = inverseArray[i * 2];
        }
        
        plot(normalisedInverse, (2 * Math.PI) / samples.length);
        plot.plot();
    }
    
    public static void plot(double[] array, double dX){
        double[][] dataset = new double[array.length][2];
        double x = 0.0;
        for(int i = 0; i < array.length; i++){
            dataset[i][0] = x;
            dataset[i][1] = array[i];
            x += dX;
        }
        
        DataSetPlot datasetPlot = new DataSetPlot(dataset);
        PlotStyle style = new PlotStyle(Style.LINES);
        datasetPlot.setPlotStyle(style);
        
        plot.addPlot(datasetPlot);
    }
    
}
