package portfolio_ana;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class markowitz {

    public static Triplet<Optimisation.State, Double, double[]> get_optim_port(RealMatrix ret_cov, double[] ret_mean, String[] stock_names, double exp_ret){

        Primitive64Matrix H = Primitive64Matrix.FACTORY.rows(ret_cov.getData());

        int num_stocks = ret_mean.length;
        //final Variable[] tmpVariables = new Variable[num_stocks];
        List<Variable> variables = new ArrayList<>();
        for(int i=0; i<num_stocks; i++){
            String var_name = stock_names[i];
            //tmpVariables[i] = Variable.make(var_name).lower(null);
            variables.add(Variable.make(var_name));
        }
        final ExpressionsBasedModel retVal = new ExpressionsBasedModel(variables);
        final Expression tmp100P = retVal.addExpression("Balance");
        for (final Variable tmpVariable : variables) {
            tmp100P.set(tmpVariable, BigDecimal.ONE);
        }
        tmp100P.level(BigDecimal.ONE);
        final Expression mean_exp = retVal.addExpression("ExpectRet");
        for (int i= 0; i<num_stocks; i++){
            mean_exp.set(variables.get(i), ret_mean[i]);
        }
        mean_exp.level(exp_ret);
        final Expression tmpVar = retVal.addExpression("Variance");
        tmpVar.setQuadraticFactors(variables, H.divide(2));
        tmpVar.weight(BigDecimal.ONE);

        Optimisation.Result tmpResult = retVal.minimise();

        final Optimisation.State tmpState = tmpResult.getState();

        double opt_var = 2*tmpResult.getValue();
        double[] opt_positions = new double[num_stocks];
        for(int i=0; i<num_stocks; i++){
            opt_positions[i] = tmpResult.doubleValue(i);
        }

        Triplet<Optimisation.State, Double, double[]> results = Triplet.with(tmpState,opt_var,opt_positions);

        return results;
    }
    public static RealVector bayes_opt_port(double lambda, RealMatrix ret_cov, RealVector ret_mean, double r0){
        assert lambda > 0;

        RealMatrix ret_cov_inv = MatrixUtils.inverse(ret_cov);
        RealVector v = ret_mean.mapSubtract(r0);
        RealVector opt_port = ret_cov_inv.operate(v).mapDivide(lambda);
        return opt_port;

    }

    public static Pair<RealVector, RealMatrix> get_jeffrey_parmas(RealMatrix ret_cov_est, RealVector ret_mean_est, int n){
        RealVector ret_mean = ret_mean_est.copy();
        double d = (double) ret_cov_est.getColumnDimension();
        assert n > d;
        double c_d_n = n/(n - d - 1) + (2*n - d - 1)/(n*(n-d-1)*(n-d-2));
        RealMatrix ret_cov = ret_cov_est.scalarMultiply(c_d_n);
        Pair<RealVector, RealMatrix> results = Pair.with(ret_mean, ret_cov);
        return results;
    }

    public static RealVector equal_w(int num_stocks){
        double w = 1.0/(double)num_stocks;
        RealVector a = new ArrayRealVector(num_stocks, w);
        return a;
    }
}
