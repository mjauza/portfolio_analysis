import java.util.List;

import org.apache.commons.math3.linear.RealMatrixFormat;
import org.apache.commons.math3.stat.correlation.Covariance;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

import portfolio_ana.Data;
import portfolio_ana.markowitz;
import org.apache.commons.math3.linear.RealMatrix;

public class main {
    public static void main(String[] args) {
        String filepath = "src/daily_fin_data.csv";
        Table returns = Data.get_returns(filepath);
        //System.out.println(returns.print());

        Table ret_num = returns.removeColumns("Date");
        String[] stock_names = ret_num.columnNames().toArray(new String[0]);

        RealMatrixFormat matrixFormat = new RealMatrixFormat();

        RealMatrix ret_mat = Data.convert_to_matrix(ret_num);
        //RealMatrix ann_ret_mat = Data.annualize_returns(ret_mat);
        //System.out.println(matrixFormat.format(ret_mat));

        // get convarinace
        RealMatrix ret_cov = Data.get_cov_matrix(ret_mat);
        double [] ret_mean = Data.get_mean_arr(ret_mat);

        for(double mean : ret_mean){
            System.out.println(mean);
        }


        double exp_ret = Math.pow(10, -4);
        markowitz.get_optim_port(ret_cov, ret_mean, stock_names, exp_ret);

    }

}
