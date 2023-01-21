package portfolio_ana;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.Covariance;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Data {

    public static Table get_returns(String price_filepath){
        // read csv
        Table t = Table.read().file(price_filepath);

        // define stock columns
        List<String> stock_cols = t.columnNames();
        stock_cols.remove(0);
        stock_cols.remove(0);

        // create table of returns
        Table returns = Table.create("returns");
        Column<?> date_ret = t.column("Date").where(Selection.withRange(1,t.column("Date").size()));
        returns.addColumns(date_ret);

        // iterate over stock price columns and calulate returns
        for (String col : stock_cols){
            Column<?> stock_price = t.column(col);
            DoubleColumn ret_col = DoubleColumn.create(col);
            for(int i = 1; i< stock_price.size();i++){
                Double ret = (Double) stock_price.get(i)/(Double) stock_price.get(i-1) - 1;
                ret_col.append(ret);
            }
            returns.addColumns(ret_col);
        }

        return returns;
    }

    public static RealMatrix convert_to_matrix(Table t){
        RealMatrix mx = MatrixUtils.createRealMatrix(t.as().doubleMatrix());
        return mx;
    }

    public static RealMatrix annualize_returns(RealMatrix ret_mat){
        RealMatrix ann_ret = ret_mat.copy();
        for(int i=0; i<ret_mat.getRowDimension();i++){
            for(int j=0; j<ret_mat.getColumnDimension(); j++){
                double a = Math.pow(ret_mat.getEntry(i,j)+1, 250)-1;
                ann_ret.setEntry(i,j,a);
            }
        }
        return ann_ret;
    }

    public static RealMatrix get_cov_matrix(RealMatrix mat){
        return new Covariance(mat).getCovarianceMatrix();
    }

    public static double[] get_mean_arr(RealMatrix mat){

        int n_col = mat.getColumnDimension();
        double [] means = new double [n_col];
        for(int i=0; i<n_col; i++){
            double [] col = mat.getColumn(i);
            double mean = StatUtils.mean(col);
            means[i] = mean;
        }

        return means;

    }
}
