package portfolio_ana;

import org.apache.commons.math3.linear.AbstractRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import static org.apache.commons.math3.util.FastMath.sqrt;

public class Metric {

    public static RealVector port_ret(RealVector port_w, RealMatrix daily_ret_mx){
        return daily_ret_mx.operate(port_w);
    }

    public static double sharpe_ratio(RealVector port_ret, double r0){
        double p = StatUtils.mean(port_ret.toArray());
        double sigma = sqrt(StatUtils.variance(port_ret.toArray()));
        return (p - r0)/sigma;
    }

    public static double get_beta(RealVector port_ret, RealVector market_ret, double r0){
        RealVector y = port_ret.mapSubtract(r0);
        RealVector x = market_ret.mapSubtract(r0);
        RealMatrix data = MatrixUtils.createRealMatrix(y.getDimension(), 2);
        data.setColumn(0, y.toArray());
        data.setColumn(1, x.toArray());
        SimpleRegression sr = new SimpleRegression(false);
        sr.addData(data.getData());
        double beta = sr.getSlope();
        return beta;
    }

    public static double jensen_index(RealVector port_ret, RealVector market_ret, double r0){
        double beta = get_beta(port_ret, market_ret, r0);
        double mp = StatUtils.mean(port_ret.toArray());
        double mt = StatUtils.mean(market_ret.toArray());
        return mp - (r0 + beta*(mt - r0));
    }

    public static double treynor_index(RealVector port_ret, RealVector market_ret, double r0){
        double mp = StatUtils.mean(port_ret.toArray());
        double beta = get_beta(port_ret, market_ret, r0);
        return (mp - r0) / beta;

    }

    public static double simple_VaR(RealVector port_ret, double alpha){
        //RealVector loss = port_ret.mapMultiply(-1);
        Percentile p = new Percentile();
        double var = p.evaluate(port_ret.toArray(), alpha);
        return var;
    }

    public static double simple_ES(RealVector port_ret, double alpha){
        double var = simple_VaR(port_ret, alpha);
        double s = 0;
        double num = 0;
        for(int i = 0; i<port_ret.getDimension(); i++){
            if(port_ret.getEntry(i) <= var ){
                s += port_ret.getEntry(i);
                num++;
            }
        }
        double es = num > 0 ? s/num : 0;
        return es;
    }
}
