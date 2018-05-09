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
        double[] samples = new double[signal.size()];
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
        
        double minFreq = 0.4;
        int numBins = (forwardArray.length) / 2;
        double Fs = 23;
        
        double minBin = Math.round((minFreq / Fs) * numBins);
        int usableBins = (numBins / 2);
        
        for (int i = 1; i < usableBins + 1; i++){
            if (i <= minBin){
                int reIndex = 2 * i;
                int imIndex = (2 * i) + 1;

                // Zero the indices
                forwardArray[reIndex] = 0;
                forwardArray[imIndex] = 0;
                forwardArray[forwardArray.length - reIndex + 1] = 0;
                forwardArray[forwardArray.length - imIndex + 1] = 0;
            }
        }
        
        for (int i = 0; i < forwardArray.length / 2; i++){
            System.out.println("Bin " + i + ": " + forwardArray[2 * i] + " " + forwardArray[2 * i + 1]);
                   
        }
        
        System.out.println(minBin);
        
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
