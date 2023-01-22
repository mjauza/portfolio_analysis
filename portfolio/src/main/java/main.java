import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrixFormat;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.ojalgo.optimisation.Optimisation;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

import portfolio_ana.Data;
import portfolio_ana.markowitz;
import portfolio_ana.Metric;
import org.apache.commons.math3.linear.RealMatrix;

public class main {
    public static void main(String[] args) {
        String filepath = "src/daily_fin_data.csv";
        Table returns = Data.get_returns(filepath);
        //System.out.println(returns.print());
        final int n = returns.rowCount();

        Table ret_num = returns.removeColumns("Date");
        String[] stock_names = ret_num.columnNames().toArray(new String[0]);

        RealMatrixFormat matrixFormat = new RealMatrixFormat();

        RealMatrix ret_mat = Data.convert_to_matrix(ret_num);
        //RealMatrix ann_ret_mat = Data.annualize_returns(ret_mat);
        //System.out.println(matrixFormat.format(ret_mat));

        // get convarinace
        RealMatrix ret_cov = Data.get_cov_matrix(ret_mat);
        double [] ret_mean = Data.get_mean_arr(ret_mat);


        double exp_ret = Math.pow(10, -4);
        Triplet<Optimisation.State, Double, double[]> res_tiplet = markowitz.get_optim_port(ret_cov, ret_mean, stock_names, exp_ret);

        Double opt_var = res_tiplet.getValue1();
        System.out.println("Optimal variance = " + opt_var);


        // jeffrey
        /*
        RealMatrix ret_cov_est = ret_cov.copy();
        RealVector ret_mean_est = new ArrayRealVector(ret_mean);

        Pair<RealVector, RealMatrix>  jeff_tuple = markowitz.get_jeffrey_parmas(ret_cov_est, ret_mean_est, n);
        //System.out.println(jeff_tuple.getValue0());

        // get jeffrey oprtimal profolio
        RealVector jeff_opt_port = markowitz.bayes_opt_port( 2, jeff_tuple.getValue1(), jeff_tuple.getValue0(), 0.02);
        System.out.println(jeff_opt_port);
        */

        RealVector port_w = new ArrayRealVector(res_tiplet.getValue2());
        RealVector port_ret = Metric.port_ret(port_w, ret_mat);
        double sr = Metric.sharpe_ratio(port_ret, 0.00001);
        System.out.println("Sharpe ratio = "+sr);

        // get market port
        RealVector equal_w = markowitz.equal_w(ret_mat.getColumnDimension());
        RealVector market_ret = Metric.port_ret(equal_w,ret_mat);
        // calculate beta
        double beta =  Metric.get_beta(port_ret, market_ret, 0.00001);
        System.out.println("Beta = " + beta);

        double jensen_idx = Metric.jensen_index(port_ret, market_ret, 0.00001);
        System.out.println("Jensen index = " + jensen_idx);

        double treynor_idx = Metric.treynor_index(port_ret, market_ret, 0.00001);
        System.out.println("Treynor index = " + treynor_idx);


        double var = Metric.simple_VaR(port_ret, 5);
        System.out.println("VaR = " + var);

        double es = Metric.simple_ES(port_ret, 5);
        System.out.println("ES = "+ es);



    }

}
