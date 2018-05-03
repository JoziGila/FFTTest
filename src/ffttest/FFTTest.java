package ffttest;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import org.jtransforms.fft.DoubleFFT_1D;

public class FFTTest {
    static JavaPlot plot;
    public static void main(String[] args) {
        plot = new JavaPlot();
        
        // Create signal
        double[] samples = new double[512];
        for (int i = 0; i < samples.length; i++){
            double x = ((double)i / samples.length) * (4 * Math.PI);
            samples[i] = Math.sin(x) + Math.sin(20 * x) + Math.sin(50 * x);
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
        
        // Print magnitudes
        for (int i = 0; i < samples.length; i++){
            double mag = Math.sqrt(Math.pow(forwardArray[2 * i], 2) + Math.pow(forwardArray[2 * i + 1], 2));
            System.out.println("Index: " + i + " Val: " + mag);
        }
        
        // Filter
        forwardArray[4] = 0.0;
        forwardArray[5] = 0.0;
        forwardArray[1021] = 0.0;
        forwardArray[1020] = 0.0;
        
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
